package me.jeffrey.open.exception;

import lombok.Getter;
import me.jeffrey.open.common.BusinessCodes;

public class BusinessException extends Exception {
  
  @java.io.Serial
  private static final long serialVersionUID = -3387516993124229948L;
  @Getter
  private BusinessCodes code = BusinessCodes.FAILURE;
  public BusinessException() {
    super();
  }
  
  
  public BusinessException(BusinessCodes code, Throwable cause) {
    super(cause.getMessage(), cause);
    this.code = code;
  }
  public BusinessException(BusinessCodes code, String msg) {
    super(msg);
    this.code = code;
  }

  public BusinessException(BusinessCodes code, Throwable cause, String msg) {
    super(msg, cause);
    this.code = code;
  }

  public String getError() {
    return "Business Error";
  }

}
