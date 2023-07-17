package me.jeffrey.open.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import lombok.Data;
import lombok.ToString;
import lombok.experimental.Accessors;
import me.jeffrey.open.common.Phone;
import me.jeffrey.open.common.SensitiveType;
import me.jeffrey.open.common.UserStatus;
import me.jeffrey.open.common.annotations.Sensitive;
import me.jeffrey.open.utils.serialize.ObjectIdSerializer;
import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.MongoId;

import java.util.List;

@Data
@ToString
@Accessors(chain = true)
public class User {
  
  /** 使用 @MongoID 能更清晰的指定 _id 主键 */
  @Id @MongoId
  @org.mongojack.ObjectId
  @JsonSerialize(using = ObjectIdSerializer.class)
  private ObjectId id;
  
  private String uname;
  @JsonIgnore
  private String password;
  private String gender;
  private Phone phone;
  @Sensitive(type=SensitiveType.EMAIL)
  private String email;
  @JsonFormat(shape = JsonFormat.Shape.NUMBER)
  private UserStatus status = UserStatus.INACTIVE;
  
  private List<ObjectId> inBox;
  
  private List<ObjectId> outBox;
  
  
  
  @JsonProperty
  @Sensitive(type = SensitiveType.MOBILE)
  public String getMobile(){
    return String.format("(%1$s)%2$s",phone.getCountry(),phone.getMobile());
  }
  @JsonProperty
  @Sensitive(type = SensitiveType.TELEPHONE)
  public String getTel(){
    return String.format("(%1$s)%2$s-%3$s",phone.getCountry(),phone.getArea(),phone.getTel());
  }
  
  
}
