package br.com.fiap.erestaurante.core.usecase.restaurante;

import br.com.fiap.erestaurante.core.domain.Endereco;
import br.com.fiap.erestaurante.core.domain.HorarioFuncionamento;
import br.com.fiap.erestaurante.core.domain.Restaurante;
import br.com.fiap.erestaurante.core.exception.RestauranteNaoEncontradoException;
import br.com.fiap.erestaurante.core.gateway.RestauranteGateway;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class AtualizarRestauranteUseCaseImpl implements AtualizarRestauranteUseCase {

    private final RestauranteGateway gateway;


    @Override
    public Restaurante execute(Long id, Command command) {
        var restaurante = gateway.buscarPorId(id)
                .orElseThrow(() -> new RestauranteNaoEncontradoException(id));

        restaurante.setNome(command.nome());
        restaurante.setTipoCozinha(command.tipoCozinha());

        restaurante.setEndereco(Endereco.builder()
                .logradouro(command.logradouro())
                .numero(command.numero())
                .complemento(command.complemento())
                .bairro(command.bairro())
                .cidade(command.cidade())
                .estado(command.estado())
                .cep(command.cep())
                .build());

        restaurante.setHorarioFuncionamento(HorarioFuncionamento.builder()
                .abertura(command.abertura())
                .fechamento(command.fechamento())
                .build());

        return gateway.salvar(restaurante);
    }
}
