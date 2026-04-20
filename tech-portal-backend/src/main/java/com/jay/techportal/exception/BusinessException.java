package com.jay.techportal.exception;

import com.jay.techportal.common.ResultCode;
import lombok.Getter;

/**
 * 业务异常
 * 用于处理可预见的业务逻辑错误
 */
@Getter
public class BusinessException extends RuntimeException {

    private final int code;
    private final String message;

    public BusinessException(ResultCode resultCode) {
        super(resultCode.getMessage());
        this.code = resultCode.getCode();
        this.message = resultCode.getMessage();
    }

    public BusinessException(ResultCode resultCode, String message) {
        super(message);
        this.code = resultCode.getCode();
        this.message = message;
    }

    public BusinessException(int code, String message) {
        super(message);
        this.code = code;
        this.message = message;
    }

    public BusinessException(String message) {
        super(message);
        this.code = ResultCode.INTERNAL_ERROR.getCode();
        this.message = message;
    }
}
