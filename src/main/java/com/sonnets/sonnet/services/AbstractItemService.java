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
    /**
     * Add item to the database.
     *
     * @param dto the dto containing the item's details.
     * @return 200 if successful.
     */
    ResponseEntity<Void> add(S dto);

    /**
     * Delete an item from the database. (Admin Only).
     *
     * @param id the id of the item to delete.
     * @return 200 if successful.
     */
    ResponseEntity<Void> delete(Long id);

    /**
     * Delete an item from the database. (Item owner only).
     *
     * @param id        the id of the item to delete.
     * @param principal the principal of the user making the request.
     * @return 200 if successful.
     */
    ResponseEntity<Void> userDelete(Long id, Principal principal);

    /**
     * Get an item by its database id.
     *
     * @param id the id of the item to get.
     * @return the item.
     */
    T getById(Long id);

    /**
     * Get multiple items from a list of database ids.
     *
     * @param ids the list of ids to get.
     * @return the items.
     */
    List<T> getByIds(Long[] ids);

    /**
     * @return all *public domain* works in the database.
     */
    List<T> getAll();

    /**
     * @return all works in the database.
     */
    List<T> authedUserGetAll();

    /**
     * @return basic details of all *public domain* works in the database.
     */
    String getAllSimple();

    /**
     * @return basic details of all works in the database.
     */
    String authedUserGetAllSimple();

    /**
     * :todo: this needs to be separated into authed/non-authed.
     *
     * @param pageable from the request.
     * @return a page of items.
     */
    Page<T> getAllPaged(Pageable pageable);

    /**
     * @param principal of the user making the request.
     * @return every item the user has added to the database.
     */
    List<T> getAllByUser(Principal principal);

    /**
     * Allows an ADMIN to make changes to any db item.
     *
     * @param dto the dto with then new details.
     * @return 200 if successful.
     */
    ResponseEntity<Void> modify(S dto);

    /**
     * Allows an item's owner to modify said item.
     *
     * @param dto       the dto with the new details.
     * @param principal of the user making the request.
     * @return 200 if successful, 401 if user doesn't own item.
     */
    ResponseEntity<Void> modifyUser(S dto, Principal principal);

}
