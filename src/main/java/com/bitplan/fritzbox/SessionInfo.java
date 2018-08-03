package com.bitplan.fritzbox;

import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name="SessionInfo")
public class SessionInfo {
 //  <?xml version="1.0" encoding="utf-8"?><SessionInfo><SID>0000000000000000</SID><Challenge>096fe520</Challenge><BlockTime>0</BlockTime><Rights></Rights></SessionInfo>
 public String SID;
 public String Challenge;
 public long BlockTime;
}
