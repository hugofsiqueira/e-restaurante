package br.com.fiap.erestaurante.domain.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

/**
 * Entidade raiz abstrata do agregado Usuario.
 *
 * Sem anotações JPA, Spring ou qualquer framework — pura regra de negócio.
 * A persistência é responsabilidade da camada de infraestrutura.
 *
 * LSP: Cliente e DonoRestaurante podem substituir Usuario em qualquer contexto.
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public abstract class Usuario {

    private Long id;
    private String nome;
    private String email;
    private String login;
    private String senha;
    private Endereco endereco;
    private LocalDateTime dataUltimaAlteracao;

    public abstract TipoUsuario getTipo();
}
