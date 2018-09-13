package com.sonnets.sonnet.persistence.models.base;

import com.sonnets.sonnet.persistence.bridges.AuthorBridge;
import com.sonnets.sonnet.persistence.dtos.base.ItemOutDto;
import org.hibernate.search.annotations.*;

import javax.persistence.*;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;
import java.util.Objects;

/**
 * This is the master class, it holds all fields required for all works of literature in the database.
 *
 * @author Josh Harkema
 */
@SqlResultSetMapping(
        name = "itemMap",
        classes = @ConstructorResult(
                targetClass = ItemOutDto.class,
                columns = {
                        @ColumnResult(name = "corpora_id", type = BigDecimal.class),
                        @ColumnResult(name = "item_id", type = BigDecimal.class),
                        @ColumnResult(name = "item_type"),
                        @ColumnResult(name = "created_by"),
                        @ColumnResult(name = "created_date", type = Date.class),
                        @ColumnResult(name = "last_modified_by"),
                        @ColumnResult(name = "last_modified_date", type = Date.class),
                        @ColumnResult(name = "category"),
                        @ColumnResult(name = "description"),
                        @ColumnResult(name = "period"),
                        @ColumnResult(name = "publication_stmt"),
                        @ColumnResult(name = "publication_year", type = int.class),
                        @ColumnResult(name = "source_desc"),
                        @ColumnResult(name = "title"),
                        @ColumnResult(name = "confirmed", type = boolean.class),
                        @ColumnResult(name = "confirmed_at", type = Date.class),
                        @ColumnResult(name = "confirmed_by"),
                        @ColumnResult(name = "pending_revision", type = boolean.class),
                        @ColumnResult(name = "parent_id", type = BigDecimal.class),
                        @ColumnResult(name = "book_tit"),
                        @ColumnResult(name = "text"),
                        @ColumnResult(name = "poem_text"),
                        @ColumnResult(name = "first_name"),
                        @ColumnResult(name = "last_name")
                }
        )
)
@MappedSuperclass
public abstract class Item extends Auditable<String> implements Serializable {
    private static final long serialVersionUID = -5596854181341354264L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @DocumentId
    private Long id;
    @Field(name = "category", store = Store.YES, analyze = Analyze.NO)
    @Column
    private String category;
    @Field(name = "author")
    @FieldBridge(impl = AuthorBridge.class)
    @ManyToOne(fetch = FetchType.EAGER)
    private Author author;
    @Field(name = "title", store = Store.YES, analyze = Analyze.YES)
    @Analyzer(definition = "textAnalyzer")
    @Column
    private String title;
    @Column
    private String description;
    @Field(name = "publicationYear", store = Store.YES, analyze = Analyze.NO)
    @Column
    private Integer publicationYear;
    @Column
    private String publicationStmt;
    @Field(name = "source", store = Store.YES, analyze = Analyze.NO)
    @Column
    private String sourceDesc;
    @Field(name = "period", store = Store.YES, analyze = Analyze.NO)
    @Column
    private String period;
    @Column
    private UserAnnotation userAnnotation;

    protected Item() {
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
