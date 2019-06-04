package org.acl.database.persistence.models.base;

import javax.persistence.Column;
import javax.persistence.Embeddable;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import java.io.Serializable;
import java.util.Date;
import java.util.Objects;

/**
 * Stores details of an Item's confirmation. Confirmations are not standalone.
 *
 * @author Josh Harkema
 */
@Embeddable
public class Confirmation implements Serializable {
    private static final long serialVersionUID = -4125120151483205315L;
    @Column
    private boolean confirmed;
    @Column
    private String confirmedBy;
    @Temporal(TemporalType.TIMESTAMP)
    @Column
    private Date confirmedAt;
    @Column
    private boolean pendingRevision;

    public Confirmation() {
        this.confirmed = false;
        this.pendingRevision = false;
    }

    public boolean isConfirmed() {
        return confirmed;
    }

    public void setConfirmed(boolean confirmed) {
        this.confirmed = confirmed;
    }

    public String getConfirmedBy() {
        return confirmedBy;
    }

    public void setConfirmedBy(String confirmedBy) {
        this.confirmedBy = confirmedBy;
    }

    public Date getConfirmedAt() {
        return confirmedAt;
    }

    public void setConfirmedAt(Date confirmedAt) {
        this.confirmedAt = confirmedAt;
    }

    public boolean isPendingRevision() {
        return pendingRevision;
    }

    public void setPendingRevision(boolean pendingRevision) {
        this.pendingRevision = pendingRevision;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Confirmation that = (Confirmation) o;
        return confirmed == that.confirmed &&
                pendingRevision == that.pendingRevision &&
                Objects.equals(confirmedBy, that.confirmedBy) &&
                Objects.equals(confirmedAt, that.confirmedAt);
    }

    @Override
    public int hashCode() {
        return Objects.hash(confirmed, confirmedBy, confirmedAt, pendingRevision);
    }

    @Override
    public String toString() {
        return "Confirmation{" +
                "confirmed=" + confirmed +
                ", confirmedBy='" + confirmedBy + '\'' +
                ", confirmedAt=" + confirmedAt +
                ", pendingRevision=" + pendingRevision +
                '}';
    }
}
