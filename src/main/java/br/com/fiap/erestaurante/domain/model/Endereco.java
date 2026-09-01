package br.com.fiap.erestaurante.domain.model;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * Value Object — imutável, sem identidade própria.
 * Pertence ao agregado Usuario.
 * Sem anotações JPA: o domain é puro e independente de frameworks.
 */
@Getter
@AllArgsConstructor
public class Endereco {

    private final String logradouro;
    private final String numero;
    private final String complemento;
    private final String bairro;
    private final String cidade;
    private final String estado;
    private final String cep;
}
