package io.wherein.token;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@MapperScan("io.wherein.cnstm.mapper")
@EnableScheduling
public class CnstmApplication {

  public static void main(String[] args) {
    SpringApplication.run(CnstmApplication.class, args);
  }

}
