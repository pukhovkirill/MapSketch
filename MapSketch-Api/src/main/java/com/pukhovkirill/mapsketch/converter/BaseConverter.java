package com.pukhovkirill.mapsketch.converter;

import com.pukhovkirill.mapsketch.entity.BaseEntity;

/**
 * Base class for converting between entity and DTO representations.
 *
 * <p>This abstract converter defines a bi-directional transformation contract
 * between a domain entity and its corresponding DTO.</p>
 *
 * @param <E> the type of the entity, extending {@link BaseEntity}
 * @param <D> the type of the Data Transfer Object
 */
public abstract class BaseConverter<E extends BaseEntity, D> {

    /**
     * Converts a DTO to its corresponding entity.
     *
     * @param dto the DTO to convert
     * @return the converted entity
     */
    public abstract E convert(D dto);

    /**
     * Converts an entity to its corresponding DTO.
     *
     * @param entity the entity to convert
     * @return the converted DTO
     */
    public abstract D convert(E entity);
}
