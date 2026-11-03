package br.com.fiap.erestaurante.infra.web.json;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class ItemCardapioResponse {

    private Long id;
    private String nome;
    private String descricao;
    private BigDecimal preco;
    private boolean disponivel;
    private String fotoPath;
    private Long restauranteId;
    private String restauranteNome;
}
