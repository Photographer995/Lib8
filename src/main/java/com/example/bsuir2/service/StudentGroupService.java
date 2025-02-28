package com.example.bsuir2.service;

import com.example.bsuir2.model.Student;
import com.example.bsuir2.model.StudentGroup;
import com.example.bsuir2.repository.StudentGroupRepository;
import com.example.bsuir2.repository.StudentRepository;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.List;
import java.util.Optional;

@Service
public class StudentGroupService {
    private final StudentGroupRepository groupRepository;
    private final StudentRepository studentRepository;
    private final RestTemplate restTemplate;

    private static final String BSUIR_API_URL = "https://iis.bsuir.by/api/v1/student-groups";

    public StudentGroupService(StudentGroupRepository groupRepository, StudentRepository studentRepository, RestTemplate restTemplate) {
        this.groupRepository = groupRepository;
        this.studentRepository = studentRepository;
        this.restTemplate = restTemplate;
    }

    // Получение списка групп из API БГУИР и БД
    public List<StudentGroup> getAllGroups() {
        // Загружаем группы из API БГУИР
        StudentGroup[] apiGroups = restTemplate.getForObject(BSUIR_API_URL, StudentGroup[].class);
        List<StudentGroup> localGroups = groupRepository.findAll();

        // Объединяем списки (API + БД)
        List<StudentGroup> allGroups = new java.util.ArrayList<>(localGroups);
        if (apiGroups != null) {
            for (StudentGroup apiGroup : apiGroups) {
                // Добавляем группу только если ее нет в БД (чтобы избежать дубликатов)
                if (groupRepository.findById(apiGroup.getId()).isEmpty()) {
                    allGroups.add(apiGroup);
                }
            }
        }
        return allGroups;
    }

    // Получение группы по ID (сначала ищем в БД, если нет — запрашиваем у API)
    public StudentGroup getGroupById(Long id) {
        Optional<StudentGroup> localGroup = groupRepository.findById(id);
        if (localGroup.isPresent()) {
            return localGroup.get();
        }

        String groupUrl = BSUIR_API_URL + "/" + id;
        return restTemplate.getForObject(groupUrl, StudentGroup.class);
    }

    // Запрещаем создание группы вручную (так как они приходят из API)
    public void createGroup(StudentGroup group) {
        throw new UnsupportedOperationException("Создание групп вручную запрещено. Группы загружаются из API БГУИР.");
    }

    // Запрещаем обновление группы
    public void updateGroup(Long id, StudentGroup updatedGroup) {
        throw new UnsupportedOperationException("Обновление групп запрещено. Группы загружаются из API БГУИР.");
    }

    // Запрещаем удаление группы
    public void deleteGroup(Long id) {
        throw new UnsupportedOperationException("Удаление групп запрещено. Группы загружаются из API БГУИР.");
    }

    // Добавление студента в группу
    public StudentGroup addStudentToGroup(Long groupId, Long studentId) {
        StudentGroup group = getGroupById(groupId);
        Student student = studentRepository.findById(studentId)
                .orElseThrow(() -> new RuntimeException("Студент не найден"));

        group.getStudents().add(student);
        student.getGroups().add(group);

        groupRepository.save(group);
        studentRepository.save(student);

        return group;
    }

    // Удаление студента из группы
    public StudentGroup removeStudentFromGroup(Long groupId, Long studentId) {
        StudentGroup group = getGroupById(groupId);
        Student student = studentRepository.findById(studentId)
                .orElseThrow(() -> new RuntimeException("Студент не найден"));

        group.getStudents().remove(student);
        student.getGroups().remove(group);

        groupRepository.save(group);
        studentRepository.save(student);

        return group;
    }
}
