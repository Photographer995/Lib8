package com.example.bsuir2.controller;

import com.example.bsuir2.model.Student;
import com.example.bsuir2.service.StudentService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/students")
public class StudentController {
    private final StudentService studentService;

    public StudentController(StudentService studentService) {
        this.studentService = studentService;
    }

    @GetMapping
    public List<Student> getAllStudents() {
        return studentService.getAllStudents();
    }

    @GetMapping("/{id}")
    public Student getStudentById(@PathVariable Long id) {
        return studentService.getStudentById(id);
    }

    @PostMapping
    public Student createStudent(@RequestBody Student student) {
        return studentService.createStudent(student);
    }

    @PutMapping("/{id}")
    public Student updateStudent(@PathVariable Long id, @RequestBody Student student) {
        return studentService.updateStudent(id, student);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteStudent(@PathVariable Long id) {
        studentService.deleteStudent(id);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/{studentId}/add-to-group/{groupId}")
    public Student addStudentToGroup(@PathVariable Long studentId, @PathVariable Long groupId) {
        return studentService.addStudentToGroup(studentId, groupId);
    }

    @DeleteMapping("/{studentId}/remove-from-group/{groupId}")
    public Student removeStudentFromGroup(@PathVariable Long studentId, @PathVariable Long groupId) {
        return studentService.removeStudentFromGroup(studentId, groupId);
    }

    @GetMapping("/filter")
    public List<Student> filterStudents(
            @RequestParam(required = false) String groupName,
            @RequestParam(required = false) String namePart,
            @RequestParam(required = false) String emailDomain) {
        return studentService.findStudentsByFilters(groupName, namePart, emailDomain);
    }
}
