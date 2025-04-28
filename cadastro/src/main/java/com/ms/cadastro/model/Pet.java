package com.ms.cadastro.model;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

import com.ms.cadastro.enums.Species;
import com.ms.cadastro.enums.Temperament;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "tb_pets")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Pet {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    private LocalDate birthDate;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Species species;

    @Column(nullable = false)
    private String breed;

    @Column(nullable = false)
    private String color;

    @Column(nullable = false)
    private Double weight;

    @Enumerated(EnumType.STRING)
    private Temperament temperament;

    private String description;

    private String imageUrl;

    private LocalDate lastVaccinationDate;

    @ManyToMany
    @JoinTable(name = "pet_owner",
        joinColumns = @JoinColumn(name = "pet_id"),
        inverseJoinColumns = @JoinColumn(name = "owner_id"))
    private Set<Owner> owners = new HashSet<>();

    // ✅ equals e hashCode com base apenas no ID
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Pet)) return false;
        Pet pet = (Pet) o;
        return id != null && id.equals(pet.getId());
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
