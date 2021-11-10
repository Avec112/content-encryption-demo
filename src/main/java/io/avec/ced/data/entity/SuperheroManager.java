package io.avec.ced.data.entity;

import io.avec.ced.data.AbstractEntity;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "superhero_manager")
@Entity
public class SuperheroManager extends AbstractEntity {

    @ManyToOne
    @JoinColumn(name = "manager_id")
    private Manager manager;

    @Lob
    private String rsaEncryptedPassword; // base64 encoded

    @ManyToOne
    @JoinColumn(name = "superhero_id")
    private Superhero superhero;
}