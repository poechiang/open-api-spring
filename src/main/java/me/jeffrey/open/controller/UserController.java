package me.jeffrey.open.controller;

import java.util.List;

import io.swagger.annotations.Api;
import me.jeffrey.open.common.BusinessCodes;
import me.jeffrey.open.dto.User;
import me.jeffrey.open.common.PagingResponse;
import me.jeffrey.open.common.Paging;
import me.jeffrey.open.common.Response;
import me.jeffrey.open.exception.DataBaseException;
import me.jeffrey.open.services.UserService;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.web.bind.annotation.*;

@RestController
@CrossOrigin("*")
@RequestMapping("/api/v1")
@Api(tags = "用户管理")
public class UserController {
  @Autowired protected UserService userService;

  @GetMapping("users")
  public PagingResponse<List<User>> getUsers() {
    long total = userService.count();
    Paging paging = new Paging();
    paging.setTotal(total);
    List<User> users = userService.allUsers(1, 10);
    
    return PagingResponse.Ok(users,paging);
  }
  
  @GetMapping("users/{uid}")
  public Response<User> findUserById(@PathVariable("uid") String uid) {
    
    User user = userService.find(uid);
    return Response.Ok(user);
  }

  @GetMapping("users/{uid}/exist")
  public Response<Boolean> checkExistByUserName(@PathVariable("uname") String uname) {

    Criteria criteria = Criteria.where("uname").is(uname);
    
    User user = userService.find(criteria);
    return Response.Ok(user!=null);
  }
  
  @PostMapping("users")
  public Response<ObjectId> createUser(@RequestBody  User user) throws DataBaseException {

    try{
      
      User newUser =  userService.save(user);
      
      return Response.Ok(newUser.getId());
    }
    catch (DuplicateKeyException e){
      throw new DataBaseException(BusinessCodes.DB_DUPLICATE_KEY,e);
    }
  }
}
