package org.acl.database.controllers.base;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;

import java.security.Principal;
import java.util.List;

/**
 * Defines the methods all item objects (Object that extend the Item class) controllers require.
 *
 * @param <T> the object type.
 * @param <S> the object's dto type.
 * @param <P> the object's outbound projection dto.
 */
public interface AbstractItemController<T, S, P> {
    ResponseEntity<Void> add(S dto);

    ResponseEntity<Void> delete(Long id, Principal principal);

    T getById(Long id);

    List<T> getByIds(Long[] ids);

    List<P> getAll();

    List<P> authedUserGetAll();

    Page<T> getAllPaged(Pageable pageable);

    List<T> getAllByUser(Principal principal);

    ResponseEntity<Void> modify(S dto, Principal principal);
}
