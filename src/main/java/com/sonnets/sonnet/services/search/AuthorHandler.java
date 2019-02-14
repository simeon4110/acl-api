package com.sonnets.sonnet.services.search;

import com.sonnets.sonnet.persistence.dtos.base.AuthorDto;
import org.apache.lucene.index.Term;
import org.apache.lucene.search.*;

/**
 * Returns an Author specific query. Should not be chained in with other Queries, use AuthorFilter instead.
 *
 * @author Josh Harkema
 */
public interface AuthorHandler {
    static Query getQuery(AuthorDto dto) {
        return processQuery(dto);
    }

    private static Query processQuery(AuthorDto dto) {
        BooleanQuery.Builder query = new BooleanQuery.Builder();

        if (NullFieldParser.isNull(dto.getFirstName())) {
            query.add(new TermQuery(new Term(SearchConstants.AUTHOR_FIRST_NAME_RAW,
                            dto.getFirstName().toLowerCase())),
                    BooleanClause.Occur.MUST);
        }
        if (NullFieldParser.isNull(dto.getLastName())) {
            if (dto.getLastName().split(" ").length > 1) {
                PhraseQuery.Builder phraseQuery = new PhraseQuery.Builder();
                phraseQuery.setSlop(SearchConstants.SLOP);
                int counter = 0;
                for (String term : dto.getLastName().split(" ")) {
                    phraseQuery.add(new Term(SearchConstants.AUTHOR_FIRST_NAME_RAW, term), counter);
                    counter++;
                }
                query.add(phraseQuery.build(), BooleanClause.Occur.SHOULD);
            } else {
                query.add(new TermQuery(new Term(SearchConstants.AUTHOR_LAST_NAME_RAW,
                                dto.getLastName().toLowerCase())),
                        BooleanClause.Occur.MUST);
            }
        }
        return query.build();
    }
}