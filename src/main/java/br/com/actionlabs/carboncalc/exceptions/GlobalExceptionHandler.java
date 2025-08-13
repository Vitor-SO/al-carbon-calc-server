package br.com.actionlabs.carboncalc.exceptions;

import br.com.actionlabs.carboncalc.dto.StartCalcResponseDTO;
import jakarta.validation.ConstraintViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.server.ResponseStatusException;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public StartCalcResponseDTO handleValidationException(MethodArgumentNotValidException ex) {
        // Pega a primeira mensagem de erro de validação
        String errorMessage = ex.getBindingResult()
                .getFieldErrors()
                .stream()
                .map(err -> err.getField() + ": " + err.getDefaultMessage())
                .findFirst()
                .orElse("Invalid input");

        return new StartCalcResponseDTO("", ex.getStatusCode(), errorMessage);
    }

    @ExceptionHandler(ResponseStatusException.class)
    public StartCalcResponseDTO handleResponseStatusException(ResponseStatusException ex) {
        return new StartCalcResponseDTO("", ex.getStatusCode(), ex.getReason());
    }
}

