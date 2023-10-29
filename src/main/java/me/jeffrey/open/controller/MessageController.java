package me.jeffrey.open.controller;


import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

import lombok.extern.slf4j.Slf4j;
import me.jeffrey.open.common.BusinessCodes;
import me.jeffrey.open.common.Paging;
import me.jeffrey.open.common.PagingResponse;
import me.jeffrey.open.common.Response;
import me.jeffrey.open.dto.MessageDTO;
import me.jeffrey.open.exception.DataBaseException;
import me.jeffrey.open.services.MessageService;
import me.jeffrey.open.services.UserService;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@CrossOrigin(allowCredentials = "true", originPatterns = "*")
@RequestMapping("/api/v1")
public class MessageController {
  
  @Autowired protected MessageService msgService;
  @Autowired protected UserService userService;

  @GetMapping("messages")
  public PagingResponse<List<MessageDTO>> getMessage(
      @RequestParam(value = "filters", required = false) String filters,
      @RequestParam(value = "from", required = false) String from,
      @RequestParam(value = "to", required = false) String to,
      @RequestParam(value = "page", defaultValue = "1", required = false) int pageIndex,
      @RequestParam(value = "pageSize", defaultValue = "0", required = false) int pageSize) {

    Paging paging = null;
    log.info(
            "filter:{}, from:{}, to:{}, pageIndex:{}, pageSize:{}",
            filters,
            from,
            to,
            pageIndex,
            pageSize);
    
    Criteria filterCriteria = new Criteria(),
            toCriteria = new Criteria(),
            fromCriteria = new Criteria();
    if (null != filters) {
      filterCriteria = filterCriteria.orOperator(Criteria.where("title")
                      .regex(filters),
              Criteria.where("content").regex(filters));
    }
    
    if (null != to) {
      toCriteria = Criteria.where("receiver.uname").is(to);
    }
    if (null != from) {
      fromCriteria = Criteria.where("sender.uname").is(from);
    }

//    Criteria criteria = new Criteria().andOperator(filterCriteria, toCriteria, fromCriteria);
    Criteria criteria = filterCriteria;
    if (pageSize > 0) {
      long total = msgService.count(criteria);
      paging = new Paging(pageIndex, pageSize).setTotal(total);
    }
    
    List<MessageDTO> messages = msgService.selectMessages(criteria, pageIndex, pageSize);
    return PagingResponse.Ok(messages, paging);
  }

  @PostMapping("messages")
  public Response<ObjectId> sendMessage(@RequestBody Map<String,String> data) throws DataBaseException {

    try {
      log.info("sender system message with data:{}", data);
      MessageDTO msg = msgService.sendSystemMessage(data);
      userService.sendMessageTo(data.get("to"),msg);
      return Response.Ok(msg.getId());
    } catch (DuplicateKeyException e) {
      throw new DataBaseException(BusinessCodes.DB_DUPLICATE_KEY, e);
    }
    catch (RuntimeException e){
      e.printStackTrace();
      throw e;
    }
  }
  
}
