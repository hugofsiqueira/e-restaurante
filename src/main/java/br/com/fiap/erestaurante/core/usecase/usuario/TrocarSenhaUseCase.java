package br.com.fiap.erestaurante.core.usecase.usuario;

public interface TrocarSenhaUseCase {

    record Command(
            String senhaAtual,
            String novaSenha
    ) {}

    void execute(Long id, Command command);
}
