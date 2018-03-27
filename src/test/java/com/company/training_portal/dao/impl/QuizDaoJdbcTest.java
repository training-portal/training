package com.company.training_portal.dao.impl;

import com.company.training_portal.config.AppConfig;
import com.company.training_portal.dao.QuestionDao;
import com.company.training_portal.dao.QuizDao;
import com.company.training_portal.model.OpenedQuiz;
import com.company.training_portal.model.PassedQuiz;
import com.company.training_portal.model.Question;
import com.company.training_portal.model.Quiz;
import com.company.training_portal.model.enums.StudentQuizStatus;
import com.company.training_portal.model.enums.TeacherQuizStatus;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.context.web.WebAppConfiguration;

import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.company.training_portal.model.enums.QuestionType.ONE_ANSWER;
import static com.company.training_portal.model.enums.TeacherQuizStatus.PUBLISHED;
import static java.time.temporal.ChronoUnit.MINUTES;
import static java.time.temporal.ChronoUnit.SECONDS;
import static java.util.Arrays.asList;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.*;

@RunWith(SpringRunner.class)
@ContextConfiguration(classes = AppConfig.class)
@WebAppConfiguration
@Sql(executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD,
        scripts = {"classpath:schema.sql", "classpath:test-data.sql"})
public class QuizDaoJdbcTest {

    @Autowired
    private QuizDao quizDao;

    @Autowired
    private QuestionDao questionDao;

    public void setQuizDao(QuizDao quizDao) {
        this.quizDao = quizDao;
    }

    public void setQuestionDao(QuestionDao questionDao) {
        this.questionDao = questionDao;
    }

    @Test
    public void test_find_quiz_by_quizId() {
        Quiz testQuiz = new Quiz.QuizBuilder()
                .quizId(1L)
                .name("Procedural")
                .description("Try your procedural skills")
                .explanation("Hope you had procedural fun :)")
                .creationDate(LocalDate.of(2018, 3, 1))
                .passingTime(Duration.of(10, MINUTES))
                .authorId(1L)
                .questionsNumber(12)
                .score(30)
                .teacherQuizStatus(PUBLISHED)
                .build();

        Quiz quizByQuizId = quizDao.findQuiz(1L);

        assertEquals(testQuiz, quizByQuizId);
    }

    @Test
    public void find_all_quizzes() {
        List<Quiz> testQuizzes = new ArrayList<>();
        testQuizzes.add(quizDao.findQuiz(1L));
        testQuizzes.add(quizDao.findQuiz(2L));
        testQuizzes.add(quizDao.findQuiz(3L));
        testQuizzes.add(quizDao.findQuiz(4L));
        testQuizzes.add(quizDao.findQuiz(5L));
        testQuizzes.add(quizDao.findQuiz(6L));
        testQuizzes.add(quizDao.findQuiz(7L));
        testQuizzes.add(quizDao.findQuiz(8L));
        testQuizzes.add(quizDao.findQuiz(9L));
        testQuizzes.add(quizDao.findQuiz(10L));

        List<Quiz> quizzes = quizDao.findAllQuizzes();

        assertEquals(testQuizzes, quizzes);
    }

    @Test
    public void test_find_all_quizzes_by_authorId() {
        List<Quiz> testQuizzes = new ArrayList<>();
        testQuizzes.add(quizDao.findQuiz(1L));
        testQuizzes.add(quizDao.findQuiz(2L));
        testQuizzes.add(quizDao.findQuiz(10L));
        testQuizzes.add(quizDao.findQuiz(9L));
        testQuizzes.add(quizDao.findQuiz(5L));

        List<Quiz> quizzes = quizDao.findTeacherQuizzes(1L);

        assertEquals(testQuizzes, quizzes);
    }

    @Test
    public void test_find_all_quizzes_by_author_id_and_teacher_quiz_status() {
        List<Quiz> testQuizzes = new ArrayList<>();
        testQuizzes.add(quizDao.findQuiz(1L));
        testQuizzes.add(quizDao.findQuiz(2L));
        testQuizzes.add(quizDao.findQuiz(5L));

        List<Quiz> quizzes = quizDao.findTeacherQuizzes(1L, PUBLISHED);

        assertEquals(testQuizzes, quizzes);
    }

    @Test
    public void test_find_quizzes_by_studentId() {
        List<Quiz> testQuizzes = new ArrayList<>();
        testQuizzes.add(quizDao.findQuiz(1L));
        testQuizzes.add(quizDao.findQuiz(2L));
        testQuizzes.add(quizDao.findQuiz(3L));
        testQuizzes.add(quizDao.findQuiz(4L));
        testQuizzes.add(quizDao.findQuiz(5L));
        testQuizzes.add(quizDao.findQuiz(6L));

        List<Quiz> quizzes = quizDao.findStudentQuizzes(4L);

        assertEquals(testQuizzes, quizzes);
    }

    @Test
    public void test_find_quizzes_number_by_authorId() {
        Integer quizzesNumber = quizDao.findQuizzesNumber(1L);
        assertThat(quizzesNumber, is(5));
    }

    @Test
    public void test_find_students_number_by_authorId_and_groupId_and_quizId_with_studentQuizStatus() {
        Map<StudentQuizStatus, Integer> testResults = new HashMap<>();
        testResults.put(StudentQuizStatus.PASSED, 1);
        testResults.put(StudentQuizStatus.CLOSED, 1);

        Map<StudentQuizStatus, Integer> results
                = quizDao.findStudentsNumberWithStudentQuizStatus(
                1L, 1L, 1L);

        assertEquals(testResults, results);
    }

    @Test
    public void test_find_quizzes_number_by_AuthorId_with_teacherQuizStatus() {
        Map<TeacherQuizStatus, Integer> testResults = new HashMap<>();
        testResults.put(PUBLISHED, 3);
        testResults.put(TeacherQuizStatus.UNPUBLISHED, 2);

        Map<TeacherQuizStatus, Integer> results
                = quizDao.findQuizzesNumberByAuthorIdWithTeacherQuizStatus(1L);

        assertEquals(testResults, results);
    }

    @Test
    public void test_find_all_student_results() {
        Map<Long, Integer> testResults = new HashMap<>();
        testResults.put(1L, 20);
        testResults.put(2L, 8);
        testResults.put(3L, 1);
        testResults.put(4L, 2);
        testResults.put(5L, 0);
        testResults.put(6L, 0);

        Map<Long, Integer> results = quizDao.findAllStudentResults(3L);

        assertEquals(testResults, results);
    }

    @Test
    public void test_find_quizzes_by_studentId_and_authorId() {
        List<Quiz> testQuizzes = new ArrayList<>();
        testQuizzes.add(quizDao.findQuiz(1L));
        testQuizzes.add(quizDao.findQuiz(2L));
        testQuizzes.add(quizDao.findQuiz(5L));

        List<Quiz> quizzes = quizDao.findQuizzes(3L, 1L);
        assertEquals(testQuizzes, quizzes);
    }

    @Test
    public void test_find_common_group_quizzes_by_groupId() {
        List<Long> testCommonGroupQuizzes = new ArrayList<>(asList(1L, 2L, 3L, 4L, 5L, 6L));

        List<Long> commonGroupQuizzes = quizDao.findCommonGroupQuizIds(1L);

        assertEquals(testCommonGroupQuizzes, commonGroupQuizzes);
    }

    @Test
    public void test_find_passed_and_finished_group_quizzes() {
        List<Quiz> testQuizzes = new ArrayList<>();
        testQuizzes.add(quizDao.findQuiz(3L));
        testQuizzes.add(quizDao.findQuiz(2L));
        testQuizzes.add(quizDao.findQuiz(4L));
        testQuizzes.add(quizDao.findQuiz(1L));

        List<Quiz> quizzes = quizDao.findPassedAndClosedGroupQuizzes(1L);

        assertEquals(testQuizzes, quizzes);
    }

    @Test
    public void test_find_result_by_studentId_and_quizId() {
        Integer result = quizDao.findResult(3L, 1L);
        assertThat(result, is(20));
    }

    @Test
    public void test_find_submitDate_by_studentId_and_quizId() {
        LocalDateTime testSubmitDate
                = LocalDateTime.of(2018, 3, 5, 0, 0, 0);
        LocalDateTime submitDate
                = quizDao.findSubmitDate(3L, 1L);
        assertThat(submitDate, is(testSubmitDate));
    }

    @Test
    public void test_find_startDate_by_studentId_and_quizId() {
        LocalDateTime testStartDate
                = LocalDateTime.of(2018, 3, 5, 0, 0, 12);
        LocalDateTime startDate
                = quizDao.findStartDate(3L, 1L);
        assertThat(startDate, is(testStartDate));
    }

    @Test
    public void test_find_finishDate_by_studentId_and_quizId() {
        LocalDateTime testFinishDate
                = LocalDateTime.of(2018, 3, 5, 0, 0, 15);
        LocalDateTime finishDate
                = quizDao.findFinishDate(3L, 1L);
        assertThat(finishDate, is(testFinishDate));
    }

    @Test
    public void test_find_attempt_by_studentId_and_quizId() {
        Integer result = quizDao.findAttempt(3L, 1L);
        assertThat(result, is(2));
    }

    @Test
    public void test_find_studentQuizStatus_by_studentId_and_quizId() {
        StudentQuizStatus studentQuizStatus
                = quizDao.findStudentQuizStatus(5L, 5L);
        assertThat(studentQuizStatus, is(StudentQuizStatus.PASSED));
    }

    @Test
    public void test_find_opened_quiz_by_studentId_and_quizId() {
        OpenedQuiz testOpenedQuiz = new OpenedQuiz.OpenedQuizBuilder()
                .quizId(5L)
                .quizName("Input output")
                .description("Try your IO skills")
                .passingTime(Duration.of(15, MINUTES))
                .authorName("Bronson Andrew")
                .questionsNumber(2)
                .score(5)
                .submitDate(LocalDateTime.of(2018, 3, 5, 0, 24, 0))
                .build();

        OpenedQuiz openedQuiz = quizDao.findOpenedQuiz(4L, 5L);

        assertEquals(testOpenedQuiz, openedQuiz);
    }

    @Test
    public void test_find_passed_quiz_by_studentId_and_quizId() {
        PassedQuiz testPassedQuiz = new PassedQuiz.PassedQuizBuilder()
                .quizId(4L)
                .quizName("Multithreading")
                .description("Try your multithreading skills")
                .explanation("Hope you had multithreading fun :)")
                .authorName("Peterson Angel")
                .result(3)
                .score(4)
                .questionsNumber(1)
                .attempt(2)
                .passingTime(Duration.of(10, SECONDS))
                .submitDate(LocalDateTime.of(2018, 3, 5, 0, 8, 0))
                .finishDate(LocalDateTime.of(2018, 3, 11, 0, 16, 4))
                .timeSpent(Duration.ofSeconds(364))
                .build();

        PassedQuiz passedQuiz = quizDao.findPassedQuiz(4L, 4L);

        assertEquals(testPassedQuiz, passedQuiz);
    }

    @Test
    public void test_find_closed_quiz_by_studentId_and_quizId() {
        PassedQuiz testFinishedQuiz = new PassedQuiz.PassedQuizBuilder()
                .quizId(3L)
                .quizName("Collections")
                .description("Try your collections skills")
                .explanation("Hope you had fun with collections :)")
                .authorName("Peterson Angel")
                .result(2)
                .score(3)
                .questionsNumber(2)
                .attempt(2)
                .passingTime(Duration.of(15, MINUTES))
                .submitDate(LocalDateTime.of(2018, 3, 6, 0, 14, 0))
                .finishDate(LocalDateTime.of(2018, 3, 11, 0, 5, 0))
                .timeSpent(Duration.ofMinutes(5L))
                .build();

        PassedQuiz finishedQuiz = quizDao.findClosedQuiz(4L, 3L);

        assertEquals(testFinishedQuiz, finishedQuiz);
    }

    @Test
    public void test_find_opened_quizzes_by_studentId() {
        List<OpenedQuiz> testOpenedQuizzes = new ArrayList<>();
        testOpenedQuizzes.add(new OpenedQuiz.OpenedQuizBuilder()
                .quizId(6L)
                .quizName("Generics")
                .description("Try your generics skills")
                .passingTime(Duration.of(750, SECONDS))
                .authorName("Peterson Angel")
                .questionsNumber(1)
                .score(3)
                .submitDate(LocalDateTime.of(2018, 3, 5, 0, 31, 30))
                .build());
        testOpenedQuizzes.add(new OpenedQuiz.OpenedQuizBuilder()
                .quizId(5L)
                .quizName("Input output")
                .description("Try your IO skills")
                .passingTime(Duration.of(15, MINUTES))
                .authorName("Bronson Andrew")
                .questionsNumber(2)
                .score(5)
                .submitDate(LocalDateTime.of(2018, 3, 5, 0, 24, 0))
                .build());

        List<OpenedQuiz> openedQuizzes =
                quizDao.findOpenedQuizzes(4L);

        assertEquals(testOpenedQuizzes, openedQuizzes);
    }

    @Test
    public void test_find_passed_quizzes_by_studentId() {
        List<PassedQuiz> testPassedQuizzes = new ArrayList<>();
        testPassedQuizzes.add(new PassedQuiz.PassedQuizBuilder()
                .quizId(4L)
                .quizName("Multithreading")
                .description("Try your multithreading skills")
                .explanation("Hope you had multithreading fun :)")
                .authorName("Peterson Angel")
                .result(3)
                .score(4)
                .questionsNumber(1)
                .attempt(2)
                .passingTime(Duration.of(10, SECONDS))
                .submitDate(LocalDateTime.of(2018, 3, 5, 0, 8, 0))
                .finishDate(LocalDateTime.of(2018, 3, 11, 0, 16, 4))
                .timeSpent(Duration.ofSeconds(364))
                .build());
        testPassedQuizzes.add(new PassedQuiz.PassedQuizBuilder()
                .quizId(1L)
                .quizName("Procedural")
                .description("Try your procedural skills")
                .explanation("Hope you had procedural fun :)")
                .authorName("Bronson Andrew")
                .result(10)
                .score(30)
                .questionsNumber(12)
                .attempt(1)
                .passingTime(Duration.of(10, MINUTES))
                .submitDate(LocalDateTime.of(2018, 3, 8, 0, 13, 0))
                .finishDate(LocalDateTime.of(2018, 3, 5, 0, 4, 10))
                .timeSpent(Duration.ofSeconds(242))
                .build());

        List<PassedQuiz> passedQuizzes
                = quizDao.findPassedQuizzes(4L);

        assertEquals(testPassedQuizzes, passedQuizzes);
    }

    @Test
    public void test_find_closed_quizzes_by_studentId() {
        List<PassedQuiz> testFinishedQuizzes = new ArrayList<>();
        testFinishedQuizzes.add(new PassedQuiz.PassedQuizBuilder()
                .quizId(3L)
                .quizName("Collections")
                .description("Try your collections skills")
                .explanation("Hope you had fun with collections :)")
                .authorName("Peterson Angel")
                .result(2)
                .score(3)
                .questionsNumber(2)
                .attempt(2)
                .passingTime(Duration.of(15, MINUTES))
                .submitDate(LocalDateTime.of(2018, 3, 6, 0, 14, 0))
                .finishDate(LocalDateTime.of(2018, 3, 11, 0, 5, 0))
                .timeSpent(Duration.ofMinutes(5L))
                .build());
        testFinishedQuizzes.add(new PassedQuiz.PassedQuizBuilder()
                .quizId(2L)
                .quizName("Exceptions")
                .description("Try your exceptions skills")
                .explanation("Hope you had fun with exceptions :)")
                .authorName("Bronson Andrew")
                .result(5)
                .score(8)
                .questionsNumber(3)
                .attempt(1)
                .passingTime(Duration.of(10, MINUTES))
                .submitDate(LocalDateTime.of(2018, 3, 5, 0, 0, 0))
                .finishDate(LocalDateTime.of(2018, 3, 5, 0, 3, 4))
                .timeSpent(Duration.ofSeconds(183))
                .build());

        List<PassedQuiz> finishedQuizzes
                = quizDao.findClosedQuizzes(4L);

        assertEquals(testFinishedQuizzes, finishedQuizzes);
    }

    @Test
    public void test_find_opened_quizzes_by_studentId_and_teacherId() {
        List<OpenedQuiz> testOpenedQuizzes = new ArrayList<>();
        testOpenedQuizzes.add(quizDao.findOpenedQuiz(4L, 5L));

        List<OpenedQuiz> openedQuizzes = quizDao.findOpenedQuizzes(4L, 1L);

        assertEquals(testOpenedQuizzes, openedQuizzes);
    }

    @Test
    public void test_find_passed_quizzes_by_studentId_and_teacherId() {
        List<PassedQuiz> testPassedQuizzes = new ArrayList<>();
        testPassedQuizzes.add(quizDao.findPassedQuiz(4L, 1L));

        List<PassedQuiz> passedQuizzes = quizDao.findPassedQuizzes(4L, 1L);

        assertEquals(testPassedQuizzes, passedQuizzes);
    }

    @Test
    public void test_find_closed_quizzes_by_studentId_and_teacherId() {
        List<PassedQuiz> testClosedQuizzes = new ArrayList<>();
        testClosedQuizzes.add(quizDao.findClosedQuiz(4L, 2L));

        List<PassedQuiz> closedQuizzes = quizDao.findClosedQuizzes(4L, 1L);

        assertEquals(testClosedQuizzes, closedQuizzes);
    }

    @Test
    public void test_quiz_exists_by_name() {
        assertTrue(quizDao.quizExistsByName("Procedural"));
        assertFalse(quizDao.quizExistsByName("Test name"));
    }

    @Test
    public void test_add_published_quiz_info() {
        quizDao.addPublishedQuizInfo(5L, 1L,
                LocalDateTime.of(2018, 3, 26, 11, 37, 0));

        OpenedQuiz testOpenedQuiz = new OpenedQuiz.OpenedQuizBuilder()
                .quizId(1L)
                .quizName("Procedural")
                .description("Try your procedural skills")
                .passingTime(Duration.of(10, MINUTES))
                .authorName("Bronson Andrew")
                .submitDate(LocalDateTime.of(2018, 3, 26, 11, 37, 0))
                .questionsNumber(12)
                .score(30)
                .build();

        OpenedQuiz openedQuiz = quizDao.findOpenedQuiz(5L, 1L);

        assertEquals(testOpenedQuiz, openedQuiz);
    }

    @Test
    public void test_add_quiz() {
        Quiz testQuiz = new Quiz.QuizBuilder()
                .name("Servlet API")
                .description("description")
                .explanation("explanation")
                .creationDate(LocalDate.of(2018, 3, 7))
                .passingTime(Duration.of(10, MINUTES))
                .authorId(1L)
                .questionsNumber(1)
                .score(1)
                .teacherQuizStatus(TeacherQuizStatus.UNPUBLISHED)
                .build();
        Long quizId = quizDao.addQuiz(testQuiz);
        System.out.println(">>>>>quizId: " + quizId);
        Question question = new Question.QuestionBuilder()
                .questionId(100_000L)
                .quizId(quizId)
                .body("Question body")
                .explanation("Question explanation")
                .score(1)
                .questionType(ONE_ANSWER)
                .build();
        questionDao.addQuestion(question);

        Quiz quiz = quizDao.findQuiz(quizId);

        assertEquals(testQuiz, quiz);
    }

    @Test
    public void test_edit_student_info_about_opened_quiz() {
        quizDao.editStudentInfoAboutOpenedQuiz(4L, 1L, 56,
                LocalDateTime.of(2018, 1, 10, 10, 2),
                1, StudentQuizStatus.PASSED);

        Integer result = quizDao.findResult(4L, 1L);
        LocalDateTime finishDate = quizDao.findFinishDate(4L, 1L);
        Integer attempt = quizDao.findAttempt(4L, 1L);
        StudentQuizStatus studentQuizStatus =
                quizDao.findStudentQuizStatus(4L, 1L);

        assertThat(result, is(56));
        assertThat(finishDate, is(LocalDateTime.of(2018, 1, 10, 10, 2)));
        assertThat(attempt, is(1));
        assertThat(studentQuizStatus, is(StudentQuizStatus.PASSED));
    }

    @Test
    public void test_edit_teacherQuizStatus_by_quizId() {
        quizDao.editTeacherQuizStatus(TeacherQuizStatus.UNPUBLISHED, 4L);
        TeacherQuizStatus teacherQuizStatus = quizDao
                .findQuiz(4L)
                .getTeacherQuizStatus();
        assertThat(teacherQuizStatus, is(TeacherQuizStatus.UNPUBLISHED));
    }

    @Test
    public void test_edit_quiz_by_quiz_id_name_description_explnation_passing_time() {
        quizDao.editQuiz(1L, "Name", "Description", "Explanation",
                Duration.ofSeconds(3680));

        Quiz quiz = quizDao.findQuiz(1L);

        assertThat(quiz.getName(), is("Name"));
        assertThat(quiz.getDescription(), is("Description"));
        assertThat(quiz.getExplanation(), is("Explanation"));
        assertThat(quiz.getPassingTime(), is(Duration.ofSeconds(3680)));
    }

    @Test
    public void test_close_quiz() {
        quizDao.closeQuiz(4L, 4L);
        StudentQuizStatus studentQuizStatus =
                quizDao.findStudentQuizStatus(4L, 4L);
        assertThat(studentQuizStatus, is(StudentQuizStatus.CLOSED));
    }

    @Test(expected = EmptyResultDataAccessException.class)
    public void test_delete_unpublished_quiz() {
        quizDao.deleteUnpublishedQuiz(9L);
        quizDao.findQuiz(9L);
    }
}