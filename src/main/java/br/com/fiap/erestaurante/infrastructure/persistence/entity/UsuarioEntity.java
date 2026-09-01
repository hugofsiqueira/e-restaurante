package br.com.fiap.erestaurante.infrastructure.persistence.entity;

import jakarta.persistence.Column;
import jakarta.persistence.DiscriminatorColumn;
import jakarta.persistence.DiscriminatorType;
import jakarta.persistence.Embedded;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Inheritance;
import jakarta.persistence.InheritanceType;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.time.ZoneOffset;

/**
 * Entidade JPA base — infraestrutura de persistência.
 * Estratégia SINGLE_TABLE: uma única tabela para Cliente e DonoRestaurante,
 * diferenciados pelo campo discriminador "dtype".
 *
 * O campo dataUltimaAlteracao é gerenciado automaticamente via callbacks JPA
 * (@PrePersist e @PreUpdate), eliminando a necessidade de setar manualmente.
 */
@Entity
@Table(name = "usuario")
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
@DiscriminatorColumn(name = "dtype", discriminatorType = DiscriminatorType.STRING)
@Getter
@Setter
@NoArgsConstructor
public class UsuarioEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 150)
    private String nome;

    @Column(nullable = false, unique = true, length = 255)
    private String email;

    @Column(nullable = false, unique = true, length = 100)
    private String login;

    @Column(nullable = false, length = 255)
    private String senha;

    @Embedded
    private EnderecoEntity endereco;

    @Column(name = "data_ultima_alteracao", nullable = false)
    private LocalDateTime dataUltimaAlteracao;

    @PrePersist
    @PreUpdate
    void atualizarDataAlteracao() {
        this.dataUltimaAlteracao = LocalDateTime.now(ZoneOffset.UTC);
    }
}
