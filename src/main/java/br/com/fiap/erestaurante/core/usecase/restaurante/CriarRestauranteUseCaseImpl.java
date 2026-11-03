package br.com.fiap.erestaurante.core.usecase.restaurante;

import br.com.fiap.erestaurante.core.domain.DonoRestaurante;
import br.com.fiap.erestaurante.core.domain.Endereco;
import br.com.fiap.erestaurante.core.domain.HorarioFuncionamento;
import br.com.fiap.erestaurante.core.domain.Restaurante;
import br.com.fiap.erestaurante.core.exception.DonoRestauranteInvalidoException;
import br.com.fiap.erestaurante.core.exception.UsuarioNaoEncontradoException;
import br.com.fiap.erestaurante.core.gateway.RestauranteGateway;
import br.com.fiap.erestaurante.core.gateway.UsuarioGateway;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class CriarRestauranteUseCaseImpl implements CriarRestauranteUseCase {

    private final RestauranteGateway gateway;
    private final UsuarioGateway usuarioGateway;


    @Override
    public Restaurante execute(Command command) {
        var usuario = usuarioGateway.buscarPorId(command.donoId())
                .orElseThrow(() -> new UsuarioNaoEncontradoException(command.donoId()));

        if (!(usuario instanceof DonoRestaurante dono)) {
            throw new DonoRestauranteInvalidoException(command.donoId());
        }

        var endereco = Endereco.builder()
                .logradouro(command.logradouro())
                .numero(command.numero())
                .complemento(command.complemento())
                .bairro(command.bairro())
                .cidade(command.cidade())
                .estado(command.estado())
                .cep(command.cep())
                .build();

        var horario = HorarioFuncionamento.builder()
                .abertura(command.abertura())
                .fechamento(command.fechamento())
                .build();

        var restaurante = Restaurante.builder()
                .nome(command.nome())
                .tipoCozinha(command.tipoCozinha())
                .endereco(endereco)
                .horarioFuncionamento(horario)
                .dono(dono)
                .build();

        return gateway.salvar(restaurante);
    }
}
