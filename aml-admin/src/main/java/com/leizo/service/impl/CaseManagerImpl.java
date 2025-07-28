package com.leizo.service.impl;

import com.leizo.pojo.entity.Alert;
import com.leizo.service.CaseManager;
import org.springframework.stereotype.Service;

@Service
public class CaseManagerImpl implements CaseManager {

    public void reviewAlert(Alert alert) {
        System.out.println("[CaseManager] Assigned alert for case review: " + alert.getAlertId());
    }
}
