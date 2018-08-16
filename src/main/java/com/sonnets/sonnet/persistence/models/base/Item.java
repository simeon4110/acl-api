package com.sonnets.sonnet.persistence.models.base;

import com.sonnets.sonnet.persistence.bridges.AuthorBridge;
import org.hibernate.search.annotations.*;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Objects;

@MappedSuperclass
public abstract class Item extends Auditable<String> implements Serializable {
    private static final long serialVersionUID = -5596854181341354264L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @DocumentId
    private Long id;
    @IndexedEmbedded
    @Field(name = "category", store = Store.YES, analyze = Analyze.NO)
    @Column
    private String category;
    @IndexedEmbedded
    @Field(name = "author")
    @FieldBridge(impl = AuthorBridge.class)
    @ManyToOne
    private Author author;
    @IndexedEmbedded
    @Field(name = "title", store = Store.YES)
    @Column
    private String title;
    @Column
    private String description;
    @IndexedEmbedded
    @Field(name = "publicationYear", store = Store.YES, analyze = Analyze.NO)
    @Column
    private Integer publicationYear;
    @Column
    private String publicationStmt;
    @IndexedEmbedded
    @Field(name = "source", analyze = Analyze.NO)
    @Column
    private String sourceDesc;
    @IndexedEmbedded
    @Field(name = "period", analyze = Analyze.NO)
    @Column
    private String period;

    protected Item() {
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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;
        Item item = (Item) o;
        return Objects.equals(id, item.id) &&
                category == item.category &&
                Objects.equals(author, item.author) &&
                Objects.equals(title, item.title) &&
                Objects.equals(description, item.description) &&
                Objects.equals(publicationYear, item.publicationYear) &&
                Objects.equals(publicationStmt, item.publicationStmt) &&
                Objects.equals(sourceDesc, item.sourceDesc) &&
                Objects.equals(period, item.period);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), id, category, author, title, description, publicationYear,
                publicationStmt, sourceDesc, period);
    }

    @Override
    public String toString() {
        return "Item{" +
                "id=" + id +
                ", category=" + category +
                ", author=" + author +
                ", title='" + title + '\'' +
                ", description='" + description + '\'' +
                ", publicationYear=" + publicationYear +
                ", publicationStmt='" + publicationStmt + '\'' +
                ", sourceDesc='" + sourceDesc + '\'' +
                ", period='" + period + '\'' +
                '}';
    }
}
