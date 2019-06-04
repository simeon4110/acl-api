package org.acl.database.services.annotation;

import org.springframework.http.ResponseEntity;

import java.security.Principal;
import java.util.Optional;
import java.util.Set;

/**
 * Defines all mandatory methods for a given annotation type.
 *
 * @param <T> the object type of the annotation.
 * @param <S> the dto type of the annotation.
 * @param <U> the object's parent type (i.e. 'Book' or 'Section')
 * @author Josh Harkema
 */
public interface AbstractAnnotationService<T, S, U> {
    ResponseEntity<Void> add(S dto);

    ResponseEntity<Void> delete(Long id);

    ResponseEntity<Void> userDelete(Long id, Principal principal);

    Optional<T> getById(final Long id);

    Optional<Set<T>> getAll();

    Optional<T> getByParentItem(final Long id, final U parent);

    ResponseEntity<Void> modify(S dto);

    ResponseEntity<Void> modifyUser(S dto, Principal principal);
}
