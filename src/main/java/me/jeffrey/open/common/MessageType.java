package me.jeffrey.open.common;

import static com.mongodb.assertions.Assertions.notNull;
import static java.lang.String.format;

public enum MessageType {
  OTHER(0),
  NOTIFICATION(1),
  EMAIL(2);

  private final int value;

  MessageType(final int value) {
    this.value = value;
  }

  /**
   * Returns the UserStatus from the string representation
   *
   * @param systemMessageType the string representation of the validation action.
   * @return the UserStatus
   */
  public static MessageType fromString(final int systemMessageType) {
    notNull("validationAction", systemMessageType);
    for (MessageType mode : MessageType.values()) {
      if (systemMessageType == (mode.value)) {
        return mode;
      }
    }
    throw new IllegalArgumentException(format("'%s' is not a valid userStatus", systemMessageType));
  }

  /**
   * @return the int representation of the UserStatus
   */
  public int getValue() {
    return value;
  }
}
