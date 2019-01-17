package com.sonnets.sonnet.controllers;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;

import java.security.Principal;
import java.util.List;

/**
 * Defines the methods all item controllers require.
 *
 * @param <T> the item type.
 * @param <S> the item's dto type.
 */
public interface AbstractItemController<T, S> {
    ResponseEntity<Void> add(S dto);

    ResponseEntity<Void> delete(Long id);

    ResponseEntity<Void> userDelete(Long id, Principal principal);

    T getById(Long id);

    List<T> getByIds(Long[] ids);

    List<T> getAll();

    String getAllSimple();

    Page<T> getAllPaged(Pageable pageable);

    List<T> getAllByUser(Principal principal);

    ResponseEntity<Void> modify(S dto);

    ResponseEntity<Void> modifyUser(S dto, Principal principal);
}
