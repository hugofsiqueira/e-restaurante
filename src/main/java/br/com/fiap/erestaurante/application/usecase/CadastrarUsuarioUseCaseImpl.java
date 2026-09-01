package br.com.fiap.erestaurante.application.usecase;

import br.com.fiap.erestaurante.domain.exception.EmailJaCadastradoException;
import br.com.fiap.erestaurante.domain.model.Cliente;
import br.com.fiap.erestaurante.domain.model.DonoRestaurante;
import br.com.fiap.erestaurante.domain.model.Endereco;
import br.com.fiap.erestaurante.domain.model.TipoUsuario;
import br.com.fiap.erestaurante.domain.model.Usuario;
import br.com.fiap.erestaurante.domain.port.in.CadastrarUsuarioUseCase;
import br.com.fiap.erestaurante.domain.port.out.UsuarioRepositoryPort;
import org.springframework.security.crypto.password.PasswordEncoder;

/**
 * SRP: responsável exclusivamente pelo fluxo de cadastro de usuário.
 * Sem anotação @Service — gerenciado como @Bean em BeanConfiguration
 * para manter a camada application livre de dependência do Spring.
 */
public class CadastrarUsuarioUseCaseImpl implements CadastrarUsuarioUseCase {

    private final UsuarioRepositoryPort repository;
    private final PasswordEncoder passwordEncoder;

    public CadastrarUsuarioUseCaseImpl(UsuarioRepositoryPort repository,
                                       PasswordEncoder passwordEncoder) {
        this.repository = repository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public Usuario execute(Command command) {
        // Validação de unicidade do email na camada de serviço (requisito explícito)
        if (repository.existePorEmail(command.email())) {
            throw new EmailJaCadastradoException(command.email());
        }

        var endereco = new Endereco(command.logradouro(), command.numero(), command.complemento(),
                command.bairro(), command.cidade(), command.estado(), command.cep());
        var senhaCriptografada = passwordEncoder.encode(command.senha());

        Usuario usuario = command.tipo() == TipoUsuario.CLIENTE
                ? new Cliente(null, command.nome(), command.email(), command.login(), senhaCriptografada, endereco, null)
                : new DonoRestaurante(null, command.nome(), command.email(), command.login(), senhaCriptografada, endereco, null);

        return repository.salvar(usuario);
    }
}
