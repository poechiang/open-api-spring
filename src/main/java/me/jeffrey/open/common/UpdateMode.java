package me.jeffrey.open.common;

import static com.mongodb.assertions.Assertions.notNull;
import static java.lang.String.format;

public enum UpdateMode {
  FIRST("first"),
  MANY("many"),
  INSERT("insert");

  private final String value;

  UpdateMode(final String value) {
    this.value = value;
  }

  /**
   * Returns the UpdateMode from the string representation
   *
   * @param updateMode the string representation of the validation action.
   * @return the UpdateMode
   */
  public static UpdateMode fromString(final String updateMode) {
    notNull("validationAction", updateMode);
    for (UpdateMode mode : UpdateMode.values()) {
      if (updateMode.equalsIgnoreCase(mode.value)) {
        return mode;
      }
    }
    throw new IllegalArgumentException(format("'%s' is not a valid updateMode", updateMode));
  }

  /**
   * @return the String representation of the UpdateMode
   */
  public String getValue() {
    return value;
  }
}
