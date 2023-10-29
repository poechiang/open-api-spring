package me.jeffrey.open.services;

import static java.lang.String.format;

import com.mongodb.client.result.DeleteResult;
import com.mongodb.client.result.UpdateResult;
import java.lang.reflect.ParameterizedType;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import lombok.extern.slf4j.Slf4j;
import me.jeffrey.open.common.Paging;
import me.jeffrey.open.common.PagingResponse;
import me.jeffrey.open.common.Response;
import me.jeffrey.open.common.UpdateMode;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Service;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

@Slf4j
@Service
public abstract class DocumentService<T> {

  @Autowired protected MongoTemplate mongoTemplate;
  
  /** 设置集合名称 */
  protected abstract String getCollectionName();
  

  protected Class<T> getEntityClass() {
    return (Class<T>)
        ((ParameterizedType) getClass().getGenericSuperclass()).getActualTypeArguments()[0];
  }

  /**
   * 插入【一条】文档数据，如果文档信息已经【存在就抛出异常】
   *
   * @return 插入的文档信息
   */
  public T insert(T data) {

    T record = mongoTemplate.insert(data, getCollectionName());

    log.info("[DOCUMENT] {} 写入文档 ", record);
    return record;
  }

  /**
   * 插入【多条】文档数据，如果文档信息已经【存在就抛出异常】
   *
   * @return 插入的多个文档信息
   */
  public Collection<T> insert(ArrayList<T> list) {
    Collection<T> records = mongoTemplate.insert(list, getCollectionName());
    // 输出存储结果
    for (T r : records) {
      log.info("[DOCUMENT] {} 写入文档 ", r);
    }
    return records;
  }

  /**
   * 插入【一条】文档数据，如果文档信息已经【存在就抛出异常】
   *
   * @return 插入的文档信息
   */
  public T save(T data) {

    T record = mongoTemplate.save(data, getCollectionName());
    
    log.info("[DOCUMENT] {} 写入文档 ", record);
    return record;
  }

  /**
   * 删除集合中【符合条件】的【一个]或[多个】文档
   *
   * @return 删除用户信息的结果
   */
  public long remove(Criteria criteria) {

    // 创建查询对象，然后将条件对象添加到其中
    Query query = new Query(criteria);
    // 执行删除查找到的匹配的全部文档信息
    DeleteResult result = mongoTemplate.remove(query, getCollectionName());
    long deletedCount = result.getDeletedCount();

    // 输出结果信息
    log.info(format("成功删除 %s 条文档信息", deletedCount));
    return deletedCount;
  }

  /**
   * 删除【符合条件】的【单个文档】，并返回删除的文档。
   *
   * @return 删除的用户信息
   */
  public Object findAndRemove(Criteria criteria) {

    Query query = new Query(criteria);
    // 执行删除查找到的匹配的第一条文档,并返回删除的文档信息
    T result = mongoTemplate.findAndRemove(query, getEntityClass(), getCollectionName());
    // 输出结果信息
    log.info(format("成功删除文档信息，文档内容为：%s", result));
    return result;
  }

  /**
   * 删除【符合条件】的【全部文档】，并返回删除的文档。
   *
   * @return 删除的全部用户信息
   */
  public Object selectAndRemove(Criteria criteria) {

    Query query = new Query(criteria);
    // 执行删除查找到的匹配的全部文档,并返回删除的全部文档信息
    List<T> resultList =
        mongoTemplate.findAllAndRemove(query, getEntityClass(), getCollectionName());
    // 输出结果信息
    log.info(format("成功删除文档信息，文档内容为：%s", resultList));
    return resultList;
  }

  /**
   * 更新集合中【匹配】查询到的第一条文档数据，如果没有找到就【创建并插入一个新文档】
   *
   * @return 执行更新的结果的影响记录数
   */
  public long update(Update update, Criteria criteria, UpdateMode updateMode) {

    // 创建查询对象，然后将条件对象添加到其中
    Query query = new Query(criteria);
    UpdateResult updateResult;
    if (updateMode == UpdateMode.INSERT) {
      updateResult = mongoTemplate.upsert(query, update, getEntityClass(), getCollectionName());
    } else if (updateMode == UpdateMode.FIRST) {
      updateResult =
          mongoTemplate.updateFirst(query, update, getEntityClass(), getCollectionName());
    } else if (updateMode == UpdateMode.MANY) {
      updateResult =
          mongoTemplate.updateMulti(query, update, getEntityClass(), getCollectionName());
    } else {
      throw new IllegalArgumentException(format("'%s' is not a valid updateMode", updateMode));
    }

    long matchedCount = updateResult.getMatchedCount();
    long modifiedCount = updateResult.getModifiedCount();
    String resultInfo = format("总共匹配到 %s 条数据,修改了 %s 条数据", matchedCount, modifiedCount);
    log.info("更新结果：{}", resultInfo);
    return modifiedCount;
  }

  /**
   * 根据【文档ID】查询集合中文档数据
   *
   * @return 文档信息
   */
  public T find(String id) {
    ObjectId oid = new ObjectId(id);
    T r = mongoTemplate.findById(id, getEntityClass(), getCollectionName());
    // 输出结果
    log.info("用户信息：{} {} {}", id,oid,r);
    return r;
  }

  /**
   * 根据【条件】查询集合中【符合条件】的文档，只取【第一条】数据
   *
   * @return 符合条件的第一条文档
   */
  public T find(Criteria criteria) {

    // 创建查询对象，然后将条件对象添加到其中
    Query query = new Query(criteria);
    // 查询一条文档，如果查询结果中有多条文档，那么就取第一条
    T r = mongoTemplate.findOne(query, getEntityClass(), getCollectionName());
    // 输出结果
    log.info("用户信息：{}", r);
    return r;
  }

  /**
   * 查询集合中的【全部】文档数据
   *
   * @return 全部文档列表
   */
  public List<T> select() {
    System.out.println(this.getCollectionName());
    System.out.println(this.getEntityClass());
    return select(null, "", 0, 0);
  }

  /**
   * 根据【条件】查询集合中【符合条件】的文档，获取其【文档列表】
   *
   * @return 符合条件的文档列表
   */
  public List<T> select(Criteria criteria) {
    return select(criteria, "", 0, 0);
  }

  /**
   * 根据【条件】查询集合中【符合条件】的文档，获取其【文档列表】并【排序】
   *
   * @return 符合条件的文档列表
   */
  public List<T> select(Criteria criteria, String sort) {
    return select(criteria, sort, 0, 0);
  }

  /**
   * 根据【单个条件】查询集合中的文档数据，并【按指定字段进行排序】与【限制指定数目】
   *
   * @return 符合条件的文档列表
   */
  public List<T> select(Criteria criteria, String sort, int limit) {
    return select(criteria, sort, limit, 0);
  }

  /**
   * 根据【单个条件】查询集合中的文档数据，并【按指定字段进行排序】与【并跳过指定数目】
   *
   * @return 符合条件的文档列表
   */
  public List<T> select(Criteria criteria, String sort, int limit, long skip) {
    // 创建查询对象，然后将条件对象添加到其中
    Query query = new Query();
    if (null != criteria) {
      query = query.addCriteria(criteria);
    }
    if (null !=sort && !sort.isEmpty()) {
      query = query.with(Sort.by(sort));
    }
    if (skip > 0) {
      query = query.skip(skip);
    }
    if (limit > 0) {
      query = query.limit(limit);
    }
    // 查询并返回结果
    List<T> documentList = mongoTemplate.find(query, getEntityClass(), getCollectionName());
    // 输出结果
    for (T r : documentList) {
      log.info("[MongoDB] select：{}", r);
    }
    
    return documentList;
  }

  public PagingResponse<List<T>> select (Query query){
    
    // 查询并返回结果
    List<T> documentList = mongoTemplate.find(query, getEntityClass(), getCollectionName());
    
    Paging paging = new Paging();
    log.info("cache page info, page:{}, pageSize:{}",query.getSkip(),query.getLimit());
    paging.setPage(query.getSkip()).setPageSize(query.getLimit());
    query.skip(0).limit(0);
    
    log.info("cache page info, total:{}",paging.getTotal());
    
    paging.setTotal(mongoTemplate.count(query, getEntityClass(), getCollectionName()));
    
    return PagingResponse.Ok(documentList, paging);
  }
  /**
   * 统计集合中文档【数量】
   *
   * @return 符合条件的文档列表
   */
  public long count() {
    return count(null);
  }
  
  /**
   * 统计集合中符合【查询条件】的文档【数量】
   *
   * @return 符合条件的文档列表
   */
  public long count(Criteria criteria) {
    // 创建查询对象，然后将条件对象添加到其中
    Query query = new Query();
    if (null != criteria) {
      query.addCriteria(criteria);
    }
    // 查询并返回结果
    long count = mongoTemplate.count(query, getEntityClass(), getCollectionName());
    // 输出结果
    log.info("符合条件的文档数量：{}", count);
    return count;
  }
}
