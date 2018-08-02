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

/**
 * configuration of the irrigation system
 * see https://github.com/BITPlan/com.bitplan.sprinkler/issues/1
 * @author wf
 *
 */
public class Configuration {
  Location location;
  double lPerMinute;
  double areaSizeSquareMeter;
  double earliestSprinkleHour; 
  double latestSprinkleHour;
  int sprinklesPerDay;
  double lEvaporationPerDay;
  public Location getLocation() {
    return location;
  }
  public void setLocation(Location location) {
    this.location = location;
  }
  public double getlPerMinute() {
    return lPerMinute;
  }
  public void setlPerMinute(double lPerMinute) {
    this.lPerMinute = lPerMinute;
  }
  public double getAreaSizeSquareMeter() {
    return areaSizeSquareMeter;
  }
  public void setAreaSizeSquareMeter(double areaSizeSquareMeter) {
    this.areaSizeSquareMeter = areaSizeSquareMeter;
  }
  public double getEarliestSprinkleHour() {
    return earliestSprinkleHour;
  }
  public void setEarliestSprinkleHour(double earliestSprinkleHour) {
    this.earliestSprinkleHour = earliestSprinkleHour;
  }
  public double getLatestSprinkleHour() {
    return latestSprinkleHour;
  }
  public void setLatestSprinkleHour(double latestSprinkleHour) {
    this.latestSprinkleHour = latestSprinkleHour;
  }
  public int getSprinklesPerDay() {
    return sprinklesPerDay;
  }
  public void setSprinklesPerDay(int sprinklesPerDay) {
    this.sprinklesPerDay = sprinklesPerDay;
  }
  public double getlEvaporationPerDay() {
    return lEvaporationPerDay;
  }
  public void setlEvaporationPerDay(double lEvaporationPerDay) {
    this.lEvaporationPerDay = lEvaporationPerDay;
  }

}
