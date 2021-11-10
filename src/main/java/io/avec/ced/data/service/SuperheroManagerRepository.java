package io.avec.ced.data.service;

import io.avec.ced.data.entity.Manager;
import io.avec.ced.data.entity.SuperheroManager;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface SuperheroManagerRepository extends JpaRepository<SuperheroManager, Integer> {
    Optional<SuperheroManager> findBySuperheroNicknameEqualsIgnoreCaseAndManagers(String nickname, Manager managers);


}