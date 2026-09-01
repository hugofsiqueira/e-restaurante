package br.com.fiap.erestaurante.domain.port.in;

/**
 * Porta de entrada — troca de senha.
 * SRP: isolada do endpoint de atualização pois tem regras próprias
 * (validação de senha atual e política de nova senha).
 */
public interface TrocarSenhaUseCase {

    record Command(
            String senhaAtual,
            String novaSenha
    ) {}

    void execute(Long id, Command command);
}
