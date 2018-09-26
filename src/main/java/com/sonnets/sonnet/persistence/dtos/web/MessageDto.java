package com.sonnets.sonnet.persistence.dtos.web;

import javax.validation.constraints.NotEmpty;

/**
 * POJO for handling message form.
 *
 * @author Josh Harkema
 */
public class MessageDto {
    @NotEmpty
    private String userFrom;
    @NotEmpty
    private String userTo;
    @NotEmpty
    private String subject;
    @NotEmpty
    private String content;

    public String getUserFrom() {
        return userFrom;
    }

    public void setUserFrom(String userFrom) {
        this.userFrom = userFrom;
    }

    public String getUserTo() {
        return userTo;
    }

    public void setUserTo(String userTo) {
        this.userTo = userTo;
    }

    public String getSubject() {
        return subject;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    @Override
    public String toString() {
        return "MessageDto{" +
                "userFrom='" + userFrom + '\'' +
                ", userTo='" + userTo + '\'' +
                ", subject='" + subject + '\'' +
                ", content='" + content + '\'' +
                '}';
    }
}
