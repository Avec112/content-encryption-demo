package io.avec.ced.data.service;

import io.avec.ced.data.entity.Manager;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ManagerRepository extends JpaRepository<Manager, Integer> {

    Manager findByUsername(String username);
}