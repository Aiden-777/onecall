package org.tronproject.onecall.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.BindException;
import org.springframework.validation.ObjectError;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.tronproject.onecall.model.vo.R;

import javax.validation.ConstraintViolation;
import javax.validation.ConstraintViolationException;
import java.util.List;
import java.util.Set;

/**
 * @author Admin
 * @version 1.0
 * @time 2022-06-14 02:10
 */
@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler {


    @ExceptionHandler(Exception.class)
    public R exception(Exception e) {
        e.printStackTrace();
        return R.error("系统未知异常");
    }

    @ExceptionHandler(HttpRequestMethodNotSupportedException.class)
    public R httpRequestMethodNotSupportedException(HttpRequestMethodNotSupportedException e) {
        return R.error(e.getMessage());
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public R methodArgumentNotValidException(MethodArgumentNotValidException e) {
        return R.error(e.getAllErrors().get(0).getDefaultMessage());
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public R methodArgumentNotValidException(HttpMessageNotReadableException e) {
        return R.error("缺少请求参数");
    }

    /**
     * 参数校验异常处理
     */
    @ExceptionHandler(BindException.class)
    public R handler(BindException ex) {
        List<ObjectError> allErrors = ex.getAllErrors();
        log.warn(ex.getMessage());
        return R.error(allErrors.get(0).getDefaultMessage());
    }

    /**
     * 参数校验异常处理
     */
    @ExceptionHandler(ConstraintViolationException.class)
    public R handler(ConstraintViolationException ex) {
        String message = "参数错误";
        Set<ConstraintViolation<?>> constraintViolations = ex.getConstraintViolations();
        for (ConstraintViolation<?> constraintViolation : constraintViolations) {
            message = constraintViolation.getMessage();
            break;
        }
        return R.error(message);
    }


}
