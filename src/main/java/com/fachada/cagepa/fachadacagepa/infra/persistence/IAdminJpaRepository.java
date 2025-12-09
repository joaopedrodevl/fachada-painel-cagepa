package com.fachada.cagepa.fachadacagepa.infra.persistence;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface IAdminJpaRepository extends JpaRepository<Admin, UUID> {
    Optional<Admin> findByUsername(String username);
}
