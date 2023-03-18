package me.jeffrey.open.services;

import com.mongodb.client.model.ValidationAction;
import com.mongodb.client.model.ValidationLevel;
import jakarta.annotation.Resource;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import me.jeffrey.open.interfaces.CollectionCreation;
import org.bson.Document;
import org.bson.conversions.Bson;
import org.springframework.data.mongodb.core.CollectionOptions;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.CriteriaDefinition;
import org.springframework.data.mongodb.core.validation.Validator;
import org.springframework.stereotype.Service;

@Service
public class CollectionService {

  @Resource private MongoTemplate mongoTemplate;

  /**
   * 用指定名称和选项创建集合
   *
   * @param collectionName 集合名称
   * @param options 创建选项
   * @return true：创建成功； false：创建失败；
   */
  public Boolean create(String collectionName, CollectionCreation options) {

    if (null == options) {
      mongoTemplate.createCollection(collectionName);
      return mongoTemplate.collectionExists(collectionName);
    }

    CollectionOptions collectionOptions = CollectionOptions.empty();

    long maxSize = options.getDocSize();
    long docCount = options.getDocCount();
    CriteriaDefinition criteria = options.getCriteria();
    ValidationLevel level = options.getLevel();
    ValidationAction action = options.getAction();

    if (maxSize > 0) {
      collectionOptions = collectionOptions.capped().size(maxSize);
    }

    if (docCount > 0) {
      collectionOptions = collectionOptions.maxDocuments(docCount);
    }

    if (ValidationLevel.OFF == level) {
      collectionOptions = collectionOptions.schemaValidationLevel(ValidationLevel.OFF);
    } else if (null != criteria) {
      collectionOptions =
          collectionOptions
              .validator(Validator.criteria(criteria))
              .schemaValidationLevel(level)
              .schemaValidationAction(action);
    }

    mongoTemplate.createCollection(collectionName, collectionOptions);

    return mongoTemplate.collectionExists(collectionName);
  }

  /**
   * 用指定名称创建集合
   *
   * @param collectionName 集合名称
   * @return true：创建成功； false：创建失败；
   */
  public Boolean create(String collectionName) {
    return create(collectionName, null);
  }

  /**
   * 用指定名称创建固定大小的集合
   *
   * @param collectionName 集合名称
   * @param size 限制文档大小
   * @param count 限制集合文档数量
   * @return true：创建成功； false：创建失败；
   */
  public Boolean create(String collectionName, Long size, Long count) {
    return create(collectionName, new CollectionCreation(size, count));
  }

  /**
   * 创建指定名称的校验集合
   *
   * @param collectionName 集合名称
   * @param criteria 校验规则
   * @param level 级别
   * @param action 校验不通过后续动作
   * @return true：创建成功； false：创建失败；
   */
  public Boolean create(
      String collectionName,
      CriteriaDefinition criteria,
      ValidationLevel level,
      ValidationAction action) {
    return create(collectionName, new CollectionCreation(0, 0, criteria, level, action));
  }

  /**
   * 检查指定名称的集合/视图是否存在
   *
   * @param collectionName 集合名称
   * @return true：存在； false：不存在；
   */
  public Boolean exist(String collectionName) {
    return mongoTemplate.collectionExists(collectionName);
  }

  public Set<String> list() {
    return mongoTemplate.getCollectionNames();
  }

  public Boolean drop(String collectionName) {
    mongoTemplate.getCollection(collectionName).drop();
    // 检测新的集合是否存在，返回删除结果
    return !mongoTemplate.collectionExists(collectionName);
  }

  /**
   * 创建视图
   *
   * @return 创建视图结果
   */
  public Boolean view(String viewName, String collectionName, String json) {
    // 定义视图的管道,可是设置视图显示的内容多个筛选条件
    List<Bson> pipeline = new ArrayList<>();
    // 设置条件，用于筛选集合中的文档数据，只有符合条件的才会映射到视图中
    pipeline.add(Document.parse(json));
    // 执行创建视图
    mongoTemplate.getDb().createView(viewName, collectionName, pipeline);
    // 检测新的集合是否存在，返回创建结果
    return mongoTemplate.collectionExists(viewName);
  }

  /**
   * 删除视图
   *
   * @return 删除视图结果
   */
  public Boolean dropView(String viewName) {

    // 检测视图是否存在
    if (mongoTemplate.collectionExists(viewName)) {
      // 删除视图
      mongoTemplate.getDb().getCollection(viewName).drop();
      return true;
    }
    // 检测新的集合是否存在，返回创建结果
    return !mongoTemplate.collectionExists(viewName);
  }
}
