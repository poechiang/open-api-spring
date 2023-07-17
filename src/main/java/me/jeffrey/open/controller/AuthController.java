package me.jeffrey.open.controller;

import io.jsonwebtoken.Claims;
import io.swagger.annotations.Api;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.Objects;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import lombok.extern.slf4j.Slf4j;
import me.jeffrey.open.common.BusinessCodes;
import me.jeffrey.open.common.LoginData;
import me.jeffrey.open.common.LoginResult;
import me.jeffrey.open.common.Response;
import me.jeffrey.open.dto.User;
import me.jeffrey.open.services.CacheRedisService;
import me.jeffrey.open.services.UserService;
import me.jeffrey.open.utils.ImageVerificationCode;
import me.jeffrey.open.utils.JwtHelper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@CrossOrigin(allowCredentials = "true", originPatterns = "*")
@RequestMapping("/api/v1/auth")
@Api(tags = "鉴权")
public class AuthController {

  @Autowired protected CacheRedisService cacheRedisService;
  @Autowired protected UserService userService;

  static final String CAPTCHA_SESSION_KEY = "captcha_code";

  @GetMapping("me")
  @ResponseBody
  public Response<Object> getCurrentLoginUser(
      HttpServletRequest request, HttpServletResponse response) throws IOException {
    String token = request.getHeader("token");
    Claims tokenData = JwtHelper.checkJwtToken(token);

    if (tokenData == null) {
      return Response.Fault("无效的token", BusinessCodes.INVALID_ARGUMENT);
    }
    String uname = (String) tokenData.get("uname");
    ;

    if (uname == null) {
      return Response.Fault("您当前未登录", BusinessCodes.UNAUTHORIZED);
    }
    User user = userService.findByName(uname);
    if (user == null) {
      return Response.Fault("当前用户不存在", BusinessCodes.NOT_FOUND);
    }
    user.setPassword(null);
    return Response.Ok(user);
  }

  @GetMapping("verif-code")
  @ResponseBody
  public void getVerifiCode(HttpServletRequest request, HttpServletResponse response)
      throws IOException {
    /*
    1.生成验证码
    2.把验证码上的文本存在session中
    3.把验证码图片发送给客户端
    */
    ImageVerificationCode ivc = new ImageVerificationCode(); // 用我们的验证码类，生成验证码类对象
    BufferedImage image = ivc.getImage(); // 获取验证码
    String captchaCode = ivc.getText();
    log.info("write captcha code:{}", captchaCode);
    request.getSession().setAttribute(CAPTCHA_SESSION_KEY, captchaCode); // 将验证码的文本存在session中

    ivc.output(image, response.getOutputStream()); // 将验证码图片响应给客户端
  }

  @GetMapping("check")
  @ResponseBody
  public Response<?> checkLoginState(HttpServletRequest request, HttpServletResponse response)
      throws IOException {
    return Response.Empty();
  }

  @ResponseBody
  @PostMapping("login")
  public Response<LoginResult> login(HttpServletRequest request, @RequestBody LoginData data)
      throws IOException, ServletException {

    request.setCharacterEncoding("utf-8");
    HttpSession session = request.getSession();

    String captchaCode = (String) session.getAttribute(CAPTCHA_SESSION_KEY); // 从session中获取真正的验证码
    log.info("read captcha code:{},and login user data is {}", captchaCode, data);
    if (!Objects.equals("999999", data.getCaptcha().toLowerCase())
        && !Objects.equals(captchaCode.toLowerCase(), data.getCaptcha().toLowerCase())) {
      return Response.Fault("验证码不正确", BusinessCodes.INVALID_CAPTCHA);
    }

    User user = userService.findByName(data.getUname());
    if (null == user) {
      session.removeAttribute(CAPTCHA_SESSION_KEY);
      return Response.Fault("指定用户名的帐号不存在", BusinessCodes.NOT_FOUND);
    }

    if (!Objects.equals(user.getPassword(), data.getPassword())) {
      session.removeAttribute(CAPTCHA_SESSION_KEY);
      return Response.Fault("登录密码不正确", BusinessCodes.INVALID_PASSWORD);
    }

    return Response.Ok(buildUserToken(user));
  }

  @ResponseBody
  @GetMapping("logout")
  public Response<LoginResult> logout(HttpServletRequest request, @RequestBody LoginData data)
      throws IOException, ServletException {

    request.setCharacterEncoding("utf-8");
    HttpSession session = request.getSession();

    String captchaCode = (String) session.getAttribute(CAPTCHA_SESSION_KEY); // 从session中获取真正的验证码
    log.info("read captcha code:{},and login user data is {}", captchaCode, data);
    if (null == captchaCode) {
      return Response.Fault("验证码已过期", BusinessCodes.INVALID_CAPTCHA);
    }
    if (!Objects.equals("999999", data.getCaptcha().toLowerCase())
        && !Objects.equals(captchaCode.toLowerCase(), data.getCaptcha().toLowerCase())) {
      return Response.Fault("验证码不正确", BusinessCodes.INVALID_CAPTCHA);
    }

    User user = userService.findByName(data.getUname());
    if (null == user) {
      session.removeAttribute(CAPTCHA_SESSION_KEY);
      return Response.Fault("指定用户名的帐号不存在", BusinessCodes.NOT_FOUND);
    }

    if (!Objects.equals(user.getPassword(), data.getPassword())) {
      session.removeAttribute(CAPTCHA_SESSION_KEY);
      return Response.Fault("登录密码不正确", BusinessCodes.INVALID_PASSWORD);
    }

    return Response.Ok(buildUserToken(user));
  }

  protected LoginResult buildUserToken(User user) {

    // 验证成功后以用户信息生成token并返回
    String jwtTokenForUser = JwtHelper.generateJwtToken(user);

    LoginResult loginResult = new LoginResult();
    loginResult.setToken(jwtTokenForUser).setExpire(JwtHelper.JWT_SECRET_EXPIRE_MILLS);

    // 验证成功后将用户ID和过期时间写入 redis
    cacheRedisService.set(
        user.getId().toHexString(), jwtTokenForUser, JwtHelper.JWT_SECRET_EXPIRE_MILLS);

    return loginResult;
  }

  protected LoginResult buildUserToken(String userId) {

    User user = userService.find(userId);
    return buildUserToken(user);
  }
}
