package br.com.fiap.erestaurante.infra.database.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalTime;

@Embeddable
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class HorarioFuncionamentoEntity {

    @Column(name = "hora_abertura", nullable = false)
    private LocalTime abertura;

    @Column(name = "hora_fechamento", nullable = false)
    private LocalTime fechamento;
}
