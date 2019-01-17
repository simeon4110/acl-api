package com.sonnets.sonnet.services;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;

import java.security.Principal;
import java.util.List;

/**
 * Defines all mandatory methods for a service.
 *
 * @param <T> the object the service provides access to.
 * @param <S> the dto the service uses.
 */
public interface AbstractItemService<T, S> {
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
