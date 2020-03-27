package io.wherein.token;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.retry.annotation.EnableRetry;
import org.springframework.scheduling.annotation.EnableScheduling;

@EnableRetry
@SpringBootApplication
@MapperScan("io.wherein.token.mapper")
@EnableScheduling
public class TokenApplication {

  public static void main(String[] args) {
    SpringApplication.run(TokenApplication.class, args);
  }

}
