package br.com.fiap.erestaurante.core.usecase.restaurante;

import br.com.fiap.erestaurante.core.domain.Restaurante;

import java.util.List;

public interface BuscarRestauranteUseCase {

    Restaurante buscarPorId(Long id);

    List<Restaurante> buscarPorNome(String nome);
}
