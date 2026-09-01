package br.com.fiap.erestaurante.infrastructure.web.dto.response;

import lombok.Data;

@Data
public class EnderecoResponse {

    private String logradouro;
    private String numero;
    private String complemento;
    private String bairro;
    private String cidade;
    private String estado;
    private String cep;
}
