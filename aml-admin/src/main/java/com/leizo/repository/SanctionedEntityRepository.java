package com.leizo.repository;

import com.leizo.model.SanctionedEntity;
import java.util.List;

/**
 * SanctionedEntityRepository provides a contract for saving and retrieving
 * sanctioned entities in the system.
 *
 * This interface supports modularity and abstraction for different implementations,
 * whether in-memory, database-backed, or file-based storage.
 */
public interface SanctionedEntityRepository {

    /**
     * Persists a sanctioned entity into the repository.
     *
     * @param entity the SanctionedEntity object to be stored
     */
    void saveEntity(SanctionedEntity entity);

    /**
     * Retrieves a list of all stored sanctioned entities.
     *
     * @return List of SanctionedEntity objects
     */
    List<SanctionedEntity> getAllEntities();
}
