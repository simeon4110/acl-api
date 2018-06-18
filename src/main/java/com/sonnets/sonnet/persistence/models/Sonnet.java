package com.sonnets.sonnet.persistence.models;

import com.sonnets.sonnet.persistence.dtos.sonnet.SonnetDto;
import org.apache.lucene.analysis.core.LowerCaseFilterFactory;
import org.apache.lucene.analysis.snowball.SnowballPorterFilterFactory;
import org.apache.lucene.analysis.standard.StandardTokenizerFactory;
import org.hibernate.search.annotations.*;
import org.hibernate.search.annotations.Index;
import org.hibernate.search.annotations.Parameter;
import org.springframework.data.annotation.LastModifiedDate;

import javax.persistence.*;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Objects;

/**
 * Model to store the sonnet info in MySQL.
 *
 * @author Josh Harkema
 */
@Indexed
@Entity
@Table(name = "sonnets")
@AnalyzerDef(name = "noStopWords", tokenizer = @TokenizerDef(factory = StandardTokenizerFactory.class),
        filters = {@TokenFilterDef(factory = LowerCaseFilterFactory.class),
                @TokenFilterDef(factory = SnowballPorterFilterFactory.class, params = {
                        @Parameter(name = "language", value = "English")
                })})
public class Sonnet {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @DocumentId
    private Long id;
    @Field(name = "firstName", index = Index.YES, analyze = Analyze.YES)
    @Analyzer(definition = "noStopWords")
    @Column
    private String firstName;
    @Field(name = "lastName", index = Index.YES, analyze = Analyze.YES)
    @Analyzer(definition = "noStopWords")
    @Column
    private String lastName;
    @Field(name = "title", index = Index.YES, analyze = Analyze.YES)
    @Column
    private String title;
    @Field(name = "line_length", index = Index.YES, analyze = Analyze.NO)
    @Column
    private Integer numOfLines;
    @Field(name = "period", index = Index.YES, analyze = Analyze.NO)
    @Column
    private String period;
    @Field(name = "publicationYear", index = Index.YES, analyze = Analyze.NO)
    @Column
    private Integer publicationYear;
    @Column
    private String publicationStmt;
    @Field(name = "source", index = Index.YES, analyze = Analyze.NO)
    @Column
    private String sourceDesc;
    @Column
    @Temporal(TemporalType.TIMESTAMP)
    @LastModifiedDate
    private Date updatedAt;
    @Column
    private String addedBy;
    @Column
    @IndexedEmbedded
    @Field(name = "text", index = Index.YES, analyze = Analyze.YES)
    @ElementCollection
    private List<String> text;

    /**
     * Default constructor generates a timestamp.
     */
    public Sonnet() {
        this.updatedAt = new Timestamp(System.currentTimeMillis());
    }

    public Sonnet(SonnetDto sonnetDto) {
        this.updatedAt = new Timestamp(System.currentTimeMillis());
        this.firstName = sonnetDto.getFirstName().trim();
        this.lastName = sonnetDto.getLastName().trim();
        this.text = parseText(sonnetDto.getText().split("\\r?\\n"));
        if (Objects.equals(sonnetDto.getTitle(), "") || sonnetDto.getTitle() == null) {
            this.title = this.text.get(0);
        } else {
            this.title = sonnetDto.getTitle().trim().replace("\n", "");
        }
        this.publicationYear = sonnetDto.getPublicationYear();
        this.period = sonnetDto.getPeriod();
        this.publicationStmt = sonnetDto.getPublicationStmt().trim();
        this.sourceDesc = sonnetDto.getSourceDesc().trim();
        this.addedBy = sonnetDto.getAddedBy();
        this.numOfLines = this.text.size();
    }

    /**
     * Parses text input into an Array for database storage.
     *
     * @param text a string[] of the text.
     * @return an ArrayList of the string[].
     */
    public static List<String> parseText(String[] text) {
        List<String> strings = new ArrayList<>();

        for (String s : text) {
            strings.add(s.trim());
        }

        return strings;
    }


    /**
     * Update an existing sonnet from a SonnetDto object.
     *
     * @param sonnetDto the SonnetDto with the new data.
     * @return the updated Sonnet object.
     */
    public Sonnet update(SonnetDto sonnetDto) {
        this.updatedAt = new Timestamp(System.currentTimeMillis());
        this.firstName = sonnetDto.getFirstName();
        this.lastName = sonnetDto.getLastName();
        this.text = parseText(sonnetDto.getText().split("\\r?\\n"));
        if (Objects.equals(sonnetDto.getTitle(), "") || sonnetDto.getTitle() == null) {
            this.title = this.text.get(0);
        } else {
            this.title = sonnetDto.getTitle().trim();
        }
        this.period = sonnetDto.getPeriod();
        this.publicationYear = sonnetDto.getPublicationYear();
        this.publicationStmt = sonnetDto.getPublicationStmt();
        this.sourceDesc = sonnetDto.getSourceDesc();
        this.addedBy = sonnetDto.getAddedBy();
        this.numOfLines = this.text.size();

        return this;
    }

    /**
     * This parses a Sonnet so it shows "pretty" in html <textarea></textarea> elements. (adds \n for newlines.)
     *
     * @return a nicely formatted string.
     */
    public String getTextPretty() {
        StringBuilder sb = new StringBuilder();
        for (String s : text) {
            sb.append(s).append("\n");
        }

        return sb.toString();
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public Integer getNumOfLines() {
        return numOfLines;
    }

    public void setNumOfLines(Integer numOfLines) {
        this.numOfLines = numOfLines;
    }

    public String getPeriod() {
        return period;
    }

    public void setPeriod(String period) {
        this.period = period;
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

    public Date getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(Date updatedAt) {
        this.updatedAt = updatedAt;
    }

    public String getAddedBy() {
        return addedBy;
    }

    public void setAddedBy(String addedBy) {
        this.addedBy = addedBy;
    }

    public List<String> getText() {
        return text;
    }

    public void setText(List<String> text) {
        this.text = text;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Sonnet sonnet = (Sonnet) o;
        return Objects.equals(id, sonnet.id) &&
                Objects.equals(firstName, sonnet.firstName) &&
                Objects.equals(lastName, sonnet.lastName) &&
                Objects.equals(title, sonnet.title) &&
                Objects.equals(numOfLines, sonnet.numOfLines) &&
                Objects.equals(period, sonnet.period) &&
                Objects.equals(publicationYear, sonnet.publicationYear) &&
                Objects.equals(publicationStmt, sonnet.publicationStmt) &&
                Objects.equals(sourceDesc, sonnet.sourceDesc) &&
                Objects.equals(updatedAt, sonnet.updatedAt) &&
                Objects.equals(addedBy, sonnet.addedBy) &&
                Objects.equals(text, sonnet.text);
    }

    @Override
    public int hashCode() {

        return Objects.hash(id, firstName, lastName, title, numOfLines, period, publicationYear, publicationStmt,
                sourceDesc, updatedAt, addedBy, text);
    }

    @Override
    public String toString() {
        return "Sonnet{" +
                "id=" + id +
                ", firstName='" + firstName + '\'' +
                ", lastName='" + lastName + '\'' +
                ", title='" + title + '\'' +
                ", numOfLines=" + numOfLines +
                ", period='" + period + '\'' +
                ", publicationYear=" + publicationYear +
                ", publicationStmt='" + publicationStmt + '\'' +
                ", sourceDesc='" + sourceDesc + '\'' +
                ", updatedAt=" + updatedAt +
                ", addedBy='" + addedBy + '\'' +
                ", text=" + text +
                '}';
    }
}
