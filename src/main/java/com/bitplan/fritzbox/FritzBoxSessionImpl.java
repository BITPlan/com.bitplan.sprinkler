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
package com.bitplan.fritzbox;

import java.io.IOException;
import java.io.StringReader;
import java.nio.charset.Charset;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.X509Certificate;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;
import javax.xml.bind.JAXB;

import org.apache.commons.codec.digest.DigestUtils;

import com.bitplan.util.JsonUtil;

/**
 * a FritzBox! session
 * @author wf
 *
 */
public class FritzBoxSessionImpl implements FritzBoxSession {
  
  public static boolean debug=false;
  // prepare a LOGGER
  protected static Logger LOGGER = Logger.getLogger("com.bitplan.fritzbox");
  
  String LOGIN_URL="/login_sid.lua";
  private static final String DEFAULT_SESSION_ID="0000000000000000";
  private Fritzbox fritzbox;
  Charset UTF_16LE = Charset.forName("utf-16le");
  private SessionInfo sessionInfo;
  
  private void disableSslVerification() {
    try
    {
        // Create a trust manager that does not validate certificate chains
        TrustManager[] trustAllCerts = new TrustManager[] {new X509TrustManager() {
            public java.security.cert.X509Certificate[] getAcceptedIssuers() {
                return null;
            }
            public void checkClientTrusted(X509Certificate[] certs, String authType) {
            }
            public void checkServerTrusted(X509Certificate[] certs, String authType) {
            }
        }
        };

        // Install the all-trusting trust manager
        SSLContext sc = SSLContext.getInstance("SSL");
        sc.init(null, trustAllCerts, new java.security.SecureRandom());
        HttpsURLConnection.setDefaultSSLSocketFactory(sc.getSocketFactory());

        // Create all-trusting host name verifier
        HostnameVerifier allHostsValid = new HostnameVerifier() {
            public boolean verify(String hostname, SSLSession session) {
                return true;
            }
        };

        // Install the all-trusting host verifier
        HttpsURLConnection.setDefaultHostnameVerifier(allHostsValid);
    } catch (NoSuchAlgorithmException e) {
        e.printStackTrace();
    } catch (KeyManagementException e) {
        e.printStackTrace();
    }
}
  
 /**
  * create a FritzBox Session
  */
  public FritzBoxSessionImpl(Fritzbox fritzbox) {
    this.fritzbox=fritzbox;
    // https://stackoverflow.com/questions/19540289/how-to-fix-the-java-security-cert-certificateexception-no-subject-alternative
    this.disableSslVerification();
  }

  @Override
  public void login() {
    
    try {
      sessionInfo = this.getSessionInfo("");
      if (!DEFAULT_SESSION_ID.equals(sessionInfo.SID)) {
        throw new IllegalStateException("login called twice session "+sessionInfo.SID+" is active");
      }
      String challengeResponse=sessionInfo.Challenge+"-"+this.getMd5(sessionInfo.Challenge+"-"+fritzbox.password);
      String params=String.format("?username=%s&response=%s", fritzbox.username,challengeResponse);
      sessionInfo=this.getSessionInfo(params);
    } catch (Throwable th) {
      String msg=th.getMessage();
      LOGGER.log(Level.SEVERE, msg, th);
    }
  }
  
  /**
   * get the session info
   * @param params
   * @return - the SessionInfo
   * @throws Exception
   * @throws IOException
   */
  protected SessionInfo getSessionInfo(String params) throws Exception, IOException {
    String xml;
    xml = JsonUtil.read(fritzbox.url+LOGIN_URL+params);
    if (debug)
      LOGGER.log(Level.INFO, xml);
    SessionInfo sessionInfo=JAXB.unmarshal(new StringReader(xml), SessionInfo.class);
    return sessionInfo;
  }

  @Override
  public void logout() {
    // TODO Auto-generated method stub
    
  }

  @Override
  public String getMd5(String input) {
    String md5=DigestUtils.md5Hex(input.getBytes(UTF_16LE));
    return md5;
  }

}
