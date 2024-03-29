package me.jeffrey.open.services;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.TimeUnit;

import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class CacheRedisService {

  @Resource
  private RedisTemplate redisTemplate;
  /**
   * set /remove expiration duration for key
   *
   * @param key
   * @param milseconds less than or equals to 0 means remove, otherwise set
   * @return true if succeed otherwise false
   */
  public boolean expire(String key, long milseconds) {
    if( milseconds <=0){
      return redisTemplate.boundValueOps(key).persist();
    } else {
      return redisTemplate.expire(key, milseconds, TimeUnit.MILLISECONDS);
    }
  }
  /**
   * get expireation duration for key
   *
   * @param key
   * @return seconds
   */
  public long expire(String key) {
    return redisTemplate.getExpire(key, TimeUnit.MILLISECONDS);
  }
  /**
   * check if the key exists
   *
   * @param key
   * @return true if exists or false
   */
  public boolean hasKey(String key) {
    return redisTemplate.hasKey(key);
  }

  // - - - - - - - - - - - - - - - - - - - - -  String类型 - - - - - - - - - - - - - - - - - - - -

  /**
   * 根据key获取值
   *
   * @param key 键
   * @return 值
   */
  public Object get(String key) {
    return key == null ? null : redisTemplate.opsForValue().get(key);
  }

  /**
   * 将值放入缓存
   *
   * @param key 键
   * @param value 值
   * @return true成功 false 失败
   */
  public void set(String key, String value) {
    redisTemplate.opsForValue().set(key, value);
  }

  /**
   * 将值放入缓存并设置时间
   *
   * @param key 键
   * @param value 值
   * @param milseconds 时间(秒) -1为无期限
   * @return true成功 false 失败
   */
  public void set(String key, String value, long milseconds) {
    if (milseconds > 0) {
      redisTemplate.opsForValue().set(key, value, milseconds, TimeUnit.MILLISECONDS);
    } else {
      redisTemplate.opsForValue().set(key, value);
    }
  }

  /**
   * 批量添加 key (重复的键会覆盖)
   *
   * @param keyAndValue
   */
  public void set(Map<String, String> keyAndValue) {
    redisTemplate.opsForValue().multiSet(keyAndValue);
  }

  /**
   * 批量添加 key-value 只有在键不存在时,才添加 map 中只要有一个key存在,则全部不添加
   *
   * @param keyAndValue
   */
  public void setIfAbsnet(Map<String, String> keyAndValue) {
    redisTemplate.opsForValue().multiSetIfAbsent(keyAndValue);
  }

  /**
   * 对一个 key-value 的值进行加减操作, 如果该 key 不存在 将创建一个key 并赋值该 number 如果 key 存在,但 value 不是长整型 ,将报错
   *
   * @param key
   * @param number
   */
  public Long increment(String key, long number) {
    return redisTemplate.opsForValue().increment(key, number);
  }

  /**
   * 对一个 key-value 的值进行加减操作, 如果该 key 不存在 将创建一个key 并赋值该 number 如果 key 存在,但 value 不是 纯数字 ,将报错
   *
   * @param key
   * @param number
   */
  public Double increment(String key, double number) {
    return redisTemplate.opsForValue().increment(key, number);
  }

  // - - - - - - - - - - - - - - - - - - - - -  set类型 - - - - - - - - - - - - - - - - - - - -

  /**
   * 将数据放入set缓存
   *
   * @param key 键
   * @return
   */
  public void sSet(String key, String value) {
    redisTemplate.opsForSet().add(key, value);
  }


  /**
   * 随机获取变量中指定个数的元素
   *
   * @param key 键
   * @param count 值
   * @return
   */
  public void randomMembers(String key, long count) {
    redisTemplate.opsForSet().randomMembers(key, count);
  }

  /**
   * 随机获取变量中的元素
   *
   * @param key 键
   * @return
   */
  public Object randomMember(String key) {
    return redisTemplate.opsForSet().randomMember(key);
  }

  /**
   * 弹出变量中的元素
   *
   * @param key 键
   * @return
   */
  public Object pop(String key) {
    return redisTemplate.opsForSet().pop("setValue");
  }

  /**
   * 获取变量中值的长度
   *
   * @param key 键
   * @return
   */
  public long size(String key) {
    return redisTemplate.opsForSet().size(key);
  }

  /**
   * 根据value从一个set中查询,是否存在
   *
   * @param key 键
   * @param value 值
   * @return true 存在 false不存在
   */
  public boolean sHasKey(String key, Object value) {
    return redisTemplate.opsForSet().isMember(key, value);
  }

  /**
   * 检查给定的元素是否在变量中。
   *
   * @param key 键
   * @param obj 元素对象
   * @return
   */
  public boolean isMember(String key, Object obj) {
    return redisTemplate.opsForSet().isMember(key, obj);
  }

  /**
   * 转移变量的元素值到目的变量。
   *
   * @param key 键
   * @param value 元素对象
   * @param destKey 元素对象
   * @return
   */
  public boolean move(String key, String value, String destKey) {
    return redisTemplate.opsForSet().move(key, value, destKey);
  }

  /**
   * 批量移除set缓存中元素
   *
   * @param key 键
   * @param values 值
   * @return
   */
  public void remove(String key, Object... values) {
    redisTemplate.opsForSet().remove(key, values);
  }


  // - - - - - - - - - - - - - - - - - - - - -  hash类型 - - - - - - - - - - - - - - - - - - - -

  /**
   * 加入缓存
   *
   * @param key 键
   * @param map 键
   * @return
   */
  public void add(String key, Map<String, String> map) {
    redisTemplate.opsForHash().putAll(key, map);
  }

  /**
   * 获取 key 下的 所有 hashkey 和 value
   *
   * @param key 键
   * @return
   */
  public Map<Object, Object> getHashEntries(String key) {
    return redisTemplate.opsForHash().entries(key);
  }

  /**
   * 验证指定 key 下 有没有指定的 hashkey
   *
   * @param key
   * @param hashKey
   * @return
   */
  public boolean hashKey(String key, String hashKey) {
    return redisTemplate.opsForHash().hasKey(key, hashKey);
  }

  /**
   * 获取指定key的值string
   *
   * @param key 键
   * @param key2 键
   * @return
   */
  public String getMapString(String key, String key2) {
    return redisTemplate.opsForHash().get("map1", "key1").toString();
  }

  /**
   * 获取指定的值Int
   *
   * @param key 键
   * @param key2 键
   * @return
   */
  public Integer getMapInt(String key, String key2) {
    return (Integer) redisTemplate.opsForHash().get("map1", "key1");
  }

  /**
   * 弹出元素并删除
   *
   * @param key 键
   * @return
   */
  public String popValue(String key) {
    return redisTemplate.opsForSet().pop(key).toString();
  }

  /**
   * 删除指定 hash 的 HashKey
   *
   * @param key
   * @param hashKeys
   * @return 删除成功的 数量
   */
  public void delete(String key, String... hashKeys) {
    redisTemplate.opsForHash().delete(key, hashKeys);
  }

  /**
   * 给指定 hash 的 hashkey 做增减操作
   *
   * @param key
   * @param hashKey
   * @param number
   * @return
   */
  public Long increment(String key, String hashKey, long number) {
    return redisTemplate.opsForHash().increment(key, hashKey, number);
  }

  /**
   * 给指定 hash 的 hashkey 做增减操作
   *
   * @param key
   * @param hashKey
   * @param number
   * @return
   */
  public Double increment(String key, String hashKey, Double number) {
    return redisTemplate.opsForHash().increment(key, hashKey, number);
  }

  /**
   * 获取 key 下的 所有 hashkey 字段
   *
   * @param key
   * @return
   */
  public Set<Object> hashKeys(String key) {
    return redisTemplate.opsForHash().keys(key);
  }

  /**
   * 获取指定 hash 下面的 键值对 数量
   *
   * @param key
   * @return
   */
  public Long hashSize(String key) {
    return redisTemplate.opsForHash().size(key);
  }


  /**
   * 获取集合指定位置的值。
   *
   * @param key
   * @param index
   * @return
   */
  public Object index(String key, long index) {
    return redisTemplate.opsForList().index("list", 1);
  }

  /**
   * 把最后一个参数值放到指定集合的第一个出现中间参数的前面， 如果中间参数值存在的话。
   *
   * @param key
   * @param pivot
   * @param value
   * @return
   */
  public void leftPush(String key, String pivot, String value) {
    redisTemplate.opsForList().leftPush(key, pivot, value);
  }

  /**
   * 向左边批量添加参数元素。
   *
   * @param key
   * @param values
   */
  public void leftPushAll(String key, String... values) {
    //        redisTemplate.opsForList().leftPushAll(key,"w","x","y");
    redisTemplate.opsForList().leftPushAll(key, values);
  }

  /**
   * 向集合最右边添加元素。
   *
   * @param key
   * @param value
   */
  public void leftPushAll(String key, String value) {
    redisTemplate.opsForList().rightPush(key, value);
  }

  /**
   * 向左边批量添加参数元素。
   *
   * @param key
   * @param values
   */
  public void rightPushAll(String key, String... values) {
    // redisTemplate.opsForList().leftPushAll(key,"w","x","y");
    redisTemplate.opsForList().rightPushAll(key, values);
  }

  /**
   * 向已存在的集合中添加元素。
   *
   * @param key
   */
  public long listLength(String key) {
    return redisTemplate.opsForList().size(key);
  }

  /**
   * 移除集合中的左边第一个元素。
   *
   * @param key
   */
  public void leftPop(String key) {
    redisTemplate.opsForList().leftPop(key);
  }

  /**
   * 移除集合中左边的元素在等待的时间里，如果超过等待的时间仍没有元素则退出。
   *
   * @param key
   * @return
   */
  public void leftPop(String key, long timeout, TimeUnit unit) {
    redisTemplate.opsForList().leftPop(key, timeout, unit);
  }

  /**
   * 移除集合中右边的元素。
   *
   * @param key
   */
  public void rightPop(String key) {
    redisTemplate.opsForList().rightPop(key);
  }

  /**
   * 移除集合中右边的元素在等待的时间里，如果超过等待的时间仍没有元素则退出。
   *
   * @param key
   */
  public void rightPop(String key, long timeout, TimeUnit unit) {
    redisTemplate.opsForList().rightPop(key, timeout, unit);
  }
}
