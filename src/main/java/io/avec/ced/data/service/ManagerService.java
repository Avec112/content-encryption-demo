package io.avec.ced.data.service;

import io.avec.ced.data.entity.Manager;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.vaadin.artur.helpers.CrudService;

@RequiredArgsConstructor
@Service
public class ManagerService extends CrudService<Manager, Integer> {

    private final ManagerRepository repository;

    @Override
    protected ManagerRepository getRepository() {
        return repository;
    }

}
