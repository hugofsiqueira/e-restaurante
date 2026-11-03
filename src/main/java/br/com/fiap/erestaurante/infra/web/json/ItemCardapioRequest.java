package br.com.fiap.erestaurante.infra.web.json;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.math.BigDecimal;

@Data
@Schema(description = "Dados do item do cardápio")
public class ItemCardapioRequest {

    @NotBlank(message = "Nome é obrigatório")
    @Size(max = 200)
    private String nome;

    @Size(max = 2000)
    private String descricao;

    @NotNull(message = "Preço é obrigatório")
    @DecimalMin(value = "0.01", message = "Preço deve ser maior que zero")
    @Digits(integer = 8, fraction = 2)
    private BigDecimal preco;

    private boolean disponivel = true;

    @Size(max = 500)
    private String fotoPath;

    @NotNull(message = "ID do restaurante é obrigatório")
    private Long restauranteId;
}
