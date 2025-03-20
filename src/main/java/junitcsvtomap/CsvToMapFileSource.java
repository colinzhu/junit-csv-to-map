package junitcsvtomap;

import org.junit.jupiter.params.provider.ArgumentsSource;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Annotation for loading test parameters from CSV files.
 * Converts CSV file content to Map and optionally extracts specific columns as parameters.
 */
@Target({ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@ArgumentsSource(CsvToMapArgumentsProvider.class)
public @interface CsvToMapFileSource {
    /**
     * Path to the CSV file
     */
    String file();
    
    /**
     * Column name that indicates whether a record is active.
     * Only records with "true" in this column will be processed.
     */
    String columnForIsActive() default "isActive";
    
    /**
     * Array of column names to be passed as separate parameters
     */
    String[] columnsAsParams() default {};
    
    /**
     * Column name for dataset identification
     */
    String columnForDataSet() default "dataSet";
    
    /**
     * Array of dataset values to filter by.
     * Empty array means no filtering by dataset.
     */
    String[] dataSet() default {};
}