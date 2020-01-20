package io.wherein.token.controller;

import io.wherein.token.service.impl.TokenServiceImpl;
import io.wherein.token.utils.DateTimeUtils;
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
