package me.jeffrey.open.common;

import static com.mongodb.assertions.Assertions.notNull;
import static java.lang.String.format;

import com.fasterxml.jackson.annotation.JsonEnumDefaultValue;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonValue;

@JsonFormat(shape = JsonFormat.Shape.NUMBER)
public enum BusinessCodes {
    
    
    @JsonEnumDefaultValue
    SUCCESS(0),
    
    /**
     * [通用] 身份验证失败
     */
    UNAUTHORIZED(4010401),
    /**
     * [AUTH] TOKEN过期
     */
    TOKEN_EXPIRED(4010402),
    
    /**
     * [通用] 鉴权失败
     */
    FORBIDDEN(4030403),
    /**
     * [通用] 资源未找到
     */
    NOT_FOUND(4000404),
    /**
     * [AUTH] 无效参数
     */
    INVALID_ARGUMENT(4000410),
    /**
     * [AUTH] 验证码错误
     */
    INVALID_CAPTCHA(4000411),
    /**
     * [AUTH] 密码错误
     */
    INVALID_PASSWORD(4000412),
    
    FAILURE(5000500),
    /**
     * [数据库] 键值重复
     * @implNote implNote
     * @implSpec implSpec
     * @apiNote apiNote
     * @serial serial
     * @see see
     * @since since
     */
    DB_DUPLICATE_KEY(4001100),
    
    /**
     * 未知的敏感类型
     */
    UNKNOWN_SENSITIVE_TYPE(1901000);
    @JsonValue
    public final int value;
    
    BusinessCodes(final int value) {
        this.value = value;
    }
    
    
    
    
    /**
     * Returns the UpdateMode from the string representation
     *
     * @param errorCode the int representation of the validation action.
     * @return the BusinessCodes
     */
    public static BusinessCodes fromInt(final Integer errorCode) {
        notNull("validationAction", errorCode);
        for (BusinessCodes code : BusinessCodes.values()) {
            if (errorCode.equals(code.value)) {
                return code;
            }
        }
        throw new IllegalArgumentException(format("'%s' is not a valid BusinessCodes", errorCode));
    }
    
}
