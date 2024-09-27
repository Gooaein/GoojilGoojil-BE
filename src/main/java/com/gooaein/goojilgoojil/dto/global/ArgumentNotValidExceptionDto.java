package com.gooaein.goojilgoojil.dto.global;

import com.gooaein.goojilgoojil.exception.ErrorCode;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import lombok.Getter;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;

import java.util.HashMap;
import java.util.Map;

@Getter
public class ArgumentNotValidExceptionDto extends ExceptionDto {
    private final Map<String, String> errorFields;

    public ArgumentNotValidExceptionDto(final MethodArgumentNotValidException methodArgumentNotValidException) {
        super(ErrorCode.INVALID_ARGUMENT);

        this.errorFields = new HashMap<>();
        methodArgumentNotValidException.getBindingResult()
                .getAllErrors().forEach(e -> this.errorFields.put(((FieldError) e).getField(), e.getDefaultMessage()));
    }

    public ArgumentNotValidExceptionDto(final ConstraintViolationException constraintViolationException) {
        super(ErrorCode.INVALID_ARGUMENT);

        this.errorFields = new HashMap<>();
        for (ConstraintViolation<?> violation : constraintViolationException.getConstraintViolations()) {
            this.errorFields.put(violation.getPropertyPath().toString(), violation.getMessage());
        }
    }

    public ArgumentNotValidExceptionDto(final BindingResult bindingResult) {
        super(ErrorCode.INVALID_ARGUMENT);

        this.errorFields = new HashMap<>();
        bindingResult.getAllErrors().forEach(e -> {
            String field = ((FieldError) e).getField();
            String errorMessage = e.getDefaultMessage();
            this.errorFields.put(field, errorMessage);
        });
    }
}
