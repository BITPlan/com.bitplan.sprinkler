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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import org.junit.Test;

import com.bitplan.fritzbox.FritzBoxSession;
import com.bitplan.fritzbox.FritzBoxSessionImpl;
import com.bitplan.fritzbox.Fritzbox;

/**
 * test the fritz box access
 */
public class TestFritzBox {

  @Test
  public void testLogin() throws Exception {
    Configuration config = Configuration.getConfiguration("default");
    if (config != null) {
      assertNotNull("There should be a fritzbox configuration",
          config.fritzbox);
      FritzBoxSession session = new FritzBoxSessionImpl(config.fritzbox);
      session.login();
    }
  }

  @Test
  public void testMd5() {
    FritzBoxSession session = new FritzBoxSessionImpl(new Fritzbox());

    String inputs[] = { "secret1", "", "test1", "12345678z-äbc","!\"§$%&/()=?ßüäöÜÄÖé-.,;:_`´+*#'<>≤|"
        };
    String expected[] = { "210df5bd3d63f46e3a0b68e967eae6d8",
        "d41d8cd98f00b204e9800998ecf8427e", "16c47151c18ac087cd12b3a70746c790",
        "a59dffd36899371d6e8ba951c1eb1171", "cb3bab6405639bf466eac17e43534a7d"  };
    for (int i = 0; i < inputs.length; i++) {
      String md5 = session.getMd5(inputs[i]);
      // System.out.println(inputs[i]+"="+md5);
      assertEquals(expected[i], md5);
    }

  }

}
