package org.exception;

import org.dto.response.ApiResponse;
import org.constant.ResponseCode;
import org.springframework.http.HttpStatus;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.List;

@RestControllerAdvice
public class GlobalExceptionHandler {

    /**
     * 处理参数验证异常
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ApiResponse<?> handleValidationException(MethodArgumentNotValidException ex) {
        BindingResult bindingResult = ex.getBindingResult();
        List<FieldError> fieldErrors = bindingResult.getFieldErrors();

        if (!fieldErrors.isEmpty()) {
            FieldError firstError = fieldErrors.get(0);
            return ApiResponse.error(ResponseCode.PARAM_ERROR, firstError.getDefaultMessage());
        }

        return ApiResponse.error(ResponseCode.PARAM_ERROR, "参数验证失败");
    }

    /**
     * 处理业务异常
     */
    @ExceptionHandler(BusinessException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ApiResponse<?> handleBusinessException(BusinessException ex) {
        return ApiResponse.error(ex.getCode(), ex.getMessage());
    }

    /**
     * 处理其他所有异常
     */
    @ExceptionHandler(Exception.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ApiResponse<?> handleException(Exception ex) {
        ex.printStackTrace();
        return ApiResponse.error(ResponseCode.SYSTEM_ERROR, "系统异常，请稍后重试");
    }
}