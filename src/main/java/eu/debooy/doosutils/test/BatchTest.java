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
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.LineNumberReader;
import java.io.PrintStream;
import java.io.StringReader;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;


/**
 * @author Marco de Booij
 */
public abstract class BatchTest {
  private static  final ByteArrayOutputStream outContent  =
      new ByteArrayOutputStream();
  private static  final ByteArrayOutputStream errContent  =
      new ByteArrayOutputStream();
  private static  final PrintStream           originalOut = System.out;
  private static  final PrintStream           originalErr = System.err;

  protected static  List<String>    err             = new ArrayList<>();
  protected static  List<String>    out             = new ArrayList<>();
  protected static  ResourceBundle  resourceBundle;
  private   static  String          temp            = null;

  protected static void after() {
    err.clear();
    out.clear();
    setErr();
    setOut();
    System.setOut(originalOut);
    System.setErr(originalErr);
  }

  protected static void before() {
    err.clear();
    out.clear();
    errContent.reset();
    outContent.reset();
    System.setOut(new PrintStream(outContent));
    System.setErr(new PrintStream(errContent));
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

    naarScherm("");
    naarScherm("<-->");
  }

  protected static void foutNaarScherm(String regel) {
      System.err.println(regel);
  }

  protected static String getTemp() {
    if (null == temp) {
      temp  = System.getProperty("java.io.tmpdir");
    }

    return temp;
  }

  private static List<String> getUitvoer(String bos) {
    List<String>  uitvoer = new ArrayList<>();
    var           reader  = new BufferedReader(
                                new StringReader(bos));
    try {
      var         lijn    = reader.readLine();

      while (null != lijn) {
        uitvoer.add(lijn);
        lijn  = reader.readLine();
      }
    } catch (IOException e) {
      uitvoer.add(e.getLocalizedMessage());
    }

    return uitvoer;
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

  private static void setErr() {
    err = getUitvoer(errContent.toString());
  }

  private static void setOut() {
    out = getUitvoer(outContent.toString());
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
