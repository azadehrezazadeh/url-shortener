package ir.neshan.urlshortener.exception;

import org.springframework.http.ResponseEntity;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

@ControllerAdvice
public class APIExceptionHandler extends ResponseEntityExceptionHandler {

    @ExceptionHandler(APIException.class)
    protected ResponseEntity<ErrorDTO> handleAPIException(APIException exception) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body( new ErrorDTO(exception.getCode().name(), exception.getMessage()));
    }
}
