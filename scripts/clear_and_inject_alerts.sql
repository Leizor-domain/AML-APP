-- =====================================================
-- AML Application - Clear and Inject Alerts Script
-- =====================================================
-- This script clears existing alerts and injects new ones
-- WARNING: This will delete all existing alerts!
-- =====================================================

-- Clear all existing alerts
DELETE FROM alerts;
ALTER SEQUENCE alerts_id_seq RESTART WITH 1;

-- Now run the main injection script
\i scripts/inject_alerts.sql
