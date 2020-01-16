package io.wherein.cnstm.schedule;

import io.wherein.cnstm.service.impl.TokenServiceImpl;
import javax.annotation.Resource;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

/**
 * Sync data in schedule.
 */
@Component
public class SyncSchedule {

  @Resource
  private TokenServiceImpl tokenService;

  /**
   * Synchronize at 8 o'clock every day.
   */
  @Scheduled(cron = "0 0 8 * * ?")
  private void process() {
    tokenService.syncFromSteem();
  }
}
