package com.sonnets.sonnet.services.search;

import com.sonnets.sonnet.persistence.dtos.base.SearchDto;
import org.apache.lucene.index.Term;
import org.apache.lucene.search.*;

/**
 * Returns a Poem specific query; can be paired with other results.
 *
 * @author Josh Harkema
 */
public interface PoemHandler {
    static Query getQuery(SearchDto dto) {
        return processQuery(dto);
    }

    private static Query processQuery(SearchDto dto) {
        BooleanQuery.Builder query = new BooleanQuery.Builder();
        if (dto.getAuthor() != null) {
            if (dto.getAuthor().getFirstName() != null && !dto.getAuthor().getFirstName().isEmpty()) {
                query.add(new TermQuery(new Term(SearchConstants.AUTHOR_FIRST_NAME,
                                dto.getAuthor().getFirstName().toLowerCase())),
                        BooleanClause.Occur.MUST);
            }
            if (dto.getAuthor().getLastName() != null && !dto.getAuthor().getLastName().isEmpty()) {
                query.add(new TermQuery(new Term(SearchConstants.AUTHOR_LAST_NAME,
                                dto.getAuthor().getLastName().toLowerCase())),
                        BooleanClause.Occur.MUST);
            }
        }
        if (dto.getTitle() != null && !dto.getTitle().isEmpty()) {
            query.add(new FuzzyQuery(new Term(SearchConstants.TITLE, dto.getTitle().toLowerCase()),
                            SearchConstants.EDIT_DISTANCE, SearchConstants.PREFIX_LENGTH),
                    BooleanClause.Occur.MUST);
        }
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
        if (dto.getForm() != null && !dto.getForm().isEmpty()) {
            query.add(new TermQuery(new Term(SearchConstants.POEM_FORM, dto.getForm())), BooleanClause.Occur.MUST);
        }
        if (dto.getAuthor() != null) {
            AuthorFilter.filterAuthor(dto, query);
        }
        return query.build();
    }
}
