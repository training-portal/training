-- FIND_ANSWER_SIMPLE_BY_ANSWER_SIMPLE_ID
SELECT * FROM ANSWERS_SIMPLE WHERE ANSWER_SIMPLE_ID = ?;

-- FIND_ALL_ANSWERS_SIMPLE_BY_QUESTION_ID
SELECT * FROM ANSWERS_SIMPLE WHERE QUESTION_ID = ?;

-- ADD_ANSWER_SIMPLE
INSERT INTO ANSWERS_SIMPLE (QUESTION_ID, BODY, CORRECT) VALUES (?, ?, ?);

-- EDIT_ANSWER_SIMPLE
UPDATE ANSWERS_SIMPLE SET QUESTION_ID = ?, BODY = ?, CORRECT = ? WHERE ANSWER_SIMPLE_ID = ?;

-- DELETE_ANSWERS_SIMPLE_BY_QUESTION_ID
DELETE FROM ANSWERS_SIMPLE WHERE QUESTION_ID = ?;