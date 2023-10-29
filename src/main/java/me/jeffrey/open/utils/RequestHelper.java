package me.jeffrey.open.utils;

import com.alibaba.fastjson.JSONObject;
import io.jsonwebtoken.Claims;
import java.io.IOException;
import java.io.PrintWriter;
import java.lang.reflect.Field;
import java.util.*;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import me.jeffrey.open.common.BusinessCodes;
import me.jeffrey.open.common.Response;
import me.jeffrey.open.common.annotations.ExactQuery;
import me.jeffrey.open.common.annotations.FuzzyQuery;
import org.springframework.data.domain.Sort;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

public class RequestHelper {
  public static HttpServletRequest getRequest() {
    ServletRequestAttributes attributes =
        (ServletRequestAttributes) RequestContextHolder.currentRequestAttributes();
    return  attributes.getRequest();
  }

  public static HttpServletResponse getResponse() {
    ServletRequestAttributes attributes =
        (ServletRequestAttributes) RequestContextHolder.currentRequestAttributes();
    return  attributes.getResponse();
  }

  public static HttpSession getSession() {
    return getRequest().getSession();
  }
  //
  public static String getCurrentUserId() {
    HttpSession httpSession = getSession();
    return (String) httpSession.getAttribute("userId");
  }

  public static void setCurrentUserId(String userId) {
    HttpSession httpSession = getSession();
    httpSession.setAttribute("userId", userId);
  }
  
  public static String getCaptchaHeader() {
    return getRequest().getHeader("X-Captcha-Key");
  }
  
  public static String getTokenString() {
    return getRequest().getHeader("token");
  }

  @Nullable
  public static Claims getTokenClaims() {
    try {
      return JwtHelper.checkJwtToken(getTokenString());
    } catch (Exception e) {
      return null;
    }
  }

  public static void enableCors() {
    HttpServletRequest request = getRequest();
    HttpServletResponse response = getResponse();
    String allowOrigin = request.getHeader("Origin");
    String allowHeaders = request.getHeader("Access-Control-Request-Headers");
    response.setHeader("Access-Control-Allow-Origin", allowOrigin);
    response.setHeader("Access-Control-Allow-Credentials", "true");
    response.setHeader("Access-Control-Allow-Expose-Headers", "*");
    response.setHeader(
        "Access-Control-Allow-Methods", "HEAD, OPTIONS, POST, PUT, GET, OPTIONS, DELETE");
    response.setHeader("Access-Control-Allow-Headers", allowHeaders);
    response.setIntHeader("Access-Control-Allow-Max-Age", 3600);
  }

  public static <T> void sendJSON(Response<T> body) throws IOException {

    HttpServletResponse response = getResponse();
    response.setCharacterEncoding("UTF-8");
    response.setContentType("application/json; charset=UTF-8");
    response.setStatus(HttpServletResponse.SC_OK);
    PrintWriter writer = response.getWriter();

    writer.write(JSONObject.toJSONString(body));
    writer.close();
  }

  public static void setStatus(BusinessCodes code) {
    HttpServletResponse resp = getResponse();
    if (code == BusinessCodes.SUCCESS) {
      setStatus(200);
    } else {
      setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
    }
  }

  public static void setStatus(int code) {
    HttpServletResponse resp = getResponse();
    resp.setStatus(code);
  }

  @NotNull
  public static <T> Query getRequestQuery(Class<T> dtoClass) {

    HttpServletRequest request = getRequest();
    String queryString = request.getQueryString();

    Query query = new Query();
    if (null == queryString) {
      return query;
    }
    Criteria criteria = getExactQueryCriteriaFromKeyString(queryString,dtoClass);
    
    Stream<String> queryStream = Arrays.stream(queryString.split("&"));
    queryStream.forEach(
        (String item) -> {
          String[] items = item.split("=");

          if (Objects.equals(items[0], "keys")) {
            Criteria fuzzyCriteria = getFuzzyQueryCriteriaFromKeyString(items[1], dtoClass);
            criteria.andOperator(fuzzyCriteria);
          }
          if (Objects.equals(items[0], "order")) {
            query.with(getSortFromOrderString(items[1]));
          }
        });
    
    setQueryPaging(query, queryString);
    
    query.addCriteria(criteria);
    
    return query;
  }

  /**
   * 根据查询字符串设置模糊查询条件
   *
   * @param queryString 查询字符串
   * @param dtoClass 数据传输对象类
   * @return 查询条件
   */
  @NotNull
  protected static Criteria getExactQueryCriteriaFromKeyString(
      @NotNull String queryString, @NotNull Class<?> dtoClass) {
    Stream<String> queryStream = Arrays.stream(queryString.split("&"));
    // 获取dtoClass上带有ExactQuery注解的所有字段名字
    List<String> exactFieldNames =
        Arrays.stream(dtoClass.getDeclaredFields())
            .filter((Field field) -> null != field.getAnnotation(ExactQuery.class))
            .map((Field field) -> field.getName()).toList();

    Criteria criteria = new Criteria();
    
    
    queryStream.forEach(
            (String item) -> {
              String[] items = item.split("=");
              if(exactFieldNames.contains(items[0])){
                criteria.and(items[0]).is(items[1]);
              }
            });
    
    return criteria;
  }

  /**
   * 根据查询字符串设置模糊查询条件
   *
   * @param keyString 查询字符串
   * @param dtoClass 数据传输对象类
   * @return 查询条件
   */
  @NotNull
  protected static Criteria getFuzzyQueryCriteriaFromKeyString(
      @NotNull String keyString, @NotNull Class<?> dtoClass) {

    Criteria criteria = new Criteria();
    String keys =
        String.join(
            "|",
            Arrays.stream(keyString.split(","))
                .map((String k) -> String.format("(.*%1$s.*)", k))
                .toList());
    Pattern pattern = Pattern.compile(keys);

    // list of the field with [@FuzzyQuery] annotation in specialized dto class
    List<Criteria> fuzzyFields =
        Arrays.stream(dtoClass.getDeclaredFields())
            .filter(f -> null != f.getAnnotation(FuzzyQuery.class))
            .map(f -> Criteria.where(f.getName()).regex(pattern))
            .toList();

    if (fuzzyFields.size() > 0) {
      criteria.orOperator(fuzzyFields);
    }
    return criteria;
  }

  /**
   * 根据查询字符串设置分页
   *
   * @param query 查询对象
   * @param queryString 查询字符串
   */
  protected static void setQueryPaging(@Nullable Query query, String queryString) {

    HashMap<String, Long> map = new HashMap<>();
    
    Stream<String> queryStream = Arrays.stream(queryString.split("&"));
    queryStream.forEach(
        (String item) -> {
          String[] items = item.split("=");
          if (Objects.equals(items[0], "page")) {
            map.put("page", (Long.parseLong(items[1])));
          }
          if (Objects.equals(items[0], "pageSize")) {
            map.put("pageSize", (Long.parseLong(items[1])));
          }
        });

    if (map.containsKey("page")) {
      long page = map.get("page");
      int pageSize = Math.toIntExact(map.getOrDefault("pageSize", 10L));
      
      query.skip((page - 1) * pageSize);
      query.limit(pageSize);
    }
  }

  /**
   * 根据查询字符串获取排序条件
   *
   * @param orderString 查询字符串
   * @return 排序条件
   */
  @NotNull
  protected static Sort getSortFromOrderString(@NotNull String orderString) {

    // items[1]: ...&order=a:D,b:A,c:A,d:D&...
    List<Sort.Order> orderList =
        Arrays.stream(orderString.split(","))
            .map(
                x -> {
                  String[] segments = x.split(":");
                  Sort.Direction direction =Sort.Direction.ASC;
                  if (segments.length >= 2 && Objects.equals(segments[1], "desc")) {
                    direction = Sort.Direction.DESC;
                  }
                  return new Sort.Order(direction, segments[0]);
                })
            .collect(Collectors.toList());
    return Sort.by(orderList);
  }
}
