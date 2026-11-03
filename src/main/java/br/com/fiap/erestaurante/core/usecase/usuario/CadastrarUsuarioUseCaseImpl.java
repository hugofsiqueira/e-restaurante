package br.com.fiap.erestaurante.core.usecase.usuario;

import br.com.fiap.erestaurante.core.domain.Cliente;
import br.com.fiap.erestaurante.core.domain.DonoRestaurante;
import br.com.fiap.erestaurante.core.domain.Endereco;
import br.com.fiap.erestaurante.core.domain.TipoUsuario;
import br.com.fiap.erestaurante.core.domain.Usuario;
import br.com.fiap.erestaurante.core.exception.EmailJaCadastradoException;
import br.com.fiap.erestaurante.core.exception.TipoUsuarioNaoEncontradoException;
import br.com.fiap.erestaurante.core.gateway.TipoUsuarioGateway;
import br.com.fiap.erestaurante.core.gateway.UsuarioGateway;
import org.springframework.security.crypto.password.PasswordEncoder;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class CadastrarUsuarioUseCaseImpl implements CadastrarUsuarioUseCase {

    private static final String DONO_RESTAURANTE = "DONO_RESTAURANTE";

    private final UsuarioGateway gateway;
    private final TipoUsuarioGateway tipoUsuarioGateway;
    private final PasswordEncoder passwordEncoder;


    @Override
    public Usuario execute(Command command) {
        if (gateway.existePorEmail(command.email())) {
            throw new EmailJaCadastradoException(command.email());
        }

        TipoUsuario tipoUsuario = tipoUsuarioGateway.buscarPorId(command.tipoUsuarioId())
                .orElseThrow(() -> new TipoUsuarioNaoEncontradoException(command.tipoUsuarioId()));

        var endereco = Endereco.builder()
                .logradouro(command.logradouro())
                .numero(command.numero())
                .complemento(command.complemento())
                .bairro(command.bairro())
                .cidade(command.cidade())
                .estado(command.estado())
                .cep(command.cep())
                .build();
        var senhaCriptografada = passwordEncoder.encode(command.senha());

        Usuario usuario = DONO_RESTAURANTE.equalsIgnoreCase(tipoUsuario.getNome())
                ? DonoRestaurante.builder()
                        .nome(command.nome())
                        .email(command.email())
                        .login(command.login())
                        .senha(senhaCriptografada)
                        .endereco(endereco)
                        .tipoUsuario(tipoUsuario)
                        .build()
                : Cliente.builder()
                        .nome(command.nome())
                        .email(command.email())
                        .login(command.login())
                        .senha(senhaCriptografada)
                        .endereco(endereco)
                        .tipoUsuario(tipoUsuario)
                        .build();

        return gateway.salvar(usuario);
    }
}
