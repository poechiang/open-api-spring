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
    UNAUTHORIZED(4010000),
    /**
     * [AUTH] TOKEN过期
     */
    TOKEN_EXPIRED(4010001),
    
    /**
     * [通用] 鉴权失败
     */
    FORBIDDEN(4030000),
    /**
     * [通用] 资源未找到
     */
    NOT_FOUND(4040000),
    /**
     * [AUTH] 无效参数
     */
    INVALID_ARGUMENT(4000001),
    /**
     * [AUTH] 验证码错误
     */
    INVALID_CAPTCHA(4000002),
    /**
     * [AUTH] 密码错误
     */
    INVALID_PASSWORD(4000003),
    /**
     * 未知的敏感类型
     */
    UNKNOWN_SENSITIVE_TYPE(4000004),
    
    FAILURE(5000000),
    /**
     * [数据库] 键值重复
     */
    DB_DUPLICATE_KEY(5001901);
    
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
