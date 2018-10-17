package com.sonnets.sonnet.services.search;

import com.sonnets.sonnet.persistence.dtos.base.SearchDto;
import org.apache.lucene.index.Term;
import org.apache.lucene.search.*;

/**
 * Returns a BookCharacter specific query. Can be combined with other queries.
 *
 * @author Josh Harkema
 */
public interface CharacterHandler {
    static Query getQuery(SearchDto dto) {
        return processQuery(dto);
    }

    private static Query processQuery(SearchDto dto) {
        BooleanQuery.Builder query = new BooleanQuery.Builder();
        if (dto.getCharFirstName() != null && !dto.getCharFirstName().isEmpty()) {
            query.add(new FuzzyQuery(new Term(SearchConstants.BOOK_CHARACTER_FN,
                    dto.getCharFirstName().toLowerCase()),
                    SearchConstants.EDIT_DISTANCE, SearchConstants.PREFIX_LENGTH), BooleanClause.Occur.MUST);
        }
        if (dto.getCharLastName() != null && !dto.getCharLastName().isEmpty()) {
            query.add(new FuzzyQuery(new Term(SearchConstants.BOOK_CHARACTER_LN, dto.getCharLastName().toLowerCase()),
                    SearchConstants.EDIT_DISTANCE, SearchConstants.PREFIX_LENGTH), BooleanClause.Occur.MUST);
        }
        if (dto.getCharGender() != null && !dto.getCharGender().isEmpty()) {
            query.add(new TermQuery(new Term(SearchConstants.BOOK_CHARACTER_SEX, dto.getCharGender())),
                    BooleanClause.Occur.MUST);
        }
        if (dto.getText() != null && !dto.getText().isEmpty()) {
            query.add(ParseText.getPhraseQuery(dto), BooleanClause.Occur.MUST);
        }
        if (dto.getAuthor() != null) {
            AuthorFilter.filterAuthor(dto, query);
        }
        return query.build();
    }
}
