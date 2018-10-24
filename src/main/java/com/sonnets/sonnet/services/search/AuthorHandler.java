package com.sonnets.sonnet.services.search;

import com.sonnets.sonnet.persistence.dtos.base.AuthorDto;
import org.apache.lucene.index.Term;
import org.apache.lucene.search.BooleanClause;
import org.apache.lucene.search.BooleanQuery;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.TermQuery;

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
            query.add(new TermQuery(new Term(SearchConstants.AUTHOR_LAST_NAME_RAW,
                            dto.getLastName().toLowerCase())),
                    BooleanClause.Occur.MUST);
        }
        return query.build();
    }
}
