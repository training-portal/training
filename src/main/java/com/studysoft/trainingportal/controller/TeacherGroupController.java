package com.studysoft.trainingportal.controller;

import com.studysoft.trainingportal.dao.GroupDao;
import com.studysoft.trainingportal.dao.QuizDao;
import com.studysoft.trainingportal.dao.UserDao;
import com.studysoft.trainingportal.model.Group;
import com.studysoft.trainingportal.model.Quiz;
import com.studysoft.trainingportal.model.SecurityUser;
import com.studysoft.trainingportal.model.User;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.SessionAttributes;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

import static java.util.Arrays.asList;

@Controller
@SessionAttributes("teacherId")
@PreAuthorize("hasRole('ROLE_TEACHER')")
public class TeacherGroupController {

    private UserDao userDao;
    private GroupDao groupDao;
    private QuizDao quizDao;

    private static final Logger logger = Logger.getLogger(TeacherGroupController.class);

    @Autowired
    public TeacherGroupController(UserDao userDao,
                                  GroupDao groupDao,
                                  QuizDao quizDao) {
        this.userDao = userDao;
        this.groupDao = groupDao;
        this.quizDao = quizDao;
    }

    @ModelAttribute("teacherId")
    public Long getTeacherId(@AuthenticationPrincipal SecurityUser securityUser) {
        return securityUser.getUserId();
    }

    @ModelAttribute("resourceBundle")
    public ResourceBundle getResourceBundle() {
        return ResourceBundle.getBundle("i18n/language", LocaleContextHolder.getLocale());
    }

    // GROUP SHOW ===================================================================

    /**
     * Показує групи, яким викладач публікував вікторини
     *
     * @param teacherId ID авторизованого користувача у HTTP-сесії
     * @param model     інтерфейс для додавання атрибутів до моделі на UI
     * @return teacher_general/groups.jsp
     */
    @RequestMapping(value = "/teacher/groups", method = RequestMethod.GET)
    public String showTeacherGroups(@ModelAttribute("teacherId") Long teacherId, Model model) {
        List<Group> groups = groupDao.findGroupsWhichTeacherGaveQuiz(teacherId);
        List<Group> teacherGroups = groupDao.findGroups(teacherId);
        groups.removeAll(teacherGroups);

        List<Integer> studentsNumberForGroups = new ArrayList<>();
        List<Integer> studentsNumberForTeacherGroups = new ArrayList<>();
        List<User> authors = new ArrayList<>();
        for (Group group : groups) {
            Integer studentsNumber = groupDao.findStudentsNumberInGroup(group.getGroupId());
            studentsNumberForGroups.add(studentsNumber);
            User author = userDao.findUser(group.getAuthorId());
            authors.add(author);
        }
        for (Group group : teacherGroups) {
            Integer studentsNumber = groupDao.findStudentsNumberInGroup(group.getGroupId());
            studentsNumberForTeacherGroups.add(studentsNumber);
        }

        model.addAttribute("groups", groups);
        model.addAttribute("studentsNumberForGroups", studentsNumberForGroups);
        model.addAttribute("authors", authors);
        model.addAttribute("teacherGroups", teacherGroups);
        model.addAttribute("studentsNumberForTeacherGroups", studentsNumberForTeacherGroups);

        return "teacher_general/groups";
    }

    /**
     * Показує інформацію про групу та опубліковані їй вікторини
     *
     * @param teacherId ID авторизованого користувача у HTTP-сесії
     * @param groupId   ID групи
     * @param locale    об'єкт, що містить інформацію про мову, обрану користувачем
     * @param model     інтерфейс для додавання атрибутів до моделі на UI
     * @return teacher_group/own-group-info.jsp, якщо групу створював даний викладач,
     * teacher_group/foreign-group-info.jsp, якщо група іншого викладача
     */
    @RequestMapping(value = "/teacher/groups/{groupId}", method = RequestMethod.GET)
    public String showGroupInfo(@ModelAttribute("teacherId") Long teacherId,
                                @PathVariable("groupId") Long groupId, Locale locale, Model model) {
        List<Long> teacherGroupIds = groupDao.findTeacherGroupIds(teacherId);

        Group group = groupDao.findGroup(groupId);
        Integer studentsNumber = groupDao.findStudentsNumberInGroup(groupId);
        List<User> studentsList = userDao.findStudents(groupId);
        List<Quiz> publishedQuizzes = quizDao.findPublishedQuizzes(groupId, teacherId);

        List<String> statuses = new ArrayList<>();
        Map<Long, List<Integer>> studentsProgress = new HashMap<>();

        ResourceBundle bundle = ResourceBundle.getBundle("i18n/language", locale);
        for (Quiz quiz : publishedQuizzes) {
            Long quizId = quiz.getQuizId();
            Integer studentsNumberForQuiz =
                    userDao.findStudentsNumber(groupId, quizId);
            Integer closedStudents =
                    userDao.findStudentsNumberInGroupWithClosedQuiz(groupId, quizId);
            studentsProgress.put(quizId, asList(closedStudents, studentsNumberForQuiz));
            if (closedStudents.equals(studentsNumberForQuiz)) {
                statuses.add(bundle.getString("group.quiz.closed"));
            } else {
                statuses.add(bundle.getString("group.quiz.passes"));
            }
        }

        model.addAttribute("group", group);
        model.addAttribute("studentsNumber", studentsNumber);
        model.addAttribute("studentsList", studentsList);
        model.addAttribute("publishedQuizzes", publishedQuizzes);
        model.addAttribute("statuses", statuses);
        model.addAttribute("studentsProgress", studentsProgress);

        if (teacherGroupIds.contains(groupId)) {
            return "teacher_group/own-group-info";
        } else {
            return "teacher_group/foreign-group-info";
        }
    }

    // GROUP CREATE ===============================================================

    /**
     * Показує сторінку з формою створення групи
     *
     * @param model інтерфейс для додавання атрибутів до моделі на UI
     * @return teacher_group/group-create.jsp
     */
    @RequestMapping(value = "/teacher/groups/create", method = RequestMethod.GET)
    public String showCreateGroup(Model model) {
        List<User> students = userDao.findStudentsWithoutGroup();
        model.addAttribute("students", students);
        return "teacher_group/group-create";
    }

    /**
     * Створення нової групи та додавання до неї студентів. Проводиться валідація параметрів, введених користувачем.
     * Якщо валідація успішна - група створюється у БД та додається сповіщення успіху на UI
     *
     * @param name               ім'я групи
     * @param description        опис групи
     * @param studentIdsMap      ID студентів для додавання у групу
     * @param teacherId          ID авторизованого користувача у HTTP-сесії
     * @param bundle             об'єкт для ініціалізації текстових повідомлень, залежно від мови, обраної користувачем
     * @param redirectAttributes інтерфейс для збереження атрибутів під час перенапрямлення HTTP-запиту
     * @param model              інтерфейс для додавання атрибутів до моделі на UI
     * @return teacher_group/group-create.jsp при помилках валідації або проводить перенапрямлення HTTP-запиту
     * на /teacher/groups/{groupId} при успішному створенні групи
     */
    @RequestMapping(value = "/teacher/groups/create", method = RequestMethod.POST)
    public String createGroup(@RequestParam("name") String name,
                              @RequestParam("description") String description,
                              @RequestParam Map<String, String> studentIdsMap,
                              @ModelAttribute("teacherId") Long teacherId,
                              @ModelAttribute("resourceBundle") ResourceBundle bundle,
                              RedirectAttributes redirectAttributes, ModelMap model) {
        logger.info("request param 'name' = " + name);
        logger.info("request param 'description' = " + description);
        logger.info("request param 'studentIdsMap' = " + studentIdsMap);

        name = name.trim();
        if (name.isEmpty()) {
            String emptyName = bundle.getString("validation.group.name.empty");
            List<User> students = userDao.findStudentsWithoutGroup();
            model.addAttribute("emptyName", emptyName);
            model.addAttribute("students", students);
            return "teacher_group/group-create";
        }
        if (groupDao.groupExists(name)) {
            String groupExists = bundle.getString("validation.group.name.exists");
            List<User> students = userDao.findStudentsWithoutGroup();
            model.addAttribute("groupExists", groupExists);
            model.addAttribute("students", students);
            return "teacher_group/group-create";
        }

        studentIdsMap.remove("name");
        studentIdsMap.remove("description");

        LocalDate creationDate = LocalDate.now();
        Group group = new Group.GroupBuilder()
                .name(name)
                .description(description.isEmpty() ? null : description)
                .creationDate(creationDate)
                .authorId(teacherId)
                .build();
        Long groupId = groupDao.addGroup(group);

        List<Long> studentIds = studentIdsMap.values().stream()
                .map(Long::valueOf)
                .collect(Collectors.toList());
        userDao.addStudentsToGroup(groupId, studentIds);

        redirectAttributes.addFlashAttribute("createSuccess", true);
        model.clear();
        return "redirect:/teacher/groups/" + groupId;
    }

    /**
     * Показує сторінку з формою додавання студентів до групи
     *
     * @param teacherId ID авторизованого користувача у HTTP-сесії
     * @param groupId   ID групи
     * @param model     інтерфейс для додавання атрибутів до моделі на UI
     * @return teacher_group/group-add-students.jsp
     */
    @RequestMapping(value = "/teacher/groups/{groupId}/add-students", method = RequestMethod.GET)
    public String showAddStudents(@ModelAttribute("teacherId") Long teacherId,
                                  @PathVariable("groupId") Long groupId, Model model) {
        if (checkGroupAccessDenied(teacherId, groupId)) {
            throw new AccessDeniedException("Access denied to group");
        }

        Group group = groupDao.findGroup(groupId);
        List<User> students = userDao.findStudentsWithoutGroup();

        model.addAttribute("group", group);
        model.addAttribute("students", students);

        return "teacher_group/group-add-students";
    }

    /**
     * Додавання студентів до вже створеної групи. Якщо операція успішна - додається сповіщення успіху на UI
     *
     * @param groupId       ID групи
     * @param studentIdsMap ID студентів для додавання у групу
     * @return ResponseEntity зі списком доданих студентів у тілі і HTTP-статусом 200 OK
     */
    @RequestMapping(value = "/teacher/groups/{groupId}/add-students", method = RequestMethod.POST)
    @ResponseBody
    public List<User> addStudents(@PathVariable("groupId") Long groupId,
                                  @RequestParam Map<String, String> studentIdsMap) {
        logger.info("request param map: " + studentIdsMap);

        List<Long> studentIds = studentIdsMap.values().stream()
                .map(Long::valueOf)
                .collect(Collectors.toList());
        userDao.addStudentsToGroup(groupId, studentIds);

        List<User> students = new ArrayList<>();
        for (Long studentId : studentIds) {
            User student = userDao.findUser(studentId);
            students.add(student);
        }
        Collections.sort(students);

        return students;
    }

    // GROUP EDIT ==================================================================

    /**
     * Показує сторінку з формою редагування групи
     *
     * @param teacherId ID авторизованого користувача у HTTP-сесії
     * @param groupId   ID групи
     * @param model     інтерфейс для додавання атрибутів до моделі на UI
     * @return teacher_group/group-edit.jsp
     */
    @RequestMapping(value = "/teacher/groups/{groupId}/edit", method = RequestMethod.GET)
    public String showEditGroup(@ModelAttribute("teacherId") Long teacherId,
                                @PathVariable("groupId") Long groupId, Model model) {
        if (checkGroupAccessDenied(teacherId, groupId)) {
            throw new AccessDeniedException("Access denied to group");
        }

        Group group = groupDao.findGroup(groupId);
        List<User> students = userDao.findStudents(groupId);

        model.addAttribute("group", group);
        model.addAttribute("students", students);

        return "teacher_group/group-edit";
    }

    /**
     * Редагування групи. Проводиться валідація параметрів, введених користувачем. Якщо валідація успішна -
     * група оновлюється у БД та додається сповіщення успіху на UI
     *
     * @param groupId            ID групи
     * @param editedName         нове ім'я
     * @param editedDescription  новий опис
     * @param bundle             об'єкт для ініціалізації текстових повідомлень, залежно від мови, обраної користувачем
     * @param redirectAttributes інтерфейс для збереження атрибутів під час перенапрямлення HTTP-запиту
     * @param model              інтерфейс для додавання атрибутів до моделі на UI
     * @return teacher_group/group-edit.jsp при помилках валідації або проводить перенапрямлення HTTP-запиту
     * на /teacher/groups/{groupId} при успішному оновленні групи
     */
    @RequestMapping(value = "/teacher/groups/{groupId}/edit", method = RequestMethod.POST)
    public String editGroup(@PathVariable("groupId") Long groupId,
                            @RequestParam("name") String editedName,
                            @RequestParam("description") String editedDescription,
                            @ModelAttribute("resourceBundle") ResourceBundle bundle,
                            RedirectAttributes redirectAttributes,
                            ModelMap model) {
        Group oldGroup = groupDao.findGroup(groupId);

        editedName = editedName.trim();
        if (editedName.isEmpty()) {
            String emptyName = bundle.getString("validation.group.name.empty");
            List<User> students = userDao.findStudents(groupId);
            model.addAttribute("group", oldGroup);
            model.addAttribute("students", students);
            model.addAttribute("emptyName", emptyName);
            return "teacher_group/group-edit";
        }
        String name = oldGroup.getName();
        if (!editedName.equals(name) && groupDao.groupExists(editedName)) {
            String groupExists = bundle.getString("validation.group.name.exists");
            List<User> students = userDao.findStudents(groupId);
            model.addAttribute("group", oldGroup);
            model.addAttribute("students", students);
            model.addAttribute("groupExists", groupExists);
            return "teacher_group/group-edit";
        }

        Group editedGroup = new Group.GroupBuilder()
                .groupId(oldGroup.getGroupId())
                .name(editedName)
                .description(editedDescription.isEmpty() ? null : editedDescription)
                .creationDate(oldGroup.getCreationDate())
                .authorId(oldGroup.getAuthorId())
                .build();
        groupDao.editGroup(editedGroup);

        if (!oldGroup.equals(editedGroup)) {
            redirectAttributes.addFlashAttribute("editSuccess", true);
        }
        model.clear();
        return "redirect:/teacher/groups/" + groupId;
    }

    // GROUP DELETE ================================================================

    /**
     * Видалення студента із групи
     *
     * @param groupId   ID групи
     * @param studentId ID студента для видалення
     * @return ResponseEntity із ID видаленого студента у тілі і HTTP-статусом 200 OK
     */
    @RequestMapping(value = "/teacher/groups/{groupId}/delete-student", method = RequestMethod.POST)
    @ResponseBody
    public Long deleteStudentFromGroup(@PathVariable("groupId") Long groupId,
                                       @RequestParam("studentId") Long studentId) {
        userDao.deleteStudentFromGroupByUserId(studentId);
        return studentId;
    }

    /**
     * Видалення групи. Студенти які були в групі, залишаються без групи
     *
     * @param groupId ID групи
     * @param model   інтерфейс для додавання атрибутів до моделі на UI
     * @return teacher_group/group-deleted.jsp
     */
    @RequestMapping(value = "/teacher/groups/{groupId}/delete", method = RequestMethod.POST)
    public String deleteGroup(@PathVariable("groupId") Long groupId, Model model) {
        try {
            Group group = groupDao.findGroup(groupId);
            List<User> students = userDao.findStudents(groupId);
            model.addAttribute("group", group);
            model.addAttribute("students", students);
            groupDao.deleteGroup(groupId);
        } catch (EmptyResultDataAccessException e) {
            model.addAttribute("groupAlreadyDeleted", true);
            return "teacher_group/group-deleted";
        }
        return "teacher_group/group-deleted";
    }

    // INTERNALS =========================================================================

    private boolean checkGroupAccessDenied(Long teacherId, Long groupId) {
        List<Long> teacherGroupIds = groupDao.findTeacherGroupIds(teacherId);
        return !teacherGroupIds.contains(groupId);
    }
}
