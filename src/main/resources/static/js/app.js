document.addEventListener("DOMContentLoaded", () => {
    loadStudents();

    document.getElementById("add-student-form").addEventListener("submit", function (e) {
        e.preventDefault();
        const name = document.getElementById("name").value;
        const email = document.getElementById("email").value;
        addStudent({ name, email });
    });
});

function loadStudents() {
    fetch("/students")
        .then(res => res.json())
        .then(data => {
            const list = document.getElementById("student-list");
            list.innerHTML = "";
            data.forEach(student => {
                const div = document.createElement("div");
                div.innerHTML = `
                    <p><strong>${student.name}</strong> (${student.email})</p>
                    <button onclick="deleteStudent(${student.id})">Удалить</button>
                    <button onclick="editStudent(${student.id}, '${student.name}', '${student.email}')">Редактировать</button>
                    <hr>
                `;
                list.appendChild(div);
            });
        });
}

function addStudent(student) {
    fetch("/students", {
        method: "POST",
        headers: {
            'Content-Type': 'application/json'
        },
        body: JSON.stringify(student)
    }).then(() => loadStudents());
}

function deleteStudent(id) {
    fetch(`/students/${id}`, {
        method: "DELETE"
    }).then(() => loadStudents());
}

function editStudent(id, name, email) {
    const newName = prompt("Новое имя:", name);
    const newEmail = prompt("Новый email:", email);
    if (newName && newEmail) {
        fetch(`/students/${id}`, {
            method: "PUT",
            headers: {
                'Content-Type': 'application/json'
            },
            body: JSON.stringify({ name: newName, email: newEmail })
        }).then(() => loadStudents());
    }
}
// Загрузка всех групп
function loadGroups() {
    fetch("/groups")
        .then(res => res.json())
        .then(groups => {
            const groupList = document.getElementById("groupList");
            groupList.innerHTML = "";
            groups.forEach(group => {
                const li = document.createElement("li");
                li.innerHTML = `<strong>${group.name}</strong> (ID: ${group.id}) - Студентов: ${group.students.length}`;
                groupList.appendChild(li);
            });
        });
}

// Добавление студента в группу
function addStudentToGroup(studentId, groupId) {
    fetch(`/students/${studentId}/add-to-group/${groupId}`, {
        method: "POST"
    }).then(() => {
        loadStudents();
        loadGroups();
    });
}

// Удаление студента из группы
function removeStudentFromGroup(studentId, groupId) {
    fetch(`/students/${studentId}/remove-from-group/${groupId}`, {
        method: "DELETE"
    }).then(() => {
        loadStudents();
        loadGroups();
    });
}