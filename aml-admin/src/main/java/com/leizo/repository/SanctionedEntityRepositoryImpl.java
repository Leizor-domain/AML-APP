package com.leizo.repository;

import com.leizo.model.SanctionedEntity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import javax.sql.DataSource;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * SanctionedEntityRepositoryImpl provides a PostgreSQL-backed implementation of the
 * SanctionedEntityRepository interface. It allows storing and retrieving sanctioned entities
 * using Spring Boot's injected DataSource for database access.
 */
@Repository
public class SanctionedEntityRepositoryImpl implements SanctionedEntityRepository {

    @Autowired
    private DataSource dataSource;

    private static final String INSERT_SQL =
            "INSERT INTO sanctioned_entities (name, country, dob, sanctioning_body) VALUES (?, ?, ?, ?)";

    private static final String SELECT_ALL_SQL =
            "SELECT name, country, dob, sanctioning_body FROM sanctioned_entities";

    /**
     * Saves a sanctioned entity into the database.
     *
     * @param entity the sanctioned entity to persist
     */
    @Override
    public void saveEntity(SanctionedEntity entity) {
        try (Connection conn = dataSource.getConnection();
             PreparedStatement stmt = conn.prepareStatement(INSERT_SQL)) {

            stmt.setString(1, entity.getName());
            stmt.setString(2, entity.getCountry());
            stmt.setString(3, entity.getDob());
            stmt.setString(4, entity.getSanctioningBody());

            stmt.executeUpdate();
            System.out.println("[DB] Sanctioned entity saved: " + entity.getName());

        } catch (SQLException e) {
            System.err.println("[DB ERROR] Failed to save sanctioned entity: " + e.getMessage());
        }
    }

    /**
     * Retrieves all sanctioned entities from the database.
     *
     * @return a list of sanctioned entities
     */
    @Override
    public List<SanctionedEntity> getAllEntities() {
        List<SanctionedEntity> entities = new ArrayList<>();
        try (Connection conn = dataSource.getConnection();
             PreparedStatement stmt = conn.prepareStatement(SELECT_ALL_SQL);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                String name = rs.getString("name");
                String country = rs.getString("country");
                String dob = rs.getString("dob");
                String source = rs.getString("sanctioning_body");

                entities.add(new SanctionedEntity(name, country, dob, source));
            }

        } catch (SQLException e) {
            System.err.println("[DB ERROR] Failed to load sanctioned entities: " + e.getMessage());
        }
        return entities;
    }
}
