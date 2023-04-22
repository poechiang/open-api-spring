package me.jeffrey.open.advise;

import me.jeffrey.open.common.Response;
import me.jeffrey.open.exception.BusinessException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionAdvise {
    private static final Logger log = LoggerFactory.getLogger(GlobalExceptionAdvise.class);
    
    @ExceptionHandler(BusinessException.class)
    public Response mongoDuplicateKeyHandler(BusinessException e){
        
        log.warn(String.format("[%1$s] 截获并处理异常(%4$s): (%2$s) %3$s","Mongo",e.getCode(),e.getMessage(),e.getClass()));
        return Response.Fault(e);
    }
  @ExceptionHandler(Exception.class)
  public Response exceptionHandler(Exception e) {
    // todo 生成普通异常返回结果
      
      log.error(String.format("[UNKNOWN] 截获并处理异常: %1$s","Mongo",e.getClass()));
      return Response.Fault(e.getMessage());

  }
  
}
