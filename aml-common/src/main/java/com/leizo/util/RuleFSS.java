package com.leizo.util;

import com.leizo.model.Rule;
import com.leizo.enums.RuleSensitivity;

import java.util.ArrayList;
import java.util.List;

public class RuleFSS {

    // Filter rules by sensitivity and tags
    public static List<Rule> filter(List<Rule> rules, RuleSensitivity sensitivity, String tag) {
        List<Rule> filtered = new ArrayList<>();
        for (Rule rule : rules) {
            boolean matchSensitivity = (sensitivity == null || rule.getSensitivity() == sensitivity);
            boolean matchTag = (tag == null || (rule.getTags() != null && rule.getTags().contains(tag)));
            if (matchSensitivity && matchTag) {
                filtered.add(rule);
            }
        }
        return filtered;
    }

    // Search rules by keyword in description
    public static List<Rule> search(List<Rule> rules, String keyword) {
        List<Rule> result = new ArrayList<>();
        for (Rule rule : rules) {
            if (rule.getDescription().toLowerCase().contains(keyword.toLowerCase())) {
                result.add(rule);
            }
        }
        return result;
    }

    // Sort rules by sensitivity (ascending or descending by weight)
    public static Rule[] sortBySensitivity(List<Rule> rules, boolean descending) {
        Rule[] array = rules.toArray(new Rule[0]);

        for (int i = 1; i < array.length; i++) {
            Rule key = array[i];
            int j = i - 1;

            while (j >= 0 && compare(array[j], key, descending)) {
                array[j + 1] = array[j];
                j--;
            }
            array[j + 1] = key;
        }

        return array;
    }

    private static boolean compare(Rule a, Rule b, boolean descending) {
        int weightA = a.getSensitivity().getWeight();
        int weightB = b.getSensitivity().getWeight();
        return descending ? weightA < weightB : weightA > weightB;
    }
}


