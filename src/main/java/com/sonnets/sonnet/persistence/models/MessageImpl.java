package com.sonnets.sonnet.persistence.models;

import javax.persistence.*;
import java.util.Objects;

/**
 * Database object for storing messages.
 *
 * @author Josh Harkema
 */
@Entity
@Table(name = "messages")
public class MessageImpl {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column
    private String fromUser;
    @Column
    private String toUser;
    @Column
    private String messageContent;
    @Column
    private Boolean isRead;

    public MessageImpl() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getFromUser() {
        return fromUser;
    }

    public void setFromUser(String fromUser) {
        this.fromUser = fromUser;
    }

    public String getToUser() {
        return toUser;
    }

    public void setToUser(String toUser) {
        this.toUser = toUser;
    }

    public String getMessageContent() {
        return messageContent;
    }

    public void setMessageContent(String messageContent) {
        this.messageContent = messageContent;
    }

    public Boolean isRead() {
        return isRead;
    }

    public void setIsRead(Boolean isRead) {
        this.isRead = isRead;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        MessageImpl message1 = (MessageImpl) o;
        return isRead == message1.isRead &&
                Objects.equals(id, message1.id) &&
                Objects.equals(fromUser, message1.fromUser) &&
                Objects.equals(toUser, message1.toUser) &&
                Objects.equals(messageContent, message1.messageContent);
    }

    @Override
    public int hashCode() {

        return Objects.hash(id, fromUser, toUser, messageContent, isRead);
    }

    @Override
    public String toString() {
        return "MessageImpl{" +
                "id=" + id +
                ", fromUser='" + fromUser + '\'' +
                ", toUser='" + toUser + '\'' +
                ", messageContent='" + messageContent + '\'' +
                ", isRead=" + isRead +
                '}';
    }

}
