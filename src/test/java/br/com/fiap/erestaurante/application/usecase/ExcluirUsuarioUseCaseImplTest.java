package br.com.fiap.erestaurante.application.usecase;

import br.com.fiap.erestaurante.domain.exception.UsuarioNaoEncontradoException;
import br.com.fiap.erestaurante.domain.model.Cliente;
import br.com.fiap.erestaurante.domain.model.Endereco;
import br.com.fiap.erestaurante.domain.port.out.UsuarioRepositoryPort;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
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
    private UsuarioRepositoryPort repository;

    @InjectMocks
    private ExcluirUsuarioUseCaseImpl useCase;

    @Test
    void deveExcluirUsuarioExistente() {
        var endereco = new Endereco("Rua A", "1", null, "Bairro", "Cidade", "SP", "01000-000");
        var cliente = new Cliente(1L, "João", "joao@email.com", "joao", "hash", endereco, null);

        when(repository.buscarPorId(1L)).thenReturn(Optional.of(cliente));

        useCase.execute(1L);

        verify(repository).excluir(1L);
    }

    @Test
    void deveLancarUsuarioNaoEncontradoExceptionSeIdInexistente() {
        when(repository.buscarPorId(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> useCase.execute(99L))
                .isInstanceOf(UsuarioNaoEncontradoException.class);

        verify(repository, never()).excluir(99L);
    }
}
