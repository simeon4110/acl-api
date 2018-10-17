package com.sonnets.sonnet.services.search;

import com.sonnets.sonnet.persistence.dtos.base.SearchDto;
import org.apache.lucene.index.Term;
import org.apache.lucene.search.PhraseQuery;

/**
 * Turns raw text into a parsed phraseQuery.
 *
 * @author Josh Harkema
 */
public interface ParseText {
    static PhraseQuery getPhraseQuery(SearchDto dto) {
        PhraseQuery.Builder phraseQuery = new PhraseQuery.Builder();
        phraseQuery.setSlop(SearchConstants.SLOP);
        int counter = 0;
        for (String term : dto.getText().split(" ")) {
            phraseQuery.add(new Term(SearchConstants.TEXT, term.toLowerCase()), counter);
            counter++;
        }
        return phraseQuery.build();
    }
}
