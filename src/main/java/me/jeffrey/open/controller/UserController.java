package me.jeffrey.open.controller;

import io.swagger.annotations.Api;
import java.util.List;
import lombok.extern.slf4j.Slf4j;
import me.jeffrey.open.common.*;
import me.jeffrey.open.dto.MessageDTO;
import me.jeffrey.open.dto.UserDTO;
import me.jeffrey.open.exception.DataBaseException;
import me.jeffrey.open.services.MessageService;
import me.jeffrey.open.services.UserService;
import me.jeffrey.open.utils.RequestHelper;
import org.bson.types.ObjectId;
import org.checkerframework.checker.units.qual.C;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@CrossOrigin(allowCredentials = "true", originPatterns = "*")
@RequestMapping("/api/v1")
@Api(tags = "用户管理")
public class UserController {
  @Autowired protected UserService userService;

  @Autowired protected MessageService msgService;

  @GetMapping("users")
  public PagingResponse<List<UserDTO>> getUsers(
      @RequestParam(value = "uname", required = false) String unameKey,
      @RequestParam(value = "rname", required = false) String rnameKey) {

    Query query = RequestHelper.getRequestQuery(UserDTO.class);

    Criteria criteria = Criteria.where("status").is(UserStatus.ACTIVE);

    query.addCriteria(criteria);
    
    return userService.select(query);
  }

  @GetMapping("users/{uid}")
  public Response<UserDTO> findUserById(@PathVariable("uid") String uid) {

    UserDTO user = userService.find(uid);
    return Response.Ok(user);
  }

  @GetMapping("users/my/messages")
  public Response<List<MessageDTO>> getMySystemMessage(
      @RequestParam(value = "from", required = false) String from,
      @RequestParam(value = "to", required = false) String to) {

    Query query = RequestHelper.getRequestQuery(MessageDTO.class);

    Criteria toCriteria = new Criteria(),
        fromCriteria = new Criteria();

    if (null != to) {
      toCriteria = Criteria.where("receiver.uname").is(to);
    }
    if (null != from) {
      fromCriteria = Criteria.where("sender.uname").is(from);
    }
    query.addCriteria(new Criteria().andOperator(toCriteria, fromCriteria));
    return msgService.select(query);
  }

  @GetMapping("users/{uid}/exist")
  public Response<Boolean> checkExistByUserName(@PathVariable("uname") String uname) {

    UserDTO user = userService.findByName(uname);
    return Response.Ok(user != null);
  }

  @PostMapping("users")
  public Response<ObjectId> createUser(@RequestBody UserDTO user) throws DataBaseException {

    try {

      UserDTO newUser = userService.save(user);

      return Response.Ok(newUser.getId());
    } catch (DuplicateKeyException e) {
      throw new DataBaseException(BusinessCodes.DB_DUPLICATE_KEY, e);
    }
  }
}
