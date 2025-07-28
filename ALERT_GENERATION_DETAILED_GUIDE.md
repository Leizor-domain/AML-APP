# AML Alert Generation - Detailed Guide

## Overview
This document provides a comprehensive breakdown of all conditions that trigger alert generation in the AML (Anti-Money Laundering) system.

## Alert Generation Hierarchy

### 1. **SANCTIONS ALERTS** (Highest Priority)
Sanctions alerts are checked **FIRST** and have the highest priority. If a sanctions match is found, no other rules are evaluated.

#### 1.1 OFAC SDN List Matches
- **Condition**: Transaction sender name matches OFAC Specially Designated Nationals (SDN) list
- **Trigger**: `sanctionsChecker.isSanctionedEntity(senderName, country, dob, "Any")`
- **Alert Type**: `SANCTIONS`
- **Priority Level**: `HIGH`
- **Priority Score**: `100`
- **Example**: Transaction from "Vladimir Putin" or "Ali Khamenei"

#### 1.2 Country Sanctions
- **Condition**: Transaction country is under sanctions
- **Trigger**: `sanctionsChecker.checkCountry(country)`
- **Alert Type**: `SANCTIONS`
- **Priority Level**: `HIGH`
- **Priority Score**: `100`
- **Example**: Transactions to/from Iran, North Korea, Syria

#### 1.3 Local Sanctions List Matches
- **Condition**: Transaction sender name matches local sanctions list
- **Trigger**: `sanctionsChecker.checkName(senderName)`
- **Alert Type**: `SANCTIONS`
- **Priority Level**: `HIGH`
- **Priority Score**: `90`
- **Example**: Names from `sample_sanctions.csv`

#### 1.4 Partial Name Matches
- **Condition**: Transaction sender name partially matches sanctions list
- **Trigger**: `sanctionsChecker.checkPartialName(senderName)`
- **Alert Type**: `SANCTIONS`
- **Priority Level**: `HIGH`
- **Priority Score**: `70`
- **Example**: "John Smith" when "John Smith" is in sanctions list

### 2. **RULE-BASED ALERTS** (Second Priority)
If no sanctions alerts are triggered, the system evaluates rule-based alerts.

#### 2.1 Hardcoded Rules

##### 2.1.1 High Value Transfer Rule
- **Condition**: Transaction amount > $10,000
- **Trigger**: `amount.compareTo(new BigDecimal("10000")) > 0`
- **Sensitivity**: `HIGH`
- **Tags**: `["value", "priority", "large_txn"]`
- **Priority Score**: Base risk score + sensitivity weight

##### 2.1.2 Medium Risk Region Transfer
- **Condition**: Transaction country is Turkey, Mexico, or any high-risk country
- **Trigger**: `country.equalsIgnoreCase("Turkey") || country.equalsIgnoreCase("Mexico") || riskScoringService.getHighRiskCountries().contains(country)`
- **Sensitivity**: `MEDIUM`
- **Tags**: `["geo", "moderate_risk"]`
- **Priority Score**: Base risk score + sensitivity weight

##### 2.1.3 Low Value Routine Transfer
- **Condition**: Transaction amount < $500
- **Trigger**: `amount.compareTo(new BigDecimal("500")) < 0`
- **Sensitivity**: `LOW`
- **Tags**: `["routine", "small_amount", "low_risk"]`
- **Priority Score**: Base risk score + sensitivity weight

##### 2.1.4 Manual Flag Rule
- **Condition**: Transaction has manual flag set to TRUE
- **Trigger**: `hasManualFlag(transaction)`
- **Sensitivity**: `HIGH`
- **Tags**: `["manual", "override", "flagged"]`
- **Priority Score**: Base risk score + sensitivity weight + 10

##### 2.1.5 Frequent Sender Rule
- **Condition**: Sender has frequent transaction history
- **Trigger**: `isFrequentSender(transaction)` (Currently returns false - placeholder)
- **Sensitivity**: `MEDIUM`
- **Tags**: `["behavior", "frequency", "pattern"]`
- **Priority Score**: Base risk score + sensitivity weight

##### 2.1.6 Currency Risk Rule
- **Condition**: Transaction involves high-risk currency
- **Trigger**: `isHighRiskCurrency(transaction)`
- **Sensitivity**: `MEDIUM`
- **Tags**: `["currency", "forex", "risk"]`
- **Priority Score**: Base risk score + sensitivity weight

#### 2.2 JSON-Based Rules (from rules.json)

##### 2.2.1 High Value Transfer Rule (JSON)
- **Condition**: Transaction amount > $10,000
- **Type**: `"high_value"`
- **Sensitivity**: `HIGH`
- **Tags**: `["value", "priority", "large_txn"]`

##### 2.2.2 Medium Risk Region Transfer (JSON)
- **Condition**: Transaction country is Turkey or Mexico
- **Type**: `"medium_risk_country"`
- **Sensitivity**: `MEDIUM`
- **Tags**: `["geo", "moderate_risk"]`

##### 2.2.3 Low Value Routine Transfer (JSON)
- **Condition**: Transaction amount < $500
- **Type**: `"low_value"`
- **Sensitivity**: `LOW`
- **Tags**: `["routine", "small_amount", "low_risk"]`

### 3. **BEHAVIORAL PATTERN ALERTS** (Logging Only)
Currently, behavioral pattern detection only logs events but does NOT generate formal alerts.

#### 3.1 Amount Deviation Detection
- **Condition**: Transaction amount > 2x average historical amount
- **Trigger**: `txn.getAmount().compareTo(avg.multiply(AMOUNT_DEVIATION_THRESHOLD)) > 0`
- **Action**: Logs `BEHAVIORAL_ALERT` event
- **Note**: Does NOT create formal alert

#### 3.2 High Frequency Detection
- **Condition**: Sender has > 5 transactions in short period
- **Trigger**: `history.size() > FREQUENCY_THRESHOLD`
- **Action**: Logs `BEHAVIORAL_ALERT` event
- **Note**: Does NOT create formal alert

## Risk Scoring System

### Base Risk Score Calculation
Risk score is calculated based on multiple factors:

1. **High Amount** (≥$100,000): +25 points
2. **Sanctioned Country**: +25 points
3. **Frequent Sender**: +25 points (placeholder)
4. **Manual Flag**: +25 points

### Priority Score Enhancement
Priority score = Base risk score + Rule sensitivity weight + Bonuses

- **Rule Sensitivity Weights**:
  - HIGH: +30 points
  - MEDIUM: +20 points
  - LOW: +10 points

- **Additional Bonuses**:
  - Manual flag: +10 points
  - High-risk country: +5 points

## Alert Deduplication and Cooldown

### Duplicate Detection
- **Method**: SHA-256 hash of transaction + reason
- **Key**: `generateAlertKey(transaction, reason)`
- **Action**: Skip alert if duplicate exists

### Cooldown Mechanism
- **Duration**: Configurable cooldown period per rule
- **Scope**: Per sender + rule combination
- **Action**: Skip alert if cooldown is active

## Alert Types and Properties

### Sanctions Alerts
```json
{
  "alertType": "SANCTIONS",
  "priorityLevel": "HIGH",
  "priorityScore": 100,
  "matchedEntityName": "sender_name",
  "matchedList": "OFAC_SDN|LOCAL_SANCTIONS",
  "matchReason": "Entity matched in OFAC SDN list"
}
```

### Rule Alerts
```json
{
  "alertType": "RULE_MATCH",
  "priorityLevel": "LOW|MEDIUM|HIGH",
  "priorityScore": calculated_score,
  "reason": "Rule matched: [Rule Description]"
}
```

## Testing Alert Generation

### Confirmed Alert-Triggering Transactions

1. **Sanctions Alerts**:
   - `Vladimir Putin` → OFAC SDN match
   - `Ali Khamenei` → OFAC SDN match
   - `Kim Jong-un` → OFAC SDN match

2. **High Value Alerts**:
   - Amount > $10,000

3. **Country Sanctions**:
   - Country = Turkey, Mexico, Iran, North Korea, Syria

4. **Manual Flag Alerts**:
   - `manualFlag = TRUE`

5. **Structuring Pattern** (Multiple transactions):
   - Same sender, multiple small amounts in short time

## Alert Generation Flow

```
Transaction Received
        ↓
    Validate Transaction
        ↓
    Check Sanctions (Priority 1)
        ↓
    If Sanctions Match → Generate Sanctions Alert
        ↓
    If No Sanctions → Check Rules (Priority 2)
        ↓
    Find Matching Rules
        ↓
    Select Highest Sensitivity Rule
        ↓
    Check Duplicate/Cooldown
        ↓
    Generate Rule Alert
        ↓
    Log Event
```

## Current Limitations

1. **Behavioral Patterns**: Only logged, not formal alerts
2. **Structuring Detection**: Not implemented as formal rule
3. **Suspicious Description**: Not implemented as formal rule
4. **Frequent Sender**: Placeholder implementation
5. **Currency Risk**: Placeholder implementation

## Recommendations for Enhancement

1. **Implement Behavioral Alert Generation**: Convert behavioral logs to formal alerts
2. **Add Structuring Rule**: Implement formal rule for structuring detection
3. **Add Suspicious Description Rule**: Implement keyword-based description analysis
4. **Enhance Frequent Sender Logic**: Implement actual frequency tracking
5. **Add Currency Risk Logic**: Implement high-risk currency detection 