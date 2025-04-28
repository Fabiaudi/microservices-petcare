package com.ms.cadastro.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.ms.cadastro.model.Owner;

public interface OwnerRepository extends JpaRepository<Owner, Long> {
    Optional<Owner> findByCpf(String cpf);
    Optional<Owner> findByEmail(String email);
}
