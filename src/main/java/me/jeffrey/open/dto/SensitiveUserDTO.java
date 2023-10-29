package me.jeffrey.open.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import java.util.List;
import lombok.Data;
import lombok.ToString;
import lombok.experimental.Accessors;
import me.jeffrey.open.common.SensitiveType;
import me.jeffrey.open.common.UserStatus;
import me.jeffrey.open.common.annotations.FuzzyQuery;
import me.jeffrey.open.common.annotations.Sensitive;
import me.jeffrey.open.utils.serialize.ObjectIdSerializer;
import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.MongoId;

@Data
@ToString
@Accessors(chain = true)
public class SensitiveUserDTO {

  /** 使用 @MongoID 能更清晰的指定 _id 主键 */
  @Id
  @MongoId
  @org.mongojack.ObjectId
  @JsonSerialize(using = ObjectIdSerializer.class)
  private ObjectId id;

  @FuzzyQuery private String uname;
  @FuzzyQuery private String rname;
  @JsonIgnore private String password;
  private String gender;

  @Sensitive(type = SensitiveType.MOBILE)
  private String phone;

  @Sensitive(type = SensitiveType.EMAIL)
  private String email;

  @JsonFormat(shape = JsonFormat.Shape.NUMBER)
  private UserStatus status = UserStatus.INACTIVE;

  private List<ObjectId> inBox;

  private List<ObjectId> outBox;
}
