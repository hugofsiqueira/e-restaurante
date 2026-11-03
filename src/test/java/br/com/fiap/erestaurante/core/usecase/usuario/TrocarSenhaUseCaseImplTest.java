package br.com.fiap.erestaurante.core.usecase.usuario;

import br.com.fiap.erestaurante.core.domain.Cliente;
import br.com.fiap.erestaurante.core.domain.Endereco;
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

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class TrocarSenhaUseCaseImplTest {

    @Mock
    private UsuarioGateway gateway;

    @Mock
    private PasswordEncoder passwordEncoder;

    private TrocarSenhaUseCaseImpl useCase;

    private Cliente cliente;

    @BeforeEach
    void setUp() {
        useCase = new TrocarSenhaUseCaseImpl(gateway, passwordEncoder);
        var endereco = Endereco.builder()
                .logradouro("Rua A").numero("1").bairro("Bairro").cidade("Cidade").estado("SP").cep("01000-000").build();
        cliente = Cliente.builder()
                .id(1L).nome("João").email("joao@email.com").login("joao").senha("hashAtual").endereco(endereco).build();
    }

    @Test
    void deveTrocarSenhaComSucesso() {
        var command = new TrocarSenhaUseCase.Command("senhaAtual", "novaSenha");

        when(gateway.buscarPorId(1L)).thenReturn(Optional.of(cliente));
        when(passwordEncoder.matches("senhaAtual", "hashAtual")).thenReturn(true);
        when(passwordEncoder.encode("novaSenha")).thenReturn("hashNovo");
        when(gateway.salvar(any())).thenReturn(cliente);

        useCase.execute(1L, command);

        verify(passwordEncoder).encode("novaSenha");
        verify(gateway).salvar(cliente);
    }

    @Test
    void deveLancarSenhaInvalidaExceptionSeSenhaAtualErrada() {
        var command = new TrocarSenhaUseCase.Command("senhaErrada", "novaSenha");

        when(gateway.buscarPorId(1L)).thenReturn(Optional.of(cliente));
        when(passwordEncoder.matches("senhaErrada", "hashAtual")).thenReturn(false);

        assertThatThrownBy(() -> useCase.execute(1L, command))
                .isInstanceOf(SenhaInvalidaException.class);

        verify(passwordEncoder, never()).encode(anyString());
        verify(gateway, never()).salvar(any());
    }

    @Test
    void deveLancarUsuarioNaoEncontradoExceptionSeIdInexistente() {
        var command = new TrocarSenhaUseCase.Command("senha", "novaSenha");

        when(gateway.buscarPorId(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> useCase.execute(99L, command))
                .isInstanceOf(UsuarioNaoEncontradoException.class);

        verify(passwordEncoder, never()).matches(anyString(), anyString());
    }
}
