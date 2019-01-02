package com.sonnets.sonnet.constants;

public enum TestConstants {
    BASE_URL("http://127.0.0.1:8080/"),
    DEFAULT_PERIOD("1900-1999"),
    JSON_ID("$.id"),
    JSON_CATEGORY("$.category"),
    JSON_AUTHOR_ID("$.author.id"),
    JSON_AUTHOR_FIRST_NAME("$.author.firstName"),
    JSON_AUTHOR_LAST_NAME("$.author.lastName"),
    JSON_TITLE("$.title"),
    JSON_PUBLICATION_YEAR("$.publicationYear"),
    JSON_PUBLICATION_STMT("$.publicationStmt"),
    JSON_SOURCE_DESC("$.sourceDesc"),
    JSON_PERIOD("$.period"),
    JSON_POEM_FORM("$.form"),
    JSON_TEXT("$.text");

    private String stringValue;

    TestConstants(String stringValue) {
        this.stringValue = stringValue;
    }

    public String getStringValue() {
        return stringValue;
    }
}
