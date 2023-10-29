package me.jeffrey.open.utils;

import io.jsonwebtoken.*;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import lombok.extern.slf4j.Slf4j;
import me.jeffrey.open.dto.UserDTO;

@Slf4j
public class JwtHelper {
  
  
  public static final String JWT_SECRET_KEY = "com.jeffrey.open";
  public static final long JWT_SECRET_EXPIRE_MILLS = 7200000; // 2 hours

  public static String generateJwtToken(
      UserDTO user ) {
    Map<String, Object> claims = new HashMap<>();

    claims.put("id", user.getId().toHexString());
    log.info("\n[JWT HELPER] userId:{}",user.getId());
    claims.put("uname", user.getUname());
    claims.put("created",new Date());
    return Jwts.builder()
            .setClaims(claims)
            .signWith(SignatureAlgorithm.HS256, JWT_SECRET_KEY)
            .compact();
  }



  public static Claims checkJwtToken(String token) throws ExpiredJwtException, UnsupportedJwtException, MalformedJwtException,IllegalArgumentException {
    return Jwts.parser().setSigningKey(JWT_SECRET_KEY).parseClaimsJws(token).getBody();
  }
}
