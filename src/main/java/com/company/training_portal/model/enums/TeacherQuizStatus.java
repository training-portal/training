package com.company.training_portal.model.enums;

public enum TeacherQuizStatus {
    UNPUBLISHED("UNPUBLISHED"), PUBLISHED("PUBLISHED"), CLOSED("CLOSED");

    private String teacherQuizStatus;

    TeacherQuizStatus(String teacherQuizStatus) {
        this.teacherQuizStatus = teacherQuizStatus;
    }

    public String getTeacherQuizStatus() {
        return teacherQuizStatus;
    }
}
