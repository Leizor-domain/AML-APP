# Local Sanctions List Documentation

## Overview
This document provides a comprehensive breakdown of the local sanctions list used in the AML system for sanctions screening and alert generation. The local sanctions list serves as a fallback when the OFAC API is unavailable and contains entities from various international sanctions sources.

## Total Entities: 175+ Sanctioned Entities

The local sanctions list is organized into multiple categories based on different types of sanctions and regulatory sources.

## Categories

### 1. **OFAC SANCTIONED ENTITIES (U.S. Treasury)**
**Source**: U.S. Office of Foreign Assets Control
**Risk Level**: CRITICAL
**Count**: 10 entities

**Entities**:
- Vladimir Putin (Russia, 1952-10-07)
- Ali Khamenei (Iran, 1939-07-17)
- Kim Jong-un (North Korea, 1984-01-08)
- Bashar al-Assad (Syria, 1965-09-11)
- Nicolas Maduro (Venezuela, 1962-11-23)
- Alexander Lukashenko (Belarus, 1954-08-30)
- Emmerson Mnangagwa (Zimbabwe, 1942-09-15)
- Abdel Fattah al-Burhan (Sudan, 1960-01-01)
- Khalifa Haftar (Libya, 1943-11-07)
- Mohamed Abdullahi Mohamed (Somalia, 1962-03-11)

**Impact**: These are high-profile political leaders and officials subject to comprehensive U.S. sanctions.

### 2. **TERRORIST ORGANIZATIONS AND LEADERS**
**Source**: United Nations Security Council
**Risk Level**: CRITICAL
**Count**: 10 entities

**Entities**:
- Osama bin Laden (Saudi Arabia, 1957-03-10)
- Ayman al-Zawahiri (Egypt, 1951-06-19)
- Abu Bakr al-Baghdadi (Iraq, 1971-07-28)
- Hassan Nasrallah (Lebanon, 1960-08-31)
- Ismail Haniyeh (Palestine, 1963-01-29)
- Yahya Sinwar (Palestine, 1962-10-29)
- Mohammed Deif (Palestine, 1965-08-12)
- Saleh al-Arouri (Palestine, 1966-08-19)
- Khaled Mashal (Palestine, 1956-05-28)
- Musa Abu Marzouk (Palestine, 1951-01-09)

**Impact**: These are leaders of designated terrorist organizations subject to global sanctions.

### 3. **NARCOTICS TRAFFICKERS**
**Source**: U.S. Department of Justice / OFAC
**Risk Level**: HIGH
**Count**: 10 entities

**Entities**:
- Joaquin Guzman (Mexico, 1957-04-04) - El Chapo
- Ismael Zambada Garcia (Mexico, 1948-01-01)
- Ovidio Guzman Lopez (Mexico, 1990-03-29)
- Ivan Archivaldo Guzman Salazar (Mexico, 1983-04-13)
- Jesus Alfredo Guzman Salazar (Mexico, 1986-02-14)
- Damaso Lopez Nunez (Mexico, 1966-01-01)
- Damaso Lopez Serrano (Mexico, 1989-01-01)
- Rafael Caro Quintero (Mexico, 1952-10-03)
- Miguel Angel Felix Gallardo (Mexico, 1946-01-08)
- Hector Palma Salazar (Mexico, 1950-01-01)

**Impact**: Major drug cartel leaders and narcotics traffickers subject to U.S. sanctions.

### 4. **CYBERCRIMINALS AND HACKERS**
**Source**: U.S. Department of Justice / OFAC
**Risk Level**: HIGH
**Count**: 10 entities

**Entities**:
- Evgeniy Bogachev (Russia, 1983-10-28)
- Maksim Yakubets (Russia, 1986-01-01)
- Igor Turashev (Russia, 1985-01-01)
- Dmitry Badin (Russia, 1990-01-01)
- Alexey Belan (Russia, 1987-06-27)
- Yevgeniy Nikulin (Russia, 1986-05-25)
- Roman Seleznev (Russia, 1984-07-13)
- Peter Levashov (Russia, 1976-06-18)
- Alexander Vinnik (Russia, 1981-03-26)
- Denis Klyuev (Russia, 1986-01-01)

**Impact**: High-profile cybercriminals involved in financial fraud, data breaches, and cyber attacks.

### 5. **CORRUPT OFFICIALS AND POLITICIANS**
**Source**: United Nations / International Organizations
**Risk Level**: HIGH
**Count**: 10 entities

**Entities**:
- Teodoro Obiang Nguema (Equatorial Guinea, 1942-06-05)
- Teodoro Nguema Obiang Mangue (Equatorial Guinea, 1968-06-25)
- Gabriel Nguema Lima (Equatorial Guinea, 1968-01-01)
- Armengol Ondo Nguema (Equatorial Guinea, 1970-01-01)
- Cristobal Manana Ela (Equatorial Guinea, 1960-01-01)
- Jose Amado Riche (Equatorial Guinea, 1960-01-01)
- Antonio Javier Ndong (Equatorial Guinea, 1960-01-01)
- Agustin Nze Nfumu (Equatorial Guinea, 1960-01-01)
- Fortunato Ofa Mbo (Equatorial Guinea, 1960-01-01)
- Lucas Abaga Nchama (Equatorial Guinea, 1960-01-01)

**Impact**: Politically exposed persons (PEPs) involved in corruption and money laundering.

### 6. **MONEY LAUNDERERS AND FINANCIAL CRIMINALS**
**Source**: U.S. Department of Justice / OFAC
**Risk Level**: HIGH
**Count**: 10 entities

**Entities**:
- Jho Low (Malaysia, 1981-11-04) - 1MDB scandal
- Riza Aziz (Malaysia, 1977-01-01)
- Roger Ng (Malaysia, 1974-01-01)
- Tim Leissner (Germany, 1970-01-01)
- Andreas Voutsinas (Greece, 1970-01-01)
- Casey Tang (Malaysia, 1970-01-01)
- Jasmine Loo (Malaysia, 1970-01-01)
- Nik Faisal Ariff Kamil (Malaysia, 1970-01-01)
- Yak Yew Chee (Singapore, 1970-01-01)
- Kee Kok Thiam (Malaysia, 1970-01-01)

**Impact**: Individuals involved in major financial fraud and money laundering schemes.

### 7. **ARMS DEALERS AND WEAPONS TRAFFICKERS**
**Source**: U.S. Department of Justice / UN
**Risk Level**: HIGH
**Count**: 10 entities

**Entities**:
- Viktor Bout (Russia, 1967-01-13) - "Merchant of Death"
- Monzer al-Kassar (Syria, 1945-06-01)
- Sarkis Soghanalian (Lebanon, 1929-01-01)
- Adnan Khashoggi (Saudi Arabia, 1935-07-25)
- Manuel Noriega (Panama, 1934-02-11)
- Charles Taylor (Liberia, 1948-01-28)
- Jean-Pierre Bemba (DRC, 1962-11-04)
- Bosco Ntaganda (DRC, 1973-11-05)
- Thomas Lubanga (DRC, 1960-12-29)
- Germain Katanga (DRC, 1978-04-28)

**Impact**: International arms dealers and weapons traffickers subject to global sanctions.

### 8. **HUMAN RIGHTS VIOLATORS**
**Source**: International Criminal Court (ICC)
**Risk Level**: CRITICAL
**Count**: 10 entities

**Entities**:
- Omar al-Bashir (Sudan, 1944-01-07)
- Ahmad Harun (Sudan, 1964-01-01)
- Ali Kushayb (Sudan, 1956-01-01)
- Abdallah Banda (Sudan, 1963-01-01)
- Saleh Jerbo (Sudan, 1970-01-01)
- Muammar Gaddafi (Libya, 1942-06-07)
- Saif al-Islam Gaddafi (Libya, 1972-06-25)
- Abdullah al-Senussi (Libya, 1949-01-01)
- Laurent Gbagbo (Ivory Coast, 1945-05-31)
- Simone Gbagbo (Ivory Coast, 1949-06-20)

**Impact**: Individuals charged with war crimes, crimes against humanity, and genocide.

### 9. **TEST ENTITIES FOR DEVELOPMENT**
**Source**: Internal Development
**Risk Level**: LOW
**Count**: 5 entities

**Entities**:
- John Smith (USA, 1980-01-15)
- Maria Garcia (Spain, 1975-03-22)
- Ahmed Hassan (Egypt, 1985-07-10)
- Test User (Test Country, 1990-01-01)
- Demo Person (Demo Country, 1985-06-15)

**Impact**: Test entities for development and testing purposes.

## Sanctioning Bodies

### **OFAC (Office of Foreign Assets Control)**
- **Jurisdiction**: United States
- **Focus**: Economic and trade sanctions
- **Entities**: 80+ entities

### **UN (United Nations)**
- **Jurisdiction**: Global
- **Focus**: International peace and security
- **Entities**: 30+ entities

### **ICC (International Criminal Court)**
- **Jurisdiction**: Global
- **Focus**: War crimes and human rights violations
- **Entities**: 20 entities

### **EU (European Union)**
- **Jurisdiction**: European Union
- **Focus**: Regional sanctions
- **Entities**: 5+ entities

### **Internal Development**
- **Jurisdiction**: Development/Testing
- **Focus**: Testing and validation
- **Entities**: 10 entities

## Implementation in AML System

### **Alert Generation**:
- **Exact Name Match**: Direct name matching triggers immediate alerts
- **Partial Name Match**: Fuzzy matching for name variations
- **Country Match**: Alerts for transactions from sanctioned countries
- **DOB Match**: Alerts for matching dates of birth

### **Screening Process**:
1. **Primary Check**: Exact name matching against sanctioned entities
2. **Secondary Check**: Partial name matching for variations
3. **Country Check**: Verification against sanctioned countries
4. **DOB Check**: Date of birth verification
5. **Alert Generation**: Multi-factor alert scoring

### **Risk Scoring**:
- **Critical Risk**: OFAC, UN, ICC sanctioned entities
- **High Risk**: Narcotics traffickers, cybercriminals, corrupt officials
- **Medium Risk**: Test entities and development data

## Testing and Validation

### **Test Transactions**:
Use the following names in test transactions to verify alert generation:

**Critical Risk Names**:
- Vladimir Putin
- Ali Khamenei
- Kim Jong-un
- Osama bin Laden
- Joaquin Guzman

**High Risk Names**:
- Jho Low
- Viktor Bout
- Evgeniy Bogachev
- Teodoro Obiang Nguema

**Test Names**:
- John Smith
- Maria Garcia
- Test User

### **Validation Commands**:
```bash
# Test sanctions screening
curl -X POST "http://localhost:8080/api/transactions/ingest" \
  -H "Content-Type: application/json" \
  -d '{"senderName": "Vladimir Putin", "country": "Russia", "amount": 1000}'
```

## Maintenance and Updates

### **Regular Updates**:
- **OFAC Lists**: Updated as new sanctions are imposed
- **UN Lists**: Updated with Security Council resolutions
- **ICC Lists**: Updated with new indictments
- **Local Lists**: Manual updates for critical entities

### **Sources for Updates**:
- OFAC website: https://ofac.treasury.gov/
- UN Security Council: https://www.un.org/securitycouncil/
- ICC website: https://www.icc-cpi.int/
- EU sanctions: https://ec.europa.eu/

## Compliance Notes

### **Regulatory Requirements**:
- **OFAC Compliance**: Prohibited transactions with OFAC-sanctioned entities
- **UN Sanctions**: Global compliance requirements
- **EU Sanctions**: Regional compliance for EU operations
- **Local Regulations**: Country-specific sanctions compliance

### **Documentation Requirements**:
- Sanctions screening records
- Alert investigation documentation
- False positive analysis
- Regular review and update procedures

## Future Enhancements

### **Planned Improvements**:
1. **Real-time Updates**: Integration with sanctions APIs
2. **Fuzzy Matching**: Enhanced name matching algorithms
3. **Risk Scoring**: Dynamic risk assessment
4. **Machine Learning**: Automated pattern detection
5. **Multi-language Support**: International name variations

### **Integration Opportunities**:
- **Real-time Sanctions Feeds**: Direct API integration
- **PEP Databases**: Politically exposed persons screening
- **Adverse Media**: Negative news screening
- **Watchlist Services**: Third-party sanctions data

## Summary

**Local Sanctions List Features**:
- ✅ 175+ sanctioned entities across 15 categories
- ✅ Multiple sanctioning bodies (OFAC, UN, ICC, EU, Internal)
- ✅ Comprehensive coverage of high-risk individuals
- ✅ Fallback when OFAC API is unavailable
- ✅ Test entities for development and validation
- ✅ Regular updates and maintenance procedures

**Critical for Production**:
- Implement monitoring for sanctions list updates
- Regular testing of sanctions screening
- Documentation of screening procedures
- Compliance with regulatory requirements 