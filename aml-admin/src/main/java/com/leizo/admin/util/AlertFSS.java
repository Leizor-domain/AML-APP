package com.leizo.admin.util;

import com.leizo.admin.entity.Alert;
import java.util.ArrayList;
import java.util.List;

public class AlertFSS {

    // Filter alerts by sender and priority level
    public static List<Alert> filter(List<Alert> alerts, String sender, String priorityLevel) {
        List<Alert> filtered = new ArrayList<>();
        for (Alert alert : alerts) {
            boolean matchSender = (sender == null || (alert.getTransaction() != null && alert.getTransaction().getSender().equalsIgnoreCase(sender)));
            boolean matchPriority = (priorityLevel == null || (alert.getPriorityLevel() != null && alert.getPriorityLevel().equalsIgnoreCase(priorityLevel)));
            if (matchSender && matchPriority) {
                filtered.add(alert);
            }
        }
        return filtered;
    }

    // Search alerts by reason keyword (case-insensitive)
    public static List<Alert> searchAlertsByReason(List<Alert> alerts, String keyword) {
        List<Alert> result = new ArrayList<>();
        for (Alert alert : alerts) {
            if (alert.getReason() != null &&
                    alert.getReason().toLowerCase().contains(keyword.toLowerCase())) {
                result.add(alert);
            }
        }
        return result;
    }

    // Merge sort alerts by priority score
    public static Alert[] sortByPriority(Alert[] alerts, boolean descending) {
        mergeSort(alerts, 0, alerts.length - 1, descending);
        return alerts;
    }

    private static void mergeSort(Alert[] arr, int left, int right, boolean descending) {
        if (left < right) {
            int mid = (left + right) / 2;
            mergeSort(arr, left, mid, descending);
            mergeSort(arr, mid + 1, right, descending);
            merge(arr, left, mid, right, descending);
        }
    }

    private static void merge(Alert[] arr, int left, int mid, int right, boolean descending) {
        int n1 = mid - left + 1;
        int n2 = right - mid;
        Alert[] L = new Alert[n1];
        Alert[] R = new Alert[n2];
        for (int i = 0; i < n1; i++) L[i] = arr[left + i];
        for (int j = 0; j < n2; j++) R[j] = arr[mid + 1 + j];
        int i = 0, j = 0, k = left;
        while (i < n1 && j < n2) {
            boolean condition = L[i].getPriorityScore() <= R[j].getPriorityScore();
            if (descending) condition = !condition;
            if (condition) arr[k++] = L[i++];
            else arr[k++] = R[j++];
        }
        while (i < n1) arr[k++] = L[i++];
        while (j < n2) arr[k++] = R[j++];
    }
} 