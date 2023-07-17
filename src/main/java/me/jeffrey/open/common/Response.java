package me.jeffrey.open.common;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;
import me.jeffrey.open.exception.BusinessException;

@Data
@Accessors(chain = true)
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class Response<T> {
    
    private BusinessCodes code;
    
    
    private String message;
    
    private String error;
    
    private T payload;
    
    public static <T> Response<T> Empty(){
        Response<T> resp = new Response<>();
        
        return resp.setCode(BusinessCodes.SUCCESS);
    }
    
    public static <T> Response<T> Ok(T payload){
        Response<T> resp = new Response<>();
        
        return resp.setCode(BusinessCodes.SUCCESS).setPayload(payload);
    }
    
    public static <T> Response<T> Fault(String msg, BusinessCodes code){
        Response<T> resp = new Response<>();
        return resp.setCode(code).setMessage(msg);
    }
    
    public static <T>  Response<T> Fault(String msg){
        return Response.Fault(msg, BusinessCodes.FAILURE);
    }
    
    public static <T> Response<T> Fault(){
        return Response.Fault("Unknown Server Error", BusinessCodes.FAILURE);
    }

    public static <T> Response<T> Fault(BusinessException e){
        
        Response<T> resp= Response.Fault(e.getMessage(),e.getCode());
        return resp.setError(e.getError());
    }
    
    public String getResult(){
        return code.name();
    }
}
