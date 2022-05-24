package io.github.xahdy.socialquarkus.rest.dto;

import lombok.Data;

import javax.validation.ConstraintViolation;
import java.util.Collection;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Data
public class ResponseError {
    private String message;

    private Collection<FieldError> errors;

    public ResponseError(String message, Collection<FieldError> errors) {
        this.message = message;
        this.errors = errors;
    }

    public static <T> ResponseError createFromValidation(Set<ConstraintViolation<T>> violations){
        //gravar na listta errors todos os fielderrors mapeados
        List<FieldError> errors = violations
                .stream()
                //mapear o constraintviolation para um FieldError
                .map(cv -> new FieldError(cv.getPropertyPath().toString(), cv.getMessage()))
                //colocar cada FieldError mapeado e colocar numa lista.
                .collect(Collectors.toList());

        String message = "Validation Error";

        var responseError = new ResponseError(message, errors);
        return responseError;
    }


}
