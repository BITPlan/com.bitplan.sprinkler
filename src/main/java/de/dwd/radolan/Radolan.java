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
package de.dwd.radolan;

import java.io.InputStream;
import java.net.URL;

import org.apache.commons.io.IOUtils;

import cs.fau.de.since.radolan.Composite;
import javafx.scene.image.Image;
import javafx.scene.image.PixelFormat;
import javafx.scene.image.PixelWriter;
import javafx.scene.image.WritableImage;


/**
 * reader for RADOLAN data
 * https://www.dwd.de/DE/leistungen/radolan/radolan_info/radolan_radvor_op_komposit_format_pdf.pdf?__blob=publicationFile&v=10
 * @author wf
 *
 */
@SuppressWarnings("restriction")
public class Radolan extends Composite {
  public static final int WIDTH=900;
  public static final int HEIGHT=900;
   
  /**
   * construct me from an url;
   * @param url
   * @throws Throwable 
   */
  public Radolan(String url) throws Throwable {
    super(url);
  }
  
  /**
   * get the Image
   * https://www.dwd.de/DE/leistungen/radarniederschlag/rn_info/download_niederschlagsbestimmung.pdf?__blob=publicationFile&v=4
   * @return - the image
   */
  public Image getImage() {
    int[] pixels = new int[WIDTH*HEIGHT];
    int l=bytes.length;
    int i=0;
    for (int x=0;x<WIDTH;x++) {
      for (int y=0;y<HEIGHT;y++) {
        int ofs=l-2-y*2*WIDTH-x*2;
        int r16=bytes[ofs]<<8+bytes[ofs+1];
        switch (r16) {
        case 0:
          r16=0xFFE0E0E0; // gray
          break;
        case 167936:
        case -7864320:
          r16=0xFFF0F0F0; // light gray
          break;
        case 4096:
        case -986896:
          r16=0xFF00FF00; // green
          break;
        default:
          r16=r16 + 0;
        }
        pixels[i++]=r16;
      }
    }
    WritableImage img = new WritableImage(WIDTH, HEIGHT);
    PixelWriter pw = img.getPixelWriter();
    pw.setPixels(0, 0, WIDTH, HEIGHT, PixelFormat.getIntArgbInstance(), pixels, 0, WIDTH);
    return img;
  }
  
}
