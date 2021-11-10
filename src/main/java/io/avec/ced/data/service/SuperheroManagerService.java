package io.avec.ced.data.service;

import io.avec.ced.data.entity.SuperheroManager;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Service;
import org.vaadin.artur.helpers.CrudService;

@RequiredArgsConstructor
@Service
public class SuperheroManagerService extends CrudService<SuperheroManager, Integer> {

    private final SuperheroManagerRepository repository;

    @Override
    protected JpaRepository<SuperheroManager, Integer> getRepository() {
        return repository;
    }

    public Page<SuperheroManager> listByManager(Integer managerId, Pageable pageable) {
        return repository.findByManagerId(managerId, pageable);
    }
}
