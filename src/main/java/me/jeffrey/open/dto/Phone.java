package me.jeffrey.open.dto;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.ToString;
import lombok.experimental.Accessors;

@Data
@ToString
@Accessors(chain = true)
class Phone {
  
  /**
   * 国家代码
   */
  @ApiModelProperty("国家代码")
  private String country;
  /**
   * 区号
   */
  @ApiModelProperty("区号")
  private String area;
  /**
   * 手机号码
   */
  @ApiModelProperty("手机号码")
  private String mobile;
  /**
   * 固定电话
   */
  @ApiModelProperty("固定电话")
  private String tel;
}