package com.sonnets.sonnet.persistence.models;

import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedBy;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import javax.persistence.EntityListeners;
import javax.persistence.MappedSuperclass;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import java.sql.Date;
import java.util.Objects;

/**
 * Abstract, general use class for auditing data stored via JPA.
 *
 * @param <User> a user object.
 */
@MappedSuperclass
@EntityListeners(AuditingEntityListener.class)
public abstract class Auditable<User> {
    @CreatedBy
    protected User createdBy;

    @CreatedDate
    @Temporal(TemporalType.TIMESTAMP)
    protected Date creationDate;

    @LastModifiedBy
    protected User lastModifiedBy;

    @LastModifiedDate
    @Temporal(TemporalType.TIMESTAMP)
    protected Date lastModifiedDate;

    public User getCreatedBy() {
        return createdBy;
    }

    public void setCreatedBy(User createdBy) {
        this.createdBy = createdBy;
    }

    public Date getCreationDate() {
        return creationDate;
    }

    public void setCreationDate(Date creationDate) {
        this.creationDate = creationDate;
    }

    public User getLastModifiedBy() {
        return lastModifiedBy;
    }

    public void setLastModifiedBy(User lastModifiedBy) {
        this.lastModifiedBy = lastModifiedBy;
    }

    public Date getLastModifiedDate() {
        return lastModifiedDate;
    }

    public void setLastModifiedDate(Date lastModifiedDate) {
        this.lastModifiedDate = lastModifiedDate;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Auditable<?> auditable = (Auditable<?>) o;
        return Objects.equals(createdBy, auditable.createdBy) &&
                Objects.equals(creationDate, auditable.creationDate) &&
                Objects.equals(lastModifiedBy, auditable.lastModifiedBy) &&
                Objects.equals(lastModifiedDate, auditable.lastModifiedDate);
    }

    @Override
    public int hashCode() {
        return Objects.hash(createdBy, creationDate, lastModifiedBy, lastModifiedDate);
    }

    @Override
    public String toString() {
        return "Auditable{" +
                "createdBy=" + createdBy +
                ", creationDate=" + creationDate +
                ", lastModifiedBy=" + lastModifiedBy +
                ", lastModifiedDate=" + lastModifiedDate +
                '}';
    }

}
