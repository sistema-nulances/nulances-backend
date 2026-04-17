package com.Nulances.domain.entity;

import com.Nulances.domain.enums.*;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "anuncios")
@Getter
@Setter
public class Anuncio extends AuditableEntity {

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "vendedor_id", nullable = false)
    private Usuario vendedor;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "marca_id", nullable = false)
    private Marca marca;

    @Column(nullable = false)
    private String modelo;

    @Column(nullable = false, precision = 15, scale = 2)
    private BigDecimal preco;

    @Column(nullable = false, length = 100)
    private String cidade;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private TipoVeiculo tipo;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private CondicaoAnuncioVeiculo condicao;

    @Column(nullable = false)
    private Integer ano;

    private Long quilometragem;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private CombustivelVeiculo combustivel;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private CambioVeiculo cambio;

    @Column(name = "final_chassi", length = 10)
    private String finalChassi;

    @Column(length = 50)
    private String cor;

    @Column(nullable = false)
    private Boolean blindado = false;

    @Column(name = "placa_veiculo", length = 10)
    private String placaVeiculo;

    @Column(nullable = false, columnDefinition = "text")
    private String descricao;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private StatusAnuncio status = StatusAnuncio.PENDENTE;

    @OneToOne(mappedBy = "anuncio", fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
    private AnuncioDetalheTecnico detalheTecnico;

    @OneToMany(mappedBy = "anuncio", fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
    @OrderBy("ordem ASC")
    private List<AnuncioMidia> midias = new ArrayList<>();

    public void adicionarMidia(AnuncioMidia midia) {
        midia.setAnuncio(this);
        this.midias.add(midia);
    }

    public void removerMidia(AnuncioMidia midia) {
        this.midias.remove(midia);
        midia.setAnuncio(null);
    }

    public void definirDetalheTecnico(AnuncioDetalheTecnico detalheTecnico) {
        if (detalheTecnico != null) {
            detalheTecnico.setAnuncio(this);
        }
        this.detalheTecnico = detalheTecnico;
    }
}