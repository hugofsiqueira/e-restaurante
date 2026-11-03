package br.com.fiap.erestaurante.core.domain;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;

/**
 * Entidade de domínio — representa um item do cardápio de um restaurante.
 * Sem anotações Spring; pertence exclusivamente ao core.
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ItemCardapio {

    private Long id;
    private String nome;
    private String descricao;
    private BigDecimal preco;
    private boolean disponivel;
    private String fotoPath;
    private Restaurante restaurante;
}
