package br.com.fiap.erestaurante.application.usecase;

import br.com.fiap.erestaurante.domain.exception.SenhaInvalidaException;
import br.com.fiap.erestaurante.domain.exception.UsuarioNaoEncontradoException;
import br.com.fiap.erestaurante.domain.model.Cliente;
import br.com.fiap.erestaurante.domain.model.Endereco;
import br.com.fiap.erestaurante.domain.model.Usuario;
import br.com.fiap.erestaurante.domain.port.in.AutenticarUsuarioUseCase;
import br.com.fiap.erestaurante.domain.port.out.UsuarioRepositoryPort;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
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
    private UsuarioRepositoryPort repository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private AutenticarUsuarioUseCaseImpl useCase;

    private Cliente cliente;

    @BeforeEach
    void setUp() {
        var endereco = new Endereco("Rua A", "1", null, "Bairro", "Cidade", "SP", "01000-000");
        cliente = new Cliente(1L, "João", "joao@email.com", "joaosilva", "hashSenha", endereco, null);
    }

    @Test
    void deveAutenticarComSucesso() {
        var command = new AutenticarUsuarioUseCase.Command("joaosilva", "senhaCorreta");

        when(repository.buscarPorLogin("joaosilva")).thenReturn(Optional.of(cliente));
        when(passwordEncoder.matches("senhaCorreta", "hashSenha")).thenReturn(true);

        Usuario resultado = useCase.execute(command);

        assertThat(resultado.getLogin()).isEqualTo("joaosilva");
        assertThat(resultado.getEmail()).isEqualTo("joao@email.com");
    }

    @Test
    void deveLancarSenhaInvalidaExceptionSeSenhaIncorreta() {
        var command = new AutenticarUsuarioUseCase.Command("joaosilva", "senhaErrada");

        when(repository.buscarPorLogin("joaosilva")).thenReturn(Optional.of(cliente));
        when(passwordEncoder.matches("senhaErrada", "hashSenha")).thenReturn(false);

        assertThatThrownBy(() -> useCase.execute(command))
                .isInstanceOf(SenhaInvalidaException.class);
    }

    @Test
    void deveLancarUsuarioNaoEncontradoExceptionSeLoginInexistente() {
        var command = new AutenticarUsuarioUseCase.Command("loginInexistente", "senha");

        when(repository.buscarPorLogin("loginInexistente")).thenReturn(Optional.empty());

        // A implementação usa -1L como ID genérico para não revelar se o login existe
        assertThatThrownBy(() -> useCase.execute(command))
                .isInstanceOf(UsuarioNaoEncontradoException.class);
    }
}
