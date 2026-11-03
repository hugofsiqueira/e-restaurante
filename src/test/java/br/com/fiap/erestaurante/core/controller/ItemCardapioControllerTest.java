package br.com.fiap.erestaurante.core.controller;

import br.com.fiap.erestaurante.core.domain.ItemCardapio;
import br.com.fiap.erestaurante.core.domain.Restaurante;
import br.com.fiap.erestaurante.core.usecase.itemcardapio.AdicionarItemCardapioUseCase;
import br.com.fiap.erestaurante.core.usecase.itemcardapio.AtualizarItemCardapioUseCase;
import br.com.fiap.erestaurante.core.usecase.itemcardapio.BuscarItemCardapioUseCase;
import br.com.fiap.erestaurante.core.usecase.itemcardapio.ExcluirItemCardapioUseCase;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ItemCardapioControllerTest {

    @Mock private AdicionarItemCardapioUseCase adicionarUseCase;
    @Mock private BuscarItemCardapioUseCase buscarUseCase;
    @Mock private AtualizarItemCardapioUseCase atualizarUseCase;
    @Mock private ExcluirItemCardapioUseCase excluirUseCase;

    private ItemCardapioController controller;
    private ItemCardapio item;

    @BeforeEach
    void setUp() {
        controller = new ItemCardapioController(adicionarUseCase, buscarUseCase, atualizarUseCase, excluirUseCase);
        item = ItemCardapio.builder()
                .id(1L).nome("Pizza Margherita").preco(new BigDecimal("35.90")).disponivel(true)
                .restaurante(Restaurante.builder().id(1L).nome("Cantina da Maria").build())
                .build();
    }

    @Test
    void deveDelegarAdicionar() {
        var command = new AdicionarItemCardapioUseCase.Command(
                "Pizza Margherita", "Desc", new BigDecimal("35.90"), true, null, 1L
        );
        when(adicionarUseCase.execute(command)).thenReturn(item);

        var resultado = controller.adicionar(command);

        assertThat(resultado.getNome()).isEqualTo("Pizza Margherita");
        verify(adicionarUseCase).execute(command);
    }

    @Test
    void deveDelegarBuscarPorId() {
        when(buscarUseCase.buscarPorId(1L)).thenReturn(item);

        var resultado = controller.buscarPorId(1L);

        assertThat(resultado.getId()).isEqualTo(1L);
        verify(buscarUseCase).buscarPorId(1L);
    }

    @Test
    void deveDelegarBuscarPorRestaurante() {
        when(buscarUseCase.buscarPorRestaurante(1L)).thenReturn(List.of(item));

        var resultado = controller.buscarPorRestaurante(1L);

        assertThat(resultado).hasSize(1);
        verify(buscarUseCase).buscarPorRestaurante(1L);
    }

    @Test
    void deveDelegarAtualizar() {
        var command = new AtualizarItemCardapioUseCase.Command(
                "Pizza Especial", "Com molho especial", new BigDecimal("42.00"), true, null
        );
        when(atualizarUseCase.execute(1L, command)).thenReturn(item);

        var resultado = controller.atualizar(1L, command);

        assertThat(resultado).isEqualTo(item);
        verify(atualizarUseCase).execute(1L, command);
    }

    @Test
    void deveDelegarExcluir() {
        controller.excluir(1L);

        verify(excluirUseCase).execute(1L);
    }
}
