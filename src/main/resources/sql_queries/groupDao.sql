-- FIND_GROUP_BY_GROUP_ID
SELECT * FROM GROUPS WHERE GROUP_ID = ?;

-- FIND_GROUPS_BY_AUTHOR_ID
SELECT * FROM GROUPS WHERE AUTHOR_ID = ?;

-- FIND_GROUPS_WHICH_TEACHER_GAVE_QUIZ
SELECT GROUPS.GROUP_ID, GROUPS.NAME, GROUPS.DESCRIPTION,
  GROUPS.CREATION_DATE, GROUPS.AUTHOR_ID
FROM GROUPS INNER JOIN USERS ON GROUPS.GROUP_ID = USERS.GROUP_ID
  INNER JOIN USER_QUIZ_JUNCTIONS J ON USERS.USER_ID = J.USER_ID
  INNER JOIN QUIZZES ON J.QUIZ_ID = QUIZZES.QUIZ_ID
WHERE QUIZZES.AUTHOR_ID = ?
GROUP BY GROUPS.GROUP_ID
ORDER BY GROUPS.NAME;

-- FIND_GROUPS_FOR_QUIZ_PUBLICATION
SELECT GROUPS.GROUP_ID, GROUPS.NAME, GROUPS.DESCRIPTION,
  GROUPS.CREATION_DATE, GROUPS.AUTHOR_ID
FROM GROUPS INNER JOIN USERS ON GROUPS.GROUP_ID = USERS.GROUP_ID
  LEFT JOIN USER_QUIZ_JUNCTIONS J ON USERS.USER_ID = J.USER_ID
WHERE USERS.USER_ID NOT IN (SELECT J.USER_ID FROM USER_QUIZ_JUNCTIONS J WHERE J.QUIZ_ID = ?)
GROUP BY USERS.GROUP_ID
ORDER BY GROUPS.NAME;

-- FIND_GROUPS_FOR_WHICH_PUBLISHED_BY_QUIZ_ID
SELECT GROUPS.GROUP_ID, GROUPS.NAME, GROUPS.DESCRIPTION,
  GROUPS.CREATION_DATE, GROUPS.AUTHOR_ID
FROM GROUPS INNER JOIN USERS ON GROUPS.GROUP_ID = USERS.GROUP_ID
  INNER JOIN USER_QUIZ_JUNCTIONS J ON USERS.USER_ID = J.USER_ID
WHERE J.QUIZ_ID = ?
GROUP BY USERS.GROUP_ID
ORDER BY GROUPS.NAME;

-- FIND_ALL_GROUPS
SELECT * FROM GROUPS;

-- FIND_TEACHER_GROUP_IDS
SELECT GROUP_ID FROM GROUPS WHERE AUTHOR_ID = ?;

-- FIND_GROUPS_NUMBER_BY_AUTHOR_ID
SELECT COUNT(GROUP_ID) FROM GROUPS WHERE AUTHOR_ID = ?;

-- FIND_STUDENTS_NUMBER_IN_GROUP (groupId)
SELECT COUNT(USER_ID) FROM USERS WHERE GROUP_ID = ?;

-- FIND_ALL_GROUPS_AND_STUDENTS_NUMBER_IN_THEM
SELECT GROUPS.GROUP_ID, GROUPS.NAME, GROUPS.DESCRIPTION,
  GROUPS.CREATION_DATE, GROUPS.AUTHOR_ID, COUNT(USERS.USER_ID)
FROM USERS INNER JOIN GROUPS ON USERS.GROUP_ID = GROUPS.GROUP_ID
WHERE USERS.USER_ROLE = 'STUDENT'
GROUP BY GROUPS.NAME;

-- FIND_GROUP_BY_GROUP_NAME
SELECT * FROM GROUPS WHERE NAME = ?;

-- ADD_GROUP
INSERT INTO GROUPS (NAME, DESCRIPTION, CREATION_DATE, AUTHOR_ID)
VALUES (?, ?, ?, ?);

-- EDIT_GROUP
UPDATE GROUPS
SET NAME = ?, DESCRIPTION = ?, CREATION_DATE = ?, AUTHOR_ID = ?
WHERE GROUP_ID = ?;

-- DELETE_GROUP
DELETE FROM GROUPS WHERE GROUP_ID = ?;