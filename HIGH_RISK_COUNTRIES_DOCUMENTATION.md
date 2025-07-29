# High-Risk Countries Documentation

## Overview
This document provides a comprehensive breakdown of the high-risk countries list used in the AML system for sanctions screening and risk assessment.

## Total Countries: 200+ High-Risk Countries

The high-risk countries list is organized into multiple categories based on different risk factors and regulatory sources.

## Categories

### 1. **FATF HIGH-RISK JURISDICTIONS (2024)**
**Source**: Financial Action Task Force (FATF) - International standard-setting body
**Risk Level**: CRITICAL
**Countries**:
- Myanmar
- Democratic People's Republic of Korea (North Korea)
- Iran

**Impact**: These countries are considered to have strategic deficiencies in their AML/CTF regimes and are subject to enhanced due diligence.

### 2. **FATF JURISDICTIONS UNDER INCREASED MONITORING**
**Source**: FATF Grey List
**Risk Level**: HIGH
**Countries**:
- Albania, Barbados, Burkina Faso, Cameroon, Cayman Islands
- Croatia, Democratic Republic of the Congo, Gibraltar, Haiti
- Jamaica, Jordan, Mali, Mozambique, Nigeria, Panama
- Philippines, Senegal, South Africa, South Sudan, Syria
- Tanzania, Turkey, Uganda, United Arab Emirates, Yemen

**Impact**: These countries have committed to addressing strategic deficiencies within agreed timeframes.

### 3. **OFAC SANCTIONED COUNTRIES**
**Source**: U.S. Office of Foreign Assets Control
**Risk Level**: CRITICAL
**Countries**:
- Cuba, Venezuela, Russia, Belarus, Zimbabwe, Sudan
- Libya, Somalia, Central African Republic, Burundi
- Eritrea, Guinea-Bissau, Iraq, Lebanon, Nicaragua

**Impact**: Comprehensive sanctions that prohibit most transactions with these countries.

### 4. **EU HIGH-RISK THIRD COUNTRIES**
**Source**: European Union Commission
**Risk Level**: HIGH
**Countries**:
- Afghanistan, Botswana, Ghana, Pakistan, Trinidad and Tobago

**Impact**: Enhanced due diligence requirements for EU financial institutions.

### 5. **UN SECURITY COUNCIL SANCTIONED COUNTRIES**
**Source**: United Nations Security Council Resolutions
**Risk Level**: CRITICAL
**Countries**:
- North Korea, Iran, Syria, Libya, Somalia, Yemen, Iraq, Lebanon

**Impact**: International sanctions with global compliance requirements.

### 6. **HIGH CORRUPTION RISK COUNTRIES**
**Source**: Transparency International Corruption Perceptions Index
**Risk Level**: HIGH
**Countries**:
- Angola, Bangladesh, Cambodia, Chad, Comoros, Congo
- Djibouti, Equatorial Guinea, Eswatini, Ethiopia, Gabon
- Gambia, Guinea, Kazakhstan, Kenya, Kyrgyzstan, Laos
- Liberia, Madagascar, Malawi, Mauritania, Mauritius
- Mongolia, Nepal, Niger, Papua New Guinea, Paraguay
- Rwanda, Sierra Leone

**Impact**: High risk of bribery, corruption, and politically exposed persons (PEPs).

### 7. **MONEY LAUNDERING RISK COUNTRIES**
**Source**: Various financial intelligence units and regulatory bodies
**Risk Level**: MEDIUM-HIGH
**Countries**:
- Tax Havens: Cayman Islands, British Virgin Islands, Bermuda, Isle of Man
- Offshore Centers: Bahamas, Cyprus, Jersey, Liechtenstein, San Marino
- Caribbean Nations: Antigua and Barbuda, Aruba, Belize, Dominica
- Pacific Nations: Cook Islands, Marshall Islands, Nauru, Niue, Samoa, Vanuatu

**Impact**: Weak regulatory frameworks and banking secrecy laws.

### 8. **TERRORISM FINANCING RISK COUNTRIES**
**Source**: FATF, UN Security Council, national intelligence agencies
**Risk Level**: HIGH
**Countries**:
- Afghanistan, Algeria, Bangladesh, Egypt, Indonesia, Iraq
- Lebanon, Libya, Malaysia, Mali, Niger, Nigeria, Pakistan
- Philippines, Somalia, Sudan, Syria, Tunisia, Yemen

**Impact**: Active terrorist organizations and weak financial controls.

### 9. **NARCOTICS TRAFFICKING RISK COUNTRIES**
**Source**: UN Office on Drugs and Crime (UNODC)
**Risk Level**: HIGH
**Countries**:
- Afghanistan, Bolivia, Colombia, Ecuador, Guatemala, Honduras
- Laos, Mexico, Myanmar, Pakistan, Paraguay, Peru, Thailand, Venezuela

**Impact**: Major drug production and trafficking routes.

### 10. **CYBERCRIME AND FRAUD RISK COUNTRIES**
**Source**: International cybersecurity reports and law enforcement
**Risk Level**: MEDIUM-HIGH
**Countries**:
- Bulgaria, China, Ghana, India, Indonesia, Malaysia
- Nigeria, Pakistan, Philippines, Romania, Russia, Ukraine, Vietnam

**Impact**: High incidence of cyber fraud, phishing, and online scams.

### 11. **POLITICALLY EXPOSED PERSONS (PEP) RISK COUNTRIES**
**Source**: Various corruption indices and political risk assessments
**Risk Level**: HIGH
**Countries**:
- Azerbaijan, Belarus, Cambodia, Central African Republic, Chad
- Congo, Democratic Republic of the Congo, Equatorial Guinea
- Eritrea, Gabon, Guinea, Guinea-Bissau, Kazakhstan, Kyrgyzstan
- Laos, Liberia, Madagascar, Malawi, Mauritania, Mongolia
- Myanmar, Nepal, Papua New Guinea, Rwanda, Sierra Leone
- South Sudan, Tajikistan, Turkmenistan, Uzbekistan, Zimbabwe

**Impact**: High concentration of politically exposed persons requiring enhanced due diligence.

### 12. **ADDITIONAL HIGH-RISK COUNTRIES**
**Source**: Regional risk assessments and emerging threats
**Risk Level**: MEDIUM-HIGH
**Countries**:
- Eastern Europe: Albania, Armenia, Azerbaijan, Bosnia and Herzegovina
- Caucasus: Georgia, Kosovo, Macedonia, Moldova, Montenegro
- Balkans: Romania, Serbia, Tajikistan, Turkmenistan, Ukraine, Uzbekistan

**Impact**: Regional instability, weak institutions, and emerging financial crime risks.

## Risk Scoring System

### Risk Levels:
- **CRITICAL**: Immediate sanctions screening required
- **HIGH**: Enhanced due diligence required
- **MEDIUM-HIGH**: Standard due diligence with monitoring
- **MEDIUM**: Regular monitoring recommended

### Scoring Factors:
1. **Regulatory Requirements**: FATF, OFAC, EU, UN sanctions
2. **Financial Crime Risk**: Money laundering, corruption, fraud
3. **Geopolitical Risk**: Political instability, conflict zones
4. **Economic Risk**: Weak financial systems, currency controls
5. **Compliance Risk**: Regulatory enforcement actions

## Implementation in AML System

### Alert Generation:
- **Country Sanctions**: Automatic alerts for transactions to/from sanctioned countries
- **Risk Scoring**: Higher risk scores for transactions involving high-risk countries
- **Enhanced Due Diligence**: Triggered for medium-high and high-risk countries

### Screening Process:
1. **Primary Check**: Direct country name matching
2. **Secondary Check**: Fuzzy matching for variations
3. **Risk Assessment**: Country risk factor applied to transaction risk score
4. **Alert Generation**: Country-based alerts with appropriate priority levels

## Maintenance and Updates

### Regular Updates:
- **FATF Lists**: Updated quarterly
- **OFAC Sanctions**: Updated as new sanctions are imposed
- **EU Lists**: Updated annually
- **UN Sanctions**: Updated as Security Council resolutions change

### Sources for Updates:
- FATF website: https://www.fatf-gafi.org/
- OFAC website: https://ofac.treasury.gov/
- EU Commission: https://ec.europa.eu/
- UN Security Council: https://www.un.org/securitycouncil/

## Testing and Validation

### Test Transactions:
Use the following countries in test transactions to verify alert generation:
- **Critical Risk**: Iran, North Korea, Syria
- **High Risk**: Turkey, Mexico, Pakistan
- **Medium Risk**: Albania, Barbados, Panama

### Validation Commands:
```bash
# Test country sanctions
curl -X POST "http://localhost:8080/api/transactions/ingest" \
  -H "Content-Type: application/json" \
  -d '{"country": "Iran", "amount": 1000, "senderName": "Test User"}'
```

## Compliance Notes

### Regulatory Requirements:
- **FATF Recommendations**: Enhanced due diligence for high-risk jurisdictions
- **OFAC Compliance**: Prohibited transactions with sanctioned countries
- **EU 5AMLD**: Enhanced due diligence for high-risk third countries
- **UN Sanctions**: Global compliance requirements

### Documentation Requirements:
- Risk assessment documentation for high-risk country transactions
- Enhanced due diligence records
- Ongoing monitoring procedures
- Regular review and update of risk assessments

## Future Enhancements

### Planned Improvements:
1. **Dynamic Risk Scoring**: Real-time risk assessment based on current events
2. **Regional Risk Factors**: Sub-national risk assessment
3. **Industry-Specific Risk**: Sector-based risk factors
4. **Temporal Risk Factors**: Time-based risk adjustments
5. **Machine Learning**: Automated risk pattern detection

### Integration Opportunities:
- **Real-time Sanctions Feeds**: Direct integration with regulatory APIs
- **Geopolitical Risk Data**: Integration with political risk assessment services
- **Economic Indicators**: Integration with economic risk data providers
- **Social Media Monitoring**: Integration with social media risk assessment tools 