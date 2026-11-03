package br.com.fiap.erestaurante.core.usecase.usuario;

import br.com.fiap.erestaurante.core.domain.Endereco;
import br.com.fiap.erestaurante.core.domain.Usuario;
import br.com.fiap.erestaurante.core.exception.EmailJaCadastradoException;
import br.com.fiap.erestaurante.core.exception.UsuarioNaoEncontradoException;
import br.com.fiap.erestaurante.core.gateway.UsuarioGateway;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class AtualizarUsuarioUseCaseImpl implements AtualizarUsuarioUseCase {

    private final UsuarioGateway gateway;


    @Override
    public Usuario execute(Long id, Command command) {
        Usuario usuario = gateway.buscarPorId(id)
                .orElseThrow(() -> new UsuarioNaoEncontradoException(id));

        if (!usuario.getEmail().equalsIgnoreCase(command.email())
                && gateway.existePorEmail(command.email())) {
            throw new EmailJaCadastradoException(command.email());
        }

        usuario.setNome(command.nome());
        usuario.setEmail(command.email());
        usuario.setEndereco(Endereco.builder()
                .logradouro(command.logradouro())
                .numero(command.numero())
                .complemento(command.complemento())
                .bairro(command.bairro())
                .cidade(command.cidade())
                .estado(command.estado())
                .cep(command.cep())
                .build());

        return gateway.salvar(usuario);
    }
}
