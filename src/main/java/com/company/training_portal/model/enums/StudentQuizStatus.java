package com.company.training_portal.model.enums;

public enum StudentQuizStatus {
    OPENED("OPENED"), PASSED("PASSED"), FINISHED("FINISHED");

    private String studentQuizStatus;

    StudentQuizStatus(String studentQuizStatus) {
        this.studentQuizStatus = studentQuizStatus;
    }

    public String getStudentQuizStatus() {
        return studentQuizStatus;
    }
}