package io.wherein.cnstm.controller;

import io.wherein.cnstm.mapper.TokenMapper;
import io.wherein.cnstm.service.impl.TokenServiceImpl;
import io.wherein.cnstm.utils.DateTimeUtils;
import java.util.List;
import java.util.Map;
import javax.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Token Controller.
 */
@RestController
@RequestMapping("/token")
@Slf4j
public class TokenController {

  @Resource
  private TokenServiceImpl tokenService;

  @Resource
  private TokenMapper tokenMapper;

  /**
   * Get the sorted data.
   */
  @GetMapping("/summary")
  public List<Map<String, Object>> getToken() {
    String date = DateTimeUtils.getDate();
    return tokenService.getAll(date);
  }

  /**
   * Sync from steem manually.
   */
  @PutMapping("/sync")
  public void syncFromSteem() {
    tokenService.syncFromSteem();
  }
}
