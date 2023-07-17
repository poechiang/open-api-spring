package me.jeffrey.open.services;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

import kotlin.text.Regex;
import lombok.extern.slf4j.Slf4j;
import me.jeffrey.open.dto.Message;
import me.jeffrey.open.dto.User;
import me.jeffrey.open.utils.RequestHelper;
import org.bson.types.ObjectId;
import org.junit.jupiter.api.Test;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class UserService extends DocumentService<User> {

  /** 设置集合名称 */
  @Override
  protected String getCollectionName() {
    return "users";
  }
  
  public List<User> selectUsers(long pageIndex,int pageSize){
    return this.select(null,"",pageSize,pageSize*(pageIndex-1));
  }
  
  public List<User> filterUsers(Criteria criteria,long pageIndex,int pageSize){
    return this.select(criteria,"",pageSize,pageSize*(pageIndex-1));
  }
  
  public User findByName (String uname){
    log.info("findByName:{}",uname);
    return this.find(Criteria.where("uname").is(uname));
  }
  
  public void sendMessageTo(String to, Message msg){
    
    log.info(RequestHelper.getCurrentUserId());
    User me = find(RequestHelper.getCurrentUserId());
    User receiver = find(to);
    
    log.info("\n[UserService] {} send msg {} to {}",me.getId(),msg.getId(),receiver.getId());
    List<ObjectId> outBox = me.getOutBox();
    if(null == outBox){
      outBox = new ArrayList<ObjectId>();
    }
    outBox.add(msg.getId());
    me.setOutBox(outBox);
    
    List<ObjectId> inBox = receiver.getInBox();
    if(null == inBox){
      inBox = new ArrayList<ObjectId>();
    }
    inBox.add(msg.getId());
    receiver.setInBox(inBox);
    
    save(me);
    save(receiver);
    
  }
  
  
  @Test
  public void regexDemo(){
    System.out.println(String.format("==%1$sxxx%2$s==", 123, 456));
  }
}
