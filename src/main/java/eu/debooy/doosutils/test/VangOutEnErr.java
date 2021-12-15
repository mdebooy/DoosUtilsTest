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
import java.util.Arrays;
import java.util.List;


/**
 * @author Marco de Booij
 */
public abstract class VangOutEnErr {
  protected VangOutEnErr() {
    throw new IllegalStateException("Utility class");
  }

  public static void execute(Class<?> clazz, String methodName,
                             String[] args,
                             List<String> out, List<String> err) {
    try (var bosOut  = new ByteArrayOutputStream();
         var bosErr  = new ByteArrayOutputStream();
         var tempOut = new PrintStream(bosOut, true);
         var tempErr = new PrintStream(bosErr, true);
        ) {
      System.setOut(tempOut);
      System.setErr(tempErr);
      VangOutEnErr.invokeMethod(clazz, methodName, args);
      var reader  = new BufferedReader(new StringReader(bosOut.toString()));
      var lijn    = reader.readLine();
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
    }
  }

  public static void execute(String className, String methodName,
                             String[] args,
                             List<String> out, List<String> err)
      throws ClassNotFoundException {
    var clazz = Class.forName(className);

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

      Object[]  params  = Arrays.copyOf(args,args.length);
      method.invoke(null, params);
    } catch(NoSuchMethodException | SecurityException | IllegalAccessException
          | IllegalArgumentException | InvocationTargetException e) {
      throw new RuntimeException("Error executing " + methodName, e);
    }
  }
}
