package me.jeffrey.open.services;

import java.util.Date;
import java.util.List;
import java.util.Map;
import lombok.extern.slf4j.Slf4j;
import me.jeffrey.open.common.MessageType;
import me.jeffrey.open.common.SelectResult;
import me.jeffrey.open.dto.MessageDTO;
import org.apache.commons.lang3.ObjectUtils;
import org.springframework.data.mongodb.core.aggregation.*;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class MessageService extends DocumentService<MessageDTO> {

  @Override
  protected String getCollectionName() {
    return "messages";
  }

  public List<MessageDTO> selectMessages(Criteria criteria, long pageIndex, int pageSize) {

    LookupOperation receiverLookupOperation =
        Aggregation.lookup("users", "_id", "inBox", "receiver");

    LookupOperation senderLookupOperation = Aggregation.lookup("users", "_id", "outBox", "sender");
    Aggregation aggregation;
    if (pageSize > 0) {
      log.info("pagesize:{}", pageSize);
      long skip = (ObjectUtils.defaultIfNull(pageIndex, (long) 1) - 1) * pageSize;
      if (skip > 0) {
        skip--;
      }

      aggregation =
          Aggregation.newAggregation(
              Aggregation.match(criteria),
              receiverLookupOperation,
              senderLookupOperation,
              Aggregation.project("_id", "title", "content", "sendDate", "type", "status")
                  .and("receiver")
                  .arrayElementAt(0)
                  .as("receiver")
                  .and("sender")
                  .arrayElementAt(0)
                  .as("sender"),
              Aggregation.match(criteria),
              Aggregation.skip(skip),
              Aggregation.limit(pageSize));

    } else {
      aggregation =
          Aggregation.newAggregation(
              receiverLookupOperation,
              senderLookupOperation,
              Aggregation.project("_id", "title", "content", "sendDate", "type", "status")
                  .and("receiver")
                  .arrayElementAt(0)
                  .as("receiver")
                  .and("sender")
                  .arrayElementAt(0)
                  .as("sender"),
              Aggregation.match(criteria));
    }

    AggregationResults<MessageDTO> aggregationResults =
        mongoTemplate.aggregate(aggregation, getCollectionName(), MessageDTO.class);
    return aggregationResults.getMappedResults();
  }

  public SelectResult<MessageDTO> selectForSender(String senderId, int pageIndex, int pageSize) {
    SelectResult<MessageDTO> selectResult = new SelectResult<>();
    
    Criteria criteria = Criteria.where("senderId").is(senderId);
    selectResult.setTotal(this.count(criteria)).setList(selectMessages(criteria, pageIndex, pageSize));
    return selectResult;
  }

  public List<MessageDTO> selectForReceiver(String receiverId, int pageIndex, int pageSize) {
    return selectMessages(Criteria.where("receiverId").is(receiverId), pageIndex, pageSize);
  }

  public MessageDTO sendSystemMessage(Map<String, String> data) {

    Date date = new Date();

    MessageDTO msg = new MessageDTO().setTitle(data.get("title")).setContent(data.get("content"));
    msg.setType(MessageType.NOTIFICATION).setSendDate(date.getTime());
    return save(msg);
  }
  
}
