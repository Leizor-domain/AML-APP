package com.leizo.util.test;

import com.leizo.enums.RuleSensitivity;
import com.leizo.model.Rule;
import com.leizo.util.RuleFSS;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;

class RuleFSSUtilTest {

    private List<Rule> rules;

    @BeforeEach
    void setUp() {
        rules = new ArrayList<>();
        rules.add(new Rule("High Transfer", RuleSensitivity.HIGH, null, Set.of("money", "urgent")));
        rules.add(new Rule("Low Value Transfer", RuleSensitivity.LOW, null, Set.of("routine")));
        rules.add(new Rule("Medium Region Risk", RuleSensitivity.MEDIUM, null, Set.of("geo")));
    }

    @Test
    void testFilter_BySensitivityAndTag() {
        List<Rule> filtered = RuleFSS.filter(rules, RuleSensitivity.HIGH, "money");
        assertEquals(1, filtered.size());
        assertEquals("High Transfer", filtered.get(0).getDescription());
    }

    @Test
    void testFilter_BySensitivityOnly() {
        List<Rule> filtered = RuleFSS.filter(rules, RuleSensitivity.LOW, null);
        assertEquals(1, filtered.size());
    }

    @Test
    void testFilter_ByTagOnly() {
        List<Rule> filtered = RuleFSS.filter(rules, null, "geo");
        assertEquals(1, filtered.size());
    }

    @Test
    void testSearch_ByDescription() {
        List<Rule> found = RuleFSS.search(rules, "Transfer");
        assertEquals(2, found.size());

        List<Rule> foundSingle = RuleFSS.search(rules, "Medium");
        assertEquals(1, foundSingle.size());
    }

    @Test
    void testSortBySensitivity_Ascending() {
        Rule[] sorted = RuleFSS.sortBySensitivity(rules, false);
        assertEquals(RuleSensitivity.LOW, sorted[0].getSensitivity());
        assertEquals(RuleSensitivity.MEDIUM, sorted[1].getSensitivity());
        assertEquals(RuleSensitivity.HIGH, sorted[2].getSensitivity());
    }

    @Test
    void testSortBySensitivity_Descending() {
        Rule[] sorted = RuleFSS.sortBySensitivity(rules, true);
        assertEquals(RuleSensitivity.HIGH, sorted[0].getSensitivity());
        assertEquals(RuleSensitivity.MEDIUM, sorted[1].getSensitivity());
        assertEquals(RuleSensitivity.LOW, sorted[2].getSensitivity());
    }
}
