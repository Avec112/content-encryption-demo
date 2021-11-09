package io.avec.ced.data.service;

import io.avec.ced.data.entity.User;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.vaadin.artur.helpers.CrudService;

@RequiredArgsConstructor
@Service
public class UserService extends CrudService<User, Integer> {

    private final UserRepository repository;

    @Override
    protected UserRepository getRepository() {
        return repository;
    }

}
