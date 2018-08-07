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

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.apache.commons.io.FileUtils;
import org.openweathermap.weather.Location;

import com.bitplan.appconfig.Preferences;
import com.bitplan.fritzbox.FritzboxImpl;
import com.bitplan.json.JsonAble;
import com.bitplan.util.JsonUtil;
import com.google.gson.Gson;

/**
 * configuration of the irrigation system
 * see https://github.com/BITPlan/com.bitplan.sprinkler/issues/1
 * 
 * @author wf
 *
 */
public class Configuration implements JsonAble {
  // prepare a LOGGER
  protected static Logger LOGGER = Logger.getLogger("com.bitplan.sprinkler");
  
  Location location;
  enum SoilType{Sand,Silt,Clay};
  String appid; // openweathermap appid
  SoilType soilType;
  Double mmPerHour;
  Integer areaSizeSquareMeter;
  String earliestSprinkleHour;
  String latestSprinkleHour;
  Integer sprinklesPerDay;
  Double lEvaporationPerDay;
  String confName="default";

  public String getAppid() {
    return appid;
  }

  public void setAppid(String appid) {
    this.appid = appid;
  }

  public SoilType getSoilType() {
    return soilType;
  }

  public void setSoilType(SoilType soilType) {
    this.soilType = soilType;
  }

  public double getMmPerHour() {
    return mmPerHour;
  }

  public void setMmPerHour(double mmPerHour) {
    this.mmPerHour = mmPerHour;
  }

  public Location getLocation() {
    return location;
  }

  public void setLocation(Location location) {
    this.location = location;
  }

  public Integer getAreaSizeSquareMeter() {
    return areaSizeSquareMeter;
  }

  public void setAreaSizeSquareMeter(Integer areaSizeSquareMeter) {
    this.areaSizeSquareMeter = areaSizeSquareMeter;
  }



  public int getSprinklesPerDay() {
    return sprinklesPerDay;
  }

  public void setSprinklesPerDay(int sprinklesPerDay) {
    this.sprinklesPerDay = sprinklesPerDay;
  }

  /**
   * @return the earliestSprinkleHour
   */
  public String getEarliestSprinkleHour() {
    return earliestSprinkleHour;
  }

  /**
   * @param earliestSprinkleHour the earliestSprinkleHour to set
   */
  public void setEarliestSprinkleHour(String earliestSprinkleHour) {
    this.earliestSprinkleHour = earliestSprinkleHour;
  }

  /**
   * @return the latestSprinkleHour
   */
  public String getLatestSprinkleHour() {
    return latestSprinkleHour;
  }

  /**
   * @param latestSprinkleHour the latestSprinkleHour to set
   */
  public void setLatestSprinkleHour(String latestSprinkleHour) {
    this.latestSprinkleHour = latestSprinkleHour;
  }

  public double getlEvaporationPerDay() {
    return lEvaporationPerDay;
  }

  public void setlEvaporationPerDay(double lEvaporationPerDay) {
    this.lEvaporationPerDay = lEvaporationPerDay;
  }

  static String appName = "sprinkler";

  /**
   * get a configuration for the given configuration name
   * @param confName
   * @return - the configuration
   * @throws Exception
   */
  public static Configuration getConfiguration(String confName) throws Exception {
    Configuration configuration = null;
    File jsonFile = getJsonFile(confName);
    if (jsonFile.exists()) {
      FileInputStream fsi = new FileInputStream(jsonFile);
      String json = JsonUtil.read(fsi);
      Gson gson = new Gson();
      configuration = gson.fromJson(json, Configuration.class);
      configuration.confName=confName;
    } else {
      String msg=String.format("There is no configuration file %s yet.\nYou might want to create one as outlined in http://wiki.bitplan.com/index.php/Sprinkler#Configuration",
      Configuration.getJsonFile(confName).getPath());
      LOGGER.log(Level.WARNING,msg);
    }
    return configuration;
  }

  static File getJsonFile(String name) {
    String home = System.getProperty("user.home");
    File configDirectory = new File(home + "/." + appName + "/");
    String jsonFileName = name + ".json";
    File jsonFile = new File(configDirectory, jsonFileName);
    return jsonFile;
  }
  
  /**
   * save me
   */
  public void save() throws IOException {
    File jsonFile=getJsonFile(confName);
    if (!jsonFile.getParentFile().isDirectory())
      jsonFile.getParentFile().mkdirs();
    FileUtils.writeStringToFile(jsonFile, this.asJson(),"UTF-8");
  }

  @Override
  public void reinit() {
    
  }

  @Override
  public void fromMap(Map<String, Object> map) {
    this.appid=(String) map.get("appid");
    // this.confName=(String) map.get("confName");
    this.mmPerHour=(Double)map.get("mmPerHour");
    // @TODO change to double using a converter or at least at regexp to allow only proper entry
    this.latestSprinkleHour=(String)map.get("latestSprinkleHour");
    this.earliestSprinkleHour=(String)map.get("earliestSprinkleHour");
    this.areaSizeSquareMeter=(Integer)map.get("areaSizeSquareMeter");
    this.soilType=SoilType.valueOf((String)map.get("soilType"));
  }
}
