package io.wherein.cnstm.controller;

import io.wherein.cnstm.mapper.TokenMapper;
import io.wherein.cnstm.service.impl.TokenServiceImpl;
import io.wherein.cnstm.utils.DateTimeUtils;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
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
    // Check if the current time is before 08:05am.
    SimpleDateFormat dateFormatTime = new SimpleDateFormat(DateTimeUtils.DATE_FORMAT_TIME);
    Date nowDateTime = new Date();
    String nowTimeStr = dateFormatTime.format(nowDateTime);
    boolean isBefore = false;
    try {
      isBefore = DateTimeUtils
          .before(nowTimeStr, DateTimeUtils.SYNC_TIME, DateTimeUtils.DATE_FORMAT_TIME);
    } catch (ParseException e) {
      log.error("Parse date error!", e);
    }

    SimpleDateFormat dateFormatDay = new SimpleDateFormat(DateTimeUtils.DATE_FORMAT_DAY);
    Calendar calendar = Calendar.getInstance();
    // If current time is before 08:05am, it will get the data of the day before yesterday.
    if (isBefore) {
      calendar.add(Calendar.DATE, -2);
    }
    // If current time is not before 08:05am, it will get the data of yesterday.
    else {
      calendar.add(Calendar.DATE, -1);
    }
    String date = dateFormatDay.format(calendar.getTime());
    return tokenService.getAll(date);
  }

  /**
   * Sync from steem manually.
   */
  @PutMapping("/sync")
  public void syncFromSteem() {

  }

  @GetMapping("/totalSP")
  public List<Map<String, Object>> getTotalSP() {
    return tokenService.getTotalSP();
  }

  @GetMapping("/ctoken")
  public List<Map<String, Object>> getCurrentToken() {
    return tokenService.getCurrentToken("2019-12-11");
  }

  @GetMapping("/test")
  public String test() {
    return tokenMapper.getLastDate();
  }
}
