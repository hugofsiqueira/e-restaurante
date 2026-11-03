package br.com.fiap.erestaurante.infra.web.json;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class UsuarioResponse {

    private Long id;
    private String nome;
    private String email;
    private String login;
    private TipoUsuarioResponse tipoUsuario;
    private EnderecoResponse endereco;
    private LocalDateTime dataUltimaAlteracao;
}
