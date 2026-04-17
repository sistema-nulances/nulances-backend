package com.Nulances.service.disponibilidade;

import com.Nulances.dto.response.ComitenteDisponibilidadeResponse;
import com.Nulances.repository.ComitenteRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class ComitenteDisponibilidadeService {

    private final ComitenteRepository comitenteRepository;

    @PreAuthorize("hasRole('ADMIN')")
    @Transactional(readOnly = true)
    public ComitenteDisponibilidadeResponse verificarDisponibilidade(String documento) {
        String documentoNormalizado = normalizarDocumento(documento);

        if (documentoNormalizado == null) {
            return new ComitenteDisponibilidadeResponse(true, null);
        }

        boolean disponivel = !comitenteRepository.existsByDocumento(documentoNormalizado);

        return new ComitenteDisponibilidadeResponse(
                disponivel,
                disponivel ? null : "Documento já registrado"
        );
    }

    private String normalizarDocumento(String documento) {
        if (documento == null || documento.isBlank()) {
            return null;
        }
        return documento.trim();
    }
}