package com.sonnets.sonnet.services.search;

import com.sonnets.sonnet.persistence.dtos.base.SearchDto;
import org.apache.lucene.index.Term;
import org.apache.lucene.search.BooleanClause;
import org.apache.lucene.search.BooleanQuery;
import org.apache.lucene.search.PhraseQuery;
import org.apache.lucene.search.Query;

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
        if (dto.getText() != null && !dto.getText().isEmpty()) {
            PhraseQuery.Builder phraseQuery = new PhraseQuery.Builder();
            phraseQuery.setSlop(SearchConstants.SLOP);
            int counter = 0;
            for (String term : dto.getText().split(" ")) {
                phraseQuery.add(new Term(SearchConstants.DIALOG_BODY, term.toLowerCase()), counter);
                counter++;
            }
            query.add(phraseQuery.build(), BooleanClause.Occur.MUST);
        }
        if (dto.getAuthor() != null && (!dto.getAuthor().getLastName().equals("") ||
                !dto.getAuthor().getFirstName().equals(""))) {
            AuthorFilter.filterAuthor(dto, query);
        }
        return query.build();
    }
}
