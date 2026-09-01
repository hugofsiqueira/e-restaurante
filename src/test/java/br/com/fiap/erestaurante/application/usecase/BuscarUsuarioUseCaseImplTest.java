package br.com.fiap.erestaurante.application.usecase;

import br.com.fiap.erestaurante.domain.exception.UsuarioNaoEncontradoException;
import br.com.fiap.erestaurante.domain.model.Cliente;
import br.com.fiap.erestaurante.domain.model.Endereco;
import br.com.fiap.erestaurante.domain.model.Usuario;
import br.com.fiap.erestaurante.domain.port.out.UsuarioRepositoryPort;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class BuscarUsuarioUseCaseImplTest {

    @Mock
    private UsuarioRepositoryPort repository;

    @InjectMocks
    private BuscarUsuarioUseCaseImpl useCase;

    private Cliente cliente;

    @BeforeEach
    void setUp() {
        var endereco = new Endereco("Rua A", "1", null, "Bairro", "Cidade", "SP", "01000-000");
        cliente = new Cliente(1L, "João Silva", "joao@email.com", "joao", "hash", endereco, null);
    }

    @Test
    void deveBuscarPorIdComSucesso() {
        when(repository.buscarPorId(1L)).thenReturn(Optional.of(cliente));

        Usuario resultado = useCase.buscarPorId(1L);

        assertThat(resultado.getId()).isEqualTo(1L);
        assertThat(resultado.getNome()).isEqualTo("João Silva");
    }

    @Test
    void deveLancarUsuarioNaoEncontradoExceptionSeBuscarPorIdInexistente() {
        when(repository.buscarPorId(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> useCase.buscarPorId(99L))
                .isInstanceOf(UsuarioNaoEncontradoException.class)
                .hasMessageContaining("99");
    }

    @Test
    void deveBuscarPorNomeRetornandoLista() {
        var outro = new Cliente(2L, "João Pedro", "joaopedro@email.com", "joaop", "hash",
                new Endereco("Rua B", "2", null, "Bairro", "Cidade", "SP", "01000-000"), null);

        when(repository.buscarPorNome("João")).thenReturn(List.of(cliente, outro));

        List<Usuario> resultado = useCase.buscarPorNome("João");

        assertThat(resultado).hasSize(2);
        assertThat(resultado).extracting(Usuario::getNome)
                .containsExactlyInAnyOrder("João Silva", "João Pedro");
    }

    @Test
    void deveBuscarPorNomeRetornandoListaVaziaSeNaoEncontrado() {
        when(repository.buscarPorNome("Inexistente")).thenReturn(List.of());

        List<Usuario> resultado = useCase.buscarPorNome("Inexistente");

        assertThat(resultado).isEmpty();
    }
}
