package me.jeffrey.open.controller;

import io.swagger.annotations.Api;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import lombok.extern.slf4j.Slf4j;
import me.jeffrey.open.common.Response;
import me.jeffrey.open.dto.UserDTO;
import me.jeffrey.open.services.MessageService;
import me.jeffrey.open.services.UserService;
import me.jeffrey.open.utils.RequestHelper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@CrossOrigin(allowCredentials = "true", originPatterns = "*")
@RequestMapping("/api/v1/meta")
@Api(tags = "过滤元数据查询")
public class MetaController {

  @Autowired protected UserService userService;
  @Autowired protected MessageService messageService;

  @GetMapping("filters/{entity}")
  @ResponseBody
  public Response<Map<String, List<String>>> getFilterData(
          @PathVariable("entity") String entity) {
    // 获取查询参数
    Query query = RequestHelper.getRequestQuery(UserDTO.class);
    List<String> fields = new ArrayList<>();

    if (fields.size() == 0) {
      fields.add("uname");
      fields.add("rname");
    }
    Criteria criteria = Criteria.where("filterable").ne(false);
    Map<String, List<String>> mapData = new HashMap<>();
    if (fields.size() > 0) {
      List<UserDTO> users = userService.select(criteria, null, 10);
      fields.forEach(
          (String fieldName) -> {
            try {
              Field field = UserDTO.class.getDeclaredField(fieldName);
              field.setAccessible(true);
              mapData.put(fieldName, users.stream().map(getUserFieldPicker(field)).toList());
            } catch (NoSuchFieldException e) {
              throw new RuntimeException(e);
            }
          });
    }
    return Response.Ok(mapData);
  }

  private Function<UserDTO, String> getUserFieldPicker(Field field) {
    return new Function<UserDTO, String>() {
      @Override
      public String apply(UserDTO userDTO) {
        try {
          return field.get(userDTO).toString();
        } catch (IllegalAccessException e) {
          return "";
        }
      }
    };
  }
}
