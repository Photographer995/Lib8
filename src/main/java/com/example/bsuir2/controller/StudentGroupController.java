package com.example.bsuir2.controller;

import com.example.bsuir2.model.StudentGroup;
import com.example.bsuir2.service.StudentGroupService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/groups")
public class StudentGroupController {
    private final StudentGroupService groupService;

    public StudentGroupController(StudentGroupService groupService) {
        this.groupService = groupService;
    }

    @GetMapping
    public List<StudentGroup> getAllGroups() {
        return groupService.getAllGroups();
    }

    @GetMapping("/{id}")
    public StudentGroup getGroupById(@PathVariable Long id) {
        return groupService.getGroupById(id);
    }

    @PostMapping
    public ResponseEntity<String> createGroup(@RequestBody StudentGroup group) {
        return ResponseEntity.badRequest().body("Создание групп вручную запрещено. Группы загружаются из API БГУИР.");
    }

    @PutMapping("/{id}")
    public ResponseEntity<String> updateGroup(@PathVariable Long id, @RequestBody StudentGroup group) {
        return ResponseEntity.badRequest().body("Обновление групп запрещено. Группы загружаются из API БГУИР.");
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteGroup(@PathVariable Long id) {
        return ResponseEntity.badRequest().body("Удаление групп запрещено. Группы загружаются из API БГУИР.");
    }

    @PostMapping("/{groupId}/add-student/{studentId}")
    public StudentGroup addStudentToGroup(@PathVariable Long groupId, @PathVariable Long studentId) {
        return groupService.addStudentToGroup(groupId, studentId);
    }

    @DeleteMapping("/{groupId}/remove-student/{studentId}")
    public StudentGroup removeStudentFromGroup(@PathVariable Long groupId, @PathVariable Long studentId) {
        return groupService.removeStudentFromGroup(groupId, studentId);
    }
}
