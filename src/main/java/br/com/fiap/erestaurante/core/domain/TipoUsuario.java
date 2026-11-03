package br.com.fiap.erestaurante.core.domain;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Entidade de domínio que representa o tipo de usuário (ex: CLIENTE, DONO_RESTAURANTE).
 * Gerenciada via CRUD — os usuários são associados a um TipoUsuario.
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TipoUsuario {

    private Long id;
    private String nome;
}
