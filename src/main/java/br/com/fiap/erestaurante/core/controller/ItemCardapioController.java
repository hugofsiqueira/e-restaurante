package br.com.fiap.erestaurante.core.controller;

import br.com.fiap.erestaurante.core.domain.ItemCardapio;
import br.com.fiap.erestaurante.core.usecase.itemcardapio.AdicionarItemCardapioUseCase;
import br.com.fiap.erestaurante.core.usecase.itemcardapio.AtualizarItemCardapioUseCase;
import br.com.fiap.erestaurante.core.usecase.itemcardapio.BuscarItemCardapioUseCase;
import br.com.fiap.erestaurante.core.usecase.itemcardapio.ExcluirItemCardapioUseCase;
import lombok.RequiredArgsConstructor;

import java.util.List;

/**
 * Orquestrador de domínio — sem anotações Spring.
 */
@RequiredArgsConstructor
public class ItemCardapioController {

    private final AdicionarItemCardapioUseCase adicionarUseCase;
    private final BuscarItemCardapioUseCase buscarUseCase;
    private final AtualizarItemCardapioUseCase atualizarUseCase;
    private final ExcluirItemCardapioUseCase excluirUseCase;

    public ItemCardapio adicionar(AdicionarItemCardapioUseCase.Command command) {
        return adicionarUseCase.execute(command);
    }

    public ItemCardapio buscarPorId(Long id) {
        return buscarUseCase.buscarPorId(id);
    }

    public List<ItemCardapio> buscarPorRestaurante(Long restauranteId) {
        return buscarUseCase.buscarPorRestaurante(restauranteId);
    }

    public ItemCardapio atualizar(Long id, AtualizarItemCardapioUseCase.Command command) {
        return atualizarUseCase.execute(id, command);
    }

    public void excluir(Long id) {
        excluirUseCase.execute(id);
    }
}
