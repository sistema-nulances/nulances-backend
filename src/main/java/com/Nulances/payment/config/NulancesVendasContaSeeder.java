package com.Nulances.payment.config;

import com.Nulances.domain.entity.AssinaturaPlano;
import com.Nulances.domain.entity.PlanoAnuncio;
import com.Nulances.domain.entity.Usuario;
import com.Nulances.domain.enums.StatusAssinaturaPlano;
import com.Nulances.domain.enums.UserRole;
import com.Nulances.helpers.CpfHelper;
import com.Nulances.repository.AssinaturaPlanoRepository;
import com.Nulances.repository.PlanoAnuncioRepository;
import com.Nulances.repository.UsuarioRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;
import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class NulancesVendasContaSeeder {

    public static final String PLANO_INTERNO_NOME = "NULANCES_VENDAS_INTERNO";

    private static final String EMAIL = "contato@nulances.com";
    private static final String SENHA_PLANA = "U$Mckt#UE0q~";
    private static final String NOME = "Vendas NuLances";
    private static final String TELEFONE_DIGITOS = "31962518000";
    private static final String CPF_SEED = "11144477735";

    private final PlanoAnuncioRepository planoAnuncioRepository;
    private final UsuarioRepository usuarioRepository;
    private final AssinaturaPlanoRepository assinaturaPlanoRepository;
    private final PasswordEncoder passwordEncoder;

    @Transactional
    public void executarSeNecessario() {
        PlanoAnuncio planoInterno = garantirPlanoInterno();

        Usuario usuario = usuarioRepository.findByEmail(EMAIL)
                .map(this::alinharUsuarioExistente)
                .orElseGet(() -> criarUsuario());

        if (usuario.getRole() != UserRole.VENDEDOR && usuario.getRole() != UserRole.ADMIN) {
            usuario.setRole(UserRole.VENDEDOR);
            usuario = usuarioRepository.save(usuario);
        }

        if (assinaturaPlanoRepository.findFirstByVendedorIdAndStatusOrderByCreatedAtDesc(
                usuario.getId(), StatusAssinaturaPlano.ATIVA).isEmpty()) {
            AssinaturaPlano assinatura = new AssinaturaPlano();
            assinatura.setVendedor(usuario);
            assinatura.setPlano(planoInterno);
            assinatura.setStatus(StatusAssinaturaPlano.ATIVA);
            assinatura.setInicioVigencia(Instant.now());
            assinatura.setProximaCobranca(null);
            assinatura.setUltimaCobrancaEm(Instant.now());
            assinaturaPlanoRepository.save(assinatura);
            log.info("Assinatura interna ATIVA criada para {}.", EMAIL);
        }
    }

    private PlanoAnuncio garantirPlanoInterno() {
        Optional<PlanoAnuncio> existente = planoAnuncioRepository.findByNomeIgnoreCase(PLANO_INTERNO_NOME);
        if (existente.isPresent()) {
            PlanoAnuncio p = existente.get();
            p.setIlimitado(true);
            p.setValorMensal(BigDecimal.ZERO);
            p.setAtivo(false);
            p.setDescricao("Plano interno NuLances — anúncios ilimitados, sem cobrança recorrente.");
            if (p.getTotalAnuncios() == null || p.getTotalAnuncios() < 0) {
                p.setTotalAnuncios(0);
            }
            return planoAnuncioRepository.save(p);
        }

        PlanoAnuncio plano = new PlanoAnuncio();
        plano.setNome(PLANO_INTERNO_NOME);
        plano.setDescricao("Plano interno NuLances — anúncios ilimitados, sem cobrança recorrente.");
        plano.setValorMensal(BigDecimal.ZERO);
        plano.setTotalAnuncios(0);
        plano.setIlimitado(true);
        plano.setAtivo(false);
        return planoAnuncioRepository.save(plano);
    }

    private Usuario alinharUsuarioExistente(Usuario u) {
        boolean dirty = false;
        if (!Boolean.TRUE.equals(u.getEmailVerificado())) {
            u.setEmailVerificado(true);
            u.setEmailVerificadoEm(Instant.now());
            dirty = true;
        }
        if (u.getTelefone() == null || !TELEFONE_DIGITOS.equals(u.getTelefone().replaceAll("\\D", ""))) {
            u.setTelefone(TELEFONE_DIGITOS);
            dirty = true;
        }
        if (!NOME.equals(u.getNomeCompleto())) {
            u.setNomeCompleto(NOME);
            dirty = true;
        }
        return dirty ? usuarioRepository.save(u) : u;
    }

    private Usuario criarUsuario() {
        if (usuarioRepository.existsByCpf(CpfHelper.normalizar(CPF_SEED))) {
            log.warn(
                    "CPF do seed {} já está em uso; não foi possível criar {} automaticamente.",
                    CPF_SEED,
                    EMAIL
            );
            throw new IllegalStateException("CPF reservado ao seed NuLances já utilizado.");
        }
        if (usuarioRepository.existsByTelefone(TELEFONE_DIGITOS)) {
            log.warn("Telefone do seed já está em uso; não foi possível criar {} automaticamente.", EMAIL);
            throw new IllegalStateException("Telefone reservado ao seed NuLances já utilizado.");
        }

        Usuario u = new Usuario();
        u.setNomeCompleto(NOME);
        u.setDataNascimento(LocalDate.of(1990, 5, 20));
        u.setEmail(EMAIL);
        u.setSenha(passwordEncoder.encode(SENHA_PLANA));
        u.setCpf(CpfHelper.normalizar(CPF_SEED));
        u.setTelefone(TELEFONE_DIGITOS);
        u.setEmailVerificado(true);
        u.setEmailVerificadoEm(Instant.now());
        u.setRole(UserRole.VENDEDOR);
        u.setCidade("Belo Horizonte");
        u.setEstado("MG");
        u.setCep("30130100");
        u.setLogradouro("Seed NuLances");
        Usuario salvo = usuarioRepository.save(u);
        log.info("Usuário vendedor seed {} criado.", EMAIL);
        return salvo;
    }
}
