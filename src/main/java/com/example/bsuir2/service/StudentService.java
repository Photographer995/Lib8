package com.example.bsuir2.service;

import com.example.bsuir2.model.Student;
import com.example.bsuir2.model.StudentGroup;
import com.example.bsuir2.repository.StudentRepository;
import com.example.bsuir2.repository.StudentGroupRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class StudentService {

    private final StudentRepository studentRepository;
    private final StudentGroupRepository groupRepository;
    private final CacheService cacheService;
    private final InvocationCounter invocationCounter;

    public StudentService(StudentRepository studentRepository,
                          StudentGroupRepository groupRepository,
                          CacheService cacheService,
                          InvocationCounter invocationCounter) {
        this.studentRepository = studentRepository;
        this.groupRepository = groupRepository;
        this.cacheService = cacheService;
        this.invocationCounter = invocationCounter;
    }

    public List<Student> getAllStudents() {
        invocationCounter.increment();
        return studentRepository.findAll();
    }

    public Student getStudentById(Long id) {
        invocationCounter.increment();
        final Student cached = (Student) cacheService.getFromCache(id);
        if (cached != null) return cached;

        final Student student = studentRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Студент не найден"));
        cacheService.putInCache(id, student);
        return student;
    }

    public Student createStudent(Student student) {
        invocationCounter.increment();
        final Student saved = studentRepository.save(student);
        cacheService.putInCache(saved.getId(), saved);
        return saved;
    }

    public List<Student> bulkCreateStudents(List<Student> students) {
        invocationCounter.increment();
        final List<Student> savedStudents = students.stream()
                .map(student -> {
                    final Student saved = studentRepository.save(student);
                    cacheService.putInCache(saved.getId(), saved);
                    return saved;
                })
                .collect(Collectors.toList());
        return savedStudents;
    }

    public Student updateStudent(Long id, Student updatedStudent) {
        invocationCounter.increment();
        final Student student = getStudentById(id);
        student.setName(updatedStudent.getName());
        student.setEmail(updatedStudent.getEmail());
        final Student saved = studentRepository.save(student);
        cacheService.putInCache(id, saved);
        return saved;
    }

    public void deleteStudent(Long id) {
        invocationCounter.increment();
        studentRepository.deleteById(id);
        cacheService.removeFromCache(id);
    }

    public Student addStudentToGroup(Long studentId, Long groupId) {
        invocationCounter.increment();
        final Student student = getStudentById(studentId);
        final StudentGroup group = groupRepository.findById(groupId)
                .orElseThrow(() -> new RuntimeException("Группа не найдена"));

        student.getGroups().add(group);
        group.getStudents().add(student);

        studentRepository.save(student);
        groupRepository.save(group);

        cacheService.putInCache(studentId, student);
        cacheService.putInCache(groupId, group);

        return student;
    }

    public Student removeStudentFromGroup(Long studentId, Long groupId) {
        invocationCounter.increment();
        final Student student = getStudentById(studentId);
        final StudentGroup group = groupRepository.findById(groupId)
                .orElseThrow(() -> new RuntimeException("Группа не найдена"));

        student.getGroups().remove(group);
        group.getStudents().remove(student);

        studentRepository.save(student);
        groupRepository.save(group);

        cacheService.putInCache(studentId, student);
        cacheService.putInCache(groupId, group);

        return student;
    }

    public List<Student> findStudentsByFilters(String groupName, String namePart, String emailDomain) {
        invocationCounter.increment();
        return studentRepository.findStudentsByFilters(groupName, namePart, emailDomain);
    }
}
