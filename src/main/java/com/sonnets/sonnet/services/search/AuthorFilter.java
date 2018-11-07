package com.sonnets.sonnet.services.search;

import com.sonnets.sonnet.persistence.dtos.base.SearchDto;
import org.apache.lucene.index.Term;
import org.apache.lucene.search.BooleanClause;
import org.apache.lucene.search.BooleanQuery;
import org.apache.lucene.search.FuzzyQuery;

/**
 * Tags any BooleanQuery with MUST clauses for author.firstName and author.lastName.
 *
 * @author Josh Harkema
 */
public interface AuthorFilter {
    static void filterAuthor(SearchDto dto, BooleanQuery.Builder query) {
        addClausesToQuery(dto, query);
    }

    private static void addClausesToQuery(SearchDto dto, BooleanQuery.Builder query) {
        if (NullFieldParser.isNull(dto.getAuthor().getFirstName())) {
            query.add(new FuzzyQuery(new Term(SearchConstants.AUTHOR_FIRST_NAME, dto.getAuthor().getFirstName()),
                            SearchConstants.EDIT_DISTANCE, SearchConstants.PREFIX_LENGTH),
                    BooleanClause.Occur.MUST);
        }
        if (NullFieldParser.isNull(dto.getAuthor().getLastName())) {
            query.add(new FuzzyQuery(new Term(SearchConstants.AUTHOR_LAST_NAME, dto.getAuthor().getLastName()),
                            SearchConstants.EDIT_DISTANCE, SearchConstants.PREFIX_LENGTH),
                    BooleanClause.Occur.MUST);
        }
    }
}
