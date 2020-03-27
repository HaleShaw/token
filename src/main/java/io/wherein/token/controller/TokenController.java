package io.wherein.token.controller;

import io.wherein.token.service.impl.TokenServiceImpl;
import io.wherein.token.utils.DateTimeUtils;
import io.wherein.token.utils.StringUtils;
import java.io.IOException;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
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
  public List<Map<String, Object>> getToken(@RequestParam(name = "account") String account,
      @RequestParam(name = "date", required = false) String date) {
    if (!StringUtils.accountIsAvailable(account)) {
      String msg = "Account " + account + " is invalid!";
      return createErrReturn(msg);
    }
    if (null == date) {
      date = DateTimeUtils.getDate();
    } else if (!DateTimeUtils.validateDate(date)) {
      String msg = "Date " + date + " is invalid!";
      return createErrReturn(msg);
    }
    return tokenService.getTokenByDate(account, date);
  }

  /**
   * Create error message for return.
   *
   * @param msg error message.
   */
  private List<Map<String, Object>> createErrReturn(String msg) {
    List<Map<String, Object>> res = new ArrayList<>(1);
    Map<String, Object> map = new HashMap<>(2);
    map.put("status", "error");
    map.put("message", msg);
    res.add(map);
    return res;
  }

  /**
   * Sync from steem manually.
   *
   * @param account account.
   */
  @PutMapping("/sync")
  public void syncFromSteem(@RequestParam(name = "account") String account)
      throws IOException, URISyntaxException {
    if (!StringUtils.accountIsAvailable(account)) {
      return;
    }
    tokenService.syncFromSteem(account);
  }
}
