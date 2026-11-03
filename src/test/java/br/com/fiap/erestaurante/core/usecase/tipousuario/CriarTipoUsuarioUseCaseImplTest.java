package br.com.fiap.erestaurante.core.usecase.tipousuario;

import br.com.fiap.erestaurante.core.domain.TipoUsuario;
import br.com.fiap.erestaurante.core.exception.TipoUsuarioJaCadastradoException;
import br.com.fiap.erestaurante.core.gateway.TipoUsuarioGateway;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CriarTipoUsuarioUseCaseImplTest {

    @Mock
    private TipoUsuarioGateway gateway;

    private CriarTipoUsuarioUseCaseImpl useCase;

    @BeforeEach
    void setUp() {
        useCase = new CriarTipoUsuarioUseCaseImpl(gateway);
    }

    @Test
    void deveCriarTipoUsuarioComSucesso() {
        var command = new CriarTipoUsuarioUseCase.Command("GERENTE");
        var salvo = TipoUsuario.builder().id(1L).nome("GERENTE").build();

        when(gateway.existePorNome("GERENTE")).thenReturn(false);
        when(gateway.salvar(any())).thenReturn(salvo);

        TipoUsuario resultado = useCase.execute(command);

        assertThat(resultado.getId()).isEqualTo(1L);
        assertThat(resultado.getNome()).isEqualTo("GERENTE");
        verify(gateway).salvar(any());
    }

    @Test
    void deveLancarExcecaoSeTipoJaExiste() {
        var command = new CriarTipoUsuarioUseCase.Command("CLIENTE");

        when(gateway.existePorNome("CLIENTE")).thenReturn(true);

        assertThatThrownBy(() -> useCase.execute(command))
                .isInstanceOf(TipoUsuarioJaCadastradoException.class)
                .hasMessageContaining("CLIENTE");

        verify(gateway, never()).salvar(any());
    }
}
