package me.jeffrey.open.dto;

import static com.mongodb.assertions.Assertions.notNull;
import static java.lang.String.format;

/**
 * 用户状态
 */
public enum UserStatus {
    /**
     * 未激活
     */
    INACTIVE(0),
    /**
     * 正常状态
     */
    ACTIVE(1),
    /**
     * 冻结状态
     */
    FROZEN(-1),
    /**
     * 异常状态
     */
    ABNORMAL(-2);
    
    private final int value;
    
    UserStatus(final int value) {
        this.value = value;
    }
    
    /**
     * @return the int representation of the UserStatus
     */
    public int getValue() {
        return value;
    }
    
    /**
     * Returns the UserStatus from the string representation
     *
     * @param userStatus the string representation of the validation action.
     * @return the UserStatus
     */
    public static UserStatus fromString(final int userStatus) {
        notNull("validationAction", userStatus);
        for (UserStatus mode : UserStatus.values()) {
            if (userStatus == (mode.value)) {
                return mode;
            }
        }
        throw new IllegalArgumentException(format("'%s' is not a valid userStatus", userStatus));
    }
}
