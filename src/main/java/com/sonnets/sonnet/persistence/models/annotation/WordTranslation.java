package com.sonnets.sonnet.persistence.models.annotation;

import com.sonnets.sonnet.persistence.models.TypeConstants;
import com.sonnets.sonnet.services.search.SearchConstants;
import org.hibernate.search.annotations.*;

import javax.persistence.Column;
import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import javax.persistence.Table;
import java.util.Objects;

/**
 * Stores translated word annotations.
 *
 * @author Josh Harkema
 */
@Indexed
@Entity
@Table
@DiscriminatorValue(TypeConstants.WORD_TRANSLATION)
public class WordTranslation extends AnnotationBase {
    private static final long serialVersionUID = 7348613736265038519L;
    @Column
    private Long itemId;
    @Column
    private String itemType;
    @Column
    @Field(store = Store.YES, analyze = Analyze.YES, termVector = TermVector.YES)
    @Analyzer(definition = SearchConstants.TEXT_ANALYZER)
    private String translatedWord;

    public WordTranslation() {
        super();
    }

    public Long getItemId() {
        return itemId;
    }

    public void setItemId(Long itemId) {
        this.itemId = itemId;
    }

    public String getItemType() {
        return itemType;
    }

    public void setItemType(String itemType) {
        this.itemType = itemType;
    }

    public String getTranslatedWord() {
        return translatedWord;
    }

    public void setTranslatedWord(String translatedWord) {
        this.translatedWord = translatedWord;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;
        WordTranslation that = (WordTranslation) o;
        return Objects.equals(itemId, that.itemId) &&
                Objects.equals(itemType, that.itemType) &&
                Objects.equals(translatedWord, that.translatedWord);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), itemId, itemType, translatedWord);
    }

    @Override
    public String toString() {
        return "WordTranslation{" +
                "itemId=" + itemId +
                ", itemType='" + itemType + '\'' +
                ", translatedWord='" + translatedWord + '\'' +
                "} " + super.toString();
    }
}
