package com.sonnets.sonnet.services.search;

import com.sonnets.sonnet.persistence.dtos.base.SearchDto;
import org.apache.lucene.index.Term;
import org.apache.lucene.search.*;

/**
 * Returns a dialog specific query; can be combined with other queries.
 *
 * @author Josh Harkema
 */
public interface DialogHandler {

    static Query getQuery(SearchDto dto) {
        return processQuery(dto);
    }

    private static Query processQuery(SearchDto dto) {
        BooleanQuery.Builder query = new BooleanQuery.Builder();
        if (NullFieldParser.isNull(dto.getText())) {
            PhraseQuery.Builder phraseQuery = new PhraseQuery.Builder();
            phraseQuery.setSlop(SearchConstants.SLOP);
            int counter = 0;
            for (String term : dto.getText().split(" ")) {
                phraseQuery.add(new Term(SearchConstants.DIALOG_BODY, term.toLowerCase()), counter);
                counter++;
            }
            query.add(phraseQuery.build(), BooleanClause.Occur.MUST);
        }
        if (NullFieldParser.isNull(dto.getCharFirstName())) {
            query.add(new FuzzyQuery(new Term(SearchConstants.BOOK_CHARACTER_FN,
                    dto.getCharFirstName().toLowerCase()),
                    SearchConstants.EDIT_DISTANCE, SearchConstants.PREFIX_LENGTH), BooleanClause.Occur.MUST);
        }
        if (NullFieldParser.isNull(dto.getCharLastName())) {
            query.add(new FuzzyQuery(new Term(SearchConstants.BOOK_CHARACTER_LN, dto.getCharLastName().toLowerCase()),
                    SearchConstants.EDIT_DISTANCE, SearchConstants.PREFIX_LENGTH), BooleanClause.Occur.MUST);
        }
//        if (dto.getAuthor() != null && (NullFieldParser.isNull(dto.getAuthor().getFirstName()) ||
//            NullFieldParser.isNull(dto.getAuthor().getLastName()))) {
//            AuthorFilter.filterAuthor(dto, query);
//        }
        return query.build();
    }
}
