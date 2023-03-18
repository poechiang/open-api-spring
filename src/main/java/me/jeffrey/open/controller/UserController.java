package me.jeffrey.open.controller;

import java.util.List;
import me.jeffrey.open.dto.User;
import me.jeffrey.open.interfaces.ListResponse;
import me.jeffrey.open.interfaces.Paging;
import me.jeffrey.open.services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/users")
@CrossOrigin("*")
public class UserController {
  @Autowired protected UserService userService;

  @RequestMapping
  public ListResponse<List<User>> getUsers() {
    long total = userService.count();
    Paging paging = new Paging();
    paging.setTotal(total);
    List<User> users = userService.allUsers(1, 10);
    ListResponse<List<User>> body = new ListResponse<List<User>>();
    return (ListResponse<List<User>>) body.setPaging(paging).setPayload(users).setStatus(200);
  }
}
