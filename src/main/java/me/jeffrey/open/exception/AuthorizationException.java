package me.jeffrey.open.exception;

import me.jeffrey.open.common.BusinessCodes;

public class AuthorizationException extends BusinessException {
    
    @java.io.Serial
    private static final long serialVersionUID = -3258134144167556984L;
    
    public AuthorizationException(Throwable cause ) {
        super(BusinessCodes.UNAUTHORIZED,cause);
    }
    
    
    public AuthorizationException(String msg) {
        super(BusinessCodes.UNAUTHORIZED,msg);
    }
    
    public AuthorizationException(Throwable cause, String msg) {
        super(BusinessCodes.UNAUTHORIZED,cause,msg);
    }
    
    public String getError(){
        return "Authorization Error";
    }
    
    public static final AuthorizationException Default = new AuthorizationException("Unauthorized");
}

