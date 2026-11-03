package br.com.fiap.erestaurante.infra.web.json;

import lombok.Data;

@Data
public class RestauranteResponse {

    private Long id;
    private String nome;
    private String tipoCozinha;
    private HorarioFuncionamentoResponse horarioFuncionamento;
    private EnderecoResponse endereco;
    private Long donoId;
    private String donoNome;
}
