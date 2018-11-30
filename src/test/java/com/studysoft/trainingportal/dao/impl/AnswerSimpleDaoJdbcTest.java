package com.studysoft.trainingportal.dao.impl;

import com.studysoft.trainingportal.config.AppConfig;
import com.studysoft.trainingportal.dao.AnswerSimpleDao;
import com.studysoft.trainingportal.model.AnswerSimple;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.context.web.WebAppConfiguration;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertEquals;

@RunWith(SpringRunner.class)
@ContextConfiguration(classes = AppConfig.class)
@WebAppConfiguration
@Sql(executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD,
        scripts = {"classpath:schema_postgres.sql", "classpath:test-data.sql"})
public class AnswerSimpleDaoJdbcTest {

    @Autowired
    private AnswerSimpleDao answerSimpleDao;

    public void setAnswerSimpleDao(AnswerSimpleDao answerSimpleDao) {
        this.answerSimpleDao = answerSimpleDao;
    }

    @Test
    public void test_find_answerSimple_by_answerSimpleId() {
        AnswerSimple testAnswerSimple = new AnswerSimple.AnswerSimpleBuilder()
                .answerSimpleId(1L)
                .questionId(1L)
                .body("incorrect answer")
                .correct(false)
                .build();

        AnswerSimple answerSimple = answerSimpleDao.findAnswerSimple(1L);

        assertEquals(testAnswerSimple, answerSimple);
    }

    @Test
    public void test_find_all_answers_simple_by_questionId() {
        List<AnswerSimple> testAnswersSimple = new ArrayList<>();
        testAnswersSimple.add(answerSimpleDao.findAnswerSimple(206L));
        testAnswersSimple.add(answerSimpleDao.findAnswerSimple(207L));
//        testAnswersSimple.add(answerSimpleDao.findAnswerSimple(3L));

        List<AnswerSimple> answersSimple = answerSimpleDao.findAnswersSimple(63L);

        assertEquals(testAnswersSimple, answersSimple);
    }

    @Test
    public void test_add_answer_simple() {
        AnswerSimple testAnswerSimple = new AnswerSimple.AnswerSimpleBuilder()
                .questionId(2L)
                .body("correct answer")
                .correct(true)
                .build();
        Long testAnswerSimpleId = answerSimpleDao.addAnswerSimple(testAnswerSimple);

        AnswerSimple answerSimple =
                answerSimpleDao.findAnswerSimple(testAnswerSimpleId);

        assertEquals(testAnswerSimple, answerSimple);
    }

    @Test
    public void test_edit_answer_simple() {
        AnswerSimple testAnswerSimple = new AnswerSimple.AnswerSimpleBuilder()
                .answerSimpleId(3L)
                .questionId(1L)
                .body("incorrect answer")
                .correct(false)
                .build();
        answerSimpleDao.editAnswerSimple(testAnswerSimple);

        AnswerSimple answerSimple =
                answerSimpleDao.findAnswerSimple(3L);

        assertEquals(testAnswerSimple, answerSimple);
    }
}