package io.wherein.cnstm.controller;

import com.alibaba.fastjson.JSONObject;
import io.wherein.cnstm.service.impl.TokenServiceImpl;
import java.util.List;
import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Token Controller.
 */
@RestController
@RequestMapping("/token")
public class TokenController {

  @Autowired
  private TokenServiceImpl tokenService;

  @GetMapping("/get")
  public List<Map<String, Object>> getToken() {
    JSONObject jsonObject = new JSONObject();
    jsonObject.put("dd", "sdfs");
    List<Map<String, Object>> all = tokenService.getAll("2019-12-11");
    return all;
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
  public List test(){
    List spFromSteem = tokenService.getSPFromSteem();
    tokenService.addSP(spFromSteem);
    return tokenService.getSPFromSteem();
  }
}
