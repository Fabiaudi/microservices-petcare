package com.ms.cadastro.model;

import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "tb_owners", uniqueConstraints = {
    @UniqueConstraint(columnNames = "cpf")
})
@Getter
@Setter
public class Owner {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false, unique = true)
    private String cpf;

    @Column(nullable = false)
    private String email;

    @Column(nullable = false)
    private String phone;

    @ManyToMany(mappedBy = "owners")
    private Set<Pet> pets = new HashSet<>();

    // ✅ equals e hashCode seguros com base no CPF
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Owner)) return false;
        Owner owner = (Owner) o;
        return cpf != null && cpf.equals(owner.getCpf());
    }

    @Override
    public int hashCode() {
        return Objects.hash(cpf);
    }
}
