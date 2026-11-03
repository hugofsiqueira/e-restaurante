package br.com.fiap.erestaurante.core.controller;

import br.com.fiap.erestaurante.core.domain.DonoRestaurante;
import br.com.fiap.erestaurante.core.domain.Restaurante;
import br.com.fiap.erestaurante.core.usecase.restaurante.AtualizarRestauranteUseCase;
import br.com.fiap.erestaurante.core.usecase.restaurante.BuscarRestauranteUseCase;
import br.com.fiap.erestaurante.core.usecase.restaurante.CriarRestauranteUseCase;
import br.com.fiap.erestaurante.core.usecase.restaurante.ExcluirRestauranteUseCase;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class RestauranteControllerTest {

    @Mock private CriarRestauranteUseCase criarUseCase;
    @Mock private BuscarRestauranteUseCase buscarUseCase;
    @Mock private AtualizarRestauranteUseCase atualizarUseCase;
    @Mock private ExcluirRestauranteUseCase excluirUseCase;

    private RestauranteController controller;
    private Restaurante restaurante;

    @BeforeEach
    void setUp() {
        controller = new RestauranteController(criarUseCase, buscarUseCase, atualizarUseCase, excluirUseCase);
        restaurante = Restaurante.builder()
                .id(1L).nome("Cantina da Maria").tipoCozinha("Italiana")
                .dono(DonoRestaurante.builder().id(1L).nome("Maria").build())
                .build();
    }

    @Test
    void deveDelegarCriar() {
        var command = new CriarRestauranteUseCase.Command(
                "Cantina da Maria", 1L, "Italiana",
                "Rua A", "1", null, "Bairro", "Cidade", "SP", "00000-000",
                LocalTime.of(10, 0), LocalTime.of(22, 0)
        );
        when(criarUseCase.execute(command)).thenReturn(restaurante);

        var resultado = controller.criar(command);

        assertThat(resultado.getNome()).isEqualTo("Cantina da Maria");
        verify(criarUseCase).execute(command);
    }

    @Test
    void deveDelegarBuscarPorId() {
        when(buscarUseCase.buscarPorId(1L)).thenReturn(restaurante);

        var resultado = controller.buscarPorId(1L);

        assertThat(resultado.getId()).isEqualTo(1L);
        verify(buscarUseCase).buscarPorId(1L);
    }

    @Test
    void deveDelegarBuscarPorNome() {
        when(buscarUseCase.buscarPorNome("Cantina")).thenReturn(List.of(restaurante));

        var resultado = controller.buscarPorNome("Cantina");

        assertThat(resultado).hasSize(1);
        verify(buscarUseCase).buscarPorNome("Cantina");
    }

    @Test
    void deveDelegarAtualizar() {
        var command = new AtualizarRestauranteUseCase.Command(
                "Cantina da Maria", "Italiana",
                "Rua A", "1", null, "Bairro", "Cidade", "SP", "00000-000",
                LocalTime.of(10, 0), LocalTime.of(22, 0)
        );
        when(atualizarUseCase.execute(1L, command)).thenReturn(restaurante);

        var resultado = controller.atualizar(1L, command);

        assertThat(resultado).isEqualTo(restaurante);
        verify(atualizarUseCase).execute(1L, command);
    }

    @Test
    void deveDelegarExcluir() {
        controller.excluir(1L);

        verify(excluirUseCase).execute(1L);
    }
}
