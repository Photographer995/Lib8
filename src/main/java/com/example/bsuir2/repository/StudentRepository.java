package com.example.bsuir2.repository;

import com.example.bsuir2.model.Student;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.util.List;

public interface StudentRepository extends JpaRepository<Student, Long> {

    @Query("SELECT s FROM Student s JOIN s.groups g " +
            "WHERE (:groupName IS NULL OR g.name = :groupName) " +
            "AND (:namePart IS NULL OR s.name LIKE %:namePart%) " +
            "AND (:emailDomain IS NULL OR s.email LIKE %:emailDomain%)")
    List<Student> findStudentsByFilters(
            @Param("groupName") String groupName,
            @Param("namePart") String namePart,
            @Param("emailDomain") String emailDomain);
}
