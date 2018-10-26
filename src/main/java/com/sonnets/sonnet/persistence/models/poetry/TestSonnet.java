package com.sonnets.sonnet.persistence.models.poetry;

import com.sonnets.sonnet.persistence.models.base.Item;

import javax.persistence.Column;
import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import java.io.Serializable;
import java.util.List;
import java.util.Objects;

/**
 * This POJO is for intentionally incorrect testing sonnets.
 *
 * @author Josh Harkema
 */
@Entity
public class TestSonnet extends Item implements Serializable {
    private static final long serialVersionUID = -7418620247329135090L;
    @ElementCollection(fetch = FetchType.EAGER)
    private List<String> text;
    @Column
    private Integer pageNumber;

    public TestSonnet() {
        super();
    }

    public List<String> getText() {
        return text;
    }

    public void setText(List<String> text) {
        this.text = text;
    }

    public Integer getPageNumber() {
        return pageNumber;
    }

    public void setPageNumber(Integer pageNumber) {
        this.pageNumber = pageNumber;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;
        TestSonnet that = (TestSonnet) o;
        return Objects.equals(text, that.text) &&
                Objects.equals(pageNumber, that.pageNumber);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), text, pageNumber);
    }

    @Override
    public String toString() {
        return "TestSonnet{" +
                "text=" + text +
                ", pageNumber=" + pageNumber +
                "} " + super.toString();
    }
}
