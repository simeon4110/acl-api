package org.acl.database.persistence.dtos.web;

/**
 * An object for managing SearchParam lists from the front-end.
 *
 * @author Josh Harkema
 */
public class SearchParamDto {
    private String fieldName;
    private String joinType;
    private String searchString;

    public String getFieldName() {
        return fieldName;
    }

    public void setFieldName(String fieldName) {
        this.fieldName = fieldName;
    }

    public String getJoinType() {
        return joinType;
    }

    public void setJoinType(String joinType) {
        this.joinType = joinType;
    }

    public String getSearchString() {
        return searchString;
    }

    public void setSearchString(String searchString) {
        this.searchString = searchString;
    }

    @Override
    public String toString() {
        return "SearchParam{" +
                "fieldName='" + fieldName + '\'' +
                ", joinType='" + joinType + '\'' +
                ", searchString='" + searchString + '\'' +
                '}';
    }
}
