package br.com.fiap.erestaurante.application.usecase;

import br.com.fiap.erestaurante.domain.exception.SenhaInvalidaException;
import br.com.fiap.erestaurante.domain.exception.UsuarioNaoEncontradoException;
import br.com.fiap.erestaurante.domain.model.Cliente;
import br.com.fiap.erestaurante.domain.model.Endereco;
import br.com.fiap.erestaurante.domain.port.in.TrocarSenhaUseCase;
import br.com.fiap.erestaurante.domain.port.out.UsuarioRepositoryPort;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class TrocarSenhaUseCaseImplTest {

    @Mock
    private UsuarioRepositoryPort repository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private TrocarSenhaUseCaseImpl useCase;

    private Cliente cliente;

    @BeforeEach
    void setUp() {
        var endereco = new Endereco("Rua A", "1", null, "Bairro", "Cidade", "SP", "01000-000");
        cliente = new Cliente(1L, "João", "joao@email.com", "joao", "hashAtual", endereco, null);
    }

    @Test
    void deveTrocarSenhaComSucesso() {
        var command = new TrocarSenhaUseCase.Command("senhaAtual", "novaSenha");

        when(repository.buscarPorId(1L)).thenReturn(Optional.of(cliente));
        when(passwordEncoder.matches("senhaAtual", "hashAtual")).thenReturn(true);
        when(passwordEncoder.encode("novaSenha")).thenReturn("hashNovo");
        when(repository.salvar(any())).thenReturn(cliente);

        useCase.execute(1L, command);

        verify(passwordEncoder).encode("novaSenha");
        verify(repository).salvar(cliente);
    }

    @Test
    void deveLancarSenhaInvalidaExceptionSeSenhaAtualErrada() {
        var command = new TrocarSenhaUseCase.Command("senhaErrada", "novaSenha");

        when(repository.buscarPorId(1L)).thenReturn(Optional.of(cliente));
        when(passwordEncoder.matches("senhaErrada", "hashAtual")).thenReturn(false);

        assertThatThrownBy(() -> useCase.execute(1L, command))
                .isInstanceOf(SenhaInvalidaException.class);

        verify(passwordEncoder, never()).encode(anyString());
        verify(repository, never()).salvar(any());
    }

    @Test
    void deveLancarUsuarioNaoEncontradoExceptionSeIdInexistente() {
        var command = new TrocarSenhaUseCase.Command("senha", "novaSenha");

        when(repository.buscarPorId(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> useCase.execute(99L, command))
                .isInstanceOf(UsuarioNaoEncontradoException.class);

        verify(passwordEncoder, never()).matches(anyString(), anyString());
    }
}
