package io.github.xahdy.socialquarkus.rest.dto;

public class FieldError {
    private String field;

    public String getField() {
        return field;
    }

    public FieldError(String field, String message) {
        this.field = field;
        this.message = message;
    }

    public void setField(String field) {
        this.field = field;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    private String message;
}
