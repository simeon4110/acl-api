package org.acl.database.persistence.repositories;

import org.acl.database.persistence.dtos.base.BookOutDto;
import org.acl.database.persistence.models.base.Book;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * @author Josh Harkema
 */
@SuppressWarnings("SpringDataRepositoryMethodParametersInspection")
@Repository
public interface BookRepository extends JpaRepository<Book, Long> {
    @Query(value = "SELECT new org.acl.database.persistence.dtos.base.BookOutDto(" +
            "b.id, " +
            "b.author, " +
            "b.title, " +
            "b.type, " +
            "b.dateOfPublication) " +
            "FROM Book b " +
            "WHERE b.isPublicDomain = TRUE")
    List<BookOutDto> getAllPublicDomain();

    @Query(value = "SELECT new org.acl.database.persistence.dtos.base.BookOutDto(" +
            "b.id, " +
            "b.author, " +
            "b.title, " +
            "b.type, " +
            "b.dateOfPublication) " +
            "FROM Book b")
    List<BookOutDto> getAll();

    Optional<Book> findByAuthor_IdAndTitle(final long id, final String title);

    Optional<Book> findByTitle(final String title);

    Optional<List<Book>> findAllByCreatedBy(final String createdBy);

    Optional<Page<Book>> findAllByIsPublicDomain(final Boolean isPublicDomain, final Pageable pageable);
}
