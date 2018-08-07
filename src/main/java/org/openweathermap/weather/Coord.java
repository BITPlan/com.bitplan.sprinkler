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
package org.openweathermap.weather;

import gov.nasa.worldwind.geom.Angle;

/**
 * coordinate
 * @author wf
 *
 */
public class Coord {
  double lat;
  double lon;
  private transient Angle latangle;
  private transient Angle lonangle;
  
  public double getLat() {
    return lat;
  }
  public void setLat(double lat) {
    this.lat = lat;
  }
  public double getLon() {
    return lon;
  }
  public void setLon(double lon) {
    this.lon = lon;
  }
  
  public void init() {
    latangle = Angle.fromDegreesLatitude(Math.abs(lat));
    lonangle = Angle.fromDegreesLongitude(Math.abs(lon));
  }
  
  public String getLatDMS() {
    if (latangle==null)
      init();
    String latDMS=String.format("%s %s",latangle.toFormattedDMSString(), lat >= 0.0 ? "N" : "S");
    return latDMS;
  }
  
  public String getLonDMS() {
    if (lonangle==null)
      init();
    String lonDMS=String.format("%s %s",lonangle.toFormattedDMSString(), lon >= 0.0 ? "E" : "W");
    return lonDMS;
  }
  
  /**
   * return the GEO coordinates
   */
  public String toString() {
    String dmsString = getLatDMS()+getLonDMS();
    return dmsString;
  }
}