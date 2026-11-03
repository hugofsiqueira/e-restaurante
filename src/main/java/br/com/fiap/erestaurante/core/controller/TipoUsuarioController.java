package br.com.fiap.erestaurante.core.controller;

import br.com.fiap.erestaurante.core.domain.TipoUsuario;
import br.com.fiap.erestaurante.core.usecase.tipousuario.AtualizarTipoUsuarioUseCase;
import br.com.fiap.erestaurante.core.usecase.tipousuario.BuscarTipoUsuarioUseCase;
import br.com.fiap.erestaurante.core.usecase.tipousuario.CriarTipoUsuarioUseCase;
import br.com.fiap.erestaurante.core.usecase.tipousuario.ExcluirTipoUsuarioUseCase;
import lombok.RequiredArgsConstructor;

import java.util.List;

/**
 * Orquestrador de domínio — sem anotações Spring.
 */
@RequiredArgsConstructor
public class TipoUsuarioController {

    private final CriarTipoUsuarioUseCase criarUseCase;
    private final BuscarTipoUsuarioUseCase buscarUseCase;
    private final AtualizarTipoUsuarioUseCase atualizarUseCase;
    private final ExcluirTipoUsuarioUseCase excluirUseCase;

    public TipoUsuario criar(CriarTipoUsuarioUseCase.Command command) {
        return criarUseCase.execute(command);
    }

    public TipoUsuario buscarPorId(Long id) {
        return buscarUseCase.buscarPorId(id);
    }

    public List<TipoUsuario> buscarTodos() {
        return buscarUseCase.buscarTodos();
    }

    public TipoUsuario atualizar(Long id, AtualizarTipoUsuarioUseCase.Command command) {
        return atualizarUseCase.execute(id, command);
    }

    public void excluir(Long id) {
        excluirUseCase.execute(id);
    }
}
