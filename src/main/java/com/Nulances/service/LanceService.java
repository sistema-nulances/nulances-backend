package com.Nulances.service;

import com.Nulances.domain.entity.DocumentoValidacao;
import com.Nulances.domain.entity.Lance;
import com.Nulances.domain.entity.LeilaoLoteBem;
import com.Nulances.domain.entity.Usuario;
import com.Nulances.domain.enums.StatusDocumentoValidacao;
import com.Nulances.domain.enums.StatusItemLeilao;
import com.Nulances.domain.enums.StatusLeilao;
import com.Nulances.dto.messaging.LanceRecebidoMessage;
import com.Nulances.dto.request.LanceCreateRequest;
import com.Nulances.dto.websocket.LanceAtualizadoEvent;
import com.Nulances.messaging.publisher.LancePublisher;
import com.Nulances.repository.LanceRepository;
import com.Nulances.repository.LeilaoLoteBemRepository;
import com.Nulances.repository.UsuarioRepository;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class LanceService {

    private final LeilaoLoteBemRepository leilaoLoteBemRepository;
    private final LanceRepository lanceRepository;
    private final UsuarioRepository usuarioRepository;
    private final LancePublisher lancePublisher;
    private final SimpMessagingTemplate messagingTemplate;
    private final LeilaoService leilaoService;

    @Transactional
    public void enviarLance(Authentication authentication, LanceCreateRequest request) {
        Usuario usuario = buscarUsuarioAutenticado(authentication);
        validarUsuarioAptoParaLance(usuario);

        lancePublisher.publicar(new LanceRecebidoMessage(
                request.getLeilaoLoteBemId(),
                usuario.getId(),
                request.getValor(),
                request.getClientRequestId()
        ));
    }

    @Transactional
    public void processarLance(LanceRecebidoMessage message) {
        if (lanceRepository.existsByLeilaoLoteBemIdAndUsuarioIdAndClientRequestId(
                message.leilaoLoteBemId(),
                message.usuarioId(),
                message.clientRequestId()
        )) {
            return;
        }

        LeilaoLoteBem item = leilaoLoteBemRepository.findByIdForUpdate(message.leilaoLoteBemId())
                .orElseThrow(() -> new EntityNotFoundException("Item do leilão não encontrado."));

        validarLeilaoEItemAbertosParaLance(item);

        Instant agora = Instant.now();

        if (item.getAberturaDisputa().isAfter(agora)) {
            throw new IllegalStateException("O item ainda não iniciou a disputa.");
        }

        if (!item.getEncerramentoDisputa().isAfter(agora)) {
            throw new IllegalStateException("O item já foi encerrado para novos lances.");
        }

        BigDecimal minimoAceito = item.getProximoLance() != null
                ? item.getProximoLance()
                : item.getValorInicial();

        if (message.valor() == null || message.valor().compareTo(minimoAceito) < 0) {
            throw new IllegalArgumentException("O lance é inferior ao próximo lance permitido.");
        }

        if (lanceRepository.existsByLeilaoLoteBemIdAndUsuarioIdAndValor(
                message.leilaoLoteBemId(),
                message.usuarioId(),
                message.valor()
        )) {
            throw new IllegalArgumentException("O usuário já realizou este mesmo lance para este item.");
        }

        Usuario usuario = usuarioRepository.findById(message.usuarioId())
                .orElseThrow(() -> new EntityNotFoundException("Usuário não encontrado."));

        validarUsuarioAptoParaLance(usuario);

        Lance lance = new Lance();
        lance.setLeilaoLoteBem(item);
        lance.setUsuario(usuario);
        lance.setValor(message.valor());
        lance.setClientRequestId(message.clientRequestId());

        Lance salvo = lanceRepository.save(lance);

        item.setMaiorLance(salvo);
        item.setValorAtual(salvo.getValor());
        item.setProximoLance(salvo.getValor().add(item.getIncrementoMinimo()));

        leilaoLoteBemRepository.save(item);

        UUID leilaoId = item.getLeilaoLote().getLeilao().getId();

        messagingTemplate.convertAndSend(
                "/topic/leiloes/itens/" + item.getId(),
                new LanceAtualizadoEvent(
                        item.getId().toString(),
                        usuario.getId().toString(),
                        item.getValorAtual(),
                        item.getProximoLance()
                )
        );

        messagingTemplate.convertAndSend(
                "/topic/leiloes/" + leilaoId + "/painel",
                leilaoService.buscarPainel(leilaoId)
        );
    }

    private void validarLeilaoEItemAbertosParaLance(LeilaoLoteBem item) {
        StatusLeilao statusLeilao = item.getLeilaoLote().getLeilao().getStatus();
        if (statusLeilao != StatusLeilao.AO_VIVO) {
            throw new IllegalStateException("O leilão não está aberto para lances.");
        }

        if (item.getStatus() != StatusItemLeilao.ABERTO) {
            throw new IllegalStateException("O item não está aberto para lances.");
        }
    }

    private void validarUsuarioAptoParaLance(Usuario usuario) {
        List<DocumentoValidacao> docs = usuario.getDocumentosValidacao();

        if (docs == null || docs.isEmpty()) {
            throw new IllegalStateException("Usuário não apto para dar lance: documentos de validação não enviados.");
        }

        boolean todosAprovados = docs.stream()
                .allMatch(d -> d.getStatus() == StatusDocumentoValidacao.APROVADO);

        if (!todosAprovados) {
            throw new IllegalStateException("Usuário não apto para dar lance: documentos de validação pendentes ou recusados.");
        }
    }

    private Usuario buscarUsuarioAutenticado(Authentication authentication) {
        if (authentication == null || authentication.getName() == null || authentication.getName().isBlank()) {
            throw new IllegalStateException("Usuário não autenticado.");
        }

        return usuarioRepository.findByEmail(authentication.getName())
                .orElseThrow(() -> new EntityNotFoundException("Usuário autenticado não encontrado."));
    }
}