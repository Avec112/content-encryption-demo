package io.avec.ced.data.service;

import io.avec.ced.data.entity.Superhero;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SuperheroRepository extends JpaRepository<Superhero, Integer> {

    Superhero findByNickname(String nickname);
}
