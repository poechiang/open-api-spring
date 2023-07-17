package me.jeffrey.open.controller;

import io.swagger.annotations.Api;
import java.util.List;
import java.util.Optional;
import java.util.regex.Pattern;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import lombok.extern.slf4j.Slf4j;
import me.jeffrey.open.common.*;
import me.jeffrey.open.dto.User;
import me.jeffrey.open.exception.DataBaseException;
import me.jeffrey.open.services.UserService;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.data.domain.Example;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@CrossOrigin(allowCredentials = "true", originPatterns = "*")
@RequestMapping("/api/v1")
@Api(tags = "用户管理")
public class UserController {
  @Autowired protected UserService userService;

  @GetMapping("users")
  public PagingResponse<List<User>> getUsers(
          @RequestParam(value = "filters",required = false) String filters,
          @RequestParam(value = "page",defaultValue = "0",required = false) int pageIndex,
          @RequestParam(value = "pageSize",defaultValue = "0",required = false) int pageSize
) {
    
    Paging paging = null;
    
    Criteria criteria = Criteria.where("status").is(UserStatus.ACTIVE);
      
    if(null!=filters){
      // filter users by "uname" field with the "filters" keywords parameters
      Pattern pattern = Pattern.compile(String.format(".*%1$s.*",filters));
      criteria = criteria.and("uname").regex(pattern);
    }
    if(pageSize>0){
        long total =userService.count(criteria);
      paging = new Paging().setTotal(total).setPage(pageIndex).setPageSize(pageSize);
    }
      
     List<User> users = userService.filterUsers(criteria,pageIndex,pageSize);
    return PagingResponse.Ok(users,paging);
  }
  
  @GetMapping("users/{uid}")
  public Response<User> findUserById(@PathVariable("uid") String uid) {
    
    User user = userService.find(uid);
    return Response.Ok(user);
  }

  @GetMapping("users/{uid}/exist")
  public Response<Boolean> checkExistByUserName(@PathVariable("uname") String uname) {

    
    User user = userService.findByName(uname);
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
