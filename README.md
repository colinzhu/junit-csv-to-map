# JUnit CSV to Map

A JUnit 5 extension that allows loading test data from CSV files and converting them into Map<String, String> objects for parameterized tests.

## Features

- Load test data from CSV files
- Automatically convert CSV rows to `Map<String, String>`
- Support for filtering different set of test cases
- Support for filtering active/inactive test cases


## Usage

### Basic Usage
The first row of your CSV file should contain column names, which will become the keys in the Map:

Note: only isActive=true rows will be included in the test.
```csv
isActive,input,expected,description
true,value1,result1,test case 1
true,value2,result2,test case 2
false,value3,result3,test case 3
```

```java
@ParameterizedTest
@CsvToMapFileSource(file = "/path/to/test-data.csv")
void testWithCsvFile(Map<String, String> testData, String desc, String expected) {
    // Use testData in your test
    String input = testData.get("input");
    String expected = testData.get("expected");
    
    assertEquals(expected, myService.process(input));
}
```


### Define extra columns as parameters
```csv
isActive,input,expected,description
true,value1,result1,test case 1
true,value2,result2,test case 2
false,value3,result3,test case 3
```

```java
@ParameterizedTest
@CsvToMapFileSource(file = "/path/to/test-data.csv", columnsAsParams = {"description", "expected"})
void testWithCsvFile(Map<String, String> testData, String desc, String expected) {
    // Use testData in your test
    String input = testData.get("input");
    
    assertEquals(expected, myService.process(input), desc);
}
```

### Filter data sets
For below example, only dataSet 'A' and 'B' rows will be included in the test.
```csv
isActive,dataSet,input,expected,description
true,A,value1,result1,test case 1
true,B,value2,result2,test case 2
false,C,value3,result3,test case 3
```

```java
@ParameterizedTest
@CsvToMapFileSource(file = "/path/to/test-data.csv", dataSet = {"A", "B"})
void testWithCsvFile(Map<String, String> testData) {
    // Use testData in your test
    String input = testData.get("input");
    String expected = testData.get("expected");
    
    assertEquals(expected, myService.process(input));
}
```
