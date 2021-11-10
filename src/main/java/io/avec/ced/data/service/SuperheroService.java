package io.avec.ced.data.service;

import io.avec.ced.data.entity.Superhero;
import lombok.RequiredArgsConstructor;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Service;
import org.vaadin.artur.helpers.CrudService;

@RequiredArgsConstructor
@Service
public class SuperheroService extends CrudService<Superhero, Integer> {

    private final SuperheroRepository repository;

    @Override
    protected JpaRepository<Superhero, Integer> getRepository() {
        return repository;
    }
}
