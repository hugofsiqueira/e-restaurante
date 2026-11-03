package br.com.fiap.erestaurante.core.usecase.restaurante;

import br.com.fiap.erestaurante.core.domain.Cliente;
import br.com.fiap.erestaurante.core.domain.DonoRestaurante;
import br.com.fiap.erestaurante.core.domain.Endereco;
import br.com.fiap.erestaurante.core.domain.HorarioFuncionamento;
import br.com.fiap.erestaurante.core.domain.Restaurante;
import br.com.fiap.erestaurante.core.domain.TipoUsuario;
import br.com.fiap.erestaurante.core.exception.DonoRestauranteInvalidoException;
import br.com.fiap.erestaurante.core.exception.UsuarioNaoEncontradoException;
import br.com.fiap.erestaurante.core.gateway.RestauranteGateway;
import br.com.fiap.erestaurante.core.gateway.UsuarioGateway;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalTime;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CriarRestauranteUseCaseImplTest {

    @Mock
    private RestauranteGateway gateway;

    @Mock
    private UsuarioGateway usuarioGateway;

    private CriarRestauranteUseCaseImpl useCase;

    private DonoRestaurante dono;
    private CriarRestauranteUseCase.Command command;

    @BeforeEach
    void setUp() {
        useCase = new CriarRestauranteUseCaseImpl(gateway, usuarioGateway);

        dono = DonoRestaurante.builder()
                .id(1L).nome("Maria Souza").email("maria@email.com").login("maria")
                .tipoUsuario(TipoUsuario.builder().id(2L).nome("DONO_RESTAURANTE").build())
                .build();

        command = new CriarRestauranteUseCase.Command(
                "Cantina da Maria", 1L, "Italiana",
                "Rua das Flores", "10", null, "Centro", "São Paulo", "SP", "01310-100",
                LocalTime.of(11, 0), LocalTime.of(23, 0)
        );
    }

    @Test
    void deveCriarRestauranteComSucesso() {
        var restauranteSalvo = Restaurante.builder()
                .id(1L).nome("Cantina da Maria").tipoCozinha("Italiana").dono(dono)
                .endereco(Endereco.builder().logradouro("Rua das Flores").numero("10")
                        .bairro("Centro").cidade("São Paulo").estado("SP").cep("01310-100").build())
                .horarioFuncionamento(HorarioFuncionamento.builder()
                        .abertura(LocalTime.of(11, 0)).fechamento(LocalTime.of(23, 0)).build())
                .build();

        when(usuarioGateway.buscarPorId(1L)).thenReturn(Optional.of(dono));
        when(gateway.salvar(any())).thenReturn(restauranteSalvo);

        var resultado = useCase.execute(command);

        assertThat(resultado.getId()).isEqualTo(1L);
        assertThat(resultado.getNome()).isEqualTo("Cantina da Maria");
        assertThat(resultado.getTipoCozinha()).isEqualTo("Italiana");
        assertThat(resultado.getDono().getId()).isEqualTo(1L);
        verify(gateway).salvar(any());
    }

    @Test
    void deveLancarExcecaoSeDonoNaoEncontrado() {
        when(usuarioGateway.buscarPorId(99L)).thenReturn(Optional.empty());

        var commandComDonoInexistente = new CriarRestauranteUseCase.Command(
                "Restaurante X", 99L, "Japonesa",
                "Rua A", "1", null, "Bairro", "Cidade", "SP", "00000-000",
                LocalTime.of(10, 0), LocalTime.of(22, 0)
        );

        assertThatThrownBy(() -> useCase.execute(commandComDonoInexistente))
                .isInstanceOf(UsuarioNaoEncontradoException.class);

        verify(gateway, never()).salvar(any());
    }

    @Test
    void deveLancarExcecaoSeUsuarioNaoEhDonoRestaurante() {
        var cliente = Cliente.builder()
                .id(2L).nome("João").email("joao@email.com").login("joao")
                .tipoUsuario(TipoUsuario.builder().id(1L).nome("CLIENTE").build())
                .build();

        when(usuarioGateway.buscarPorId(2L)).thenReturn(Optional.of(cliente));

        var commandComCliente = new CriarRestauranteUseCase.Command(
                "Restaurante X", 2L, "Japonesa",
                "Rua A", "1", null, "Bairro", "Cidade", "SP", "00000-000",
                LocalTime.of(10, 0), LocalTime.of(22, 0)
        );

        assertThatThrownBy(() -> useCase.execute(commandComCliente))
                .isInstanceOf(DonoRestauranteInvalidoException.class)
                .hasMessageContaining("2");

        verify(gateway, never()).salvar(any());
    }
}
