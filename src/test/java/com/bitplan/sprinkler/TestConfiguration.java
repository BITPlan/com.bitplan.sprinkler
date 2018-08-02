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

import org.junit.Test;

/**
 * check the configuration
 * @author wf
 *
 */
public class TestConfiguration {

  @Test
  public void testConfiguration() throws Exception {
    Configuration conf=new Configuration();
    Location loc=Location.byName("Knickelsdorf");
    conf.setLocation(loc);
    conf.setAreaSizeSquareMeter(100);
    conf.setEarliestSprinkleHour(7.5);
    conf.setLatestSprinkleHour(20.5);
    conf.setlEvaporationPerDay(200);
    conf.setSprinklesPerDay(2);
  }

}
