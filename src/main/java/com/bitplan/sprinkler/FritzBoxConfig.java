/**
 * Copyright (c) 2018 BITPlan GmbH
 *
 * http://www.bitplan.com
 *
 * This file is part of the Opensource project at:
 * https://github.com/BITPlan/com.bitplan.sprinkler
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.bitplan.sprinkler;

import java.util.Map;

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
    JsonManager<FritzBoxConfig> jmConfig = new JsonManagerImpl<FritzBoxConfig>(FritzBoxConfig.class);
    instance= jmConfig.getInstance();
    
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
