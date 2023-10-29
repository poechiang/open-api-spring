package me.jeffrey.open.services;

import java.util.ArrayList;
import java.util.List;

import lombok.extern.slf4j.Slf4j;
import me.jeffrey.open.dto.MessageDTO;
import me.jeffrey.open.dto.UserDTO;
import me.jeffrey.open.utils.RequestHelper;
import org.bson.types.ObjectId;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class UserService extends DocumentService<UserDTO> {

  /** 设置集合名称 */
  @Override
  protected String getCollectionName() {
    return "users";
  }
  
  public List<UserDTO> selectUsers(long pageIndex, int pageSize){
    return this.select(null,"",pageSize,pageSize*(pageIndex-1));
  }
  
  public List<UserDTO> filterUsers(Criteria criteria, long pageIndex, int pageSize){
    return this.select(criteria,"",pageSize,pageSize*(pageIndex-1));
  }
  
  public UserDTO findByName (String uname){
    log.info("findByName:{}",uname);
    return this.find(Criteria.where("uname").is(uname));
  }
  
  public void sendMessageTo(String to, MessageDTO msg){
    
    log.info(RequestHelper.getCurrentUserId());
    UserDTO me = find(RequestHelper.getCurrentUserId());
    UserDTO receiver = find(to);
    
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
  
  
}
