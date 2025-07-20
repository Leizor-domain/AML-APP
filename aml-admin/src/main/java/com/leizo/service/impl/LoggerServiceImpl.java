package com.leizo.service.impl;

import com.leizo.model.Alert;
import com.leizo.service.LoggerService;

import java.io.IOException;
import java.util.logging.ConsoleHandler;
import java.util.logging.FileHandler;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;
import java.util.logging.*;


public class LoggerServiceImpl implements LoggerService{

    private final Logger logger;

    public LoggerServiceImpl() {
        logger = Logger.getLogger("AMLEngineLogger");

        try {
            // Write to a rotating file log: logs/amlengine.log
            FileHandler fileHandler = new FileHandler("logs/amlengine.log", true);
            fileHandler.setFormatter(new SimpleFormatter());
            logger.addHandler(fileHandler);

            // Also log to console
            ConsoleHandler consoleHandler = new ConsoleHandler();
            consoleHandler.setFormatter(new SimpleFormatter());
            logger.addHandler(consoleHandler);

            logger.setUseParentHandlers(false); // prevent double logs to console
            logger.setLevel(Level.INFO);

        } catch (IOException e) {
            System.err.println("Failed to set up logger: " + e.getMessage());
        }
    }

    @Override
    public void logRiskScore(String sender, int score) {
        System.out.printf("[Risk] [Sender: %s] Risk Score: %d%n", sender, score);
    }

    @Override
    public void logEvent(String eventType, String actor, String details) {
        String message = String.format("[%s] [%s]: %s", eventType, actor, details);
        logger.info(message);
    }

    @Override
    public void logAlert(Alert alert){
        String message = String.format(
                "[ALERT] ID: %s | Type: %s | Priority: %s | Reason: %s | Timestamp: %s | Sender: %s | Receiver: %s",
                alert.getAlertId(),
                alert.getAlertType(),
                alert.getPriorityLevel(),
                alert.getReason(),
                alert.getTimestamp(),
                alert.getTransaction().getSender(),
                alert.getTransaction().getReceiver()
        );
        logger.info(message);
    }
}
