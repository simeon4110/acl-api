package com.sonnets.sonnet.services.search;

import com.sonnets.sonnet.persistence.dtos.base.SearchDto;
import org.apache.lucene.index.Term;
import org.apache.lucene.search.*;

/**
 * Returns a Section specific query; can be paired with other results.
 *
 * @author Josh Harkema
 */
public interface SectionHandler {
    static Query getQuery(SearchDto dto) {
        return processQuery(dto);
    }

    private static Query processQuery(SearchDto dto) {
        BooleanQuery.Builder query = new BooleanQuery.Builder();
        if (dto.getPublicationYear() != 0) {
            query.add(NumericRangeQuery.newIntRange(
                    SearchConstants.YEAR, SearchConstants.PRECISION_STEP,
                    dto.getPublicationYear() - SearchConstants.INT_DISTANCE,
                    dto.getPublicationYear() + SearchConstants.INT_DISTANCE, true, true
            ), BooleanClause.Occur.MUST);
        }
        if (dto.getPeriod() != null && !dto.getPeriod().isEmpty()) {
            query.add(new TermQuery(new Term(SearchConstants.PERIOD, dto.getPeriod().toLowerCase())),
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
