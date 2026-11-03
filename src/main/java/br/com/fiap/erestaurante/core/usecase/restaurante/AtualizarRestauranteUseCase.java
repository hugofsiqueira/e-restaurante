package br.com.fiap.erestaurante.core.usecase.restaurante;

import br.com.fiap.erestaurante.core.domain.Restaurante;

import java.time.LocalTime;

public interface AtualizarRestauranteUseCase {

    record Command(
            String nome,
            String tipoCozinha,
            String logradouro,
            String numero,
            String complemento,
            String bairro,
            String cidade,
            String estado,
            String cep,
            LocalTime abertura,
            LocalTime fechamento
    ) {}

    Restaurante execute(Long id, Command command);
}
