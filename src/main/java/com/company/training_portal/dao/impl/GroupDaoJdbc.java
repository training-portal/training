package com.company.training_portal.dao.impl;

import com.company.training_portal.dao.GroupDao;
import com.company.training_portal.model.Group;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.PreparedStatementCreator;
import org.springframework.jdbc.core.ResultSetExtractor;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import javax.sql.DataSource;
import java.sql.*;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Repository
public class GroupDaoJdbc implements GroupDao {

    private JdbcTemplate template;

    private static final Logger logger = Logger.getLogger(GroupDaoJdbc.class);

    @Autowired
    public GroupDaoJdbc(DataSource dataSource) {
        template = new JdbcTemplate(dataSource);
    }

    @Transactional(readOnly = true)
    @Override
    public Group findGroupByGroupId(Long groupId) {
        Group group = template.queryForObject(FIND_GROUP_BY_GROUP_ID,
                new Object[]{groupId}, Group.class);
        logger.info("Group found by groupId");
        return group;
    }

    @Transactional(readOnly = true)
    @Override
    public List<Group> findGroupsByAuthorId(Long authorId) {
        List<Group> groups = template.query(FIND_GROUPS_BY_AUTHOR_ID,
                new Object[]{authorId}, this::mapGroup);
        logger.info("All groups by authorId found:");
        groups.forEach(logger::info);
        return groups;
    }

    @Transactional(readOnly = true)
    @Override
    public List<String> findAllGroupNames() {
       List<String> groupNames = template.queryForList(FIND_ALL_GROUP_NAMES, String.class);
       logger.info("All group names found:");
       groupNames.forEach(logger::info);
       return groupNames;
    }

    @Transactional(readOnly = true)
    @Override
    public List<String> findAllGroupNamesByAuthorId(Long authorId) {
        List<String> groupNames = template.queryForList(FIND_ALL_GROUP_NAMES_BY_AUTHOR_ID,
                new Object[]{authorId}, String.class);
        logger.info("All group names by authorId found:");
        groupNames.forEach(logger::info);
        return groupNames;
    }

    @Transactional(readOnly = true)
    @Override
    public List<Group> findAllGroups() {
        List<Group> groups = template.query(FIND_ALL_GROUPS, this::mapGroup);
        logger.info("All groups found:");
        groups.forEach(logger::info);
        return groups;
    }

    @Transactional(readOnly = true)
    @Override
    public Integer findGroupsNumberByAuthorId(Long authorId) {
        Integer groupsNumber = template.queryForObject(FIND_GROUPS_NUMBER_BY_AUTHOR_ID,
                new Object[]{authorId}, Integer.class);
        logger.info("Groups number by authorId found: " + groupsNumber);
        return groupsNumber;
    }

    @Transactional(readOnly = true)
    @Override
    public Map<String, Integer> findAllGroupsAndStudentsNumberInThem() {
        Map<String, Integer> results = new HashMap<>();
        template.query(FIND_ALL_GROUPS_AND_STUDENTS_NUMBER_IN_THEM,
                new ResultSetExtractor<Map<String, Integer>>() {
                    @Override
                    public Map<String, Integer> extractData(ResultSet rs) throws SQLException, DataAccessException {
                        while (rs.next()) {
                            results.put(rs.getString(1), rs.getInt(2));
                        }
                        return results;
                    }
                });
        logger.info("All groups and students number in them found:");
        results.forEach((k, v) -> logger.info(k + " - " + v));
        return results;
    }

    @Transactional
    @Override
    public Long addGroup(Group group) {
        KeyHolder keyHolder = new GeneratedKeyHolder();
        template.update(new PreparedStatementCreator() {
            @Override
            public PreparedStatement createPreparedStatement(Connection con) throws SQLException {
                PreparedStatement stmt = con.prepareStatement(ADD_GROUP, new String[]{"group_id"});
                stmt.setString(1, group.getName());
                stmt.setString(2, group.getDescription());
                stmt.setDate(3, Date.valueOf(group.getCreationDate()));
                stmt.setLong(4, group.getAuthorId());
                return stmt;
            }
        }, keyHolder);
        long groupId = keyHolder.getKey().longValue();
        group.setGroupId(groupId);
        logger.info("Group added: " + group);
        return groupId;
    }

    @Override
    public void editGroup(Group group) {
        throw new UnsupportedOperationException();
    }

    @Transactional
    @Override
    public void deleteGroup(Long groupId) {
        template.update(DELETE_GROUP, groupId);
        logger.info("Deleted group with groupId: " + groupId);
    }

    private Group mapGroup(ResultSet rs, int rowNum) throws SQLException {
        return new Group.GroupBuilder()
                .name(rs.getString("name"))
                .description(rs.getString("description"))
                .creationDate(rs.getDate("creation_date").toLocalDate())
                .authorId(rs.getLong("author_id"))
                .build();
    }

    private static final String FIND_GROUP_BY_GROUP_ID =
    "SELECT * FROM GROUPS WHERE GROUP_ID = ?;";

    private static final String FIND_GROUPS_BY_AUTHOR_ID =
    "SELECT * FROM GROUPS WHERE AUTHOR_ID = ?;";

    private static final String FIND_ALL_GROUP_NAMES = "SELECT NAME FROM GROUPS;";

    private static final String FIND_ALL_GROUP_NAMES_BY_AUTHOR_ID =
    "SELECT NAME FROM GROUPS WHERE AUTHOR_ID = ?;";

    private static final String FIND_ALL_GROUPS = "SELECT * FROM GROUPS;";

    private static final String FIND_GROUPS_NUMBER_BY_AUTHOR_ID =
    "SELECT COUNT(GROUP_ID) FROM GROUPS WHERE AUTHOR_ID = ?;";

    private static final String FIND_ALL_GROUPS_AND_STUDENTS_NUMBER_IN_THEM =
    "SELECT GROUPS.NAME, COUNT(USERS.USER_ID) " +
    "FROM USERS INNER JOIN GROUPS ON USERS.GROUP_ID = GROUPS.GROUP_ID " +
    "WHERE USERS.USER_ROLE = 'student' " +
    "GROUP BY GROUPS.NAME;";

    private static final String ADD_GROUP =
    "INSERT INTO GROUPS (NAME, DESCRIPTION, CREATION_DATE, AUTHOR_ID) " +
    "VALUES (?, ?, ?, ?);";

    private static final String DELETE_GROUP = "DELETE FROM GROUPS WHERE GROUP_ID = ?;";
}