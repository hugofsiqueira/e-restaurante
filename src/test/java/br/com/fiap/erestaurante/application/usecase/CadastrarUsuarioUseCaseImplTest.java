package br.com.fiap.erestaurante.application.usecase;

import br.com.fiap.erestaurante.domain.exception.EmailJaCadastradoException;
import br.com.fiap.erestaurante.domain.model.Cliente;
import br.com.fiap.erestaurante.domain.model.DonoRestaurante;
import br.com.fiap.erestaurante.domain.model.Endereco;
import br.com.fiap.erestaurante.domain.model.TipoUsuario;
import br.com.fiap.erestaurante.domain.model.Usuario;
import br.com.fiap.erestaurante.domain.port.in.CadastrarUsuarioUseCase;
import br.com.fiap.erestaurante.domain.port.out.UsuarioRepositoryPort;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CadastrarUsuarioUseCaseImplTest {

    @Mock
    private UsuarioRepositoryPort repository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private CadastrarUsuarioUseCaseImpl useCase;

    private Endereco endereco;

    @BeforeEach
    void setUp() {
        endereco = new Endereco("Rua das Flores", "123", null, "Centro", "São Paulo", "SP", "01310-100");
    }

    @Test
    void deveCadastrarClienteComSucesso() {
        var command = new CadastrarUsuarioUseCase.Command(
                "João Silva", "joao@email.com", "joaosilva", "senha123",
                TipoUsuario.CLIENTE,
                "Rua das Flores", "123", null, "Centro", "São Paulo", "SP", "01310-100"
        );
        var clienteSalvo = new Cliente(1L, "João Silva", "joao@email.com", "joaosilva",
                "hashSenha", endereco, null);

        when(repository.existePorEmail("joao@email.com")).thenReturn(false);
        when(passwordEncoder.encode("senha123")).thenReturn("hashSenha");
        when(repository.salvar(any(Usuario.class))).thenReturn(clienteSalvo);

        Usuario resultado = useCase.execute(command);

        assertThat(resultado).isInstanceOf(Cliente.class);
        assertThat(resultado.getNome()).isEqualTo("João Silva");
        assertThat(resultado.getEmail()).isEqualTo("joao@email.com");
        assertThat(resultado.getTipo()).isEqualTo(TipoUsuario.CLIENTE);
        verify(passwordEncoder).encode("senha123");
        verify(repository).salvar(any(Usuario.class));
    }

    @Test
    void deveCadastrarDonoRestauranteComSucesso() {
        var command = new CadastrarUsuarioUseCase.Command(
                "Maria Souza", "maria@email.com", "mariarestaurante", "senha456",
                TipoUsuario.DONO_RESTAURANTE,
                "Av. Paulista", "1000", "Apto 5", "Bela Vista", "São Paulo", "SP", "01311-000"
        );
        var donoSalvo = new DonoRestaurante(2L, "Maria Souza", "maria@email.com",
                "mariarestaurante", "hashSenha456", endereco, null);

        when(repository.existePorEmail("maria@email.com")).thenReturn(false);
        when(passwordEncoder.encode("senha456")).thenReturn("hashSenha456");
        when(repository.salvar(any(Usuario.class))).thenReturn(donoSalvo);

        Usuario resultado = useCase.execute(command);

        assertThat(resultado).isInstanceOf(DonoRestaurante.class);
        assertThat(resultado.getTipo()).isEqualTo(TipoUsuario.DONO_RESTAURANTE);
    }

    @Test
    void deveLancarEmailJaCadastradoExceptionSeEmailJaExiste() {
        var command = new CadastrarUsuarioUseCase.Command(
                "Duplicado", "existente@email.com", "login", "senha123",
                TipoUsuario.CLIENTE,
                "Rua A", "1", null, "Bairro", "Cidade", "SP", "01310-100"
        );

        when(repository.existePorEmail("existente@email.com")).thenReturn(true);

        assertThatThrownBy(() -> useCase.execute(command))
                .isInstanceOf(EmailJaCadastradoException.class)
                .hasMessageContaining("existente@email.com");

        verify(repository, never()).salvar(any());
        verify(passwordEncoder, never()).encode(anyString());
    }

    @Test
    void deveCriptografarSenhaAntesDesSalvar() {
        var command = new CadastrarUsuarioUseCase.Command(
                "Teste", "teste@email.com", "teste", "senhaPlana",
                TipoUsuario.CLIENTE,
                "Rua B", "2", null, "Bairro", "Cidade", "SP", "01310-100"
        );
        var salvo = new Cliente(1L, "Teste", "teste@email.com", "teste", "BCrypt$hash", endereco, null);

        when(repository.existePorEmail(anyString())).thenReturn(false);
        when(passwordEncoder.encode("senhaPlana")).thenReturn("BCrypt$hash");
        when(repository.salvar(any())).thenReturn(salvo);

        useCase.execute(command);

        verify(passwordEncoder).encode("senhaPlana");
    }
}
