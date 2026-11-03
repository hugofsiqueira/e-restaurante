package br.com.fiap.erestaurante.core.usecase.usuario;

import br.com.fiap.erestaurante.core.domain.Cliente;
import br.com.fiap.erestaurante.core.domain.Endereco;
import br.com.fiap.erestaurante.core.domain.Usuario;
import br.com.fiap.erestaurante.core.exception.UsuarioNaoEncontradoException;
import br.com.fiap.erestaurante.core.gateway.UsuarioGateway;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
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
    private UsuarioGateway gateway;

    private BuscarUsuarioUseCaseImpl useCase;

    private Cliente cliente;

    @BeforeEach
    void setUp() {
        useCase = new BuscarUsuarioUseCaseImpl(gateway);
        var endereco = Endereco.builder()
                .logradouro("Rua A").numero("1").bairro("Bairro").cidade("Cidade").estado("SP").cep("01000-000").build();
        cliente = Cliente.builder()
                .id(1L).nome("João Silva").email("joao@email.com").login("joao").senha("hash").endereco(endereco).build();
    }

    @Test
    void deveBuscarPorIdComSucesso() {
        when(gateway.buscarPorId(1L)).thenReturn(Optional.of(cliente));

        Usuario resultado = useCase.buscarPorId(1L);

        assertThat(resultado.getId()).isEqualTo(1L);
        assertThat(resultado.getNome()).isEqualTo("João Silva");
    }

    @Test
    void deveLancarUsuarioNaoEncontradoExceptionSeBuscarPorIdInexistente() {
        when(gateway.buscarPorId(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> useCase.buscarPorId(99L))
                .isInstanceOf(UsuarioNaoEncontradoException.class)
                .hasMessageContaining("99");
    }

    @Test
    void deveBuscarPorNomeRetornandoLista() {
        var outro = Cliente.builder()
                .id(2L).nome("João Pedro").email("joaopedro@email.com").login("joaop").senha("hash")
                .endereco(Endereco.builder()
                        .logradouro("Rua B").numero("2").bairro("Bairro").cidade("Cidade").estado("SP").cep("01000-000").build())
                .build();

        when(gateway.buscarPorNome("João")).thenReturn(List.of(cliente, outro));

        List<Usuario> resultado = useCase.buscarPorNome("João");

        assertThat(resultado).hasSize(2);
        assertThat(resultado).extracting(Usuario::getNome)
                .containsExactlyInAnyOrder("João Silva", "João Pedro");
    }

    @Test
    void deveBuscarPorNomeRetornandoListaVaziaSeNaoEncontrado() {
        when(gateway.buscarPorNome("Inexistente")).thenReturn(List.of());

        List<Usuario> resultado = useCase.buscarPorNome("Inexistente");

        assertThat(resultado).isEmpty();
    }
}
