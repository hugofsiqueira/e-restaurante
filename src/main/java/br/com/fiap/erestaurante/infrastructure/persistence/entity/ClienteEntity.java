package br.com.fiap.erestaurante.infrastructure.persistence.entity;

import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import lombok.NoArgsConstructor;

@Entity
@DiscriminatorValue("CLIENTE")
@NoArgsConstructor
public class ClienteEntity extends UsuarioEntity {
}
