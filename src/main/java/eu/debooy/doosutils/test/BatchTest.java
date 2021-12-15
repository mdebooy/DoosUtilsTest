/**
 * Copyright 2018 Marco de Booij
 *
 * Licensed under the EUPL, Version 1.1 or - as soon they will be approved by
 * the European Commission - subsequent versions of the EUPL (the "Licence");
 * you may not use this work except in compliance with the Licence. You may
 * obtain a copy of the Licence at:
 *
 * https://joinup.ec.europa.eu/software/page/eupl
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the Licence is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the Licence for the specific language governing permissions and
 * limitations under the Licence.
 */
package eu.debooy.doosutils.test;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.LineNumberReader;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;
import org.junit.After;


/**
 * @author Marco de Booij
 */
public abstract class BatchTest {
  protected List<String>  err = new ArrayList<>();
  protected List<String>  out = new ArrayList<>();

  protected static  ResourceBundle  resourceBundle;
  private static    String          temp            = null;

  @After
  public void after() {
    err.clear();
    out.clear();
  }

  public void debug() {
    naarScherm("Out: " + out.size());
    for (var i = 0; i < out.size(); i++) {
      naarScherm(String.format("%3d - %s", i, out.get(i)));
    }
    naarScherm("Err: " + err.size());
    for (var i = 0; i < err.size(); i++) {
      foutNaarScherm(String.format("%3d - %s", i, err.get(i)));
    }
    naarScherm("<-->");
  }

  protected static void foutNaarScherm(String regel) {
      System.err.printf(regel);
  }

  protected static String getTemp() {
    if (null == temp) {
      temp  = System.getProperty("java.io.tmpdir");
    }

    return temp;
  }

  protected static void kopieerBestand(BufferedReader bron,
                                       BufferedWriter doel)
      throws IOException {
    String  data;

    while (null != (data = bron.readLine())) {
      doel.write(data);
    }
  }

  protected static void kopieerBestand(ClassLoader classLoader,
                                       String bron, String doel)
      throws IOException {
    var invoer  = classLoader.getResourceAsStream(bron);
    Files.copy(invoer, Paths.get(doel), StandardCopyOption.REPLACE_EXISTING);
  }

  protected static void naarScherm(String regel) {
    System.out.println(regel);
  }

  protected static void printBestand(String bestand) {
    try (var bron =
          new LineNumberReader(
              new InputStreamReader(new FileInputStream(new File(bestand))))) {
      String  data;
      while (null != (data = bron.readLine())) {
        naarScherm(data);
      }
    } catch (IOException e) {
      foutNaarScherm(e.getLocalizedMessage());
    }
  }

  protected static void verwijderBestanden(String directory,
                                           String[] bestanden) {
    for (var bestand : bestanden) {
      try {
        Files.delete(Paths.get(directory + File.separator + bestand));
      } catch (IOException ex) {
        // Kan gebeuren als een test niet volledig gelopen heeft.
      }
    }
  }
}
