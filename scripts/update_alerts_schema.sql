-- =====================================================
-- AML Application - Alerts Table Schema Update
-- =====================================================
-- This script updates the alerts table to match frontend expectations
-- Adds missing columns and removes unnecessary ones
-- =====================================================

-- Step 1: Backup existing data (if any)
-- CREATE TABLE alerts_backup AS SELECT * FROM alerts;

-- Step 2: Drop existing table and recreate with correct schema
DROP TABLE IF EXISTS alerts CASCADE;

-- Step 3: Create new alerts table with frontend-compatible schema
CREATE TABLE alerts (
    id SERIAL PRIMARY KEY,
    alert_id VARCHAR(100) NOT NULL UNIQUE,
    alert_type VARCHAR(50),
    reason TEXT NOT NULL,
    description TEXT,
    timestamp TIMESTAMP NOT NULL,
    transaction_id INTEGER,

    -- Frontend-expected fields
    risk_level VARCHAR(20),
    status VARCHAR(20) DEFAULT 'OPEN',
    matched_rules TEXT[],
    sanction_flags TEXT[],

    -- Additional useful fields
    matched_entity_name VARCHAR(255),
    priority_level VARCHAR(20),
    priority_score INTEGER,
    match_reason VARCHAR(255),
    matched_list VARCHAR(255)
);

-- Step 4: Create indexes for better performance
CREATE INDEX idx_alerts_timestamp ON alerts(timestamp DESC);
CREATE INDEX idx_alerts_risk_level ON alerts(risk_level);
CREATE INDEX idx_alerts_status ON alerts(status);
CREATE INDEX idx_alerts_type ON alerts(alert_type);
CREATE INDEX idx_alerts_priority ON alerts(priority_level);

-- Step 5: Insert updated mock data with all required fields
INSERT INTO alerts (
    alert_id, alert_type, reason, description, timestamp, transaction_id,
    risk_level, status, matched_rules, sanction_flags,
    matched_entity_name, priority_level, priority_score, match_reason, matched_list
) VALUES
-- High Priority Alerts
('ALERT-001', 'HIGH_VALUE', 'High value transaction detected: $150,000 USD transfer to offshore account', 'Large transaction exceeding threshold limits', '2024-07-29 10:30:00', 1001, 'HIGH', 'OPEN', ARRAY['Amount Threshold', 'High Value Monitoring'], ARRAY['High Risk'], 'John Smith', 'HIGH', 95, 'Amount exceeds $100,000 threshold', 'High Value Transactions'),

('ALERT-002', 'SANCTIONS', 'Sanctioned entity match: Transaction involving OFAC-listed individual', 'Exact name match found on sanctions database', '2024-07-29 11:15:00', 1002, 'HIGH', 'OPEN', ARRAY['Sanctions Screening', 'OFAC Check'], ARRAY['OFAC', 'SDN List'], 'Ahmed Al-Zahawi', 'HIGH', 100, 'Exact name match on sanctions list', 'OFAC SDN List'),

('ALERT-003', 'STRUCTURING', 'Large cash deposit: $75,000 USD in multiple small transactions', 'Suspicious structuring behavior detected', '2024-07-29 12:00:00', 1003, 'HIGH', 'IN_REVIEW', ARRAY['Structuring Detection', 'Cash Monitoring'], ARRAY['Structuring'], 'David Chen', 'HIGH', 88, 'Multiple deposits under $10,000 limit', 'Structuring Detection'),

('ALERT-004', 'MONEY_LAUNDERING', 'Suspicious money laundering indicators: Complex transaction chain', 'Multiple shell company involvement detected', '2024-07-29 13:45:00', 1004, 'HIGH', 'OPEN', ARRAY['Money Laundering Risk', 'Transaction Chain Analysis'], ARRAY['ML Risk'], 'Global Trading Network', 'HIGH', 90, 'Multiple shell company involvement', 'ML Risk Assessment'),

('ALERT-005', 'PEP', 'PEP transaction detected: Politically exposed person involved', 'Government official transaction flagged', '2024-07-29 14:20:00', 1005, 'HIGH', 'OPEN', ARRAY['PEP Screening', 'Political Risk'], ARRAY['PEP'], 'Minister Sarah Johnson', 'HIGH', 85, 'Government official transaction', 'PEP Database'),

-- Medium Priority Alerts
('ALERT-006', 'BEHAVIORAL', 'Unusual transaction pattern: Rapid transfers to multiple accounts', 'Behavioral anomaly detected', '2024-07-29 15:10:00', 1006, 'MEDIUM', 'OPEN', ARRAY['Behavioral Analysis', 'Pattern Detection'], ARRAY['Behavioral'], 'Robert Wilson', 'MEDIUM', 75, 'Multiple rapid transfers detected', 'Behavioral Analysis'),

('ALERT-007', 'GEOGRAPHIC', 'High-risk country: Transaction to FATF grey-listed country', 'Geographic risk assessment triggered', '2024-07-29 16:30:00', 1007, 'MEDIUM', 'OPEN', ARRAY['Geographic Risk', 'FATF Monitoring'], ARRAY['FATF Grey List'], 'Pakistan Trading Ltd', 'MEDIUM', 65, 'Destination in FATF grey list', 'FATF Grey List'),

('ALERT-008', 'CROSS_BORDER', 'Cross-border transfer: International wire to new beneficiary', 'First-time international transfer detected', '2024-07-29 17:15:00', 1008, 'MEDIUM', 'IN_REVIEW', ARRAY['Cross Border Monitoring', 'International Transfer'], ARRAY['International'], 'European Trading Co', 'MEDIUM', 40, 'First-time international transfer', 'International Transfers'),

('ALERT-009', 'NEW_ACCOUNT', 'New account activity: High-value transaction on new account', 'Large transaction on account < 30 days old', '2024-07-29 18:00:00', 1009, 'MEDIUM', 'OPEN', ARRAY['New Account Monitoring', 'Account Age Check'], ARRAY['New Account'], 'Thomas Anderson', 'MEDIUM', 50, 'Large transaction on account < 30 days old', 'New Account Monitoring'),

('ALERT-010', 'CURRENCY_CONVERSION', 'Currency conversion: Large amount converted to foreign currency', 'Unusual currency conversion pattern', '2024-07-29 19:30:00', 1010, 'MEDIUM', 'OPEN', ARRAY['Currency Conversion', 'Forex Monitoring'], ARRAY['Currency'], 'Forex Trading Ltd', 'MEDIUM', 35, 'Large currency conversion', 'Currency Operations'),

-- Low Priority Alerts
('ALERT-011', 'THRESHOLD', 'Minor threshold breach: Transaction slightly above normal limit', 'Threshold monitoring alert', '2024-07-29 20:15:00', 1011, 'LOW', 'OPEN', ARRAY['Threshold Monitoring', 'Limit Check'], ARRAY['Threshold'], 'Small Business Inc', 'LOW', 25, 'Transaction 10% above normal', 'Threshold Monitoring'),

('ALERT-012', 'FREQUENCY', 'Frequency alert: Multiple small transactions in short period', 'Transaction frequency anomaly', '2024-07-29 21:00:00', 1012, 'LOW', 'RESOLVED', ARRAY['Frequency Analysis', 'Transaction Count'], ARRAY['Frequency'], 'Online Retailer', 'LOW', 20, 'Multiple transactions in 1 hour', 'Frequency Analysis'),

('ALERT-013', 'GEOGRAPHIC', 'Geographic anomaly: Transaction from unusual location', 'Location-based risk assessment', '2024-07-29 22:30:00', 1013, 'LOW', 'OPEN', ARRAY['Location Monitoring', 'Geographic Risk'], ARRAY['Location'], 'Traveling Customer', 'LOW', 15, 'Transaction from new location', 'Location Monitoring'),

('ALERT-014', 'DEVICE', 'Device alert: Transaction from new device', 'New device detection', '2024-07-29 23:15:00', 1014, 'LOW', 'DISMISSED', ARRAY['Device Monitoring', 'Device Check'], ARRAY['Device'], 'Mobile User', 'LOW', 10, 'First transaction from new device', 'Device Monitoring'),

('ALERT-015', 'PATTERN', 'Pattern alert: Minor deviation from normal behavior', 'Behavioral pattern analysis', '2024-07-30 00:00:00', 1015, 'LOW', 'OPEN', ARRAY['Behavioral Analysis', 'Pattern Detection'], ARRAY['Pattern'], 'Regular Customer', 'LOW', 5, 'Slight deviation from normal pattern', 'Behavioral Analysis'),

-- Recent Alerts (Last 24 hours)
('ALERT-016', 'CASH_WITHDRAWAL', 'URGENT: Large cash withdrawal: $50,000 USD from ATM', 'Emergency cash withdrawal detected', '2024-07-30 01:30:00', 1016, 'HIGH', 'OPEN', ARRAY['Cash Operations', 'ATM Monitoring'], ARRAY['Cash', 'Emergency'], 'Emergency Withdrawal', 'HIGH', 85, 'Large ATM withdrawal', 'Cash Operations'),

('ALERT-017', 'SANCTIONS', 'Sanctioned country: Transfer to embargoed jurisdiction', 'Country-based sanctions violation', '2024-07-30 02:15:00', 1017, 'HIGH', 'OPEN', ARRAY['Sanctions Screening', 'Country Check'], ARRAY['Sanctions', 'Embargo'], 'North Korea Import Co', 'HIGH', 95, 'Destination under comprehensive sanctions', 'Sanctioned Countries'),

('ALERT-018', 'MONEY_LAUNDERING', 'Suspicious source: Large cash deposit from unknown source', 'Source of funds investigation required', '2024-07-30 03:00:00', 1018, 'HIGH', 'IN_REVIEW', ARRAY['Source of Funds', 'Cash Monitoring'], ARRAY['ML Risk', 'Unknown Source'], 'Anonymous Depositor', 'HIGH', 78, 'Unverified large cash deposit', 'Source of Funds'),

('ALERT-019', 'BEHAVIORAL', 'Suspicious activity: Unusual transaction timing and frequency', 'Behavioral risk assessment', '2024-07-30 04:30:00', 1019, 'MEDIUM', 'OPEN', ARRAY['Behavioral Analysis', 'Timing Analysis'], ARRAY['Behavioral'], 'Lisa Thompson', 'MEDIUM', 70, 'Transactions outside normal hours', 'Behavioral Analysis'),

('ALERT-020', 'HIGH_RISK_BUSINESS', 'High-risk business: Transaction with money service business', 'Business risk assessment', '2024-07-30 05:15:00', 1020, 'MEDIUM', 'OPEN', ARRAY['Business Risk', 'MSB Monitoring'], ARRAY['High Risk Business'], 'QuickCash Express', 'MEDIUM', 55, 'MSB transaction pattern', 'High Risk Businesses'),

-- Additional alerts for comprehensive testing
('ALERT-021', 'UNUSUAL_AMOUNT', 'Unusual transaction amount: Round number transfer', 'Amount pattern analysis', '2024-07-30 06:00:00', 1021, 'MEDIUM', 'OPEN', ARRAY['Transaction Analysis', 'Amount Pattern'], ARRAY['Unusual'], 'Jennifer Davis', 'MEDIUM', 45, 'Suspicious round number amount', 'Transaction Analysis'),

('ALERT-022', 'TIMING', 'Unusual timing: Transaction outside business hours', 'Timing-based risk assessment', '2024-07-30 07:30:00', 1022, 'MEDIUM', 'RESOLVED', ARRAY['Transaction Timing', 'Business Hours'], ARRAY['Timing'], 'Night Owl Trading', 'MEDIUM', 30, 'Transaction at unusual hour', 'Transaction Timing'),

('ALERT-023', 'STRUCTURING', 'Pattern detection: Structuring behavior over 30-day period', 'Extended pattern analysis', '2024-07-30 08:15:00', 1023, 'HIGH', 'OPEN', ARRAY['Structuring Detection', 'Long-term Analysis'], ARRAY['Structuring'], 'Michael Brown', 'HIGH', 82, 'Multiple small deposits pattern', 'Structuring Detection'),

('ALERT-024', 'GEOGRAPHIC', 'High-risk region: Transaction to conflict zone', 'Regional risk assessment', '2024-07-30 09:00:00', 1024, 'MEDIUM', 'OPEN', ARRAY['Geographic Risk', 'Conflict Zone'], ARRAY['High Risk Region'], 'Syrian Relief Fund', 'MEDIUM', 60, 'Transfer to conflict-affected area', 'High Risk Regions'),

('ALERT-025', 'SANCTIONS', 'High-risk country transaction: Transfer to sanctioned jurisdiction', 'Country sanctions violation', '2024-07-30 10:30:00', 1025, 'HIGH', 'OPEN', ARRAY['Sanctions Screening', 'Country Risk'], ARRAY['Sanctions'], 'Iranian Trading Corp', 'HIGH', 98, 'Destination country under sanctions', 'High Risk Countries'),

-- More recent alerts for dashboard testing
('ALERT-026', 'HIGH_VALUE', 'Suspicious high-value transfer: $250,000 USD to known risk country', 'High value with geographic risk', '2024-07-30 11:00:00', 1026, 'HIGH', 'OPEN', ARRAY['High Value Monitoring', 'Geographic Risk'], ARRAY['High Risk'], 'Maria Rodriguez', 'HIGH', 92, 'Transfer to high-risk jurisdiction', 'High Value Transactions'),

('ALERT-027', 'PEP', 'PEP transaction: High-ranking government official involved', 'Political exposure risk', '2024-07-30 12:30:00', 1027, 'HIGH', 'IN_REVIEW', ARRAY['PEP Screening', 'Political Risk'], ARRAY['PEP'], 'Senator John Smith', 'HIGH', 90, 'High-ranking government official', 'PEP Database'),

('ALERT-028', 'MONEY_LAUNDERING', 'Complex transaction chain: Multiple jurisdictions involved', 'Multi-jurisdictional ML risk', '2024-07-30 13:15:00', 1028, 'HIGH', 'OPEN', ARRAY['Money Laundering Risk', 'Transaction Chain'], ARRAY['ML Risk', 'Complex'], 'International Trading Corp', 'HIGH', 88, 'Multiple jurisdictions involved', 'ML Risk Assessment'),

('ALERT-029', 'BEHAVIORAL', 'Unusual customer behavior: First-time large transaction', 'Behavioral anomaly detection', '2024-07-30 14:00:00', 1029, 'MEDIUM', 'OPEN', ARRAY['Behavioral Analysis', 'Customer History'], ARRAY['Behavioral'], 'New Customer LLC', 'MEDIUM', 65, 'First-time large transaction', 'Behavioral Analysis'),

('ALERT-030', 'CASH_DEPOSIT', 'Large cash deposit: $100,000 USD from unknown source', 'Cash deposit risk assessment', '2024-07-30 15:45:00', 1030, 'HIGH', 'OPEN', ARRAY['Cash Operations', 'Source of Funds'], ARRAY['Cash', 'Unknown Source'], 'Cash Depositor', 'HIGH', 80, 'Large cash deposit from unknown source', 'Cash Operations');

-- Step 6: Verify the data
SELECT
    COUNT(*) as total_alerts,
    COUNT(CASE WHEN risk_level = 'HIGH' THEN 1 END) as high_risk_alerts,
    COUNT(CASE WHEN status = 'OPEN' THEN 1 END) as open_alerts,
    COUNT(CASE WHEN status = 'IN_REVIEW' THEN 1 END) as in_review_alerts
FROM alerts;

-- Step 7: Show sample data structure
SELECT
    alert_id,
    alert_type,
    risk_level,
    status,
    reason,
    matched_rules,
    sanction_flags,
    timestamp
FROM alerts
ORDER BY timestamp DESC
LIMIT 5;

-- =====================================================
-- SCHEMA UPDATE COMPLETE
-- =====================================================
-- The alerts table now includes all fields expected by the frontend:
-- - risk_level (maps to frontend riskLevel)
-- - status (maps to frontend status)
-- - description (maps to frontend description)
-- - matched_rules (maps to frontend matchedRules)
-- - sanction_flags (maps to frontend sanctionFlags)
-- - Plus all original fields for backward compatibility
-- =====================================================
