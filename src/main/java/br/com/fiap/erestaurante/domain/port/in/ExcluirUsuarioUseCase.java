package br.com.fiap.erestaurante.domain.port.in;

/**
 * Porta de entrada — exclusão de usuário por id.
 */
public interface ExcluirUsuarioUseCase {

    void execute(Long id);
}
