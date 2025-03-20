package junitcsvtomap;

import org.junit.jupiter.api.extension.ExtensionContext;
import org.junit.jupiter.params.provider.AnnotationBasedArgumentsProvider;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.shadow.com.univocity.parsers.csv.CsvParser;
import org.junit.jupiter.params.shadow.com.univocity.parsers.csv.CsvParserSettings;
import org.junit.platform.commons.JUnitException;

import java.io.IOException;
import java.io.InputStream;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Arguments provider that reads CSV files and converts them to Maps for parameterized tests.
 * Supports filtering by dataset and active status, and extracting specific columns as parameters.
 */
class CsvToMapArgumentsProvider extends AnnotationBasedArgumentsProvider<CsvToMapFileSource> {

    /**
     * CSV parser configured with comma delimiter and double quote character
     */
    private final CsvParser csvParser = new CsvParser(new CsvParserSettings() {{
        getFormat().setDelimiter(',');
        getFormat().setQuote('"');
    }});

    /**
     * Provides arguments for parameterized tests by reading and processing a CSV file.
     *
     * @param context            The extension context
     * @param csvToMapFileSource The annotation containing configuration for CSV processing
     * @return Stream of Arguments objects containing Maps and optional extracted columns
     */
    @Override
    protected Stream<? extends Arguments> provideArguments(ExtensionContext context, CsvToMapFileSource csvToMapFileSource) {
        String filePath = csvToMapFileSource.file();
        String[] dataSetOfTestMethod = csvToMapFileSource.dataSet();
        boolean hasDataSetFilter = dataSetOfTestMethod.length > 0;
        String columnNameOfDataSet = csvToMapFileSource.columnForDataSet();
        String columnNameOfIsActive = csvToMapFileSource.columnForIsActive();
        String[] columnsAsParams = csvToMapFileSource.columnsAsParams();

        try (InputStream inputStream = getClass().getResourceAsStream(filePath)) {
            if (inputStream == null) {
                throw new JUnitException("Resource not found: " + filePath);
            }
            csvParser.beginParsing(inputStream);

            String[] headers = csvParser.parseNext();
            if (headers == null) {
                throw new JUnitException("No headers found in the CSV file: " + filePath);
            }

            return csvParser.parseAll().stream()
                    .map(valuesOfOneRow -> createMap(headers, valuesOfOneRow))
                    .filter(mapOfOneRow -> !hasDataSetFilter || Arrays.asList(dataSetOfTestMethod).contains(mapOfOneRow.get(columnNameOfDataSet)))
                    .filter(mapOfOneRow -> "true".equalsIgnoreCase(mapOfOneRow.get(columnNameOfIsActive)))
                    .map(mapOfOneRow -> createArguments(mapOfOneRow, columnsAsParams));
        } catch (IOException e) {
            throw new JUnitException("Failed to read CSV file: " + filePath, e);
        }
    }

    /**
     * Creates a map from CSV headers and valuesOfOneRow.
     * Handles null valuesOfOneRow and trims string valuesOfOneRow.
     *
     * @param headers        Array of column headers
     * @param valuesOfOneRow Array of column valuesOfOneRow
     * @return Map with header-value pairs
     */
    private Map<String, String> createMap(String[] headers, String[] valuesOfOneRow) {
        Map<String, String> map = new HashMap<>();
        for (int i = 0; i < headers.length; i++) {
            String value = valuesOfOneRow[i] != null ? valuesOfOneRow[i].trim() : null;
            map.put(headers[i], "null".equalsIgnoreCase(value) ? null : value);
        }
        return map;
    }

    /**
     * Creates Arguments object from a mapOfOneRow and optional extracted columns.
     * If columnsAsParams is empty, returns the mapOfOneRow as a single argument.
     * Otherwise, returns the mapOfOneRow as the first argument followed by the specified column values.
     *
     * @param mapOfOneRow     Map containing all CSV data for a row
     * @param columnsAsParams Array of column names to extract as separate parameters
     * @return Arguments object for JUnit parameterized tests
     */
    private Arguments createArguments(Map<String, String> mapOfOneRow, String[] columnsAsParams) {
        if (columnsAsParams.length == 0) {
            return Arguments.of(mapOfOneRow);
        } else {
            List<Object> list = new ArrayList<>();
            list.add(mapOfOneRow);
            list.addAll(Arrays.stream(columnsAsParams).map(mapOfOneRow::get).collect(Collectors.toList()));
            return Arguments.of(list.toArray());
        }
    }
}