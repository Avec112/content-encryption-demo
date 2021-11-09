package io.avec.ced.data.service;

import io.avec.ced.data.entity.SamplePerson;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.vaadin.artur.helpers.CrudService;

@RequiredArgsConstructor
@Service
public class SamplePersonService extends CrudService<SamplePerson, Integer> {

    private final SamplePersonRepository repository;

    @Override
    protected SamplePersonRepository getRepository() {
        return repository;
    }

}
