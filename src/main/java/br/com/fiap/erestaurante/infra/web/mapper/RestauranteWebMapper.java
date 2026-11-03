package br.com.fiap.erestaurante.infra.web.mapper;

import br.com.fiap.erestaurante.core.domain.Restaurante;
import br.com.fiap.erestaurante.core.usecase.restaurante.AtualizarRestauranteUseCase;
import br.com.fiap.erestaurante.core.usecase.restaurante.CriarRestauranteUseCase;
import br.com.fiap.erestaurante.infra.web.json.RestauranteRequest;
import br.com.fiap.erestaurante.infra.web.json.RestauranteResponse;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

@Mapper(componentModel = "spring")
public interface RestauranteWebMapper {

    @Mapping(source = "endereco.logradouro", target = "logradouro")
    @Mapping(source = "endereco.numero", target = "numero")
    @Mapping(source = "endereco.complemento", target = "complemento")
    @Mapping(source = "endereco.bairro", target = "bairro")
    @Mapping(source = "endereco.cidade", target = "cidade")
    @Mapping(source = "endereco.estado", target = "estado")
    @Mapping(source = "endereco.cep", target = "cep")
    @Mapping(source = "horarioFuncionamento.abertura", target = "abertura")
    @Mapping(source = "horarioFuncionamento.fechamento", target = "fechamento")
    CriarRestauranteUseCase.Command toCreateCommand(RestauranteRequest request);

    @Mapping(source = "endereco.logradouro", target = "logradouro")
    @Mapping(source = "endereco.numero", target = "numero")
    @Mapping(source = "endereco.complemento", target = "complemento")
    @Mapping(source = "endereco.bairro", target = "bairro")
    @Mapping(source = "endereco.cidade", target = "cidade")
    @Mapping(source = "endereco.estado", target = "estado")
    @Mapping(source = "endereco.cep", target = "cep")
    @Mapping(source = "horarioFuncionamento.abertura", target = "abertura")
    @Mapping(source = "horarioFuncionamento.fechamento", target = "fechamento")
    AtualizarRestauranteUseCase.Command toUpdateCommand(RestauranteRequest request);

    @Mapping(source = "dono.id", target = "donoId")
    @Mapping(source = "dono.nome", target = "donoNome")
    RestauranteResponse toResponse(Restaurante restaurante);

    List<RestauranteResponse> toResponseList(List<Restaurante> restaurantes);
}
