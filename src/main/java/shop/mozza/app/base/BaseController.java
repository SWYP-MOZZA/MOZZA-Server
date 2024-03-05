package shop.mozza.app.base;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.HashMap;
import java.util.Map;

@RestControllerAdvice
public class BaseController {
    protected final Logger logger = LoggerFactory.getLogger(getClass());

    @ExceptionHandler(value = Exception.class)
    public ResponseEntity handleException(Exception e) {
        return handleApiException(e, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    protected ResponseEntity handleApiException(Exception e, HttpStatus status) {
        Map<String, String> res = new HashMap<>();
        res.put("statusCode", "error");
        res.put("responseMessage", e.getMessage());
        logger.info("error:{}", e.getMessage());
        return new ResponseEntity<>(res, status);
    }
}