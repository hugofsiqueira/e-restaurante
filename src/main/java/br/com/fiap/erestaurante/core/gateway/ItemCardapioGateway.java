package br.com.fiap.erestaurante.core.gateway;

import br.com.fiap.erestaurante.core.domain.ItemCardapio;

import java.util.List;
import java.util.Optional;

public interface ItemCardapioGateway {

    ItemCardapio salvar(ItemCardapio item);

    Optional<ItemCardapio> buscarPorId(Long id);

    List<ItemCardapio> buscarPorRestaurante(Long restauranteId);

    void excluir(Long id);
}
