package com.leizo.service;

import com.leizo.pojo.entity.Alert;

import java.util.List;

public interface AlertHistoryService {
    void saveAlert(Alert alert);

    List<Alert> getAllAlerts();


}
