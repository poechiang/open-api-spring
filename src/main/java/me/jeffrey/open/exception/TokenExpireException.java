package me.jeffrey.open.exception;

import lombok.Getter;
import me.jeffrey.open.common.BusinessCodes;

public class TokenExpireException extends BusinessException {
    
    @java.io.Serial
    private static final long serialVersionUID = -8816222141796545212L;
    
    public TokenExpireException(Throwable cause ) {
        super(BusinessCodes.TOKEN_EXPIRED,cause);
    }
    
    
    public TokenExpireException(String msg) {
        super(BusinessCodes.TOKEN_EXPIRED,msg);
    }
    
    public TokenExpireException(Throwable cause, String msg) {
        super(BusinessCodes.TOKEN_EXPIRED,cause,msg);
    }
    
    public String getError(){
        return "TokenExpire Error";
    }
    
    public static final TokenExpireException Default = new TokenExpireException("token expired");
}
