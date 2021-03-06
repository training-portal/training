-- FIND_ANSWER_NUMBER_BY_QUESTION_ID
SELECT * FROM ANSWERS_NUMBER WHERE QUESTION_ID = ?;

-- ADD_ANSWER_NUMBER
INSERT INTO ANSWERS_NUMBER (QUESTION_ID, CORRECT) VALUES (?, ?);

-- EDIT_ANSWER_NUMBER
UPDATE ANSWERS_NUMBER SET CORRECT = ? WHERE QUESTION_ID = ?;

-- DELETE_ANSWER_NUMBER
DELETE FROM ANSWERS_NUMBER WHERE QUESTION_ID = ?;