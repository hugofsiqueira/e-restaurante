package br.com.fiap.erestaurante.core.domain;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

import java.time.LocalDateTime;

/**
 * Entidade raiz abstrata do agregado Usuario.
 * Pura regra de negócio — sem anotações de framework.
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
public abstract class Usuario {

    private Long id;
    private String nome;
    private String email;
    private String login;
    private String senha;
    private Endereco endereco;
    private TipoUsuario tipoUsuario;
    private LocalDateTime dataUltimaAlteracao;
}
