package com.company.training_portal.dao.impl;

import com.company.training_portal.config.AppConfig;
import com.company.training_portal.dao.QuizDao;
import com.company.training_portal.dao.UserDao;
import com.company.training_portal.model.User;
import com.company.training_portal.model.enums.StudentQuizStatus;
import com.company.training_portal.model.enums.UserRole;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.context.web.WebAppConfiguration;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.*;

@RunWith(SpringRunner.class)
@ContextConfiguration(classes = AppConfig.class)
@WebAppConfiguration
@Sql(executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD,
        scripts = {"classpath:schema.sql", "classpath:test-data.sql"})
public class UserDaoJdbcTest {

    @Autowired
    private UserDao userDao;

    @Autowired
    private QuizDao quizDao;

    public void setUserDao(UserDao userDao) {
        this.userDao = userDao;
    }

    public void setQuizDao(QuizDao quizDao) {
        this.quizDao = quizDao;
    }

    @Test
    public void test_find_user_by_userId_login_email_phoneNumber() {
        User testUser = new User.UserBuilder()
                .userId(4L)
                .groupId(1L)
                .firstName("Artem")
                .lastName("Yakovenko")
                .email("artem@example.com")
                .dateOfBirth(LocalDate.of(1996, 1, 28))
                .phoneNumber("095-98-76-54")
                .photo(null)
                .login("Artem")
                .password("123")
                .userRole(UserRole.STUDENT)
                .build();

        User userByUserId = userDao.findUser(4L);
        User userByLogin = userDao.findUserByLogin("Artem");
        User userByEmail = userDao.findUserByEmail("artem@example.com");
        User userByPhoneNumber = userDao.findUserByPhoneNumber("095-98-76-54");

        assertEquals(testUser, userByUserId);
        assertEquals(testUser, userByLogin);
        assertEquals(testUser, userByEmail);
        assertEquals(testUser, userByPhoneNumber);
    }

    @Test
    public void test_find_userName_by_userId() {
        String userName = userDao.findUserName(4L);
        assertThat(userName, is("Yakovenko Artem"));
    }

    @Test
    public void test_find_users_by_firstName_and_lastName_and_UserRole() {
        User testUser = userDao.findUser(4L);
        List<User> testUsersList = new ArrayList<>();
        testUsersList.add(testUser);

        List<User> userList = userDao.findUsers("Artem",
                "Yakovenko", UserRole.STUDENT);

        assertEquals(testUsersList, userList);
    }

    @Test
    public void test_find_students_by_groupGroupId() {
        List<User> testStudents = new ArrayList<>();
        testStudents.add(userDao.findUser(3L));
        testStudents.add(userDao.findUser(4L));

        List<User> students = userDao.findStudents(1L);

        assertEquals(testStudents, students);
    }

    @Test
    public void test_find_all_students() {
        List<User> testStudents = new ArrayList<>();
        testStudents.add(userDao.findUser(3L));
        testStudents.add(userDao.findUser(4L));
        testStudents.add(userDao.findUser(5L));
        testStudents.add(userDao.findUser(6L));
        testStudents.add(userDao.findUser(7L));
        testStudents.add(userDao.findUser(8L));

        List<User> students = userDao.findAllStudents();

        assertEquals(testStudents, students);
    }

    @Test
    public void test_find_all_teachers() {
        List<User> testTeachers = new ArrayList<>();
        testTeachers.add(userDao.findUser(1L));
        testTeachers.add(userDao.findUser(2L));

        List<User> teachers = userDao.findAllTeachers();

        assertEquals(testTeachers, teachers);
    }

    @Test
    public void test_find_all_student_ids_by_groupId_and_quizId() {
        List<Long> testStudentIds = new ArrayList<>(Arrays.asList(3L, 4L));

        List<Long> studentIds =
                userDao.findStudentIdsByGroupIdAndQuizId(1L, 1L);

        assertEquals(testStudentIds, studentIds);
    }

    @Test
    public void test_find_students_number() {
        Integer studentsNumber = userDao.findStudentsNumber();
        assertThat(studentsNumber, is(6));
    }

    @Test
    public void test_find_teachers_number() {
        Integer teachersNumber = userDao.findTeachersNumber();
        assertThat(teachersNumber, is(2));
    }

    @Test
    public void test_find_students_number_in_group_with_finished_quiz() {
        Integer studentsNumber =
                userDao.findStudentsNumberInGroupWithFinishedQuiz(1L, 1L);
        assertThat(studentsNumber, is(1));
    }

    @Test
    public void test_find_results_number_by_groupId_and_quizId() {
        Integer resultsNumber =
                userDao.findResultsNumber(null, 1L);
        assertThat(resultsNumber, is(1));
    }

    @Test
    public void test_find_final_results_number_by_groupId_and_quizId() {
        Integer finalResultsNumber =
                userDao.findFinalResultsNumber(1L, 1L);
        assertThat(finalResultsNumber, is(1));
    }

    @Test
    public void test_find_student_ids_without_group() {
        List<Long> testStudentIds = new ArrayList<>(Arrays.asList(7L, 8L));
        List<Long> studentIds = userDao.findStudentIdsWithoutGroup();
        assertEquals(testStudentIds, studentIds);
    }

    @Test
    public void test_find_studentIds_and_results_by_groupId_and_quizId() {
        Map<Long, Integer> testResults = new HashMap<>();
        testResults.put(3L, 19);
        testResults.put(4L, 24);

        Map<Long, Integer> results =
                userDao.findStudentIdsAndResultsByGroupIdAndQuizId(1L, 1L);

        assertEquals(testResults, results);
    }

    @Test
    public void test_find_userQuizJunctionId_by_studentId_and_quizId() {
        Long id = userDao.findUserQuizJunctionId(4L, 1L);
        assertThat(id, is(7L));
    }

    @Test
    public void test_user_exists_by_login() {
        assertTrue(userDao.userExistsByLogin("Artem"));
        assertFalse(userDao.userExistsByLogin("Login"));
    }

    @Test
    public void test_user_exists_by_email() {
        assertTrue(userDao.userExistsByEmail("artem@example.com"));
        assertFalse(userDao.userExistsByEmail("email@example.com"));
    }

    @Test
    public void test_user_exists_by_phoneNumber() {
        assertTrue(userDao.userExistsByPhoneNumber("095-98-76-54"));
        assertFalse(userDao.userExistsByPhoneNumber("000-00-00-00"));
    }

    @Test
    public void test_check_user_by_login_and_password() {
        User testUser = userDao.findUser(4L);
        User user = userDao.checkUserByLoginAndPassword("Artem", "123");
        assertEquals(testUser, user);

        assertNull(userDao.checkUserByLoginAndPassword("Artem", "incorrect"));
    }

    @Test
    public void test_register_user() {
        User testUser = new User.UserBuilder()
                .groupId(0L)
                .firstName("Enrico")
                .lastName("Tester")
                .email("enrico@example.com")
                .dateOfBirth(LocalDate.of(2018, 3, 7))
                .phoneNumber("095-345-67-89")
                .login("Enrico")
                .password("123")
                .userRole(UserRole.STUDENT)
                .build();
        Long testUserId = userDao.registerUser(testUser);

        assertEquals(testUser, userDao.findUser(testUserId));
    }

    @Test
    public void test_add_student_to_group_by_groupId_and_userId() {
        userDao.addStudentToGroup(1L, 7L);

        User user = userDao.findUser(7L);
        Long groupId = user.getGroupId();

        assertThat(groupId, is(1L));
    }

    @Test
    public void test_add_students_to_group() {
        userDao.addStudentsToGroup(1L, Arrays.asList(7L, 8L));

        Long jasonGroupId = userDao.findUser(7L).getGroupId();
        Long williamGroupId = userDao.findUser(8L).getGroupId();

        assertThat(jasonGroupId, is(1L));
        assertThat(williamGroupId, is(1L));
    }

    @Test
    public void test_add_student_info_about_quiz() {
        Long userQuizJunctionId = userDao.addStudentInfoAboutQuiz(3L, 5L, 56,
                LocalDateTime.of(2018, 3, 7, 20, 48),
                LocalDateTime.of(2018, 4, 8, 21, 40),
                LocalDateTime.of(2018, 4, 8, 21, 49),
                StudentQuizStatus.PASSED);

        Long testUserQuizJunctionId =
                userDao.findUserQuizJunctionId(3L, 5L);

        assertEquals(userQuizJunctionId, testUserQuizJunctionId);
    }

    @Test
    public void test_update_student_info_about_quiz() {
        userDao.updateStudentInfoAboutQuiz(7L, 56,
                LocalDateTime.of(2018, 1, 10, 10, 2),
                LocalDateTime.of(2018, 1, 10, 10, 10),
                1, StudentQuizStatus.PASSED);

        Integer result = quizDao.findResult(4L, 1L);
        LocalDateTime startDate = quizDao.findStartDate(4L, 1L);
        LocalDateTime finishDate = quizDao.findFinishDate(4L, 1L);
        Integer reopenCounter = quizDao.findAttempt(4L, 1L);
        StudentQuizStatus studentQuizStatus =
                quizDao.findStudentQuizStatus(4L, 1L);

        assertThat(result, is(56));
        assertThat(startDate, is(LocalDateTime.of(2018, 1, 10, 10, 2)));
        assertThat(finishDate, is(LocalDateTime.of(2018, 1, 10, 10, 10)));
        assertThat(reopenCounter, is(1));
        assertThat(studentQuizStatus, is(StudentQuizStatus.PASSED));
    }

    @Test
    public void test_delete_student_from_group_by_userId() {
        userDao.deleteStudentFromGroupByUserId(4L);
        Long groupId = userDao.findUser(4L).getGroupId();
        assertThat(groupId, is(0L));
    }

    @Test
    public void test_delete_students_form_group_by_groupId() {
        userDao.deleteStudentsFromGroupByGroupId(2L);

        Long mikeGroupId = userDao.findUser(5L).getGroupId();
        Long saraGroupId = userDao.findUser(6L).getGroupId();

        assertThat(mikeGroupId, is(0L));
        assertThat(saraGroupId, is(0L));
    }
}