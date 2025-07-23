package com.leizo.service;

import com.leizo.admin.entity.SanctionedEntity;

import java.util.List;
import java.util.Set;

public interface FileImportService {

    List<SanctionedEntity> importCsv(String filePath);

    List<SanctionedEntity> importXml(String filePath);

    List<SanctionedEntity> importJson(String filePath);

    List<SanctionedEntity> importExcel(String filePath);

    List<String> importPdf(String filePath); // optional: PDF mostly text-based

    // In FileImportService
    Set<String> importCountriesList(String filePath);

}
