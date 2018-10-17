package com.sonnets.sonnet.persistence.models.base;

import com.google.gson.annotations.Expose;
import com.sonnets.sonnet.persistence.bridges.AuthorBridge;
import com.sonnets.sonnet.services.search.SearchConstants;
import org.hibernate.search.annotations.*;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Objects;

/**
 * This is the master class, it holds all fields required for all works of literature in the database.
 *
 * @author Josh Harkema
 */
@NamedStoredProcedureQueries({
        @NamedStoredProcedureQuery(
                name = "getUserItems",
                procedureName = "get_user_items",
                parameters = {
                        @StoredProcedureParameter(name = "userName", mode = ParameterMode.IN, type = String.class)
                }
        )
})
@MappedSuperclass
public abstract class Item extends Auditable<String> implements Serializable {
    private static final long serialVersionUID = -5596854181341354264L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @DocumentId
    @Expose
    private Long id;
    @Field(name = SearchConstants.CATEGORY, store = Store.YES, analyze = Analyze.NO)
    @Column
    @Expose
    private String category;
    @Field(name = SearchConstants.AUTHOR)
    @FieldBridge(impl = AuthorBridge.class)
    @ManyToOne(fetch = FetchType.EAGER)
    @Expose
    private Author author;
    @Field(name = SearchConstants.TITLE, store = Store.YES, analyze = Analyze.YES)
    @Analyzer(definition = SearchConstants.TEXT_ANALYZER)
    @Column
    @Expose
    private String title;
    @Column
    private String description;
    @Field(name = SearchConstants.YEAR, store = Store.YES, analyze = Analyze.NO)
    @Column
    @Expose
    private Integer publicationYear;
    @Column
    @Expose
    private String publicationStmt;
    @Field(name = SearchConstants.SOURCE, store = Store.YES, analyze = Analyze.NO)
    @Column
    @Expose
    private String sourceDesc;
    @Field(name = SearchConstants.PERIOD, store = Store.YES, analyze = Analyze.NO)
    @Column
    @Expose
    private String period;
    @OneToOne(cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private UserAnnotation userAnnotation;

    public Item() {
        // Empty for spring data.
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public Author getAuthor() {
        return author;
    }

    public void setAuthor(Author author) {
        this.author = author;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Integer getPublicationYear() {
        return publicationYear;
    }

    public void setPublicationYear(Integer publicationYear) {
        this.publicationYear = publicationYear;
    }

    public String getPublicationStmt() {
        return publicationStmt;
    }

    public void setPublicationStmt(String publicationStmt) {
        this.publicationStmt = publicationStmt;
    }

    public String getSourceDesc() {
        return sourceDesc;
    }

    public void setSourceDesc(String sourceDesc) {
        this.sourceDesc = sourceDesc;
    }

    public String getPeriod() {
        return period;
    }

    public void setPeriod(String period) {
        this.period = period;
    }

    public UserAnnotation getUserAnnotation() {
        return userAnnotation;
    }

    public void setUserAnnotation(UserAnnotation userAnnotation) {
        this.userAnnotation = userAnnotation;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;
        Item item = (Item) o;
        return Objects.equals(id, item.id) &&
                Objects.equals(category, item.category) &&
                Objects.equals(author, item.author) &&
                Objects.equals(title, item.title) &&
                Objects.equals(description, item.description) &&
                Objects.equals(publicationYear, item.publicationYear) &&
                Objects.equals(publicationStmt, item.publicationStmt) &&
                Objects.equals(sourceDesc, item.sourceDesc) &&
                Objects.equals(period, item.period) &&
                Objects.equals(userAnnotation, item.userAnnotation);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), id, category, author, title, description, publicationYear,
                publicationStmt, sourceDesc, period, userAnnotation);
    }

    @Override
    public String toString() {
        return "Item{" +
                "id=" + id +
                ", category='" + category + '\'' +
                ", author=" + author +
                ", title='" + title + '\'' +
                ", description='" + description + '\'' +
                ", publicationYear=" + publicationYear +
                ", publicationStmt='" + publicationStmt + '\'' +
                ", sourceDesc='" + sourceDesc + '\'' +
                ", period='" + period + '\'' +
                ", userAnnotation=" + userAnnotation +
                "} " + super.toString();
    }
}
