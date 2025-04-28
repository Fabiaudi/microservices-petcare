package com.ms.cadastro.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.ms.cadastro.model.Breed;

public interface BreedRepository extends JpaRepository<Breed, Long> {
    @Query("SELECT b FROM Breed b WHERE " +
            "LOWER(REPLACE(REPLACE(REPLACE(b.name, '-', ''), '(', ''), ')', '')) = " +
            "LOWER(REPLACE(REPLACE(REPLACE(:name, '-', ''), '(', ''), ')', ''))")
    List<Breed> findAllByName(@Param("name") String name);
}
