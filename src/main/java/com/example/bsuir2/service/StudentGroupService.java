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

    private static final String BSUIR_API_URL = "https://iis.bsuir.by/api/v1/student-groups";

    public StudentGroupService(StudentGroupRepository groupRepository, RestTemplate restTemplate, CacheService cacheService) {
        this.groupRepository = groupRepository;
        this.restTemplate = restTemplate;
        this.cacheService = cacheService;
    }

    public List<StudentGroup> getAllGroups() {
        // Ваш текущий код для получения групп
        return groupRepository.findAll();  // Например, только из БД для этого примера
    }

    public StudentGroup getGroupById(Long id) {
        // Сначала проверяем кэш
        StudentGroup cachedGroup = (StudentGroup) cacheService.getFromCache(id);
        if (cachedGroup != null) {
            return cachedGroup;
        }

        // Если данных нет в кэше, получаем их из БД или API
        Optional<StudentGroup> groupFromDb = groupRepository.findById(id);
        if (groupFromDb.isPresent()) {
            cacheService.putInCache(id, groupFromDb.get());
            return groupFromDb.get();
        }

        // Если группа не найдена в БД, пытаемся получить из API
        String groupUrl = BSUIR_API_URL + "/" + id;
        StudentGroup groupFromApi = restTemplate.getForObject(groupUrl, StudentGroup.class);

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
        StudentGroup group = getGroupById(groupId);
        return group;
    }

    public StudentGroup removeStudentFromGroup(Long groupId, Long studentId) {
        StudentGroup group = getGroupById(groupId);
        return group;
    }
}
