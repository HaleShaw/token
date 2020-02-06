package io.wherein.token.service.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONException;
import io.wherein.token.mapper.TokenMapper;
import io.wherein.token.service.TokenService;
import io.wherein.token.utils.DateTimeUtils;
import io.wherein.token.utils.HttpClientUtils;
import java.io.IOException;
import java.math.BigDecimal;
import java.net.URISyntaxException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Map;
import javax.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

/**
 * Token service implement.
 */
@Service
@Slf4j
public class TokenServiceImpl implements TokenService {

  @Value("${url}")
  private String url;

  @Value("${spring.mail.properties.toAddr}")
  private String toAddr;

  @Resource
  private TokenMapper tokenMapper;

  @Resource
  private MailServiceImpl mailService;

  /**
   * Get token by date.
   *
   * @return List.
   */
  @Override
  public List<Map<String, Object>> getTokenByDate(String account, String date) {
    return tokenMapper.getTokenByDate(account, date);
  }

  /**
   * Get sp from steem of current day.
   *
   * @return List.
   */
  @Override
  public List<Map> getSPFromSteem() throws IOException, URISyntaxException, JSONException {
    String response = null;
    List<Map> sp = null;
    try {
      response = HttpClientUtils.doGet(url);
      sp = JSON.parseArray(response, Map.class);
    } catch (URISyntaxException | IOException e) {
      log.error("Http request error!", e);
      throw e;
    } catch (JSONException e) {
      log.error("Parse JSON error!", e);
      throw e;
    }
    return sp;
  }

  /**
   * Get count by date.
   *
   * @param date date.
   * @return count of data.
   */
  @Override
  public int getCountByDate(String account, String date) {
    return tokenMapper.getCountByDate(account, date);
  }

  @Override
  public void addSP(List list) {
    if (list != null && !list.isEmpty()) {
      tokenMapper.addSP(list);
    }
  }

  /**
   * Sync from steem.
   *
   * @param account account.
   */
  @Override
  public void syncFromSteem(String account) {
    SimpleDateFormat dateFormatDay = new SimpleDateFormat(DateTimeUtils.DATE_FORMAT_DAY);
    Calendar calendar = Calendar.getInstance();
    calendar.add(Calendar.DATE, -1);
    String date = dateFormatDay.format(calendar.getTime());
    int count = getCountByDate(account, date);
    if (count != 0) {
      log.info("There is already the data for {}!", date);
      return;
    }
    List<Map> spFromSteem = null;
    try {
      spFromSteem = getSPFromSteem();
    } catch (URISyntaxException | IOException | JSONException e) {
      String time =
          "GMT+8:00 " + new SimpleDateFormat(DateTimeUtils.DATE_FORMAT_DAY_TIME).format(new Date());
      String content = "Hi,\nFailed to synchronize data of " + date + " from Steemit at " + time
          + ".\nFollowing is the error log.\n\n" + e.toString()
          + "\n\nSystem mail, please do not reply.";
      mailService.sendMail(toAddr, "[System mail] Sync SP from Steemit failed", content);
    }
    if (spFromSteem != null && !spFromSteem.isEmpty()) {
      BigDecimal totalSP = BigDecimal.ZERO;
      // Get total sp.
      for (Map<String, Object> delegator : spFromSteem) {
        totalSP = totalSP.add(new BigDecimal(delegator.get("sp").toString()));
      }

      // Calculate token.
      for (Map<String, Object> delegator : spFromSteem) {
        BigDecimal sp = new BigDecimal(delegator.get("sp").toString());
        BigDecimal divide = sp.divide(totalSP, 15, BigDecimal.ROUND_HALF_UP);
        BigDecimal token = divide.multiply(new BigDecimal("7200"));
        delegator.put("token", token);
      }
    }
    addSP(spFromSteem);
  }
}
