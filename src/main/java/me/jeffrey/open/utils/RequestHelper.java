package me.jeffrey.open.utils;

import com.alibaba.fastjson.JSONObject;
import io.jsonwebtoken.Claims;

import javax.annotation.Nullable;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import me.jeffrey.open.common.BusinessCodes;
import me.jeffrey.open.common.Response;
import org.bson.types.ObjectId;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.http.HttpRequest;

public class RequestHelper {
  public static HttpServletRequest getRequest() {
    ServletRequestAttributes attributes =
            (ServletRequestAttributes) RequestContextHolder.currentRequestAttributes();
    return attributes.getRequest();
  }
  public static HttpServletResponse getResponse() {
    ServletRequestAttributes attributes =
            (ServletRequestAttributes) RequestContextHolder.currentRequestAttributes();
    return attributes.getResponse();
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
  public static void enableCors(){
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
}
