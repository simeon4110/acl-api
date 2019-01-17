package com.sonnets.sonnet.persistence.repositories.book;

import com.sonnets.sonnet.persistence.models.base.Book;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * @author Josh Harkema
 */
@SuppressWarnings("SpringDataRepositoryMethodParametersInspection")
@Repository
public interface BookRepository extends JpaRepository<Book, Long>, BookRepositoryStoredProcedures {
    Optional<Book> findByAuthor_IdAndTitle(final long id, final String title);

    Optional<Book> findByTitle(final String title);

    Optional<List<Book>> findAllByCreatedBy(final String createdBy);
}
