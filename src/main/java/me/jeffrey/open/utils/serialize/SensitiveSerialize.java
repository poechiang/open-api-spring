package me.jeffrey.open.utils.serialize;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.BeanProperty;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.ser.ContextualSerializer;
import java.io.IOException;
import java.util.Objects;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.SneakyThrows;
import me.jeffrey.open.common.BusinessCodes;
import me.jeffrey.open.common.SensitiveType;
import me.jeffrey.open.common.annotations.Sensitive;
import me.jeffrey.open.exception.SerializeException;
import me.jeffrey.open.utils.Desensitizer;

@NoArgsConstructor
@AllArgsConstructor
public class SensitiveSerialize extends JsonSerializer<String> implements ContextualSerializer {
  private SensitiveType sensitiveType;
  /**
   * 模式字符串
   *
   * @implNote 当指定SensitiveType.CUSTOM时,需要指定使用的匹配模式
   */
  private String pattern;
  /** 掩码字符 */
  private char symbol;

  private Boolean keepLength;

  @SneakyThrows
  @Override
  public void serialize(String value, JsonGenerator gen, SerializerProvider serializers)
      throws IOException {
    switch (sensitiveType) {
      case ADDRESS -> gen.writeString(Desensitizer.address(value, '*'));
      case CN_NAME -> gen.writeString(Desensitizer.cnName(value, symbol));
      case CUSTOM -> gen.writeString(Desensitizer.custom(value, pattern, symbol));
      case EN_NAME -> gen.writeString(Desensitizer.enName(value, symbol));
      case EMAIL -> gen.writeString(Desensitizer.email(value, symbol));
      case ID_NUMBER -> gen.writeString(Desensitizer.idNumber(value, symbol));
      case MOBILE -> gen.writeString(Desensitizer.mobile(value, symbol));
      case TELEPHONE -> gen.writeString(Desensitizer.telephone(value, symbol));
      default -> throw new SerializeException(
          BusinessCodes.UNKNOWN_SENSITIVE_TYPE, String.format("未知的敏感类型参数 {%1$s}", sensitiveType));
    }
  }

  @Override
  public JsonSerializer<?> createContextual(
      SerializerProvider serializerProvider, BeanProperty beanProperty)
      throws JsonMappingException {
    if (beanProperty != null) {
      if (Objects.equals(beanProperty.getType().getRawClass(), String.class)) {
        Sensitive sensitive = beanProperty.getAnnotation(Sensitive.class);
        if (sensitive == null) {
          sensitive = beanProperty.getContextAnnotation(Sensitive.class);
        }
        if (sensitive != null) {
          return new SensitiveSerialize(
              sensitive.type(), sensitive.pattern(), sensitive.symbol(), sensitive.keepLength());
        }
      }
      return serializerProvider.findValueSerializer(beanProperty.getType(), beanProperty);
    }
    return serializerProvider.findNullValueSerializer(null);
  }
}
