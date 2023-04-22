package me.jeffrey.open.common;

import com.fasterxml.jackson.annotation.JsonEnumDefaultValue;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonRawValue;
import com.fasterxml.jackson.annotation.JsonValue;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.EnumSerializer;
import jdk.jfr.Description;

import static com.mongodb.assertions.Assertions.notNull;
import static java.lang.String.format;

public enum BusinessCodes {
    
    
    @JsonEnumDefaultValue
    SUCCESS(0),
    
    FAILURE(5000500),
    /**
     * [数据库] 键值重复
     * @implNote implNote
     * @implSpec implSpec
     * @apiNote apiNote
     * @deprecated deprecated
     *
     * @serial serial
     * @see see
     * @since since
     */
    DB_DUPLICATE_KEY(4001100),
    
    /**
     * 未知的敏感类型
     */
    UNKOWN_SENSITIVE_TYPE(1901000);
    private final int value;
    
    BusinessCodes(final int value) {
        this.value = value;
    }
    
    /**
     * @return the String representation of the UpdateMode
     */
    public int getValue() {
        return value;
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
