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

import org.openweathermap.weather.Coord;
import org.openweathermap.weather.Location;

import com.bitplan.json.JsonAble;

/**
 * FritzBox Configuration
 * 
 * @author wf
 *
 */
public class LocationConfig implements JsonAble {
  String name;
  String country;
  String lat;
  String lon;
  Long id;

  @Override
  public void reinit() {

  }

  public Location getLocation() {
    Location location = new Location();
    location.setId(id);
    location.setName(name);
    location.setCountry(country);
    // @TODO set coordinates
    Coord coord = new Coord();
    location.setCoord(coord);
    return location;
  }

  public void fromLocation(Location location) {
    this.id = location.getId();
    this.name = location.getName();
    this.country = location.getCountry();
  }

  @Override
  public void fromMap(Map<String, Object> map) {
    this.name = (String) map.get("name");
    this.country = (String) map.get("country");
    this.lat = (String) map.get("lat");
    this.lon = (String) map.get("long");
    this.id = (Long) map.get("id");
  }

}
