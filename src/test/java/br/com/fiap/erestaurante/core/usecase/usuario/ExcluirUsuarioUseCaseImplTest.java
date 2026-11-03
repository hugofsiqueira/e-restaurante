package br.com.fiap.erestaurante.core.usecase.usuario;

import br.com.fiap.erestaurante.core.domain.Cliente;
import br.com.fiap.erestaurante.core.domain.Endereco;
import br.com.fiap.erestaurante.core.exception.UsuarioNaoEncontradoException;
import br.com.fiap.erestaurante.core.gateway.UsuarioGateway;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ExcluirUsuarioUseCaseImplTest {

    @Mock
    private UsuarioGateway gateway;

    private ExcluirUsuarioUseCaseImpl useCase;

    @BeforeEach
    void setUp() {
        useCase = new ExcluirUsuarioUseCaseImpl(gateway);
    }

    @Test
    void deveExcluirUsuarioExistente() {
        var endereco = Endereco.builder()
                .logradouro("Rua A").numero("1").bairro("Bairro").cidade("Cidade").estado("SP").cep("01000-000").build();
        var cliente = Cliente.builder()
                .id(1L).nome("João").email("joao@email.com").login("joao").senha("hash").endereco(endereco).build();

        when(gateway.buscarPorId(1L)).thenReturn(Optional.of(cliente));

        useCase.execute(1L);

        verify(gateway).excluir(1L);
    }

    @Test
    void deveLancarUsuarioNaoEncontradoExceptionSeIdInexistente() {
        when(gateway.buscarPorId(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> useCase.execute(99L))
                .isInstanceOf(UsuarioNaoEncontradoException.class);

        verify(gateway, never()).excluir(99L);
    }
}
