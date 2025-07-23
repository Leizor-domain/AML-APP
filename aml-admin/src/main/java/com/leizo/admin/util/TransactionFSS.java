package com.leizo.admin.util;

import com.leizo.admin.entity.Transaction;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

public class TransactionFSS {

    // Filter by sender, country, min/max amount
    public static List<Transaction> filter(List<Transaction> transactions,
                                           String sender,
                                           String country,
                                           BigDecimal minAmount,
                                           BigDecimal maxAmount) {
        List<Transaction> result = new ArrayList<>();
        for (Transaction t : transactions) {
            if (sender != null && !t.getSender().equalsIgnoreCase(sender)) continue;
            if (country != null && !t.getCountry().equalsIgnoreCase(country)) continue;
            if (minAmount != null && t.getAmount().compareTo(minAmount) < 0) continue;
            if (maxAmount != null && t.getAmount().compareTo(maxAmount) > 0) continue;
            result.add(t);
        }
        return result;
    }

    // Search by receiver name
    public static List<Transaction> searchByReceiver(List<Transaction> transactions, String receiver) {
        List<Transaction> result = new ArrayList<>();
        for (Transaction t : transactions) {
            if (receiver != null && t.getReceiver().toLowerCase().contains(receiver.toLowerCase())) {
                result.add(t);
            }
        }
        return result;
    }

    // Sort by amount
    public static Transaction[] sortByAmount(List<Transaction> transactions, boolean descending) {
        Transaction[] arr = transactions.toArray(new Transaction[0]);
        mergeSortAmount(arr, 0, arr.length - 1, descending);
        return arr;
    }

    // Sort by risk score
    public static Transaction[] sortByRiskScore(List<Transaction> transactions, boolean descending) {
        Transaction[] arr = transactions.toArray(new Transaction[0]);
        mergeSortRisk(arr, 0, arr.length - 1, descending);
        return arr;
    }

    // --- Merge sort by amount ---
    private static void mergeSortAmount(Transaction[] arr, int left, int right, boolean desc) {
        if (left < right) {
            int mid = (left + right) / 2;
            mergeSortAmount(arr, left, mid, desc);
            mergeSortAmount(arr, mid + 1, right, desc);
            mergeAmount(arr, left, mid, right, desc);
        }
    }

    private static void mergeAmount(Transaction[] arr, int l, int m, int r, boolean desc) {
        int n1 = m - l + 1, n2 = r - m;
        Transaction[] L = new Transaction[n1];
        Transaction[] R = new Transaction[n2];
        for (int i = 0; i < n1; i++) L[i] = arr[l + i];
        for (int j = 0; j < n2; j++) R[j] = arr[m + 1 + j];
        int i = 0, j = 0, k = l;
        while (i < n1 && j < n2) {
            int cmp = L[i].getAmount().compareTo(R[j].getAmount());
            if ((cmp <= 0 && !desc) || (cmp > 0 && desc)) arr[k++] = L[i++];
            else arr[k++] = R[j++];
        }
        while (i < n1) arr[k++] = L[i++];
        while (j < n2) arr[k++] = R[j++];
    }

    // --- Merge sort by risk ---
    private static void mergeSortRisk(Transaction[] arr, int left, int right, boolean desc) {
        if (left < right) {
            int mid = (left + right) / 2;
            mergeSortRisk(arr, left, mid, desc);
            mergeSortRisk(arr, mid + 1, right, desc);
            mergeRisk(arr, left, mid, right, desc);
        }
    }

    private static void mergeRisk(Transaction[] arr, int l, int m, int r, boolean desc) {
        int n1 = m - l + 1, n2 = r - m;
        Transaction[] L = new Transaction[n1];
        Transaction[] R = new Transaction[n2];
        for (int i = 0; i < n1; i++) L[i] = arr[l + i];
        for (int j = 0; j < n2; j++) R[j] = arr[m + 1 + j];
        int i = 0, j = 0, k = l;
        while (i < n1 && j < n2) {
            int cmp = L[i].getRiskScore().compareTo(R[j].getRiskScore());
            if ((cmp <= 0 && !desc) || (cmp > 0 && desc)) arr[k++] = L[i++];
            else arr[k++] = R[j++];
        }
        while (i < n1) arr[k++] = L[i++];
        while (j < n2) arr[k++] = R[j++];
    }
} 