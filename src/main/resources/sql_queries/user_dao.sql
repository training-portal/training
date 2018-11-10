-- FIND_USER_BY_USER_ID
SELECT * FROM USERS WHERE USER_ID = ?;

-- FIND_USER_BY_LOGIN
SELECT * FROM USERS WHERE LOGIN = ?;

-- FIND_USER_BY_EMAIL
SELECT * FROM USERS WHERE EMAIL = ?;

-- FIND_USER_BY_PHONE_NUMBER
SELECT * FROM USERS WHERE PHONE_NUMBER = ?;

-- FIND_USER_NAME_BY_USER_ID
SELECT FIRST_NAME, LAST_NAME FROM USERS WHERE USER_ID = ?;

-- FIND_USER_BY_FIRST_NAME_AND_LAST_NAME_AND_USER_ROLE
SELECT * FROM USERS WHERE FIRST_NAME = ? AND LAST_NAME = ? AND USER_ROLE;

-- FIND_STUDENTS_BY_GROUP_ID (groupId)
SELECT *
FROM USERS
WHERE GROUP_ID = ? AND USER_ROLE = 'STUDENT'
ORDER BY LAST_NAME, FIRST_NAME;

-- FIND_STUDENTS_BY_GROUP_ID_AND_QUIZ_ID
SELECT USERS.USER_ID, USERS.GROUP_ID, USERS.FIRST_NAME, USERS.LAST_NAME, USERS.EMAIL,
  USERS.DATE_OF_BIRTH, USERS.PHONE_NUMBER, USERS.PHOTO, USERS.LOGIN, USERS.PASSWORD, USERS.USER_ROLE
FROM USERS INNER JOIN USER_QUIZ_JUNCTIONS J ON USERS.USER_ID = J.USER_ID
WHERE USERS.GROUP_ID = ? AND J.QUIZ_ID = ? AND USERS.USER_ROLE = 'STUDENT'
ORDER BY USERS.LAST_NAME, FIRST_NAME;

-- FIND_STUDENTS_BY_GROUP_ID_AND_QUIZ_ID_AND_STUDENT_QUIZ_STATUS
SELECT USERS.USER_ID, USERS.GROUP_ID, USERS.FIRST_NAME, USERS.LAST_NAME, USERS.EMAIL,
  USERS.DATE_OF_BIRTH, USERS.PHONE_NUMBER, USERS.PHOTO, USERS.LOGIN, USERS.PASSWORD, USERS.USER_ROLE
FROM USERS INNER JOIN USER_QUIZ_JUNCTIONS J ON USERS.USER_ID = J.USER_ID
WHERE USERS.GROUP_ID = ? AND J.QUIZ_ID = ?
  AND J.STUDENT_QUIZ_STATUS = ? AND USERS.USER_ROLE = 'STUDENT';

-- FIND_STUDENTS_BY_TEACHER_ID
SELECT USERS.USER_ID, USERS.GROUP_ID, USERS.FIRST_NAME, USERS.LAST_NAME, USERS.EMAIL,
  USERS.DATE_OF_BIRTH, USERS.PHONE_NUMBER, USERS.PHOTO, USERS.LOGIN, USERS.PASSWORD, USERS.USER_ROLE
FROM USERS INNER JOIN USER_QUIZ_JUNCTIONS J ON USERS.USER_ID = J.USER_ID
INNER JOIN QUIZZES ON J.QUIZ_ID = QUIZZES.QUIZ_ID
WHERE QUIZZES.AUTHOR_ID = ? AND USERS.USER_ROLE = 'STUDENT'
GROUP BY USERS.USER_ID
ORDER BY USERS.LAST_NAME, USERS.FIRST_NAME;

-- FIND_ALL_STUDENTS
SELECT * FROM USERS WHERE USER_ROLE = 'STUDENT';

-- FIND_STUDENTS_WITHOUT_GROUP
SELECT * FROM USERS WHERE GROUP_ID IS NULL AND USER_ROLE = 'STUDENT'
ORDER BY USERS.LAST_NAME, FIRST_NAME;

-- FIND_STUDENTS_WITHOUT_GROUP_BY_TEACHER_ID
SELECT USERS.USER_ID, USERS.GROUP_ID, USERS.FIRST_NAME, USERS.LAST_NAME, USERS.EMAIL,
  USERS.DATE_OF_BIRTH, USERS.PHONE_NUMBER, USERS.PHOTO, USERS.LOGIN, USERS.PASSWORD, USERS.USER_ROLE
FROM USERS INNER JOIN USER_QUIZ_JUNCTIONS J ON USERS.USER_ID = J.USER_ID
INNER JOIN QUIZZES ON J.QUIZ_ID = QUIZZES.QUIZ_ID
WHERE QUIZZES.AUTHOR_ID = ? AND USERS.USER_ROLE = 'STUDENT' AND USERS.GROUP_ID IS NULL
GROUP BY USERS.USER_ID
ORDER BY USERS.LAST_NAME, USERS.FIRST_NAME;

-- FIND_STUDENTS_WITHOUT_GROUP_FOR_QUIZ_PUBLICATION
SELECT USERS.USER_ID, USERS.GROUP_ID, USERS.FIRST_NAME, USERS.LAST_NAME, USERS.EMAIL,
USERS.DATE_OF_BIRTH, USERS.PHONE_NUMBER, USERS.PHOTO, USERS.LOGIN, USERS.PASSWORD, USERS.USER_ROLE
FROM USERS LEFT JOIN USER_QUIZ_JUNCTIONS J ON USERS.USER_ID = J.USER_ID
WHERE USERS.USER_ROLE = 'STUDENT' AND USERS.GROUP_ID IS NULL AND
  USERS.USER_ID NOT IN (SELECT J.USER_ID FROM USER_QUIZ_JUNCTIONS J WHERE J.QUIZ_ID = ?)
GROUP BY USERS.USER_ID
ORDER BY USERS.LAST_NAME, USERS.FIRST_NAME;

-- FIND_STUDENTS_FOR_QUIZ_PUBLICATION_BY_GROUP_ID_AND_QUIZ_ID
SELECT USERS.USER_ID, USERS.GROUP_ID, USERS.FIRST_NAME, USERS.LAST_NAME, USERS.EMAIL,
  USERS.DATE_OF_BIRTH, USERS.PHONE_NUMBER, USERS.PHOTO, USERS.LOGIN, USERS.PASSWORD, USERS.USER_ROLE
FROM USERS LEFT JOIN USER_QUIZ_JUNCTIONS J ON USERS.USER_ID = J.USER_ID
WHERE USERS.USER_ROLE = 'STUDENT' AND USERS.GROUP_ID = ? AND
  USERS.USER_ID NOT IN (SELECT J.USER_ID FROM USER_QUIZ_JUNCTIONS J WHERE J.QUIZ_ID = ?)
GROUP BY USERS.USER_ID
ORDER BY USERS.LAST_NAME, USERS.FIRST_NAME;

-- FIND_STUDENTS_WITHOUT_GROUP_FOR_WHOM_PUBLISHED_BY_QUIZ_ID
SELECT USERS.USER_ID, USERS.GROUP_ID, USERS.FIRST_NAME, USERS.LAST_NAME, USERS.EMAIL,
  USERS.DATE_OF_BIRTH, USERS.PHONE_NUMBER, USERS.PHOTO, USERS.LOGIN, USERS.PASSWORD, USERS.USER_ROLE
FROM USERS INNER JOIN USER_QUIZ_JUNCTIONS J ON USERS.USER_ID = J.USER_ID
WHERE USERS.USER_ROLE = 'STUDENT' AND USERS.GROUP_ID IS NULL AND J.QUIZ_ID = ?
GROUP BY USERS.USER_ID
ORDER BY USERS.LAST_NAME, USERS.FIRST_NAME;

-- FIND_STUDENTS_FOR_WHOM_PUBLISHED_BY_GROUP_ID_AND_QUIZ_ID
SELECT USERS.USER_ID, USERS.GROUP_ID, USERS.FIRST_NAME, USERS.LAST_NAME, USERS.EMAIL,
  USERS.DATE_OF_BIRTH, USERS.PHONE_NUMBER, USERS.PHOTO, USERS.LOGIN, USERS.PASSWORD, USERS.USER_ROLE
FROM USERS INNER JOIN USER_QUIZ_JUNCTIONS J ON USERS.USER_ID = J.USER_ID
WHERE USERS.USER_ROLE = 'STUDENT' AND USERS.GROUP_ID = ? AND J.QUIZ_ID = ?
GROUP BY USERS.USER_ID
ORDER BY USERS.LAST_NAME, USERS.FIRST_NAME;

-- FIND_ALL_TEACHERS
SELECT * FROM USERS WHERE USER_ROLE = 'TEACHER';

-- FIND_STUDENTS_NUMBER
SELECT COUNT(USER_ID) FROM USERS WHERE USER_ROLE = 'STUDENT';

-- FIND_STUDENTS_NUMBER_TO_WHOM_QUIZ_WAS_PUBLISHED
SELECT COUNT(USER_ID) FROM USER_QUIZ_JUNCTIONS WHERE QUIZ_ID = ?;

-- FIND_STUDENTS_NUMBER_WHO_CLOSED_QUIZ
SELECT COUNT(USER_ID) FROM USER_QUIZ_JUNCTIONS
WHERE QUIZ_ID = ? AND STUDENT_QUIZ_STATUS = 'CLOSED';

-- FIND_STUDENTS_NUMBER_BY_GROUP_ID_AND_QUIZ_ID
SELECT COUNT(USERS.USER_ID)
FROM USERS INNER JOIN USER_QUIZ_JUNCTIONS J ON USERS.USER_ID = J.USER_ID
WHERE CASE WHEN group_id IS NULL THEN FALSE ELSE USERS.GROUP_ID = ? AND J.QUIZ_ID = ? END;

-- FIND_TEACHERS_NUMBER
SELECT COUNT(USER_ID) FROM USERS WHERE USER_ROLE = 'TEACHER';

-- FIND_STUDENTS_NUMBER_IN_GROUP_WITH_CLOSED_QUIZ (groupId, quizId)
SELECT COUNT(USERS.USER_ID)
FROM USERS INNER JOIN USER_QUIZ_JUNCTIONS J ON USERS.USER_ID = J.USER_ID
WHERE USERS.USER_ROLE = 'STUDENT' AND USERS.GROUP_ID = ? AND J.QUIZ_ID = ? AND J.STUDENT_QUIZ_STATUS = 'CLOSED';

-- FIND_RESULTS_NUMBER_BY_GROUP_ID_AND_QUIZ_ID (groupId, quizId)
SELECT COUNT(J.RESULT)
FROM USER_QUIZ_JUNCTIONS J INNER JOIN USERS ON J.USER_ID = USERS.USER_ID
WHERE USERS.GROUP_ID = ? AND J.QUIZ_ID = ?;

-- FIND_FINAL_RESULTS_NUMBER_BY_GROUP_ID_AND_QUIZ_ID (groupId, quizId)
SELECT COUNT(J.RESULT)
FROM USER_QUIZ_JUNCTIONS J INNER JOIN USERS ON J.USER_ID = USERS.USER_ID
WHERE USERS.GROUP_ID = ? AND J.QUIZ_ID = ? AND J.STUDENT_QUIZ_STATUS = 'CLOSED';

-- FIND_STUDENT_IDS_WITHOUT_GROUP
SELECT USER_ID FROM USERS WHERE USER_ROLE = 'STUDENT' AND GROUP_ID IS NULL;

-- FIND_USER_BY_LOGIN_AND_PASSWORD
SELECT * FROM USERS WHERE LOGIN = ? AND PASSWORD = ?;

-- REGISTER_USER (userId, groupId)
INSERT INTO USERS (GROUP_ID, FIRST_NAME, LAST_NAME, EMAIL, DATE_OF_BIRTH, PHONE_NUMBER, LOGIN, PASSWORD, USER_ROLE)
VALUES (NULL, ?, ?, ?, ?, ?, ?, ?, ?);

-- ADD_STUDENT_TO_GROUP_BY_GROUP_ID_AND_USER_ID
UPDATE USERS
SET GROUP_ID = ?
WHERE USER_ID = ? AND USER_ROLE = 'STUDENT';

-- EDIT_USER
UPDATE USERS
SET FIRST_NAME = ?, LAST_NAME = ?, EMAIL = ?, DATE_OF_BIRTH = ?, PHONE_NUMBER = ?, PASSWORD = ?
WHERE USER_ID = ?;

-- DELETE_STUDENT_FROM_GROUP_BY_USER_ID
UPDATE USERS SET GROUP_ID = NULL WHERE USER_ID = ? AND USER_ROLE = 'STUDENT';

-- DELETE_STUDENTS_FROM_GROUP_BY_GROUP_ID
UPDATE USERS SET GROUP_ID = NULL WHERE GROUP_ID = ? AND USER_ROLE = 'STUDENT';