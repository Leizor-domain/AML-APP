package com.leizo.util.test;

import com.leizo.model.SanctionedEntity;
import com.leizo.util.SanctionedEntityFSS;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;

class SanctionedEntityFSSUtilTest {

    private List<SanctionedEntity> entities;

    @BeforeEach
    void setUp() {
        entities = new ArrayList<>();
        entities.add(new SanctionedEntity("Ali Khan", "Russia", "1980-05-10", "UN"));
        entities.add(new SanctionedEntity("Maria Petrova", "Russia", "1975-11-21", "EU"));
        entities.add(new SanctionedEntity("John Doe", "USA", "1990-03-15", "OFAC"));
    }

    @Test
    void testFilterByCountry() {
        List<SanctionedEntity> filtered = SanctionedEntityFSS.filterByCountry(entities, "Russia");
        assertEquals(2, filtered.size());

        filtered = SanctionedEntityFSS.filterByCountry(entities, "USA");
        assertEquals(1, filtered.size());
    }

    @Test
    void testSearchByName() {
        List<SanctionedEntity> results = SanctionedEntityFSS.searchByName(entities, "Ali");
        assertEquals(1, results.size());
        assertEquals("Ali Khan", results.get(0).getName());

        results = SanctionedEntityFSS.searchByName(entities, "Petrova");
        assertEquals(1, results.size());
    }

    @Test
    void testSortByName_Ascending() {
        SanctionedEntity[] sorted = SanctionedEntityFSS.sortByName(entities, false);
        assertTrue(sorted[0].getName().compareToIgnoreCase(sorted[1].getName()) <= 0);
        assertTrue(sorted[1].getName().compareToIgnoreCase(sorted[2].getName()) <= 0);
    }

    @Test
    void testSortByName_Descending() {
        SanctionedEntity[] sorted = SanctionedEntityFSS.sortByName(entities, true);
        assertTrue(sorted[0].getName().compareToIgnoreCase(sorted[1].getName()) >= 0);
        assertTrue(sorted[1].getName().compareToIgnoreCase(sorted[2].getName()) >= 0);
    }
}
