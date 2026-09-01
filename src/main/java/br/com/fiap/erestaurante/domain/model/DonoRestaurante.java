package br.com.fiap.erestaurante.domain.model;

import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * Subtipo concreto de Usuario para donos de restaurante.
 * Nas fases futuras, ganhará vínculo com a entidade Restaurante.
 * LSP: substitui Usuario em qualquer ponto do sistema sem quebrar comportamento.
 */
@NoArgsConstructor
public class DonoRestaurante extends Usuario {

    public DonoRestaurante(Long id, String nome, String email, String login,
                           String senha, Endereco endereco, LocalDateTime dataUltimaAlteracao) {
        super(id, nome, email, login, senha, endereco, dataUltimaAlteracao);
    }

    @Override
    public TipoUsuario getTipo() {
        return TipoUsuario.DONO_RESTAURANTE;
    }
}
