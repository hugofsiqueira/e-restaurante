package br.com.fiap.erestaurante.infrastructure.web.dto.response;

import br.com.fiap.erestaurante.domain.model.TipoUsuario;
import lombok.Data;

@Data
public class LoginResponse {

    private Long id;
    private String nome;
    private String login;
    private TipoUsuario tipo;
}
