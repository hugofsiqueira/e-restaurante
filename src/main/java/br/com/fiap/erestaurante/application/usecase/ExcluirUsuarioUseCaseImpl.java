package br.com.fiap.erestaurante.application.usecase;

import br.com.fiap.erestaurante.domain.exception.UsuarioNaoEncontradoException;
import br.com.fiap.erestaurante.domain.port.in.ExcluirUsuarioUseCase;
import br.com.fiap.erestaurante.domain.port.out.UsuarioRepositoryPort;

public class ExcluirUsuarioUseCaseImpl implements ExcluirUsuarioUseCase {

    private final UsuarioRepositoryPort repository;

    public ExcluirUsuarioUseCaseImpl(UsuarioRepositoryPort repository) {
        this.repository = repository;
    }

    @Override
    public void execute(Long id) {
        // Garante que o usuário existe antes de tentar excluir
        if (repository.buscarPorId(id).isEmpty()) {
            throw new UsuarioNaoEncontradoException(id);
        }
        repository.excluir(id);
    }
}
