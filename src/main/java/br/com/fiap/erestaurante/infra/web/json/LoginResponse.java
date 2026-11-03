package br.com.fiap.erestaurante.infra.web.json;

import lombok.Data;

@Data
public class LoginResponse {

    private Long id;
    private String nome;
    private String login;
    private TipoUsuarioResponse tipoUsuario;
}
