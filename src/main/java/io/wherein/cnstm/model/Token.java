package io.wherein.cnstm.model;

import java.math.BigDecimal;
import java.util.Date;
import lombok.Data;

/**
 * Token.
 */
@Data
public class Token {

  private int id;
  private String steemId;
  private BigDecimal sp;
  private BigDecimal token;
  private Date log_time;
  private Date agent_time;
}
