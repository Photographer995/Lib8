package com.example.bsuir2.service;

import com.example.bsuir2.model.Student;
import com.example.bsuir2.model.StudentGroup;
import com.example.bsuir2.repository.StudentRepository;
import com.example.bsuir2.repository.StudentGroupRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.Optional;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class StudentServiceTest {

    @Mock
    private StudentRepository studentRepository;

    @Mock
    private StudentGroupRepository groupRepository;

    @Mock
    private CacheService cacheService;

    @InjectMocks
    private StudentService studentService;

    private Student student1;
    private Student student2;

    @BeforeEach
    public void setup() {
        student1 = new Student();
        student1.setName("John Doe");
        student1.setEmail("john@example.com");

        student2 = new Student();
        student2.setName("Jane Smith");
        student2.setEmail("jane@example.com");
    }

    @Test
    public void testCreateStudent() {
        when(studentRepository.save(student1)).thenAnswer(invocation -> {
            Student s = invocation.getArgument(0);
            s.setId(1L);
            return s;
        });

        Student created = studentService.createStudent(student1);
        assertNotNull(created.getId());
        verify(cacheService, times(1)).putInCache(created.getId(), created);
    }

    @Test
    public void testBulkCreateStudents() {
        when(studentRepository.save(any(Student.class))).thenAnswer(invocation -> {
            Student s = invocation.getArgument(0);
            if (s.getId() == null) {
                s.setId(1L + (long)(Math.random()*100)); // симуляция id
            }
            return s;
        });

        List<Student> students = Arrays.asList(student1, student2);
        List<Student> savedStudents = studentService.bulkCreateStudents(students);

        assertEquals(2, savedStudents.size());
        savedStudents.forEach(s -> assertNotNull(s.getId()));
        verify(cacheService, times(2)).putInCache(anyLong(), any(Student.class));
    }

    @Test
    public void testGetStudentByIdFromCache() {
        when(cacheService.getFromCache(1L)).thenReturn(student1);
        Student cached = studentService.getStudentById(1L);
        assertEquals(student1, cached);
        verify(studentRepository, never()).findById(1L);
    }

    @Test
    public void testGetStudentByIdFromRepository() {
        when(cacheService.getFromCache(2L)).thenReturn(null);
        when(studentRepository.findById(2L)).thenReturn(Optional.of(student2));

        Student s = studentService.getStudentById(2L);
        assertEquals(student2, s);
        verify(cacheService, times(1)).putInCache(2L, student2);
    }

    @Test
    public void testUpdateStudent() {
        student1.setId(1L);
        when(cacheService.getFromCache(1L)).thenReturn(student1);
        when(studentRepository.save(any(Student.class))).thenAnswer(invocation -> invocation.getArgument(0));

        Student updatedInfo = new Student();
        updatedInfo.setName("Updated Name");
        updatedInfo.setEmail("updated@example.com");

        Student updatedStudent = studentService.updateStudent(1L, updatedInfo);
        assertEquals("Updated Name", updatedStudent.getName());
        assertEquals("updated@example.com", updatedStudent.getEmail());
        verify(cacheService, times(1)).putInCache(1L, updatedStudent);
    }

    @Test
    public void testDeleteStudent() {
        student1.setId(1L);
        doNothing().when(studentRepository).deleteById(1L);
        doNothing().when(cacheService).removeFromCache(1L);

        studentService.deleteStudent(1L);
        verify(studentRepository, times(1)).deleteById(1L);
        verify(cacheService, times(1)).removeFromCache(1L);
    }

    @Test
    public void testAddStudentToGroup() {
        student1.setId(1L);
        StudentGroup group = new StudentGroup();
        group.setId(10L);
        group.setName("Group A");

        when(cacheService.getFromCache(1L)).thenReturn(student1);
        when(groupRepository.findById(10L)).thenReturn(Optional.of(group));
        when(studentRepository.save(any(Student.class))).thenAnswer(invocation -> invocation.getArgument(0));
        when(groupRepository.save(any(StudentGroup.class))).thenAnswer(invocation -> invocation.getArgument(0));

        Student s = studentService.addStudentToGroup(1L, 10L);
        assertTrue(s.getGroups().contains(group));
        verify(cacheService, times(1)).putInCache(1L, s);
        verify(cacheService, times(1)).putInCache(10L, group);
    }

    @Test
    public void testRemoveStudentFromGroup() {
        student1.setId(1L);
        StudentGroup group = new StudentGroup();
        group.setId(10L);
        group.setName("Group A");

        // Изначально связываем студента и группу
        student1.getGroups().add(group);
        group.getStudents().add(student1);

        when(cacheService.getFromCache(1L)).thenReturn(student1);
        when(groupRepository.findById(10L)).thenReturn(Optional.of(group));
        when(studentRepository.save(any(Student.class))).thenAnswer(invocation -> invocation.getArgument(0));
        when(groupRepository.save(any(StudentGroup.class))).thenAnswer(invocation -> invocation.getArgument(0));

        Student s = studentService.removeStudentFromGroup(1L, 10L);
        assertFalse(s.getGroups().contains(group));
        verify(cacheService, times(1)).putInCache(1L, s);
        verify(cacheService, times(1)).putInCache(10L, group);
    }
}
