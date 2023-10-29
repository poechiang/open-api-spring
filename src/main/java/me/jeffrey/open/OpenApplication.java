package me.jeffrey.open;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.servlet.ServletComponentScan;

@SpringBootApplication
@ServletComponentScan
@MapperScan(basePackages = "me.jeffrey.open.mapper", sqlSessionFactoryRef = "")
public class OpenApplication {

  public static void main(String[] args) {
    SpringApplication.run(OpenApplication.class, args);
  }
}
