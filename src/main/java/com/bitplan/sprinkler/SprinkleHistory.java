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
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import com.bitplan.json.JsonAble;
import com.bitplan.json.JsonManager;
import com.bitplan.json.JsonManagerImpl;

import de.dwd.geoserver.DWDStation;
import de.dwd.geoserver.WFS;
import de.dwd.geoserver.WFS.Feature;
import de.dwd.geoserver.WFS.WFSResponse;

/**
 * keep tracke of the sprinkle periods
 * 
 * @author wf
 *
 */
public class SprinkleHistory implements JsonAble {

  String name = null;
  private List<SprinklePeriod> sprinklePeriods=new ArrayList<SprinklePeriod>();
  private Map<Date,SprinklePeriod> sprinklePeriodByDate=new TreeMap<Date,SprinklePeriod>();
  
  public List<SprinklePeriod> getSprinklePeriods() {
    return sprinklePeriods;
  }

  public void setSprinklePeriods(List<SprinklePeriod> sprinklePeriods) {
    this.sprinklePeriods = sprinklePeriods;
  }

  // make gson happy
  public SprinkleHistory() {}
  
  /**
   * construct me from a name and an array of periods
   * 
   * @param name
   * @param periods
   */
  public SprinkleHistory(String pName, SprinklePeriod... periods) {
    this.name = pName;
    setSprinklePeriods(Arrays.asList(periods));
    sortByStart();
  }

  /**
   * sort the sprinklePeriods by start date
   */
  public void sortByStart() {
    Collections.sort(getSprinklePeriods(), new Comparator<SprinklePeriod>() {
      @Override
      public int compare(SprinklePeriod lhs, SprinklePeriod rhs) {
        long diffmsecs = (lhs.start == null ? 0 : lhs.start.getTime())
            - (rhs.start == null ? 0 : rhs.start.getTime());
        return (int) diffmsecs;
      }
    });
  }
  
  /**
   * return the total precipitation
   * @param hours
   * @return - the sum
   */
  public Double totalPrecipitation(int hours) {
    double total=0.0;
    for (SprinklePeriod period:sprinklePeriods)
      total+=period.mm;
    return total;
  }
  
  public void add(SprinklePeriod period) {
    this.getSprinklePeriods().add(period);
    this.sprinklePeriodByDate.put(period.start, period);
    sortByStart();
  }

  @Override
  public void reinit() {
    sortByStart();
    this.sprinklePeriodByDate.clear();
    for (SprinklePeriod period:sprinklePeriods) {
      sprinklePeriodByDate.put(period.start, period);
    }
  }

  @SuppressWarnings("unchecked")
  @Override
  public void fromMap(Map<String, Object> map) {
    this.name = (String) map.get("name");
    this.setSprinklePeriods((List<SprinklePeriod>) map.get("sprinklePeriods"));
  }

  @Override
  public File getJsonFile() {
    String appName=this.getClass().getSimpleName() + (name==null?"":"_" + name);
    return JsonAble.getJsonFile(appName);
  }

  static SprinkleHistory instance = null;

  /**
   * singleton access
   * @return the singleton
   */
  public static SprinkleHistory getInstance() {
    if (instance == null) {
      JsonManager<SprinkleHistory> jmConfig = new JsonManagerImpl<SprinkleHistory>(
          SprinkleHistory.class);
      instance = jmConfig.getInstance();
    }
    return instance;
  }

  /**
   * get a response for the given station
   * @param dwdStation
   * @throws Exception
   */
  public void addFromDWDStation(DWDStation dwdStation) throws Exception {
    reinit();
    WFSResponse wfsresponse = WFS.getRainHistory(dwdStation);
    for (Feature feature:wfsresponse.features) {
      SprinklePeriod period=new SprinklePeriod();
      period.source=SprinklePeriod.SprinkleSource.Rain;
      period.mm=feature.properties.PRECIPITATION;
      period.start=feature.properties.getDate();
      period.stop=new Date(period.start.getTime()+60000*60*12);
      if (!this.sprinklePeriodByDate.containsKey(period.start))
        add(period);
    }
  }

}
