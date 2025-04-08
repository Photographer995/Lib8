package com.example.bsuir2.service;

import com.example.bsuir2.model.StudentGroup;
import com.example.bsuir2.repository.StudentGroupRepository;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.List;
import java.util.Optional;
@Service
public class StudentGroupService {

    private final StudentGroupRepository groupRepository;
    private final RestTemplate restTemplate;
    private final CacheService cacheService;

    private static final String BSUIR_API_URL = "***";

    public StudentGroupService(StudentGroupRepository groupRepository, RestTemplate restTemplate, CacheService cacheService) {
        this.groupRepository = groupRepository;
        this.restTemplate = restTemplate;
        this.cacheService = cacheService;
    }

    public List<StudentGroup> getAllGroups() {
        return groupRepository.findAll();
    }

    public StudentGroup getGroupById(Long id) {
       final StudentGroup cached = (StudentGroup) cacheService.getFromCache(id);
        if (cached != null) return cached;

        final Optional<StudentGroup> groupFromDb = groupRepository.findById(id);
        if (groupFromDb.isPresent()) {
            cacheService.putInCache(id, groupFromDb.get());
            return groupFromDb.get();
        }

        final String groupUrl = BSUIR_API_URL + "/" + id;
        final StudentGroup groupFromApi = restTemplate.getForObject(groupUrl, StudentGroup.class);
        if (groupFromApi != null) {
            cacheService.putInCache(id, groupFromApi);
            return groupFromApi;
        }

        throw new RuntimeException("Группа не найдена");
    }

    public void createGroup(StudentGroup group) {
        throw new UnsupportedOperationException("Создание групп вручную запрещено.");
    }

    public void updateGroup(Long id, StudentGroup updatedGroup) {
        throw new UnsupportedOperationException("Обновление групп запрещено.");
    }

    public void deleteGroup(Long id) {
        groupRepository.deleteById(id);
        cacheService.removeFromCache(id);
    }

    public StudentGroup addStudentToGroup(Long groupId, Long studentId) {
        final StudentGroup group = getGroupById(groupId);
        cacheService.putInCache(groupId, group);
        return group;
    }

    public StudentGroup removeStudentFromGroup(Long groupId, Long studentId) {
        final StudentGroup group = getGroupById(groupId);
        cacheService.putInCache(groupId, group);
        return group;
    }
}

