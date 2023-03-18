package me.jeffrey.open.dto;

import lombok.Data;
import lombok.ToString;
import lombok.experimental.Accessors;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.MongoId;

@Data
@ToString
@Accessors(chain = true)
public class User {

  /** 使用 @MongoID 能更清晰的指定 _id 主键 */
  @Id @MongoId private String id;

  private String uname;
  private String gender;
  private String phone;
  private String email;

  private Integer status;
}
