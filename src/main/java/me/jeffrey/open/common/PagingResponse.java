package me.jeffrey.open.common;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

@Data
@Accessors(chain = true)
@NoArgsConstructor
@AllArgsConstructor
public class PagingResponse<T>  extends Response<T> {
  
  protected Paging paging;
  
  public static <T> PagingResponse<T>  Ok(T payload,Paging paging){
    PagingResponse<T> resp = new PagingResponse<>();
    return resp.setCode(BusinessCodes.SUCCESS).setPayload(payload).setPaging(paging);
  }

  public PagingResponse<T> setCode(BusinessCodes code){
    return (PagingResponse<T>) super.setCode(code);
  }

  public PagingResponse<T> setPayload(T payload){
    return (PagingResponse<T>) super.setPayload(payload);
  }
  
  public PagingResponse<T> setMessage(String message){
    return (PagingResponse<T>) super.setMessage(message);
  }
  
}
