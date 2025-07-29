-- =====================================================
-- AML Application - Alert Injection Script
-- =====================================================
-- This script carefully injects alert transactions into the database
-- Execute this script in your PostgreSQL database
-- =====================================================

-- First, let's check if the alerts table exists and create it if needed
-- (Based on your exact table structure)

CREATE TABLE IF NOT EXISTS alerts (
    id SERIAL PRIMARY KEY,
    alert_id VARCHAR(100) NOT NULL UNIQUE,
    alert_type VARCHAR(50),
    match_reason VARCHAR(255),
    matched_entity_name VARCHAR(255),
    matched_list VARCHAR(255),
    priority_level VARCHAR(20),
    priority_score INTEGER,
    reason TEXT NOT NULL,
    timestamp TIMESTAMP NOT NULL,
    transaction_id INTEGER
);

-- Create index for better performance
CREATE INDEX IF NOT EXISTS idx_alerts_timestamp ON alerts(timestamp DESC);
CREATE INDEX IF NOT EXISTS idx_alerts_priority ON alerts(priority_level);
CREATE INDEX IF NOT EXISTS idx_alerts_type ON alerts(alert_type);

-- =====================================================
-- CLEAR EXISTING ALERTS (Optional - Uncomment if needed)
-- =====================================================
-- DELETE FROM alerts;
-- ALTER SEQUENCE alerts_id_seq RESTART WITH 1;

-- =====================================================
-- INJECT HIGH-PRIORITY ALERTS
-- =====================================================

-- 1. High Value Transaction Alerts
INSERT INTO alerts (alert_id, alert_type, match_reason, matched_entity_name, matched_list, priority_level, priority_score, reason, timestamp, transaction_id) VALUES
('ALERT-001', 'HIGH_VALUE', 'Amount exceeds $100,000 threshold', 'John Smith', 'High Value Transactions', 'HIGH', 95, 'High value transaction detected: $150,000 USD transfer to offshore account', '2024-07-29 10:30:00', 1001),
('ALERT-002', 'HIGH_VALUE', 'Transfer to high-risk jurisdiction', 'Maria Rodriguez', 'High Value Transactions', 'HIGH', 92, 'Suspicious high-value transfer: $250,000 USD to known risk country', '2024-07-29 11:15:00', 1002),
('ALERT-003', 'STRUCTURING', 'Multiple deposits under $10,000 limit', 'David Chen', 'Structuring Detection', 'HIGH', 88, 'Large cash deposit: $75,000 USD in multiple small transactions', '2024-07-29 12:00:00', 1003);

-- 2. Sanctions Screening Alerts
INSERT INTO alerts (alert_id, alert_type, match_reason, matched_entity_name, matched_list, priority_level, priority_score, reason, timestamp, transaction_id) VALUES
('ALERT-004', 'SANCTIONS', 'Exact name match on sanctions list', 'Ahmed Al-Zahawi', 'OFAC SDN List', 'HIGH', 100, 'Sanctioned entity match: Transaction involving OFAC-listed individual', '2024-07-29 13:45:00', 1004),
('ALERT-005', 'SANCTIONS', 'Destination country under sanctions', 'Iranian Trading Corp', 'High Risk Countries', 'HIGH', 98, 'High-risk country transaction: Transfer to sanctioned jurisdiction', '2024-07-29 14:20:00', 1005),
('ALERT-006', 'PEP', 'Government official transaction', 'Minister Sarah Johnson', 'PEP Database', 'HIGH', 85, 'PEP transaction detected: Politically exposed person involved', '2024-07-29 15:10:00', 1006);

-- 3. Behavioral Pattern Alerts
INSERT INTO alerts (alert_id, alert_type, match_reason, matched_entity_name, matched_list, priority_level, priority_score, reason, timestamp, transaction_id) VALUES
('ALERT-007', 'BEHAVIORAL', 'Multiple rapid transfers detected', 'Robert Wilson', 'Behavioral Analysis', 'MEDIUM', 75, 'Unusual transaction pattern: Rapid transfers to multiple accounts', '2024-07-29 16:30:00', 1007),
('ALERT-008', 'BEHAVIORAL', 'Transactions outside normal hours', 'Lisa Thompson', 'Behavioral Analysis', 'MEDIUM', 70, 'Suspicious activity: Unusual transaction timing and frequency', '2024-07-29 17:15:00', 1008),
('ALERT-009', 'STRUCTURING', 'Multiple small deposits pattern', 'Michael Brown', 'Structuring Detection', 'HIGH', 82, 'Pattern detection: Structuring behavior over 30-day period', '2024-07-29 18:00:00', 1009);

-- 4. Geographic Risk Alerts
INSERT INTO alerts (alert_id, alert_type, match_reason, matched_entity_name, matched_list, priority_level, priority_score, reason, timestamp, transaction_id) VALUES
('ALERT-010', 'GEOGRAPHIC', 'Destination in FATF grey list', 'Pakistan Trading Ltd', 'FATF Grey List', 'MEDIUM', 65, 'High-risk country: Transaction to FATF grey-listed country', '2024-07-29 19:30:00', 1010),
('ALERT-011', 'SANCTIONS', 'Destination under comprehensive sanctions', 'North Korea Import Co', 'Sanctioned Countries', 'HIGH', 95, 'Sanctioned country: Transfer to embargoed jurisdiction', '2024-07-29 20:15:00', 1011),
('ALERT-012', 'GEOGRAPHIC', 'Transfer to conflict-affected area', 'Syrian Relief Fund', 'High Risk Regions', 'MEDIUM', 60, 'High-risk region: Transaction to conflict zone', '2024-07-29 21:00:00', 1012);

-- 5. Money Laundering Risk Alerts
INSERT INTO alerts (alert_id, alert_type, match_reason, matched_entity_name, matched_list, priority_level, priority_score, reason, timestamp, transaction_id) VALUES
('ALERT-013', 'MONEY_LAUNDERING', 'Multiple shell company involvement', 'Global Trading Network', 'ML Risk Assessment', 'HIGH', 90, 'Suspicious money laundering indicators: Complex transaction chain', '2024-07-29 22:30:00', 1013),
('ALERT-014', 'MONEY_LAUNDERING', 'MSB transaction pattern', 'QuickCash Express', 'High Risk Businesses', 'MEDIUM', 55, 'High-risk business: Transaction with money service business', '2024-07-29 23:15:00', 1014),
('ALERT-015', 'MONEY_LAUNDERING', 'Unverified large cash deposit', 'Anonymous Depositor', 'Source of Funds', 'HIGH', 78, 'Suspicious source: Large cash deposit from unknown source', '2024-07-30 00:00:00', 1015);

-- =====================================================
-- INJECT MEDIUM-PRIORITY ALERTS
-- =====================================================

-- 6. Medium Priority Alerts
INSERT INTO alerts (alert_id, alert_type, match_reason, matched_entity_name, matched_list, priority_level, priority_score, reason, timestamp, transaction_id) VALUES
('ALERT-016', 'UNUSUAL_AMOUNT', 'Suspicious round number amount', 'Jennifer Davis', 'Transaction Analysis', 'MEDIUM', 45, 'Unusual transaction amount: Round number transfer', '2024-07-30 01:30:00', 1016),
('ALERT-017', 'NEW_ACCOUNT', 'Large transaction on account < 30 days old', 'Thomas Anderson', 'New Account Monitoring', 'MEDIUM', 50, 'New account activity: High-value transaction on new account', '2024-07-30 02:15:00', 1017),
('ALERT-018', 'CROSS_BORDER', 'First-time international transfer', 'European Trading Co', 'International Transfers', 'MEDIUM', 40, 'Cross-border transfer: International wire to new beneficiary', '2024-07-30 03:00:00', 1018),
('ALERT-019', 'CURRENCY_CONVERSION', 'Large currency conversion', 'Forex Trading Ltd', 'Currency Operations', 'MEDIUM', 35, 'Currency conversion: Large amount converted to foreign currency', '2024-07-30 04:30:00', 1019),
('ALERT-020', 'TIMING', 'Transaction at unusual hour', 'Night Owl Trading', 'Transaction Timing', 'MEDIUM', 30, 'Unusual timing: Transaction outside business hours', '2024-07-30 05:15:00', 1020);

-- =====================================================
-- INJECT LOW-PRIORITY ALERTS
-- =====================================================

-- 7. Low Priority Alerts
INSERT INTO alerts (alert_id, alert_type, match_reason, matched_entity_name, matched_list, priority_level, priority_score, reason, timestamp, transaction_id) VALUES
('ALERT-021', 'THRESHOLD', 'Transaction 10% above normal', 'Small Business Inc', 'Threshold Monitoring', 'LOW', 25, 'Minor threshold breach: Transaction slightly above normal limit', '2024-07-30 06:00:00', 1021),
('ALERT-022', 'FREQUENCY', 'Multiple transactions in 1 hour', 'Online Retailer', 'Frequency Analysis', 'LOW', 20, 'Frequency alert: Multiple small transactions in short period', '2024-07-30 07:30:00', 1022),
('ALERT-023', 'GEOGRAPHIC', 'Transaction from new location', 'Traveling Customer', 'Location Monitoring', 'LOW', 15, 'Geographic anomaly: Transaction from unusual location', '2024-07-30 08:15:00', 1023),
('ALERT-024', 'DEVICE', 'First transaction from new device', 'Mobile User', 'Device Monitoring', 'LOW', 10, 'Device alert: Transaction from new device', '2024-07-30 09:00:00', 1024),
('ALERT-025', 'PATTERN', 'Slight deviation from normal pattern', 'Regular Customer', 'Behavioral Analysis', 'LOW', 5, 'Pattern alert: Minor deviation from normal behavior', '2024-07-30 10:30:00', 1025);

-- =====================================================
-- INJECT RECENT ALERTS (Last 24 hours)
-- =====================================================

-- 8. Recent High-Priority Alerts
INSERT INTO alerts (alert_id, alert_type, match_reason, matched_entity_name, matched_list, priority_level, priority_score, reason, timestamp, transaction_id) VALUES
('ALERT-026', 'CASH_WITHDRAWAL', 'Large ATM withdrawal', 'Emergency Withdrawal', 'Cash Operations', 'HIGH', 85, 'URGENT: Large cash withdrawal: $50,000 USD from ATM', '2024-07-30 11:00:00', 1026),
('ALERT-027', 'SANCTIONS', 'Direct match on sanctions list', 'Banned Entity Corp', 'OFAC SDN List', 'HIGH', 100, 'CRITICAL: Sanctioned entity transaction detected', '2024-07-30 11:30:00', 1027),
('ALERT-028', 'STRUCTURING', 'Confirmed structuring behavior', 'Structuring Suspect', 'Structuring Detection', 'HIGH', 90, 'HIGH RISK: Structuring pattern confirmed', '2024-07-30 12:00:00', 1028),
('ALERT-029', 'INTERNATIONAL', 'Multiple international transfers', 'International Trader', 'International Monitoring', 'HIGH', 80, 'SUSPICIOUS: Unusual international transfer pattern', '2024-07-30 12:30:00', 1029),
('ALERT-030', 'OFFSHORE', 'Transfer to offshore jurisdiction', 'Offshore Account Holder', 'Offshore Monitoring', 'HIGH', 95, 'ALERT: High-value transaction to offshore account', '2024-07-30 13:00:00', 1030);

-- =====================================================
-- VERIFICATION QUERIES
-- =====================================================

-- Check total number of alerts inserted
SELECT 'Total Alerts' as metric, COUNT(*) as count FROM alerts
UNION ALL
SELECT 'High Priority Alerts', COUNT(*) FROM alerts WHERE priority_level = 'HIGH'
UNION ALL
SELECT 'Medium Priority Alerts', COUNT(*) FROM alerts WHERE priority_level = 'MEDIUM'
UNION ALL
SELECT 'Low Priority Alerts', COUNT(*) FROM alerts WHERE priority_level = 'LOW';

-- Check alerts by type
SELECT alert_type, COUNT(*) as count
FROM alerts
GROUP BY alert_type
ORDER BY count DESC;

-- Check recent alerts (last 24 hours)
SELECT alert_id, reason, priority_level, timestamp
FROM alerts
WHERE timestamp >= NOW() - INTERVAL '24 hours'
ORDER BY timestamp DESC;

-- =====================================================
-- SCRIPT COMPLETION MESSAGE
-- =====================================================

DO $$
BEGIN
    RAISE NOTICE 'Alert injection script completed successfully!';
    RAISE NOTICE 'Total alerts inserted: %', (SELECT COUNT(*) FROM alerts);
    RAISE NOTICE 'High priority alerts: %', (SELECT COUNT(*) FROM alerts WHERE priority_level = 'HIGH');
    RAISE NOTICE 'Medium priority alerts: %', (SELECT COUNT(*) FROM alerts WHERE priority_level = 'MEDIUM');
    RAISE NOTICE 'Low priority alerts: %', (SELECT COUNT(*) FROM alerts WHERE priority_level = 'LOW');
END $$;
