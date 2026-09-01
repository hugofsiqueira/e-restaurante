package br.com.fiap.erestaurante.application.usecase;

import br.com.fiap.erestaurante.domain.exception.EmailJaCadastradoException;
import br.com.fiap.erestaurante.domain.exception.UsuarioNaoEncontradoException;
import br.com.fiap.erestaurante.domain.model.Endereco;
import br.com.fiap.erestaurante.domain.model.Usuario;
import br.com.fiap.erestaurante.domain.port.in.AtualizarUsuarioUseCase;
import br.com.fiap.erestaurante.domain.port.out.UsuarioRepositoryPort;

public class AtualizarUsuarioUseCaseImpl implements AtualizarUsuarioUseCase {

    private final UsuarioRepositoryPort repository;

    public AtualizarUsuarioUseCaseImpl(UsuarioRepositoryPort repository) {
        this.repository = repository;
    }

    @Override
    public Usuario execute(Long id, Command command) {
        Usuario usuario = repository.buscarPorId(id)
                .orElseThrow(() -> new UsuarioNaoEncontradoException(id));

        // Valida email único apenas se o email foi alterado
        if (!usuario.getEmail().equalsIgnoreCase(command.email())
                && repository.existePorEmail(command.email())) {
            throw new EmailJaCadastradoException(command.email());
        }

        usuario.setNome(command.nome());
        usuario.setEmail(command.email());
        usuario.setEndereco(new Endereco(command.logradouro(), command.numero(), command.complemento(),
                command.bairro(), command.cidade(), command.estado(), command.cep()));

        return repository.salvar(usuario);
    }
}
