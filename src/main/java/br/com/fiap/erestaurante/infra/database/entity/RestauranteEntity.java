package br.com.fiap.erestaurante.infra.database.entity;

import jakarta.persistence.AttributeOverride;
import jakarta.persistence.AttributeOverrides;
import jakarta.persistence.Column;
import jakarta.persistence.Embedded;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "restaurante")
@Getter
@Setter
@NoArgsConstructor
public class RestauranteEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 200)
    private String nome;

    @Column(name = "tipo_cozinha", nullable = false, length = 100)
    private String tipoCozinha;

    @Embedded
    @AttributeOverrides({
            @AttributeOverride(name = "logradouro", column = @Column(name = "end_logradouro")),
            @AttributeOverride(name = "numero", column = @Column(name = "end_numero")),
            @AttributeOverride(name = "complemento", column = @Column(name = "end_complemento")),
            @AttributeOverride(name = "bairro", column = @Column(name = "end_bairro")),
            @AttributeOverride(name = "cidade", column = @Column(name = "end_cidade")),
            @AttributeOverride(name = "estado", column = @Column(name = "end_estado")),
            @AttributeOverride(name = "cep", column = @Column(name = "end_cep"))
    })
    private EnderecoEntity endereco;

    @Embedded
    private HorarioFuncionamentoEntity horarioFuncionamento;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "dono_id", nullable = false)
    private DonoRestauranteEntity dono;
}
