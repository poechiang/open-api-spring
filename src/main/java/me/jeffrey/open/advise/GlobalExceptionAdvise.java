package me.jeffrey.open.advise;

import lombok.extern.slf4j.Slf4j;
import me.jeffrey.open.common.Response;
import me.jeffrey.open.exception.AuthorizationException;
import me.jeffrey.open.exception.BusinessException;
import me.jeffrey.open.exception.TokenExpireException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionAdvise {

  @ExceptionHandler(BusinessException.class)
  public Response<?> mongoDuplicateKeyHandler(BusinessException e) {

    log.warn("[MONGO] 截获并处理异常: ({}) {}", e.getCode(), e.getMessage());
    return Response.Fault(e);
  }

  @ExceptionHandler(TokenExpireException.class)
  public Response<?> tokenExpireExceptionHandler(TokenExpireException e) {

    log.warn("[TOKEN] 截获并处理异常:({}) {}", e.getCode(), e.getMessage());
    return Response.Fault(e);
  }

  @ExceptionHandler(AuthorizationException.class)
  public Response<?> authorizationExceptionHandler(AuthorizationException e) {

    log.warn("[TOKEN] 截获并处理异常:({}) {}", e.getCode(), e.getMessage());
    return Response.Fault(e);
  }

  @ExceptionHandler(RuntimeException.class)
  public Response<?> runtimeExceptionHandler(RuntimeException e) {

    // todo 生成普通异常返回结果

    log.error("[TOKEN] 截获并处理异常: ({}) {}", e.getClass(), e.getMessage());
    return Response.Fault(e.getMessage());
  }

  @ExceptionHandler(Exception.class)
  public Response<?> exceptionHandler(Exception e) {

    // todo 生成普通异常返回结果

    log.error("[UNKNOWN] 截获并处理异常: ({}) {}", e.getClass(), e.getMessage());
    return Response.Fault(e.getMessage());
  }
}
