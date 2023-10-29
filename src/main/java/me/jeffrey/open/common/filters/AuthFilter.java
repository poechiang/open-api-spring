package me.jeffrey.open.common.filters;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import jakarta.servlet.*;
import jakarta.servlet.annotation.WebFilter;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import me.jeffrey.open.advise.GlobalExceptionAdvise;
import me.jeffrey.open.common.Response;
import me.jeffrey.open.exception.AuthorizationException;
import me.jeffrey.open.exception.TokenExpireException;
import me.jeffrey.open.services.CacheRedisService;
import me.jeffrey.open.services.UserService;
import me.jeffrey.open.utils.JwtHelper;
import me.jeffrey.open.utils.RequestHelper;
import org.springframework.beans.factory.annotation.Autowired;

@Slf4j
@WebFilter(urlPatterns = "/api/v1/*")
public class AuthFilter implements Filter {

  List<String> whiteUriList = new ArrayList<String>();
  @Autowired private UserService userService;
  @Autowired private CacheRedisService cacheRedisService;
  @Autowired private GlobalExceptionAdvise globalExceptionAdvise;

  @Override
  public void init(FilterConfig filterConfig) {

    log.info("\n[AuthFilter] initialized...");

    whiteUriList.add(0, "/api/v1/auth/login");
    whiteUriList.add(0, "/api/v1/auth/register");
    whiteUriList.add(0, "/api/v1/auth/verif-code");
  }

  @Override
  public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
      throws IOException, ServletException {
    HttpServletRequest httpRequest = (HttpServletRequest) request;
    HttpServletResponse httpResponse = (HttpServletResponse) response;
    String httpUri = httpRequest.getRequestURI();
    String httpMethod = httpRequest.getMethod();

    log.info("\n[AuthFilter] check request uri \"[{}]{}\"", httpMethod, httpUri);

    if ("OPTIONS".equals(httpMethod) || whiteUriList.contains(httpUri)) {
      chain.doFilter(request, response);
      return;
    }
    try {

      Claims tokenClaims = RequestHelper.getTokenClaims();
      if (null == tokenClaims) {
        throw new JwtException("invalid token!");
      }

      String userId = (String) tokenClaims.get("id");

      if (!cacheRedisService.hasKey(userId)) {
        throw AuthorizationException.Default;
      }
      long redisExpire = cacheRedisService.expire(userId);
      if (redisExpire < 0) {
        throw TokenExpireException.Default;
      }
      cacheRedisService.expire(userId, JwtHelper.JWT_SECRET_EXPIRE_MILLS);
      log.info(
          "\n[AuthFilter] redis expiration {} => {}",
          redisExpire,
          cacheRedisService.expire(userId));
      
      RequestHelper.setCurrentUserId(userId);

      chain.doFilter(request, response);

    } catch (AuthorizationException e) {

      log.info("\n[AuthFilter] {} denied --- {}", httpUri, e.getMessage());

      RequestHelper.enableCors();

      Response<?> r = globalExceptionAdvise.authorizationExceptionHandler(e);
      RequestHelper.sendJSON(r);
    } catch (TokenExpireException e) {

      log.info("\n[AuthFilter] {} denied --- {}", httpUri, e.getMessage());

      RequestHelper.enableCors();

      Response<?> r = globalExceptionAdvise.tokenExpireExceptionHandler(e);
      RequestHelper.sendJSON(r);
    } catch (JwtException e) {

      log.info("\n[AuthFilter] {} denied --- {}", httpUri, e.getMessage());
      Response<?> r =
          globalExceptionAdvise.tokenExpireExceptionHandler(new TokenExpireException(e));
      RequestHelper.enableCors();
      RequestHelper.sendJSON(r);
    } catch (Exception e) {

      log.info("\n[AuthFilter] {} denied  --- unknown exception:{}", httpUri, e.getMessage());
      Response<?> r = globalExceptionAdvise.exceptionHandler(e);

      RequestHelper.enableCors();
      RequestHelper.sendJSON(r);
    }
  }

  @Override
  public void destroy() {

    log.info("\n[AuthFilter] filter destroy ...");
  }
}
