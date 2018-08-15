package com.sonnets.sonnet.persistence.models;

import javax.persistence.*;
import java.util.Objects;

/**
 * User-to-user and system-to-user message object.
 *
 * @author Josh Harkema
 */
@Entity
@Table
public class Message extends Auditable<String> {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column
    private String userFrom;
    @Column
    private String userTo;
    @Column
    private String subject;
    @Column
    private String content;
    @Column
    private boolean isRead;

    public Message() {
        // Default constructor for spring data.
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

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

    public boolean isRead() {
        return isRead;
    }

    public void setRead(boolean read) {
        isRead = read;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;
        Message message = (Message) o;
        return isRead == message.isRead &&
                Objects.equals(id, message.id) &&
                Objects.equals(userFrom, message.userFrom) &&
                Objects.equals(userTo, message.userTo) &&
                Objects.equals(subject, message.subject) &&
                Objects.equals(content, message.content);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), id, userFrom, userTo, subject, content, isRead);
    }

    @Override
    public String toString() {
        return "Message{" +
                "id=" + id +
                ", userFrom='" + userFrom + '\'' +
                ", userTo='" + userTo + '\'' +
                ", subject='" + subject + '\'' +
                ", content='" + content + '\'' +
                ", isRead=" + isRead +
                '}';
    }
}
