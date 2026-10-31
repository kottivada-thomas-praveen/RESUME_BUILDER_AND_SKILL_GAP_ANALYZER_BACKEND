package com.thomas.rgsga.dto;

public class ApiResponseWrapper<T> extends ApiResponse<T> {

    public ApiResponseWrapper() {
        super();
    }

    public ApiResponseWrapper(boolean success, String message, T data) {
        super(success, message, data, java.time.LocalDateTime.now());
    }

    public static <T> ApiResponse<T> success(String message, T data) {
        return ApiResponse.success(message, data);
    }

    public static <T> ApiResponse<T> error(String message) {
        return ApiResponse.error(message);
    }
}
