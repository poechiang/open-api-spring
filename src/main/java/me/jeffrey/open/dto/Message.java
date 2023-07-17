package me.jeffrey.open.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import lombok.Data;
import lombok.ToString;
import lombok.experimental.Accessors;
import me.jeffrey.open.common.MessageType;
import me.jeffrey.open.common.UserStatus;
import me.jeffrey.open.utils.serialize.ObjectIdSerializer;
import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.MongoId;

import java.util.List;


@Data
@ToString
@Accessors(chain = true)
public class Message {
    
    
    /** 使用 @MongoID 能更清晰的指定 _id 主键 */
    @Id
    @MongoId
    @org.mongojack.ObjectId
    @JsonSerialize(using = ObjectIdSerializer.class)
    private ObjectId id;
    
    @DBRef
    private User sender;
    
    @DBRef
    private User receiver;
    private String title;
    
    private String content;
    
    @JsonFormat(shape = JsonFormat.Shape.NUMBER)
    private MessageType type;
    
    private long sendDate;
    @JsonFormat(shape = JsonFormat.Shape.NUMBER)
    private UserStatus status = UserStatus.INACTIVE;
    
}
