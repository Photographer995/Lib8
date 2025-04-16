package com.example.bsuir2.service;

import com.example.bsuir2.model.Student;
import com.example.bsuir2.model.StudentGroup;
import com.example.bsuir2.repository.StudentRepository;
import com.example.bsuir2.repository.StudentGroupRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.*;

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
        student1 = mock(Student.class);
        student2 = mock(Student.class);

        when(student1.getId()).thenReturn(1L);
        when(student1.getName()).thenReturn("Svyat Doe");
        when(student1.getEmail()).thenReturn("sssvu@example.com");

        when(student2.getId()).thenReturn(2L);
        when(student2.getName()).thenReturn("Blu Smith");
        when(student2.getEmail()).thenReturn("sky@example.com");
    }

    @Test
    public void testCreateStudent() {
        when(studentRepository.save(any(Student.class))).thenAnswer(invocation -> {
            Student s = invocation.getArgument(0);
            when(s.getId()).thenReturn(1L);
            return s;
        });

        Student created = studentService.createStudent(student1);
        assertNotNull(created.getId());
        verify(cacheService).putInCache(created.getId(), created);
    }

    @Test
    public void testBulkCreateStudents() {
        when(studentRepository.save(any(Student.class))).thenAnswer(invocation -> {
            Student s = invocation.getArgument(0);
            when(s.getId()).thenReturn((long) (1 + Math.random() * 100));
            return s;
        });

        List<Student> students = Arrays.asList(student1, student2);
        List<Student> savedStudents = studentService.bulkCreateStudents(students);

        assertEquals(2, savedStudents.size());
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
        verify(cacheService).putInCache(2L, student2);
    }

    @Test
    public void testUpdateStudent() {
        when(cacheService.getFromCache(1L)).thenReturn(student1);
        when(studentRepository.save(any(Student.class))).thenAnswer(invocation -> invocation.getArgument(0));

        Student updatedInfo = mock(Student.class);
        when(updatedInfo.getName()).thenReturn("Updated Name");
        when(updatedInfo.getEmail()).thenReturn("updated@example.com");

        Student updatedStudent = studentService.updateStudent(1L, updatedInfo);

        verify(student1).setName("Updated Name");
        verify(student1).setEmail("updated@example.com");
        verify(cacheService).putInCache(1L, student1);
    }

    @Test
    public void testDeleteStudent() {
        doNothing().when(studentRepository).deleteById(1L);
        doNothing().when(cacheService).removeFromCache(1L);

        studentService.deleteStudent(1L);

        verify(studentRepository).deleteById(1L);
        verify(cacheService).removeFromCache(1L);
    }

    @Test
    public void testAddStudentToGroup() {
        StudentGroup group = mock(StudentGroup.class);
        Set<StudentGroup> groups = new HashSet<>();
        when(student1.getGroups()).thenReturn(groups);
        when(cacheService.getFromCache(1L)).thenReturn(student1);
        when(groupRepository.findById(10L)).thenReturn(Optional.of(group));
        when(studentRepository.save(any(Student.class))).thenReturn(student1);
        when(groupRepository.save(any(StudentGroup.class))).thenReturn(group);

        Student result = studentService.addStudentToGroup(1L, 10L);

        assertTrue(result.getGroups().contains(group));
        verify(cacheService).putInCache(1L, student1);
        verify(cacheService).putInCache(10L, group);
    }

    @Test
    public void testRemoveStudentFromGroup() {
        StudentGroup group = mock(StudentGroup.class);
        Set<StudentGroup> groups = new HashSet<>();
        groups.add(group);

        when(student1.getGroups()).thenReturn(groups);
        when(cacheService.getFromCache(1L)).thenReturn(student1);
        when(groupRepository.findById(10L)).thenReturn(Optional.of(group));
        when(studentRepository.save(any(Student.class))).thenReturn(student1);
        when(groupRepository.save(any(StudentGroup.class))).thenReturn(group);

        Student result = studentService.removeStudentFromGroup(1L, 10L);

        assertFalse(result.getGroups().contains(group));
        verify(cacheService).putInCache(1L, student1);
        verify(cacheService).putInCache(10L, group);
    }
}
