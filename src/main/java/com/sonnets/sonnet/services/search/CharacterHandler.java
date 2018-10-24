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

    private static Query processQuery(final SearchDto dto) {
        BooleanQuery.Builder query = new BooleanQuery.Builder();
        if (NullFieldParser.isNull(dto.getCharFirstName())) {
            query.add(new FuzzyQuery(new Term(SearchConstants.BOOK_CHARACTER_FN,
                    dto.getCharFirstName().toLowerCase()),
                    SearchConstants.EDIT_DISTANCE, SearchConstants.PREFIX_LENGTH), BooleanClause.Occur.MUST);
        }
        if (NullFieldParser.isNull(dto.getCharLastName())) {
            query.add(new FuzzyQuery(new Term(SearchConstants.BOOK_CHARACTER_LN, dto.getCharLastName().toLowerCase()),
                    SearchConstants.EDIT_DISTANCE, SearchConstants.PREFIX_LENGTH), BooleanClause.Occur.MUST);
        }
        if (NullFieldParser.isNull(dto.getCharGender())) {
            query.add(new TermQuery(new Term(SearchConstants.BOOK_CHARACTER_SEX, dto.getCharGender())),
                    BooleanClause.Occur.MUST);
        }
        if (NullFieldParser.isNull(dto.getText())) {
            PhraseQuery.Builder phraseQuery = new PhraseQuery.Builder();
            phraseQuery.setSlop(SearchConstants.SLOP);
            int counter = 0;
            for (String term : dto.getText().split(" ")) {
                phraseQuery.add(new Term(SearchConstants.CHARACTER_DIALOG, term.toLowerCase()), counter);
                counter++;
            }
            query.add(phraseQuery.build(), BooleanClause.Occur.MUST);
        }
        if (dto.getAuthor() != null) {
            AuthorFilter.filterAuthor(dto, query);
        }
        return query.build();
    }
}
