package com.studysoft.trainingportal.dao.impl;

import com.studysoft.trainingportal.config.ApplicationConfiguration;
import com.studysoft.trainingportal.dao.AnswerAccordanceDao;
import com.studysoft.trainingportal.dao.QuestionDao;
import com.studysoft.trainingportal.model.AnswerAccordance;
import com.studysoft.trainingportal.model.Question;
import com.studysoft.trainingportal.model.enums.QuestionType;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.context.web.WebAppConfiguration;

import java.util.ArrayList;
import java.util.List;

import static java.util.Arrays.asList;
import static org.junit.Assert.assertEquals;

@RunWith(SpringRunner.class)
@ContextConfiguration(classes = ApplicationConfiguration.class)
@WebAppConfiguration
@Sql(executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD,
        scripts = {"classpath:dump_postgres.sql"})
public class AnswerAccordanceDaoJdbcTest {

    @Autowired
    private AnswerAccordanceDao answerAccordanceDao;

    @Autowired
    private QuestionDao questionDao;

    public void setAnswerAccordanceDao(AnswerAccordanceDao answerAccordanceDao) {
        this.answerAccordanceDao = answerAccordanceDao;
    }

    @Test
    public void test_find_answerAccordance_by_questionId() {
        List<String> leftSide = new ArrayList<>(asList("SQL", "Java", "HTML", "Pascal"));
        List<String> rightSide = new ArrayList<>(asList("database", "backend", "frontend", "Dead"));
        AnswerAccordance testAnswerAccordance
                = new AnswerAccordance.AnswerAccordanceBuilder()
                .questionId(3L)
                .leftSide(leftSide)
                .rightSide(rightSide)
                .build();

        AnswerAccordance answerAccordance
                = answerAccordanceDao.findAnswerAccordance(3L);

        assertEquals(testAnswerAccordance, answerAccordance);
    }

    @Test
    public void test_add_answerAccordance() {
        Question testQuestion = new Question.QuestionBuilder()
                .quizId(1L)
                .body("Question 1.6 body?")
                .explanation("Question 1.6 explanation")
                .questionType(QuestionType.ACCORDANCE)
                .score(1)
                .build();
        Long questionId = questionDao.addQuestion(testQuestion);

        List<String> leftSide = new ArrayList<>(asList("Model", "View", "Controller", "Spring"));
        List<String> rightSide = new ArrayList<>(asList("Dao", "Pages", "Request handler",
                "Business logic container"));
        AnswerAccordance testAnswerAccordance
                = new AnswerAccordance.AnswerAccordanceBuilder()
                .questionId(questionId)
                .leftSide(leftSide)
                .rightSide(rightSide)
                .build();
        answerAccordanceDao.addAnswerAccordance(testAnswerAccordance);

        AnswerAccordance answerAccordance =
                answerAccordanceDao.findAnswerAccordance(questionId);

        assertEquals(testAnswerAccordance, answerAccordance);
    }

    @Test
    public void edit_answerAccordance() {
        List<String> leftSide = new ArrayList<>(asList("Model", "View", "Controller", "Spring"));
        List<String> rightSide = new ArrayList<>(asList("Dao", "Pages", "Request handler",
                "Business logic container"));
        AnswerAccordance testAnswerAccordance
                = new AnswerAccordance.AnswerAccordanceBuilder()
                .questionId(3L)
                .leftSide(leftSide)
                .rightSide(rightSide)
                .build();
        answerAccordanceDao.editAnswerAccordance(testAnswerAccordance);

        AnswerAccordance answerAccordance
                = answerAccordanceDao.findAnswerAccordance(3L);

        assertEquals(testAnswerAccordance, answerAccordance);
    }

    @Test(expected = EmptyResultDataAccessException.class)
    public void delete_answerAccordance() {
        answerAccordanceDao.deleteAnswerAccordance(3L);
        answerAccordanceDao.findAnswerAccordance(3L);
    }
}