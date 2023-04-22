package me.jeffrey.open.exception;

import lombok.Getter;
import me.jeffrey.open.common.BusinessCodes;

public class SerializeException extends BusinessException {
    @java.io.Serial
    private static final long serialVersionUID = -414074759029150125L;
    
    @Getter
    private Object context;
    
    public String getError(){
        return "Serialization Error";
    }
    
    
    public SerializeException(BusinessCodes code, Object context) {
        this(code,"序列化错误",context);
    }
    public SerializeException(BusinessCodes code, String msg,Object context) {
        super(code,msg);
        this.context = context;
    }
    
    public SerializeException(BusinessCodes code, Throwable cause,String msg,Object context) {
        super(code,cause,msg);
        this.context = context;
    }
    
    public SerializeException() {
        super();
    }
    
    
}
