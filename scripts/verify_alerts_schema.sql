-- =====================================================
-- AML Application - Alerts Schema Verification
-- =====================================================
-- Run this script after executing update_alerts_schema.sql
-- to verify the schema and data are correct
-- =====================================================

-- 1. Check table structure
\d alerts;

-- 2. Count total alerts
SELECT COUNT(*) as total_alerts FROM alerts;

-- 3. Check risk level distribution
SELECT
    risk_level,
    COUNT(*) as count,
    ROUND(COUNT(*) * 100.0 / (SELECT COUNT(*) FROM alerts), 2) as percentage
FROM alerts
GROUP BY risk_level
ORDER BY count DESC;

-- 4. Check status distribution
SELECT
    status,
    COUNT(*) as count,
    ROUND(COUNT(*) * 100.0 / (SELECT COUNT(*) FROM alerts), 2) as percentage
FROM alerts
GROUP BY status
ORDER BY count DESC;

-- 5. Check alert types
SELECT
    alert_type,
    COUNT(*) as count
FROM alerts
GROUP BY alert_type
ORDER BY count DESC;

-- 6. Show recent alerts (last 5)
SELECT
    alert_id,
    alert_type,
    risk_level,
    status,
    LEFT(reason, 50) as reason_preview,
    timestamp
FROM alerts
ORDER BY timestamp DESC
LIMIT 5;

-- 7. Check for frontend-required fields
SELECT
    'risk_level' as field_name,
    COUNT(CASE WHEN risk_level IS NOT NULL THEN 1 END) as non_null_count,
    COUNT(*) as total_count
FROM alerts
UNION ALL
SELECT
    'status' as field_name,
    COUNT(CASE WHEN status IS NOT NULL THEN 1 END) as non_null_count,
    COUNT(*) as total_count
FROM alerts
UNION ALL
SELECT
    'description' as field_name,
    COUNT(CASE WHEN description IS NOT NULL THEN 1 END) as non_null_count,
    COUNT(*) as total_count
FROM alerts
UNION ALL
SELECT
    'matched_rules' as field_name,
    COUNT(CASE WHEN matched_rules IS NOT NULL THEN 1 END) as non_null_count,
    COUNT(*) as total_count
FROM alerts
UNION ALL
SELECT
    'sanction_flags' as field_name,
    COUNT(CASE WHEN sanction_flags IS NOT NULL THEN 1 END) as non_null_count,
    COUNT(*) as total_count
FROM alerts;

-- 8. Show sample data with all frontend fields
SELECT
    id,
    alert_id,
    alert_type,
    risk_level,
    status,
    LEFT(reason, 30) as reason_preview,
    LEFT(description, 30) as description_preview,
    matched_rules,
    sanction_flags,
    timestamp
FROM alerts
ORDER BY timestamp DESC
LIMIT 3;

-- 9. Check for any alerts with missing critical fields
SELECT
    alert_id,
    CASE WHEN risk_level IS NULL THEN 'MISSING' ELSE 'OK' END as risk_level_status,
    CASE WHEN status IS NULL THEN 'MISSING' ELSE 'OK' END as status_status,
    CASE WHEN description IS NULL THEN 'MISSING' ELSE 'OK' END as description_status
FROM alerts
WHERE risk_level IS NULL OR status IS NULL OR description IS NULL;

-- 10. Summary for dashboard testing
SELECT
    'Dashboard Summary' as info,
    COUNT(*) as total_alerts,
    COUNT(CASE WHEN risk_level = 'HIGH' THEN 1 END) as high_risk_count,
    COUNT(CASE WHEN status = 'OPEN' THEN 1 END) as open_alerts_count,
    COUNT(CASE WHEN timestamp >= NOW() - INTERVAL '24 hours' THEN 1 END) as recent_24h_count
FROM alerts;
