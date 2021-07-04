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
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.io.StringReader;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.List;


/**
 * @author Marco de Booij
 */
public abstract class VangOutEnErr {
  private static final PrintStream OUT = System.out;
  private static final PrintStream ERR = System.err;

  private VangOutEnErr() {
    throw new IllegalStateException("Utility class");
  }

  private static void recoverOriginalOutput() {
    System.err.flush();
    System.out.flush();
    System.setOut(OUT);
    System.setErr(ERR);
  }

  public static void execute(Class<?> clazz, String methodName,
                             String[] args,
                             List<String> out, List<String> err) {
    ByteArrayOutputStream bosOut  = new ByteArrayOutputStream();
    ByteArrayOutputStream bosErr  = new ByteArrayOutputStream();
    PrintStream           tempOut = new PrintStream(bosOut, true);
    PrintStream           tempErr = new PrintStream(bosErr, true);
    System.setOut(tempOut);
    System.setErr(tempErr);

    try {
      VangOutEnErr.invokeMethod(clazz, methodName, args);
      BufferedReader  reader  =
          new BufferedReader(new StringReader(bosOut.toString()));
      String          lijn    = reader.readLine();
      while (null != lijn) {
        out.add(lijn);
        lijn  = reader.readLine();
      }
      reader  = new BufferedReader(new StringReader(bosErr.toString()));
      lijn    = reader.readLine();
      while (null != lijn) {
        err.add(lijn);
        lijn  = reader.readLine();
      }
    } catch(IOException e) {
      throw new RuntimeException("Error obtaining output for ["
                                  + clazz.getName() + "." + methodName + "]",
                                 e);
    } finally {
      recoverOriginalOutput();
      try {
        bosErr.close();
        bosOut.close();
        tempErr.close();
        tempOut.close();
      } catch (IOException e) {
        throw new RuntimeException("Error closing output for ["
            + clazz.getName() + "." + methodName + "]",
          e);
      }
    }
  }

  public static void execute(String className, String methodName,
                             String[] args,
                             List<String> out, List<String> err)
      throws ClassNotFoundException {
    @SuppressWarnings("rawtypes")
    Class   clazz   = Class.forName(className);

    execute(clazz, methodName, args, out, err);
  }

  private static void invokeMethod(Class<?> clazz, String methodName,
                                   String[] args) {
    try {
      Method method;
        method = clazz.getMethod(methodName, (new String[0]).getClass());

      if ((method.getReturnType() != Void.TYPE)
          || (!Modifier.isStatic(method.getModifiers()))) {
        throw new RuntimeException("No executable found: static "
                                   + methodName + "(String[])");
      }

      Object[]  params  = { args };
      method.invoke(null, params);
    } catch(NoSuchMethodException | SecurityException | IllegalAccessException
          | IllegalArgumentException | InvocationTargetException e) {
      throw new RuntimeException("Error executing " + methodName, e);
    }
  }
}
