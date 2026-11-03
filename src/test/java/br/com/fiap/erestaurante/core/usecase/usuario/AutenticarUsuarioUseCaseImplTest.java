package br.com.fiap.erestaurante.core.usecase.usuario;

import br.com.fiap.erestaurante.core.domain.Cliente;
import br.com.fiap.erestaurante.core.domain.Endereco;
import br.com.fiap.erestaurante.core.domain.Usuario;
import br.com.fiap.erestaurante.core.exception.SenhaInvalidaException;
import br.com.fiap.erestaurante.core.exception.UsuarioNaoEncontradoException;
import br.com.fiap.erestaurante.core.gateway.UsuarioGateway;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AutenticarUsuarioUseCaseImplTest {

    @Mock
    private UsuarioGateway gateway;

    @Mock
    private PasswordEncoder passwordEncoder;

    private AutenticarUsuarioUseCaseImpl useCase;

    private Cliente cliente;

    @BeforeEach
    void setUp() {
        useCase = new AutenticarUsuarioUseCaseImpl(gateway, passwordEncoder);
        var endereco = Endereco.builder()
                .logradouro("Rua A").numero("1").bairro("Bairro").cidade("Cidade").estado("SP").cep("01000-000").build();
        cliente = Cliente.builder()
                .id(1L).nome("João").email("joao@email.com").login("joaosilva").senha("hashSenha").endereco(endereco).build();
    }

    @Test
    void deveAutenticarComSucesso() {
        var command = new AutenticarUsuarioUseCase.Command("joaosilva", "senhaCorreta");

        when(gateway.buscarPorLogin("joaosilva")).thenReturn(Optional.of(cliente));
        when(passwordEncoder.matches("senhaCorreta", "hashSenha")).thenReturn(true);

        Usuario resultado = useCase.execute(command);

        assertThat(resultado.getLogin()).isEqualTo("joaosilva");
        assertThat(resultado.getEmail()).isEqualTo("joao@email.com");
    }

    @Test
    void deveLancarSenhaInvalidaExceptionSeSenhaIncorreta() {
        var command = new AutenticarUsuarioUseCase.Command("joaosilva", "senhaErrada");

        when(gateway.buscarPorLogin("joaosilva")).thenReturn(Optional.of(cliente));
        when(passwordEncoder.matches("senhaErrada", "hashSenha")).thenReturn(false);

        assertThatThrownBy(() -> useCase.execute(command))
                .isInstanceOf(SenhaInvalidaException.class);
    }

    @Test
    void deveLancarUsuarioNaoEncontradoExceptionSeLoginInexistente() {
        var command = new AutenticarUsuarioUseCase.Command("loginInexistente", "senha");

        when(gateway.buscarPorLogin("loginInexistente")).thenReturn(Optional.empty());

        assertThatThrownBy(() -> useCase.execute(command))
                .isInstanceOf(UsuarioNaoEncontradoException.class);
    }
}
