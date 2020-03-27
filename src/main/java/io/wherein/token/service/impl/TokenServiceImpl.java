package io.wherein.token.service.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONException;
import io.wherein.token.mapper.TokenMapper;
import io.wherein.token.service.TokenService;
import io.wherein.token.utils.DateTimeUtils;
import io.wherein.token.utils.HttpClientUtils;
import io.wherein.token.utils.StringUtils;
import java.io.IOException;
import java.math.BigDecimal;
import java.net.URISyntaxException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import javax.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Recover;
import org.springframework.retry.annotation.Retryable;
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

  @Value("${spring.mail.properties.ccAddr}")
  private String ccAddr;

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
   * @param account account.
   * @return List.
   */
  @Override
  public List<Map> getSPFromSteem(String account)
      throws IOException, URISyntaxException, JSONException {
    String response = null;
    List<Map> sp = null;
    String realURL = String.format(Locale.ENGLISH, url, account);
    try {
      response = HttpClientUtils.doGet(realURL);
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
   * @param account account.
   * @param date date.
   * @return count of data.
   */
  @Override
  public int getCountByDate(String account, String date) {
    return tokenMapper.getCountByDate(account, date);
  }

  /**
   * Insert sp into DB.
   *
   * @param account account.
   * @param list sp of the list.
   */
  @Override
  public void insertSP(String account, List list) {
    if (list != null && !list.isEmpty()) {
      log.info("Insert data of {} {}:", account, JSON.toJSONString(list));
      tokenMapper.insertSP(account, list);
    }
  }

  /**
   * Sync from steem. The first retry will delay 10 min, second delay 20 min.
   *
   * @param account account.
   */
  @Override
  @Retryable(value = {
      Exception.class}, maxAttempts = 2, backoff = @Backoff(delay = 600000L, multiplier = 2))
  public void syncFromSteem(String account, String date)
      throws IOException, URISyntaxException, JSONException {

    int count = getCountByDate(account, date);
    if (count != 0) {
      log.info("There is already the data for {}!", date);
      return;
    }

    List<Map> spFromSteem = getSPFromSteem(account);
    log.info("Data of {} from Steem: {}", account, JSON.toJSONString(spFromSteem));

    if (spFromSteem != null && !spFromSteem.isEmpty()) {
      BigDecimal totalSP = BigDecimal.ZERO;

      // Get total sp.
      for (Map<String, Object> delegator : spFromSteem) {
        BigDecimal sp = new BigDecimal(delegator.get("sp").toString());

        // ignore the sp which is less than 100 from wherein.
        if (StringUtils.ACCOUNT_wherein.equalsIgnoreCase(account) && sp.intValue() < 100) {
          continue;
        }
        totalSP = totalSP.add(sp);
      }

      // Calculate token.
      for (Map<String, Object> delegator : spFromSteem) {
        BigDecimal sp = new BigDecimal(delegator.get("sp").toString());
        BigDecimal token = BigDecimal.ZERO;

        if (StringUtils.ACCOUNT_CNSTM.equalsIgnoreCase(account)) {
          BigDecimal divide = sp.divide(totalSP, 15, BigDecimal.ROUND_HALF_UP);
          token = divide.multiply(new BigDecimal("7200"));
        } else if (StringUtils.ACCOUNT_wherein.equalsIgnoreCase(account) && sp.intValue() >= 100) {
          BigDecimal divide = sp.divide(totalSP, 15, BigDecimal.ROUND_HALF_UP);
          token = divide.multiply(new BigDecimal("1800"));
        }
        delegator.put("token", token);
      }
    }
    insertSP(account, spFromSteem);
  }

  @Recover
  public void recover(Exception e, String account, String date) {
    String time =
        "GMT+8:00 " + new SimpleDateFormat(DateTimeUtils.DATE_FORMAT_DAY_TIME).format(new Date());
    String content =
        "Hi,\nFailed to synchronize data of " + account + " of " + date + " from Steemit at "
            + time + ".\nFollowing is the error log.\n\n" + e.toString()
            + "\n\nSystem mail, please do not reply.";
    mailService.sendMail(toAddr, ccAddr, "[System mail] Sync SP from Steemit failed", content);
  }
}
