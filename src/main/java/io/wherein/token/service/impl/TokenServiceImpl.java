package io.wherein.token.service.impl;

import static io.wherein.token.utils.StringUtils.MAIL_SUBJECT;

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
import java.util.List;
import java.util.Locale;
import java.util.Map;
import javax.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.ObjectUtils;
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

  @Value("${skipDelegators}")
  private List<String> skipDelegators;

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
  public List<Map> getSpFromSteem(String account)
      throws IOException, URISyntaxException, JSONException {
    String response = null;
    List<Map> sp = null;
    String realUrl = String.format(Locale.ENGLISH, url, account);
    try {
      response = HttpClientUtils.doGet(realUrl);
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
  public void insertSp(String account, List list) {
    if (list != null && !list.isEmpty()) {
      log.info("Insert data of {} {}:", account, JSON.toJSONString(list));
      tokenMapper.insertSp(account, list);
    }
  }

  /**
   * Sync from steem. The first retry will delay 10 min, second delay 20 min.
   *
   * @param account account.
   */
  @Override
  @Retryable(value = {
      Exception.class}, maxAttempts = 3, backoff = @Backoff(delay = 30000L, multiplier = 2))
  public void syncFromSteemRetry(String account)
      throws IOException, URISyntaxException, JSONException {
    String date = DateTimeUtils.getYesterday();
    syncData(account, date);
  }

  @Override
  public void syncFromSteem(String account)
      throws IOException, URISyntaxException, JSONException {
    String date = DateTimeUtils.getYesterday();
    syncData(account, date);
  }

  /**
   * sync data from steem.
   *
   * @param account account.
   * @param date date.
   * @throws IOException IOException.
   * @throws URISyntaxException URISyntaxException.
   * @throws JSONException JSONException.
   */
  private void syncData(String account, String date)
      throws IOException, URISyntaxException, JSONException {
    int count = getCountByDate(account, date);
    if (count != 0) {
      log.info("There is already the data for {}!", date);
      return;
    }

    List<Map> spFromSteem = getSpFromSteem(account);
    log.info("Data of {} from Steem: {}", account, JSON.toJSONString(spFromSteem));

    if (ObjectUtils.isEmpty(spFromSteem)) {
      removeSkipDelegators(spFromSteem, skipDelegators);

      BigDecimal totalSp = BigDecimal.ZERO;

      // Get total sp.
      for (Map<String, Object> delegator : spFromSteem) {
        BigDecimal sp = new BigDecimal(delegator.get("sp").toString());

        // ignore the sp which is less than 100 from wherein.
        if (StringUtils.ACCOUNT_WHEREIN.equalsIgnoreCase(account) && sp.intValue() < 100) {
          continue;
        }
        totalSp = totalSp.add(sp);
      }

      // Calculate token.
      for (Map<String, Object> delegator : spFromSteem) {
        BigDecimal sp = new BigDecimal(delegator.get("sp").toString());
        BigDecimal token = BigDecimal.ZERO;

        if (StringUtils.ACCOUNT_CNSTM.equalsIgnoreCase(account)) {
          BigDecimal divide = sp.divide(totalSp, 15, BigDecimal.ROUND_HALF_UP);
          token = divide.multiply(new BigDecimal("7200"));
        } else if (StringUtils.ACCOUNT_WHEREIN.equalsIgnoreCase(account) && sp.intValue() >= 100) {
          BigDecimal divide = sp.divide(totalSp, 15, BigDecimal.ROUND_HALF_UP);
          token = divide.multiply(new BigDecimal("1800"));
        }
        delegator.put("token", token);
      }
    }
    insertSp(account, spFromSteem);
  }

  /**
   * Remove the skip delegators.
   *
   * @param spFromSteem spFromSteem.
   * @param skipDelegators skipDelegators.
   */
  private void removeSkipDelegators(List<Map> spFromSteem, List<String> skipDelegators) {
    spFromSteem
        .removeIf(delegator -> skipDelegators.contains(delegator.get("delegator").toString()));
  }

  @Recover
  public void recover(Exception e, String account) {
    String date = DateTimeUtils.getYesterday();
    String time = DateTimeUtils.getTime();
    String content = StringUtils.getMailContent(account, date, time, e);
    mailService.sendMail(toAddr, ccAddr, MAIL_SUBJECT, content);
  }
}
