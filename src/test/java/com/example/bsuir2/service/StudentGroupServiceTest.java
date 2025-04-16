package com.example.bsuir2.service;

import com.example.bsuir2.model.StudentGroup;
import com.example.bsuir2.repository.StudentGroupRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.client.RestTemplate;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class StudentGroupServiceTest {

    @Mock
    private StudentGroupRepository groupRepository;

    @Mock
    private RestTemplate restTemplate;

    @Mock
    private CacheService cacheService;

    @InjectMocks
    private StudentGroupService groupService;

    private StudentGroup group1;
    private StudentGroup group2;

    @BeforeEach
    public void setup() {
        group1 = mock(StudentGroup.class);
        group2 = mock(StudentGroup.class);

        when(group1.getId()).thenReturn(1L);
        when(group1.getName()).thenReturn("Group A");

        when(group2.getId()).thenReturn(2L);
        when(group2.getName()).thenReturn("Group B");
    }

    @Test
    public void testGetAllGroups() {
        when(groupRepository.findAll()).thenReturn(Arrays.asList(group1, group2));

        List<StudentGroup> groups = groupService.getAllGroups();

        assertEquals(2, groups.size());
    }

    @Test
    public void testGetGroupByIdFromCache() {
        when(cacheService.getFromCache(1L)).thenReturn(group1);

        StudentGroup cached = groupService.getGroupById(1L);

        assertEquals(group1, cached);
        verify(groupRepository, never()).findById(1L);
    }

    @Test
    public void testGetGroupByIdFromRepository() {
        when(cacheService.getFromCache(2L)).thenReturn(null);
        when(groupRepository.findById(2L)).thenReturn(Optional.of(group2));

        StudentGroup found = groupService.getGroupById(2L);

        assertEquals(group2, found);
        verify(cacheService, times(1)).putInCache(2L, group2);
    }

    @Test
    public void testDeleteGroup() {
        doNothing().when(groupRepository).deleteById(1L);
        doNothing().when(cacheService).removeFromCache(1L);

        groupService.deleteGroup(1L);

        verify(groupRepository, times(1)).deleteById(1L);
        verify(cacheService, times(1)).removeFromCache(1L);
    }
}
