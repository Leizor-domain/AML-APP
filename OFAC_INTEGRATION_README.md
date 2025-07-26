# OFAC XML Sanctions Integration

## Overview

This document describes the integration of real-time sanctions screening using the U.S. Treasury OFAC (Office of Foreign Assets Control) SDN (Specially Designated Nationals) list. The integration replaces placeholder sanctions screening with actual OFAC data from the official XML feed.

## 🔗 OFAC Data Source

- **URL**: https://www.treasury.gov/ofac/downloads/sdn.xml
- **Format**: XML
- **Update Frequency**: Daily (automatically refreshed every 24 hours)
- **Authentication**: None required (public feed)

## 🏗️ Architecture

### Core Components

1. **OfacXmlSanctionsApiClient** - Interface defining OFAC operations
2. **OfacXmlSanctionsApiClientImpl** - Implementation with XML parsing and caching
3. **SanctionsCheckerImpl** - Updated to use OFAC as primary source
4. **OfacSanctionsController** - REST API for testing and monitoring

### Integration Points

- **Transaction Ingestion**: Automatically screens sender/receiver names
- **Risk Scoring**: OFAC matches contribute to risk assessment
- **Alert Generation**: Creates alerts for sanctioned entity matches
- **Audit Trail**: Logs all sanctions screening activities

## 🚀 Features

### 1. Real-time XML Parsing
- Fetches and parses official OFAC SDN XML feed
- Extracts entity names, countries, and program information
- Handles XML parsing errors gracefully

### 2. Intelligent Caching
- In-memory caching of parsed OFAC data
- Automatic refresh every 24 hours
- Manual refresh capability via API
- Efficient lookup maps for fast screening

### 3. Advanced Matching
- **Exact Match**: Direct name comparison
- **Fuzzy Match**: Levenshtein distance with configurable threshold
- **Case Insensitive**: Normalized comparison
- **Whitespace Tolerant**: Handles extra spaces and formatting

### 4. Comprehensive Logging
- Detailed audit trail of all screening activities
- Match details and confidence scores
- Error logging with fallback mechanisms

## 📊 API Endpoints

### Health Check
```
GET /api/ofac/health
```
Returns service status and basic statistics.

### Entity Screening
```
GET /api/ofac/check?name={name}&country={country}
```
Screens a specific entity against OFAC list.

### Fuzzy Matching
```
GET /api/ofac/check-fuzzy?name={name}&threshold={0.0-1.0}
```
Performs fuzzy matching with configurable similarity threshold.

### Search Entities
```
GET /api/ofac/search?name={name}&country={country}
```
Searches for entities in the OFAC list.

### Statistics
```
GET /api/ofac/stats
```
Returns OFAC data statistics and refresh information.

### Manual Refresh
```
POST /api/ofac/refresh
```
Manually refreshes OFAC data from the source.

## 🔧 Configuration

### Default Settings
- **Refresh Interval**: 24 hours
- **Fuzzy Threshold**: 0.8 (80% similarity)
- **Cache Size**: Unlimited (all OFAC entities)
- **Timeout**: 30 seconds for HTTP requests

### Customization
Settings can be modified in `OfacXmlSanctionsApiClientImpl.java`:

```java
private static final long REFRESH_INTERVAL_HOURS = 24;
private static final double DEFAULT_FUZZY_THRESHOLD = 0.8;
```

## 🧪 Testing

### Unit Tests
- `OfacXmlSanctionsApiClientImplTest.java` - Comprehensive test coverage
- Tests XML parsing, caching, matching, and error handling

### Integration Tests
- `test-ofac-integration.ps1` - PowerShell test script
- Tests all API endpoints and transaction ingestion

### Test Data
- `sample_transactions_ofac_test.csv` - Test transactions with OFAC names
- Includes both neutral names and potential OFAC matches

## 📈 Performance

### Benchmarks
- **Initial Load**: ~5-10 seconds (depending on network)
- **Entity Screening**: <1ms per entity
- **Fuzzy Matching**: <10ms per entity
- **Memory Usage**: ~50-100MB for full OFAC list

### Optimization
- Concurrent hash maps for fast lookups
- Scheduled background refresh
- Efficient XML parsing with DOM
- Minimal memory footprint

## 🔒 Security

### Data Protection
- No sensitive data stored
- All OFAC data is publicly available
- Secure HTTP connections to Treasury servers

### Access Control
- API endpoints can be secured with Spring Security
- Audit logging for all screening activities
- Rate limiting available via Spring Boot

## 🚨 Alert Integration

### Alert Types
- **OFAC_SDN**: Direct match in OFAC SDN list
- **LOCAL_SANCTIONS**: Match in local sanctions list (fallback)

### Alert Details
- Matched entity name
- Source list (OFAC_SDN or LOCAL_SANCTIONS)
- Match reason and confidence
- Transaction details

### Priority Levels
- **HIGH**: OFAC SDN matches
- **MEDIUM**: Fuzzy matches
- **LOW**: Local list matches

## 🔄 Migration from OpenSanctions

### Changes Made
1. **Replaced OpenSanctionsService** with OfacXmlSanctionsApiClient
2. **Updated SanctionsCheckerImpl** to use OFAC as primary source
3. **Enhanced logging** with OFAC-specific messages
4. **Added comprehensive testing** for new functionality

### Backward Compatibility
- Local sanctions list still available as fallback
- Same API interfaces maintained
- Existing alerts and workflows preserved

## 📋 Monitoring

### Key Metrics
- Total OFAC entities loaded
- Last refresh timestamp
- Screening success/failure rates
- Match statistics

### Health Checks
- OFAC service availability
- Data freshness (last refresh)
- Entity count validation
- XML parsing success rate

## 🛠️ Troubleshooting

### Common Issues

1. **XML Parsing Errors**
   - Check network connectivity to Treasury servers
   - Verify XML format hasn't changed
   - Review error logs for specific issues

2. **No Entities Loaded**
   - Verify OFAC feed is accessible
   - Check XML structure matches expected format
   - Review initialization logs

3. **Performance Issues**
   - Monitor memory usage
   - Check refresh frequency
   - Optimize fuzzy matching threshold

### Debug Commands
```bash
# Check OFAC service health
curl http://localhost:8080/api/ofac/health

# Test entity screening
curl "http://localhost:8080/api/ofac/check?name=Ali Mohammed&country=US"

# Manual refresh
curl -X POST http://localhost:8080/api/ofac/refresh
```

## 📚 References

- [OFAC SDN List](https://www.treasury.gov/ofac/downloads/sdn.xml)
- [OFAC Website](https://www.treasury.gov/ofac)
- [Spring Boot Documentation](https://spring.io/projects/spring-boot)
- [Java XML Processing](https://docs.oracle.com/javase/tutorial/jaxp/)

## 🤝 Contributing

When modifying OFAC integration:

1. Update unit tests for new functionality
2. Test with real OFAC data
3. Update documentation
4. Verify backward compatibility
5. Run integration tests

## 📄 License

This integration uses publicly available OFAC data and follows Treasury guidelines for sanctions screening. 