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

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Map;

import com.bitplan.json.JsonAble;

/**
 * a single period of sprinkling (or rain)
 * @author wf
 *
 */
public class SprinklePeriod implements JsonAble {
  public static SimpleDateFormat isoDateFormatter = new SimpleDateFormat(
      "yyyy-MM-dd HH:mm");
  
  public static enum SprinkleSource{Rain,Sprinkler};
  // when sprinkling started
  Date start;
  // when sprinkling stopped
  Date stop;
  // how many mm of rain equivalent where applied
  Double mm;
  // pump energy consumed for this sprinkle period
  Double kWh;
  // the source of this sprinkling
  SprinkleSource source;
  /**
   * 
   * @param dateStr
   * @param mins
   * @param mm
   * @param kWh
   * @param source
   * @throws Exception 
   */
  public SprinklePeriod(String dateStr, int mins, double mm, double kWh, SprinkleSource source) throws Exception {
    this.mm=mm;
    this.kWh=kWh;
    start=isoDateFormatter.parse(dateStr);
    stop=new Date(start.getTime()+mins*60000);
    this.source=source;
  }
  
  @Override
  public void reinit() {
    
  }
  
  @Override
  public void fromMap(Map<String, Object> map) {
    this.kWh=(Double)map.get("kWh");
    this.mm=(Double)map.get("mm");
    
  }
  
  public String toString() {
    String periodStr=String.format("%s - %s %4.1f mm",start==null?"?":isoDateFormatter.format(start),stop==null?"?":isoDateFormatter.format(stop),mm==null?0:mm);
    return periodStr;
  }
}
