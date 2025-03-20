package junitcsvtomap;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests for the CsvToMapFileSource annotation and its provider
 */
public class CsvToMapFileSourceTest {

    @DisplayName("Test map with extracted columns")
    @ParameterizedTest(name = "{index} - {1}")
    @CsvToMapFileSource(file = "/junitcsvtomap/CsvToMapTest.csv", columnsAsParams = {"desc", "columnX"})
    void testMapAndColumns(Map<String, String> csvData, String desc, String columnX) {
        // Verify both map and extracted columns
        assertEquals("testDesc", csvData.get("desc"), "Map should contain correct 'desc' value");
        assertEquals("testValue", csvData.get("columnX"), "Map should contain correct 'columnX' value");
        assertEquals("testDesc", desc, "Extracted 'desc' parameter should match");
        assertEquals("testValue", columnX, "Extracted 'columnX' parameter should match");
    }

    @DisplayName("Test map without extracted columns")
    @ParameterizedTest(name = "{index} - Map only")
    @CsvToMapFileSource(file = "/junitcsvtomap/CsvToMapTest.csv")
    void testOnlyMap(Map<String, String> csvData) {
        assertEquals("testDesc", csvData.get("desc"), "Map should contain correct 'desc' value");
        assertEquals("testValue", csvData.get("columnX"), "Map should contain correct 'columnX' value");
    }

    @DisplayName("Test filtering by single dataset")
    @ParameterizedTest(name = "{index} - Dataset A")
    @CsvToMapFileSource(file = "/junitcsvtomap/CsvToMapTest-dataSet.csv", dataSet = "A")
    void testDataSet_dataSetA(Map<String, String> csvData) {
        assertEquals("A", csvData.get("dataSet"), "Only records with dataSet='A' should be returned");
    }

    @DisplayName("Test filtering by multiple datasets")
    @ParameterizedTest(name = "{index} - Dataset {0}")
    @CsvToMapFileSource(file = "/junitcsvtomap/CsvToMapTest-dataSet.csv", dataSet = {"A", "B"})
    void testDataSet_dataSetAnB(Map<String, String> csvData) {
        String dataSetValue = csvData.get("dataSet");
        assertTrue(
                "A".equals(dataSetValue) || "B".equals(dataSetValue),
                "Only records with dataSet='A' or 'B' should be returned"
        );
        assertNotEquals("C", dataSetValue, "Records with dataSet='C' should be filtered out");
    }

    @DisplayName("Test custom dataset column")
    @ParameterizedTest(name = "{index} - Custom dataset column")
    @CsvToMapFileSource(file = "/junitcsvtomap/CsvToMapTest-dataSet-customColumn.csv", dataSet = "A", columnForDataSet = "dataCategory")
    void testDataSet_dataSetA_customColumn(Map<String, String> csvData) {
        assertEquals("A", csvData.get("dataCategory"), "Only records with dataCategory='A' should be returned");
    }

    @DisplayName("Test isActive column filtering")
    @ParameterizedTest(name = "{index} - Active records")
    @CsvToMapFileSource(file = "/junitcsvtomap/CsvToMapTest-isActive.csv")
    void testIsActiveFiltering(Map<String, String> csvData) {
        assertEquals("true", csvData.get("isActive"), "Only active records should be returned");
        assertNotNull(csvData.get("value"), "Active records should have values");
    }

    @DisplayName("Test custom isActive column")
    @ParameterizedTest(name = "{index} - Custom active column")
    @CsvToMapFileSource(file = "/junitcsvtomap/CsvToMapTest-customActive.csv", columnForIsActive = "active")
    void testCustomIsActiveColumn(Map<String, String> csvData) {
        assertEquals("true", csvData.get("active"), "Only records with active='true' should be returned");
    }

    @DisplayName("Test null value handling")
    @ParameterizedTest(name = "{index} - {2}")
    @CsvToMapFileSource(file = "/junitcsvtomap/CsvToMapTest-nullValues.csv", columnsAsParams = {"nullableField", "note"})
    void testNullValueHandling(Map<String, String> csvData, String nullableField, String note) {
        if (null == csvData.get("type")) {
            assertNull(nullableField, "Field should be null when CSV contains 'null'");
        } else if ("empty".equals(csvData.get("type"))) {
            assertNull(nullableField, "Field should be null when CSV contains empty value");
        } else {
            assertEquals("value", nullableField, "Field should contain actual value");
        }
    }

    @DisplayName("Test combined filtering")
    @ParameterizedTest(name = "{index} - Combined filters")
    @CsvToMapFileSource(
            file = "/junitcsvtomap/CsvToMapTest-combined.csv",
            dataSet = "X",
            columnForDataSet = "group",
            columnForIsActive = "enabled"
    )
    void testCombinedFiltering(Map<String, String> csvData) {
        assertEquals("X", csvData.get("group"), "Only records with group='X' should be returned");
        assertEquals("true", csvData.get("enabled"), "Only enabled records should be returned");
    }
}