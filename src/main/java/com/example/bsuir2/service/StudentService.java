package com.example.bsuir2.service;

import com.example.bsuir2.model.Student;
import com.example.bsuir2.model.StudentGroup;
import com.example.bsuir2.repository.StudentRepository;
import com.example.bsuir2.repository.StudentGroupRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class StudentService {
    private final StudentRepository studentRepository;
    private final StudentGroupRepository groupRepository;

    public StudentService(StudentRepository studentRepository, StudentGroupRepository groupRepository) {
        this.studentRepository = studentRepository;
        this.groupRepository = groupRepository;
    }

    // Получить всех студентов
    public List<Student> getAllStudents() {
        return studentRepository.findAll();
    }

    // Получить студента по ID
    public Student getStudentById(Long id) {
        return studentRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Студент не найден"));
    }

    // Создать нового студента
    public Student createStudent(Student student) {
        return studentRepository.save(student);
    }

    // Обновить информацию о студенте
    public Student updateStudent(Long id, Student updatedStudent) {
        Student student = getStudentById(id);
        student.setName(updatedStudent.getName());
        student.setEmail(updatedStudent.getEmail());
        return studentRepository.save(student);
    }

    // Удалить студента
    public void deleteStudent(Long id) {
        studentRepository.deleteById(id);
    }

    // Добавить студента в группу
    public Student addStudentToGroup(Long studentId, Long groupId) {
        Student student = getStudentById(studentId);
        StudentGroup group = groupRepository.findById(groupId)
                .orElseThrow(() -> new RuntimeException("Группа не найдена"));

        student.getGroups().add(group);
        group.getStudents().add(student);

        studentRepository.save(student);
        groupRepository.save(group);

        return student;
    }

    // Удалить студента из группы
    public Student removeStudentFromGroup(Long studentId, Long groupId) {
        Student student = getStudentById(studentId);
        StudentGroup group = groupRepository.findById(groupId)
                .orElseThrow(() -> new RuntimeException("Группа не найдена"));

        student.getGroups().remove(group);
        group.getStudents().remove(student);

        studentRepository.save(student);
        groupRepository.save(group);

        return student;
    }
}
