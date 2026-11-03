package br.com.fiap.erestaurante.core.controller;

import br.com.fiap.erestaurante.core.domain.Restaurante;
import br.com.fiap.erestaurante.core.usecase.restaurante.AtualizarRestauranteUseCase;
import br.com.fiap.erestaurante.core.usecase.restaurante.BuscarRestauranteUseCase;
import br.com.fiap.erestaurante.core.usecase.restaurante.CriarRestauranteUseCase;
import br.com.fiap.erestaurante.core.usecase.restaurante.ExcluirRestauranteUseCase;
import lombok.RequiredArgsConstructor;

import java.util.List;

/**
 * Controlador de domínio puro — sem anotações Spring.
 * Orquestra os casos de uso do agregado Restaurante.
 * Padrão: Clean Arch Intermediário (poc-cleanarch-hexagonal-mvc).
 */
@RequiredArgsConstructor
public class RestauranteController {

    private final CriarRestauranteUseCase criarUseCase;
    private final BuscarRestauranteUseCase buscarUseCase;
    private final AtualizarRestauranteUseCase atualizarUseCase;
    private final ExcluirRestauranteUseCase excluirUseCase;

    public Restaurante criar(CriarRestauranteUseCase.Command command) {
        return criarUseCase.execute(command);
    }

    public Restaurante buscarPorId(Long id) {
        return buscarUseCase.buscarPorId(id);
    }

    public List<Restaurante> buscarPorNome(String nome) {
        return buscarUseCase.buscarPorNome(nome);
    }

    public Restaurante atualizar(Long id, AtualizarRestauranteUseCase.Command command) {
        return atualizarUseCase.execute(id, command);
    }

    public void excluir(Long id) {
        excluirUseCase.execute(id);
    }
}
