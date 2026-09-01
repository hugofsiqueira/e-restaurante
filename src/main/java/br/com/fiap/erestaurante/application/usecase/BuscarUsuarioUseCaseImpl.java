package br.com.fiap.erestaurante.application.usecase;

import br.com.fiap.erestaurante.domain.exception.UsuarioNaoEncontradoException;
import br.com.fiap.erestaurante.domain.model.Usuario;
import br.com.fiap.erestaurante.domain.port.in.BuscarUsuarioUseCase;
import br.com.fiap.erestaurante.domain.port.out.UsuarioRepositoryPort;

import java.util.List;

public class BuscarUsuarioUseCaseImpl implements BuscarUsuarioUseCase {

    private final UsuarioRepositoryPort repository;

    public BuscarUsuarioUseCaseImpl(UsuarioRepositoryPort repository) {
        this.repository = repository;
    }

    @Override
    public Usuario buscarPorId(Long id) {
        return repository.buscarPorId(id)
                .orElseThrow(() -> new UsuarioNaoEncontradoException(id));
    }

    @Override
    public List<Usuario> buscarPorNome(String nome) {
        return repository.buscarPorNome(nome);
    }
}
