package br.com.fiap.erestaurante.config;

import br.com.fiap.erestaurante.application.usecase.AutenticarUsuarioUseCaseImpl;
import br.com.fiap.erestaurante.application.usecase.AtualizarUsuarioUseCaseImpl;
import br.com.fiap.erestaurante.application.usecase.BuscarUsuarioUseCaseImpl;
import br.com.fiap.erestaurante.application.usecase.CadastrarUsuarioUseCaseImpl;
import br.com.fiap.erestaurante.application.usecase.ExcluirUsuarioUseCaseImpl;
import br.com.fiap.erestaurante.application.usecase.TrocarSenhaUseCaseImpl;
import br.com.fiap.erestaurante.domain.port.in.AutenticarUsuarioUseCase;
import br.com.fiap.erestaurante.domain.port.in.AtualizarUsuarioUseCase;
import br.com.fiap.erestaurante.domain.port.in.BuscarUsuarioUseCase;
import br.com.fiap.erestaurante.domain.port.in.CadastrarUsuarioUseCase;
import br.com.fiap.erestaurante.domain.port.in.ExcluirUsuarioUseCase;
import br.com.fiap.erestaurante.domain.port.in.TrocarSenhaUseCase;
import br.com.fiap.erestaurante.domain.port.out.UsuarioRepositoryPort;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

/**
 * Configuração explícita dos beans da aplicação.
 *
 * Os use cases são registrados aqui como @Bean em vez de usar @Service na camada application.
 * Isso mantém a camada application livre de qualquer dependência do Spring,
 * preservando a pureza do domínio e facilitando testes unitários sem contexto Spring.
 */
@Configuration
public class BeanConfiguration {

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder(12);
    }

    @Bean
    public CadastrarUsuarioUseCase cadastrarUsuarioUseCase(UsuarioRepositoryPort repository,
                                                            PasswordEncoder passwordEncoder) {
        return new CadastrarUsuarioUseCaseImpl(repository, passwordEncoder);
    }

    @Bean
    public AtualizarUsuarioUseCase atualizarUsuarioUseCase(UsuarioRepositoryPort repository) {
        return new AtualizarUsuarioUseCaseImpl(repository);
    }

    @Bean
    public ExcluirUsuarioUseCase excluirUsuarioUseCase(UsuarioRepositoryPort repository) {
        return new ExcluirUsuarioUseCaseImpl(repository);
    }

    @Bean
    public BuscarUsuarioUseCase buscarUsuarioUseCase(UsuarioRepositoryPort repository) {
        return new BuscarUsuarioUseCaseImpl(repository);
    }

    @Bean
    public TrocarSenhaUseCase trocarSenhaUseCase(UsuarioRepositoryPort repository,
                                                  PasswordEncoder passwordEncoder) {
        return new TrocarSenhaUseCaseImpl(repository, passwordEncoder);
    }

    @Bean
    public AutenticarUsuarioUseCase autenticarUsuarioUseCase(UsuarioRepositoryPort repository,
                                                              PasswordEncoder passwordEncoder) {
        return new AutenticarUsuarioUseCaseImpl(repository, passwordEncoder);
    }
}
