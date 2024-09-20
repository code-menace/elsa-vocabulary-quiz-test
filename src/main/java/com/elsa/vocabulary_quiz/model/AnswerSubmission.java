package com.elsa.vocabulary_quiz.model;

public class AnswerSubmission {
    private Long userId;
    private Long questionId;
    private String answer;

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public Long getQuestionId() {
        return questionId;
    }

    public void setQuestionId(Long questionId) {
        this.questionId = questionId;
    }

    public String getAnswer() {
        return answer;
    }

    public void setAnswer(String answer) {
        this.answer = answer;
    }

    @Override
    public String toString() {
        return "AnswerSubmission{" +
                "userId=" + userId +
                ", questionId=" + questionId +
                ", answer='" + answer + '\'' +
                '}';
    }
}

