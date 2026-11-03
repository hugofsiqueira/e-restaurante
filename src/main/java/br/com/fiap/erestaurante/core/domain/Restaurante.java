package br.com.fiap.erestaurante.core.domain;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Entidade de domínio — representa um restaurante cadastrado no sistema.
 * Sem anotações Spring; pertence exclusivamente ao core.
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Restaurante {

    private Long id;
    private String nome;
    private Endereco endereco;
    private String tipoCozinha;
    private HorarioFuncionamento horarioFuncionamento;
    private DonoRestaurante dono;
}
