package br.com.fiap.erestaurante.core.usecase.usuario;

import br.com.fiap.erestaurante.core.domain.Cliente;
import br.com.fiap.erestaurante.core.domain.Endereco;
import br.com.fiap.erestaurante.core.exception.EmailJaCadastradoException;
import br.com.fiap.erestaurante.core.exception.UsuarioNaoEncontradoException;
import br.com.fiap.erestaurante.core.gateway.UsuarioGateway;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AtualizarUsuarioUseCaseImplTest {

    @Mock
    private UsuarioGateway gateway;

    private AtualizarUsuarioUseCaseImpl useCase;

    private Cliente clienteExistente;

    @BeforeEach
    void setUp() {
        useCase = new AtualizarUsuarioUseCaseImpl(gateway);
        var endereco = Endereco.builder()
                .logradouro("Rua Antiga").numero("10").bairro("Bairro Antigo")
                .cidade("Cidade").estado("SP").cep("01000-000").build();
        clienteExistente = Cliente.builder()
                .id(1L).nome("Nome Antigo").email("antigo@email.com")
                .login("login").senha("hash").endereco(endereco).build();
    }

    @Test
    void deveAtualizarUsuarioComSucesso() {
        var command = new AtualizarUsuarioUseCase.Command(
                "Nome Novo", "novo@email.com",
                "Rua Nova", "20", null, "Bairro Novo", "Cidade Nova", "RJ", "20000-000"
        );
        var atualizado = Cliente.builder()
                .id(1L).nome("Nome Novo").email("novo@email.com").login("login").senha("hash")
                .endereco(Endereco.builder()
                        .logradouro("Rua Nova").numero("20").bairro("Bairro Novo")
                        .cidade("Cidade Nova").estado("RJ").cep("20000-000").build())
                .build();

        when(gateway.buscarPorId(1L)).thenReturn(Optional.of(clienteExistente));
        when(gateway.existePorEmail("novo@email.com")).thenReturn(false);
        when(gateway.salvar(any())).thenReturn(atualizado);

        var resultado = useCase.execute(1L, command);

        assertThat(resultado.getNome()).isEqualTo("Nome Novo");
        assertThat(resultado.getEmail()).isEqualTo("novo@email.com");
    }

    @Test
    void devePermitirMesmoEmailSemConflito() {
        var command = new AtualizarUsuarioUseCase.Command(
                "Nome Atualizado", "antigo@email.com",
                "Rua Nova", "20", null, "Bairro", "Cidade", "SP", "01000-000"
        );
        when(gateway.buscarPorId(1L)).thenReturn(Optional.of(clienteExistente));
        when(gateway.salvar(any())).thenReturn(clienteExistente);

        useCase.execute(1L, command);

        verify(gateway, never()).existePorEmail("antigo@email.com");
    }

    @Test
    void deveLancarUsuarioNaoEncontradoExceptionSeIdInexistente() {
        var command = new AtualizarUsuarioUseCase.Command(
                "Qualquer", "email@email.com",
                "Rua", "1", null, "Bairro", "Cidade", "SP", "01000-000"
        );

        when(gateway.buscarPorId(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> useCase.execute(99L, command))
                .isInstanceOf(UsuarioNaoEncontradoException.class);

        verify(gateway, never()).salvar(any());
    }

    @Test
    void deveLancarEmailJaCadastradoExceptionSeNovoEmailJaUsado() {
        var command = new AtualizarUsuarioUseCase.Command(
                "Nome", "ocupado@email.com",
                "Rua", "1", null, "Bairro", "Cidade", "SP", "01000-000"
        );

        when(gateway.buscarPorId(1L)).thenReturn(Optional.of(clienteExistente));
        when(gateway.existePorEmail("ocupado@email.com")).thenReturn(true);

        assertThatThrownBy(() -> useCase.execute(1L, command))
                .isInstanceOf(EmailJaCadastradoException.class)
                .hasMessageContaining("ocupado@email.com");

        verify(gateway, never()).salvar(any());
    }
}
