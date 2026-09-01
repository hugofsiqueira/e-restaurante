package br.com.fiap.erestaurante.infrastructure.persistence.entity;

import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import lombok.NoArgsConstructor;

@Entity
@DiscriminatorValue("DONO_RESTAURANTE")
@NoArgsConstructor
public class DonoRestauranteEntity extends UsuarioEntity {
}
