package com.example.bsuir2.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.HashSet;
import java.util.Set;

@Entity
@Getter
@Setter
public class Student {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;
    private String email;

    @ManyToMany(mappedBy = "students")
    @JsonIgnoreProperties("students") // Убираем вложенность студентов в группах
    private Set<StudentGroup> groups = new HashSet<>();
}
