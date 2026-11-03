package br.com.fiap.erestaurante.core.controller;

import br.com.fiap.erestaurante.core.domain.Usuario;
import br.com.fiap.erestaurante.core.usecase.usuario.AtualizarUsuarioUseCase;
import br.com.fiap.erestaurante.core.usecase.usuario.AutenticarUsuarioUseCase;
import br.com.fiap.erestaurante.core.usecase.usuario.BuscarUsuarioUseCase;
import br.com.fiap.erestaurante.core.usecase.usuario.CadastrarUsuarioUseCase;
import br.com.fiap.erestaurante.core.usecase.usuario.ExcluirUsuarioUseCase;
import br.com.fiap.erestaurante.core.usecase.usuario.TrocarSenhaUseCase;
import lombok.RequiredArgsConstructor;

import java.util.List;

/**
 * Controlador de domínio puro — sem anotações Spring.
 * Orquestra os casos de uso do agregado Usuario.
 * Padrão: Clean Arch Intermediário (poc-cleanarch-hexagonal-mvc).
 */
@RequiredArgsConstructor
public class UsuarioController {

    private final CadastrarUsuarioUseCase cadastrarUseCase;
    private final AtualizarUsuarioUseCase atualizarUseCase;
    private final ExcluirUsuarioUseCase excluirUseCase;
    private final BuscarUsuarioUseCase buscarUseCase;
    private final TrocarSenhaUseCase trocarSenhaUseCase;
    private final AutenticarUsuarioUseCase autenticarUseCase;

    public Usuario cadastrar(CadastrarUsuarioUseCase.Command command) {
        return cadastrarUseCase.execute(command);
    }

    public Usuario atualizar(Long id, AtualizarUsuarioUseCase.Command command) {
        return atualizarUseCase.execute(id, command);
    }

    public void excluir(Long id) {
        excluirUseCase.execute(id);
    }

    public Usuario buscarPorId(Long id) {
        return buscarUseCase.buscarPorId(id);
    }

    public List<Usuario> buscarPorNome(String nome) {
        return buscarUseCase.buscarPorNome(nome);
    }

    public void trocarSenha(Long id, TrocarSenhaUseCase.Command command) {
        trocarSenhaUseCase.execute(id, command);
    }

    public Usuario autenticar(AutenticarUsuarioUseCase.Command command) {
        return autenticarUseCase.execute(command);
    }
}
