package br.com.fiap.erestaurante.core.usecase.usuario;

import br.com.fiap.erestaurante.core.domain.Usuario;
import br.com.fiap.erestaurante.core.exception.UsuarioNaoEncontradoException;
import br.com.fiap.erestaurante.core.gateway.UsuarioGateway;
import lombok.RequiredArgsConstructor;

import java.util.List;

@RequiredArgsConstructor
public class BuscarUsuarioUseCaseImpl implements BuscarUsuarioUseCase {

    private final UsuarioGateway gateway;


    @Override
    public Usuario buscarPorId(Long id) {
        return gateway.buscarPorId(id)
                .orElseThrow(() -> new UsuarioNaoEncontradoException(id));
    }

    @Override
    public List<Usuario> buscarPorNome(String nome) {
        return gateway.buscarPorNome(nome);
    }
}
