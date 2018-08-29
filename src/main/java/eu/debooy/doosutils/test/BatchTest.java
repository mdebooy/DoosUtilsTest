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

import eu.debooy.doosutils.access.Bestand;
import eu.debooy.doosutils.exception.BestandException;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.ResourceBundle;

import org.junit.After;


/**
 * @author Marco de Booij
 */
public abstract class BatchTest {
  protected List<String>  err = new ArrayList<String>();
  protected List<String>  out = new ArrayList<String>();

  protected static final  String          CHARSET         =
      Charset.defaultCharset().name();
  protected static final  ResourceBundle  resourceBundle  =
      ResourceBundle.getBundle("ApplicatieResources", new Locale("nl"));
  protected static final  String          TEMP            =
      System.getProperty("java.io.tmpdir");

  @After
  public void after() {
    err.clear();
    out.clear();
  }

  public void debug() {
    for (int i = 0; i < out.size(); i++) {
      System.out.println(out.get(i));
    }
    for (int i = 0; i < err.size(); i++) {
      System.out.println(err.get(i));
    }
  }

  protected static void kopieerBestand(BufferedReader bron, BufferedWriter doel)
      throws BestandException {
    Bestand.copy(bron, doel);
  }
}

