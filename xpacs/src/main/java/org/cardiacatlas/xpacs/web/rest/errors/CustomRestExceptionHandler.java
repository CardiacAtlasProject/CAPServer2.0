package org.cardiacatlas.xpacs.web.rest.errors;

import org.cardiacatlas.xpacs.web.rest.ViewResource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

@ControllerAdvice
public class CustomRestExceptionHandler extends ResponseEntityExceptionHandler {
	
	private final Logger log = LoggerFactory.getLogger(ViewResource.class);

    // 500: INTERNAL SERVER ERROR
    @ExceptionHandler({ Exception.class })
    public ResponseEntity<Object> handleInternalServerError(final Exception ex) {
        log.info(ex.getClass().getName());
        log.error("error", ex);
        
        //
        final ApiError apiError = new ApiError(HttpStatus.INTERNAL_SERVER_ERROR, ex.getLocalizedMessage(), "error occurred");
        return new ResponseEntity<Object>(apiError, new HttpHeaders(), apiError.getStatus());
    }	
}
