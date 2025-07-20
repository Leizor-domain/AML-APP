package com.leizo.service.impl;

import com.leizo.model.SanctionedEntity;
import com.leizo.service.FileImportService;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;
import org.apache.poi.ss.usermodel.*;
import org.w3c.dom.*;

import javax.xml.parsers.DocumentBuilderFactory;
import java.io.*;
import java.util.*;

public class FileImportServiceImpl implements FileImportService {

    @Override
    public Set<String> importCountriesList(String filePath) {
        Set<String> countries = new HashSet<>();
        try (BufferedReader reader = new BufferedReader(new FileReader(filePath))) {
            String line;
            while ((line = reader.readLine()) != null) {
                if (!line.trim().isEmpty()) {
                    countries.add(line.trim());
                }
            }
        } catch (IOException e) {
            System.err.println("Failed to load countries: " + e.getMessage());
        }
        return countries;
    }


    @Override
    public List<SanctionedEntity> importCsv(String filePath) {
        List<SanctionedEntity> list = new ArrayList<>();
        try (BufferedReader reader = new BufferedReader(new FileReader(filePath))) {
            String line = reader.readLine(); // skip header
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split(",");
                if (parts.length >= 4) {
                    SanctionedEntity entity = new SanctionedEntity(
                            parts[0].trim(),
                            parts[1].trim(),
                            parts[2].trim(),
                            parts[3].trim()
                    );
                    list.add(entity);
                }
            }
        } catch (IOException e) {
            System.err.println("[ERROR] Failed to import CSV: " + e.getMessage());
        }
        return list;
    }

    @Override
    public List<SanctionedEntity> importXml(String filePath) {
        List<SanctionedEntity> list = new ArrayList<>();
        try {
            Document doc = DocumentBuilderFactory.newInstance().newDocumentBuilder().parse(new File(filePath));
            doc.getDocumentElement().normalize();
            NodeList nodes = doc.getElementsByTagName("Entity"); // Adjust to match actual tag name!

            for (int i = 0; i < nodes.getLength(); i++) {
                Element e = (Element) nodes.item(i);
                String name = getTagValue(e, "Name");
                String country = getTagValue(e, "Country");
                String dob = getTagValue(e, "DOB");
                String sanctioningBody = getTagValue(e, "SanctioningBody");
                list.add(new SanctionedEntity(name, country, dob, sanctioningBody));
            }

        } catch (Exception e) {
            System.err.println("[ERROR] Failed to import XML: " + e.getMessage());
        }
        return list;
    }

    @Override
    public List<SanctionedEntity> importJson(String filePath) {
        List<SanctionedEntity> list = new ArrayList<>();
        ObjectMapper mapper = new ObjectMapper();
        try {
            SanctionedEntity[] entities = mapper.readValue(new File(filePath), SanctionedEntity[].class);
            for (SanctionedEntity entity : entities) {
                list.add(entity);
            }
        } catch (IOException e) {
            System.err.println("[ERROR] Failed to import JSON: " + e.getMessage());
        }
        return list;
    }

    @Override
    public List<SanctionedEntity> importExcel(String filePath) {
        List<SanctionedEntity> list = new ArrayList<>();
        try (InputStream is = new FileInputStream(filePath);
             Workbook workbook = WorkbookFactory.create(is)) {

            Sheet sheet = workbook.getSheetAt(0);
            for (Row row : sheet) {
                if (row.getRowNum() == 0) continue; // skip header
                String name = getCellString(row, 0);
                String country = getCellString(row, 1);
                String dob = getCellString(row, 2);
                String sanctioningBody = getCellString(row, 3);
                list.add(new SanctionedEntity(name, country, dob, sanctioningBody));
            }

        } catch (Exception e) {
            System.err.println("[ERROR] Failed to import Excel: " + e.getMessage());
        }
        return list;
    }

    @Override
    public List<String> importPdf(String filePath) {
        List<String> pages = new ArrayList<>();
        try (PDDocument document = PDDocument.load(new File(filePath))) {
            PDFTextStripper stripper = new PDFTextStripper();
            pages.add(stripper.getText(document));
        } catch (IOException e) {
            System.err.println("[ERROR] Failed to import PDF: " + e.getMessage());
        }
        return pages;
    }

    private String getTagValue(Element element, String tagName) {
        NodeList list = element.getElementsByTagName(tagName);
        if (list != null && list.getLength() > 0) {
            return list.item(0).getTextContent();
        }
        return "";
    }

    private String getCellString(Row row, int index) {
        Cell cell = row.getCell(index);
        return cell != null ? cell.toString().trim() : "";
    }
}
