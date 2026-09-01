package br.com.fiap.erestaurante.infrastructure.web.dto.response;

import br.com.fiap.erestaurante.domain.model.TipoUsuario;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class UsuarioResponse {

    private Long id;
    private String nome;
    private String email;
    private String login;
    private TipoUsuario tipo;
    private EnderecoResponse endereco;
    private LocalDateTime dataUltimaAlteracao;
}
