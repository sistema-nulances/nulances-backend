package com.Nulances.domain.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(
        name = "anuncio_detalhes_tecnicos",
        uniqueConstraints = {
                @UniqueConstraint(name = "uk_anuncio_detalhe_anuncio", columnNames = "anuncio_id")
        }
)
@Getter
@Setter
public class AnuncioDetalheTecnico extends AuditableEntity {

    @OneToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "anuncio_id", nullable = false)
    private Anuncio anuncio;

    private String motorizacao;
    private String cilindros;

    @Column(name = "potencia_combinada")
    private String potenciaCombinada;

    @Column(name = "torque_combinado")
    private String torqueCombinado;

    private String transmissao;
    private String tracao;

    @Column(name = "modos_conducao")
    private String modosConducao;

    private String carroceria;

    @Column(name = "comprimento_largura_altura")
    private String comprimentoLarguraAltura;

    @Column(name = "entre_eixos")
    private String entreEixos;

    @Column(name = "porta_malas")
    private String portaMalas;

    @Column(name = "tanque_combustivel")
    private String tanqueCombustivel;

    @Column(name = "ciclos_urbano")
    private String ciclosUrbano;

    @Column(name = "uso_modo_eletrico")
    private String usoModoEletrico;

    @Column(name = "emissoes_selo_eficiencia")
    private String emissoesSeloEficiencia;

    @Column(name = "freios_dianteiros")
    private String freiosDianteiros;

    @Column(name = "suspensao_dianteira")
    private String suspensaoDianteira;

    @Column(name = "suspensao_traseira")
    private String suspensaoTraseira;

    @Column(name = "medida_pneus")
    private String medidaPneus;

    private String estepe;
    private String airbags;

    @Column(name = "abs_distribuicao_eletronica")
    private String absDistribuicaoEletronica;

    @Column(name = "controle_estabilidade_tracao")
    private String controleEstabilidadeTracao;

    @Column(name = "assistente_partida_rampa")
    private String assistentePartidaRampa;

    @Column(name = "camera_sensores_estacionamento")
    private String cameraSensoresEstacionamento;

    @Column(name = "ar_condicionado_climatizador")
    private String arCondicionadoClimatizador;

    private String direcao;

    @Column(name = "bancos_volante")
    private String bancosVolante;

    @Column(name = "multimidia_conectividade")
    private String multimidiaConectividade;

    @Column(name = "rodas_iluminacao")
    private String rodasIluminacao;

    @Column(name = "vidros_travas")
    private String vidrosTravas;

    @Column(name = "procedencia_nulances")
    private String procedenciaNulances;

    @Column(name = "licenciamento_debitos")
    private String licenciamentoDebitos;

    @Column(name = "restricoes_gravame")
    private String restricoesGravame;

    @Column(name = "chaves_manual")
    private String chavesManual;

    @Column(name = "laudo_cautelar_inspecao")
    private String laudoCautelarInspecao;
}