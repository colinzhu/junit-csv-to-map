# JUnit CSV to Map

A JUnit 5 extension that allows loading test data from CSV files and converting them into Map<String, String> objects for parameterized tests.

## Features

- Load test data from CSV files
- Automatically convert CSV rows to `Map<String, String>`
- Support for filtering different set of test cases
- Support for filtering active/inactive test cases


## Usage

### Basic Usage

```java
@ParameterizedTest
@CsvToMapFileSource(file = "/path/to/test-data.csv", columnsAsParams = {"description", "expected"})
void testWithCsvFile(Map<String, String> testData, String desc, String expected) {
    // Use testData in your test
    String input = testData.get("input");
    String expected = testData.get("expected");
    
    assertEquals(expected, myService.process(input), desc);
}
```

### CSV File Format

The first row of your CSV file should contain column names, which will become the keys in the Map:
```csv
isActive,input,expected,description
true,value1,result1,test case 1
true,value2,result2,test case 2
false,value3,result3,test case 3
```
