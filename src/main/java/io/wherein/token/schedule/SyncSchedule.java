package io.wherein.token.schedule;

import com.alibaba.fastjson.JSONException;
import io.wherein.token.service.impl.TokenServiceImpl;
import io.wherein.token.utils.DateTimeUtils;
import io.wherein.token.utils.StringUtils;
import io.wherein.token.utils.ThreadPoolService;
import java.io.IOException;
import java.net.URISyntaxException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import javax.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

/**
 * Sync data in schedule.
 */
@Component
@Slf4j
public class SyncSchedule {

  @Resource
  private TokenServiceImpl tokenService;

  /**
   * Synchronize at 8 o'clock every day.
   */
  @Scheduled(cron = "0 0 8 * * ?")
  private void process() {
    SimpleDateFormat dateFormatDay = new SimpleDateFormat(DateTimeUtils.DATE_FORMAT_DAY);
    Calendar calendar = Calendar.getInstance();
    calendar.add(Calendar.DATE, -1);
    String date = dateFormatDay.format(calendar.getTime());
    ThreadPoolService.newTask(new Runnable() {
      @Override
      public void run() {
        try {
          tokenService.syncFromSteem(StringUtils.ACCOUNT_CNSTM, date);
        } catch (IOException | URISyntaxException | JSONException e) {
          log.error("Schedule of {} failed!", StringUtils.ACCOUNT_CNSTM);
        }
      }
    });

    ThreadPoolService.newTask(new Runnable() {
      @Override
      public void run() {
        try {
          tokenService.syncFromSteem(StringUtils.ACCOUNT_wherein, date);
        } catch (IOException | URISyntaxException | JSONException e) {
          log.error("Schedule of {} failed!", StringUtils.ACCOUNT_wherein);
        }
      }
    });
  }
}
