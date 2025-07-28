package com.leizo.admin.util;

import com.leizo.pojo.entity.SanctionedEntity;

import java.util.ArrayList;
import java.util.List;

public class SanctionedEntityFSS {

    // Filter by country
    public static List<SanctionedEntity> filterByCountry(List<SanctionedEntity> list, String country) {
        List<SanctionedEntity> result = new ArrayList<>();
        for (SanctionedEntity entity : list) {
            if (entity.getCountry().equalsIgnoreCase(country)) {
                result.add(entity);
            }
        }
        return result;
    }

    // Search by name containing keyword
    public static List<SanctionedEntity> searchByName(List<SanctionedEntity> list, String keyword) {
        List<SanctionedEntity> result = new ArrayList<>();
        for (SanctionedEntity entity : list) {
            if (entity.getName().toLowerCase().contains(keyword.toLowerCase())) {
                result.add(entity);
            }
        }
        return result;
    }

    // Sort by name (A-Z or Z-A)
    public static SanctionedEntity[] sortByName(List<SanctionedEntity> list, boolean descending) {
        SanctionedEntity[] array = list.toArray(new SanctionedEntity[0]);

        for (int i = 1; i < array.length; i++) {
            SanctionedEntity key = array[i];
            int j = i - 1;

            while (j >= 0 && compare(array[j], key, descending)) {
                array[j + 1] = array[j];
                j--;
            }
            array[j + 1] = key;
        }

        return array;
    }

    private static boolean compare(SanctionedEntity a, SanctionedEntity b, boolean descending) {
        int cmp = a.getName().compareToIgnoreCase(b.getName());
        return descending ? cmp < 0 : cmp > 0;
    }
} 