package com.weighing.config;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.HashMap;
import java.util.Map;
@RestControllerAdvice
public class GlobalExceptionHandler {
    /**
         * 处理认证失败异常 (用户名或密码错误)
         * 返回 401 Unauthorized
         */
        @ExceptionHandler(BadCredentialsException.class)
        public ResponseEntity<Map<String, String>> handleBadCredentialsException(BadCredentialsException ex) {
            Map<String, String> error = new HashMap<>();
            error.put("error", "Unauthorized");
            error.put("message", ex.getMessage());

            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(error);
        }

        /**
         * 处理业务运行时异常
         * 返回 400 Bad Request，异常信息直接透传给前端
         */
        @ExceptionHandler(RuntimeException.class)
        public ResponseEntity<Map<String, String>> handleRuntimeException(RuntimeException ex) {
            Map<String, String> error = new HashMap<>();
            error.put("error", "Bad Request");
            error.put("message", ex.getMessage());

            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
        }

        /**
         * 处理其他通用异常
         * 返回 500 Internal Server Error
         */
        @ExceptionHandler(Exception.class)
        public ResponseEntity<Map<String, String>> handleGeneralException(Exception ex) {
            ex.printStackTrace(); // 生产环境建议记录日志

            Map<String, String> error = new HashMap<>();
            error.put("error", "Internal Server Error");
            error.put("message", "服务器内部错误");

            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
        }
    }

