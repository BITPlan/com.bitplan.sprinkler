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
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;

import com.bitplan.json.JsonAble;
import com.bitplan.json.JsonManager;
import com.bitplan.json.JsonManagerImpl;

/**
 * keep tracke of the sprinkle periods
 * 
 * @author wf
 *
 */
public class SprinkleHistory implements JsonAble {

  String name = null;
  List<SprinklePeriod> sprinklePeriods;

  /**
   * construct me from a name and an array of periods
   * 
   * @param name
   * @param periods
   */
  public SprinkleHistory(String pName, SprinklePeriod... periods) {
    this.name = pName;
    sprinklePeriods = Arrays.asList(periods);
    sortByStart();
  }

  /**
   * sort the sprinklePeriods by start date
   */
  public void sortByStart() {
    Collections.sort(sprinklePeriods, new Comparator<SprinklePeriod>() {
      @Override
      public int compare(SprinklePeriod lhs, SprinklePeriod rhs) {
        long diffmsecs = (lhs.start == null ? 0 : lhs.start.getTime())
            - (rhs.start == null ? 0 : rhs.start.getTime());
        return (int) diffmsecs;
      }
    });
  }

  @Override
  public void reinit() {
    sortByStart();
  }

  @SuppressWarnings("unchecked")
  @Override
  public void fromMap(Map<String, Object> map) {
    this.name = (String) map.get("name");
    this.sprinklePeriods = (List<SprinklePeriod>) map.get("sprinklePeriods");
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
}
