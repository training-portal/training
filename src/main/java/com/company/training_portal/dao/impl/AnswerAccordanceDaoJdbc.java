package com.company.training_portal.dao.impl;

import com.company.training_portal.dao.AnswerAccordanceDao;
import com.company.training_portal.model.AnswerAccordance;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.PreparedStatementCreator;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Repository
public class AnswerAccordanceDaoJdbc implements AnswerAccordanceDao {

    private JdbcTemplate template;

    private static final Logger logger = Logger.getLogger(AnswerAccordanceDaoJdbc.class);

    @Autowired
    public AnswerAccordanceDaoJdbc(DataSource dataSource) {
        template = new JdbcTemplate(dataSource);
    }

    @Transactional(readOnly = true)
    @Override
    public AnswerAccordance findAnswerAccordanceByQuestionId(Long questionId) {
        AnswerAccordance answerAccordance = template.queryForObject(
                FIND_ANSWER_ACCORDANCE_BY_QUESTION_ID,
                this::mapAnswerAccordance, questionId);
        logger.info("Found answerAccordance by questionId: " + answerAccordance);
        return answerAccordance;
    }

    @Transactional
    @Override
    public Long addAnswerAccordance(AnswerAccordance answerAccordance) {
        KeyHolder keyHolder = new GeneratedKeyHolder();
        template.update(new PreparedStatementCreator() {
            @Override
            public PreparedStatement createPreparedStatement(Connection con) throws SQLException {
                PreparedStatement stmt = con.prepareStatement(ADD_ANSWER_ACCORDANCE,
                        new String[]{"answer_accordance_id"});
                stmt.setLong(1, answerAccordance.getQuestionId());
                Map<String, String> correctMap = answerAccordance.getCorrectMap();
                int index = 2; // parameter index for next columns
                for (Map.Entry<String, String> entry : correctMap.entrySet()) {
                    stmt.setString(index++, entry.getKey());
                    stmt.setString(index++, entry.getValue());
                }
                return stmt;
            }
        }, keyHolder);
        long answerAccordanceId = keyHolder.getKey().longValue();
        answerAccordance.setAnswerAccordanceId(answerAccordanceId);
        logger.info("Added answerAccordance: " + answerAccordance);
        return answerAccordanceId;
    }

    @Transactional
    @Override
    public void editAnswerAccordance(AnswerAccordance answerAccordance) {
        Map<String, String> correctMap = answerAccordance.getCorrectMap();
        List<String> leftSide = new ArrayList<>(correctMap.keySet());
        List<String> rightSide = new ArrayList<>();
        for (String key : leftSide) {
            rightSide.add(correctMap.get(key));
        }
        template.update(EDIT_ANSWER_ACCORDANCE,
                leftSide.get(0), rightSide.get(0),
                leftSide.get(1), rightSide.get(1),
                leftSide.get(2), rightSide.get(2),
                leftSide.get(3), rightSide.get(3),
                answerAccordance.getQuestionId());
        logger.info("Edited answerAccordance: " + answerAccordance);
    }

    @Transactional
    @Override
    public void deleteAnswerAccordance(Long questionId) {
        template.update(DELETE_ANSWER_ACCORDANCE, questionId);
        logger.info("Deleted answerAccordance with questionId: " + questionId);
    }

    private AnswerAccordance mapAnswerAccordance(ResultSet rs, int rowNum) throws SQLException {
        Map<String, String> correctMap = new HashMap<>();
        correctMap.put(rs.getString("left_side_1"), rs.getString("right_side_1"));
        correctMap.put(rs.getString("left_side_2"), rs.getString("right_side_2"));
        correctMap.put(rs.getString("left_side_3"), rs.getString("right_side_3"));
        correctMap.put(rs.getString("left_side_4"), rs.getString("right_side_4"));

        return new AnswerAccordance.AnswerAccordanceBuilder()
                .answerAccordanceId(rs.getLong("answer_accordance_id"))
                .questionId(rs.getLong("question_id"))
                .correctMap(correctMap)
                .build();
    }

    private static final String FIND_ANSWER_ACCORDANCE_BY_QUESTION_ID =
    "SELECT * FROM ANSWERS_ACCORDANCE WHERE QUESTION_ID = ?;";

    private static final String ADD_ANSWER_ACCORDANCE =
    "INSERT INTO ANSWERS_ACCORDANCE (question_id, left_side_1, right_side_1, left_side_2, right_side_2, " +
    "left_side_3, right_side_3, left_side_4, right_side_4) " +
    "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?);";

    private static final String EDIT_ANSWER_ACCORDANCE =
    "UPDATE ANSWERS_ACCORDANCE " +
    "SET LEFT_SIDE_1 = ?, RIGHT_SIDE_1 = ?, LEFT_SIDE_2 = ?, RIGHT_SIDE_2 = ?, " +
    "LEFT_SIDE_3 = ?, RIGHT_SIDE_3 = ?, LEFT_SIDE_4 = ?, RIGHT_SIDE_4 = ? " +
    "WHERE QUESTION_ID = ?;";

    private static final String DELETE_ANSWER_ACCORDANCE =
    "DELETE FROM ANSWERS_ACCORDANCE WHERE QUESTION_ID = ?;";
}
