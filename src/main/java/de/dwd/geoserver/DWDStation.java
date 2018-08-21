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
package de.dwd.geoserver;

import java.util.Locale;

import org.openweathermap.weather.Coord;

/**
 * a weather station of Deutscher Wetterdienst
 * @author wf
 *
 */
public class DWDStation {
  String name;
  public String id;
  Coord coord;
  Double distance;

  public DWDStation(String id, String name, Coord coord, double distance) {
    this.id = id;
    this.name = name;
    this.coord = coord;
    this.distance = distance;
  }

  public String toString() {
    String text = String.format(Locale.GERMAN,"%s(%s) - %.1f km %s", name, id, distance,
        coord.toString());
    return text;
  }
}