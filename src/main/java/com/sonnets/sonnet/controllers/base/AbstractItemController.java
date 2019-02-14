package com.sonnets.sonnet.controllers.base;

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
 */
public interface AbstractItemController<T, S> {
    ResponseEntity<Void> add(S dto);

    ResponseEntity<Void> delete(Long id);

    ResponseEntity<Void> userDelete(Long id, Principal principal);

    T getById(Long id);

    List<T> getByIds(Long[] ids);

    List<T> getAll();

    List<T> authedUserGetAll();

    String getAllSimple();

    String authedUserGetAllSimple();

    Page<T> getAllPaged(Pageable pageable);

    List<T> getAllByUser(Principal principal);

    ResponseEntity<Void> modify(S dto);

    ResponseEntity<Void> modifyUser(S dto, Principal principal);
}
