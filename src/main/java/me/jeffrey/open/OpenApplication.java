package me.jeffrey.open;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.servlet.ServletComponentScan;

@SpringBootApplication
@ServletComponentScan
public class OpenApplication {

  public static void main(String[] args) {
    SpringApplication.run(OpenApplication.class, args);
  }
}
