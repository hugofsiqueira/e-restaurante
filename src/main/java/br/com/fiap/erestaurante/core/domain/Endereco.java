package br.com.fiap.erestaurante.core.domain;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

/**
 * Value Object — imutável, sem identidade própria.
 * Pertence ao agregado Usuario e também será reutilizado por Restaurante.
 */
@Getter
@AllArgsConstructor
@Builder
public class Endereco {

    private final String logradouro;
    private final String numero;
    private final String complemento;
    private final String bairro;
    private final String cidade;
    private final String estado;
    private final String cep;
}
