package br.com.fiap.erestaurante.core.usecase.restaurante;

import br.com.fiap.erestaurante.core.domain.DonoRestaurante;
import br.com.fiap.erestaurante.core.domain.Endereco;
import br.com.fiap.erestaurante.core.domain.HorarioFuncionamento;
import br.com.fiap.erestaurante.core.domain.Restaurante;
import br.com.fiap.erestaurante.core.exception.RestauranteNaoEncontradoException;
import br.com.fiap.erestaurante.core.gateway.RestauranteGateway;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalTime;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class BuscarRestauranteUseCaseImplTest {

    @Mock
    private RestauranteGateway gateway;

    private BuscarRestauranteUseCaseImpl useCase;

    private Restaurante restaurante;

    @BeforeEach
    void setUp() {
        useCase = new BuscarRestauranteUseCaseImpl(gateway);

        restaurante = Restaurante.builder()
                .id(1L).nome("Cantina da Maria").tipoCozinha("Italiana")
                .dono(DonoRestaurante.builder().id(1L).nome("Maria").build())
                .endereco(Endereco.builder().logradouro("Rua A").numero("1")
                        .bairro("Centro").cidade("SP").estado("SP").cep("01000-000").build())
                .horarioFuncionamento(HorarioFuncionamento.builder()
                        .abertura(LocalTime.of(11, 0)).fechamento(LocalTime.of(23, 0)).build())
                .build();
    }

    @Test
    void deveBuscarPorIdComSucesso() {
        when(gateway.buscarPorId(1L)).thenReturn(Optional.of(restaurante));

        var resultado = useCase.buscarPorId(1L);

        assertThat(resultado.getId()).isEqualTo(1L);
        assertThat(resultado.getNome()).isEqualTo("Cantina da Maria");
    }

    @Test
    void deveLancarExcecaoSePorIdNaoEncontrado() {
        when(gateway.buscarPorId(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> useCase.buscarPorId(99L))
                .isInstanceOf(RestauranteNaoEncontradoException.class)
                .hasMessageContaining("99");
    }

    @Test
    void deveBuscarPorNomeRetornandoLista() {
        var outro = Restaurante.builder()
                .id(2L).nome("Cantina do João").tipoCozinha("Brasileira")
                .dono(DonoRestaurante.builder().id(2L).nome("João").build())
                .build();

        when(gateway.buscarPorNome("Cantina")).thenReturn(List.of(restaurante, outro));

        var resultado = useCase.buscarPorNome("Cantina");

        assertThat(resultado).hasSize(2);
        assertThat(resultado).extracting(Restaurante::getNome)
                .containsExactlyInAnyOrder("Cantina da Maria", "Cantina do João");
    }

    @Test
    void deveBuscarPorNomeRetornandoListaVazia() {
        when(gateway.buscarPorNome("Inexistente")).thenReturn(List.of());

        var resultado = useCase.buscarPorNome("Inexistente");

        assertThat(resultado).isEmpty();
    }
}
