package br.com.fiap.erestaurante.application.usecase;

import br.com.fiap.erestaurante.domain.exception.EmailJaCadastradoException;
import br.com.fiap.erestaurante.domain.exception.UsuarioNaoEncontradoException;
import br.com.fiap.erestaurante.domain.model.Cliente;
import br.com.fiap.erestaurante.domain.model.Endereco;
import br.com.fiap.erestaurante.domain.port.in.AtualizarUsuarioUseCase;
import br.com.fiap.erestaurante.domain.port.out.UsuarioRepositoryPort;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
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
    private UsuarioRepositoryPort repository;

    @InjectMocks
    private AtualizarUsuarioUseCaseImpl useCase;

    private Cliente clienteExistente;

    @BeforeEach
    void setUp() {
        var endereco = new Endereco("Rua Antiga", "10", null, "Bairro Antigo", "Cidade", "SP", "01000-000");
        clienteExistente = new Cliente(1L, "Nome Antigo", "antigo@email.com", "login", "hash", endereco, null);
    }

    @Test
    void deveAtualizarUsuarioComSucesso() {
        var command = new AtualizarUsuarioUseCase.Command(
                "Nome Novo", "novo@email.com",
                "Rua Nova", "20", null, "Bairro Novo", "Cidade Nova", "RJ", "20000-000"
        );
        var atualizado = new Cliente(1L, "Nome Novo", "novo@email.com", "login", "hash",
                new Endereco("Rua Nova", "20", null, "Bairro Novo", "Cidade Nova", "RJ", "20000-000"), null);

        when(repository.buscarPorId(1L)).thenReturn(Optional.of(clienteExistente));
        when(repository.existePorEmail("novo@email.com")).thenReturn(false);
        when(repository.salvar(any())).thenReturn(atualizado);

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
        when(repository.buscarPorId(1L)).thenReturn(Optional.of(clienteExistente));
        when(repository.salvar(any())).thenReturn(clienteExistente);

        useCase.execute(1L, command);

        // Não deve verificar unicidade quando o email não mudou
        verify(repository, never()).existePorEmail("antigo@email.com");
    }

    @Test
    void deveLancarUsuarioNaoEncontradoExceptionSeIdInexistente() {
        var command = new AtualizarUsuarioUseCase.Command(
                "Qualquer", "email@email.com",
                "Rua", "1", null, "Bairro", "Cidade", "SP", "01000-000"
        );

        when(repository.buscarPorId(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> useCase.execute(99L, command))
                .isInstanceOf(UsuarioNaoEncontradoException.class);

        verify(repository, never()).salvar(any());
    }

    @Test
    void deveLancarEmailJaCadastradoExceptionSeNovoEmailJaUsado() {
        var command = new AtualizarUsuarioUseCase.Command(
                "Nome", "ocupado@email.com",
                "Rua", "1", null, "Bairro", "Cidade", "SP", "01000-000"
        );

        when(repository.buscarPorId(1L)).thenReturn(Optional.of(clienteExistente));
        when(repository.existePorEmail("ocupado@email.com")).thenReturn(true);

        assertThatThrownBy(() -> useCase.execute(1L, command))
                .isInstanceOf(EmailJaCadastradoException.class)
                .hasMessageContaining("ocupado@email.com");

        verify(repository, never()).salvar(any());
    }
}
