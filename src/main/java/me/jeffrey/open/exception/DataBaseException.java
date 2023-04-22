package me.jeffrey.open.exception;


import me.jeffrey.open.common.BusinessCodes;

public class DataBaseException extends BusinessException {
  
  
  public String getError(){
    return "Database Error";
  }
  
  
  public DataBaseException(BusinessCodes code, Throwable cause ) {
    super(code,cause);
  }
  
  public DataBaseException(BusinessCodes code, String msg) {
    super(code,msg);
  }
  
  public DataBaseException(BusinessCodes code, Throwable cause, String msg) {
    super(code,cause,msg);
  }
  
  
}
