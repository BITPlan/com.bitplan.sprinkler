package com.bitplan.sprinkler;

import java.io.File;
import java.util.Map;

import com.bitplan.error.ErrorHandler;
import com.bitplan.fritzbox.Fritzbox;
import com.bitplan.fritzbox.FritzboxImpl;
import com.bitplan.json.JsonAble;
import com.bitplan.json.JsonManager;
import com.bitplan.json.JsonManagerImpl;

/**
 * FritzBox Configuration
 * @author wf
 *
 */
public class FritzBoxConfig implements JsonAble {
  String url;
  String username;
  String password;
  String password2;
  String deviceName;
  transient Fritzbox fritzbox;
  
  @Override
  public void reinit() {

  }

  @Override
  public void fromMap(Map<String, Object> map) {
    this.url = (String) map.get("url");
    this.username = (String) map.get("username");
    this.password = (String) map.get("password");
    this.password2 = (String) map.get("password2");
    this.deviceName = (String) map.get("deviceName");
  }
  
  static FritzBoxConfig instance;
  public static FritzBoxConfig getInstance() throws Exception {
    File jsonFile = JsonAble.getJsonFile(FritzBoxConfig.class.getSimpleName());
    if (jsonFile.canRead()) {
      JsonManager<FritzBoxConfig> jmConfig = new JsonManagerImpl<FritzBoxConfig>(
          FritzBoxConfig.class);
      try {
        instance = jmConfig.fromJsonFile(jsonFile);
      } catch (Exception e) {
        ErrorHandler.handle(e);
      }
    }
    if (instance == null) {
      instance = new FritzBoxConfig();
    }
    if (instance.fritzbox == null) {
      instance.fritzbox = FritzboxImpl.readFromProperties();
      if (instance.fritzbox!=null) {
        instance.url=instance.fritzbox.getUrl();
        instance.username=instance.fritzbox.getUsername();
        instance.password=instance.fritzbox.getPassword();
        instance.password2=instance.fritzbox.getPassword();
      }
    }
    return instance;
  }

}
