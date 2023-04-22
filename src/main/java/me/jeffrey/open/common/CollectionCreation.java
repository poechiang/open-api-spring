package me.jeffrey.open.common;

import com.mongodb.client.model.ValidationAction;
import com.mongodb.client.model.ValidationLevel;
import lombok.Data;
import org.springframework.data.mongodb.core.query.CriteriaDefinition;

@Data
public class CollectionCreation {

  private long docSize = 0;
  private long docCount = 0;
  private ValidationLevel level = ValidationLevel.OFF;
  private ValidationAction action = ValidationAction.ERROR;
  private CriteriaDefinition criteria = null;

  public CollectionCreation(
      long docSize,
      long docCount,
      CriteriaDefinition criteria,
      ValidationLevel level,
      ValidationAction action) {
    this.docSize = docSize;
    this.docCount = docCount;
    this.level = level;
    this.action = action;
    this.criteria = criteria;
  }

  public CollectionCreation(
      long docSize, long docCount, CriteriaDefinition criteria, ValidationLevel level) {
    this(docSize, docCount, criteria, level, ValidationAction.ERROR);
  }

  public CollectionCreation(long docSize, long docCount, CriteriaDefinition criteria) {
    this(docSize, docCount, criteria, ValidationLevel.STRICT, ValidationAction.ERROR);
  }

  public CollectionCreation(long docSize, long docCount) {
    this(docSize, docCount, null, ValidationLevel.OFF, null);
  }

  public CollectionCreation() {
    this(0, 0, null, ValidationLevel.OFF, null);
  }
}
