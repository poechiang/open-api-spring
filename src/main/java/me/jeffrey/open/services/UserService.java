package me.jeffrey.open.services;

import lombok.extern.slf4j.Slf4j;
import me.jeffrey.open.dto.User;
import org.springframework.stereotype.Service;import java.util.List;

@Slf4j
@Service
public class UserService extends DocumentService<User> {

  /** 设置集合名称 */
  @Override
  protected String getCollectionName() {
    return "users";
  }
  
  public List<User> allUsers(long pageIndex,int pageSize){
    return this.select(null,"",pageSize,pageSize*(pageIndex-1));
  }
}
