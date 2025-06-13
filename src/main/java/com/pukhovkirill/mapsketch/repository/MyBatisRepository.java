package com.pukhovkirill.mapsketch.repository;

import java.util.List;

/**
 * Generic interface for MyBatis-based repositories.
 * Provides basic CRUD operations for entities.
 *
 * @param <T>  the entity type
 * @param <PK> the identifier type
 */

public interface MyBatisRepository<T, PK> {

    /**
     * Saves a list of entities in bulk.
     *
     * @param obj the list of entities to save
     */
    void saveAll(List<T> obj);

    /**
     * Updates the given entity.
     *
     * @param obj the entity to update
     */
    void update(Long id, T obj);

    /**
     * Deletes the entity by its identifier.
     *
     * @param identify the identifier of the entity to delete
     */
    void deleteById(PK identify);

    /**
     * Finds an entity by Id.
     *
     * @param identify the identifier of the entity
     * @return the entity, or {@code null} if not found
     */
    T findById(PK identify);

    /**
     * Returns all entities.
     *
     * @return a list of all entities
     */
    List<T> findAll();
}
