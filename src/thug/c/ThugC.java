package thug.c;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FilenameFilter;
import java.io.IOException;
import java.util.HashMap;

import org.antlr.runtime.ANTLRInputStream;
import org.antlr.runtime.CommonTokenStream;
import org.antlr.runtime.RecognitionException;
import thug.c.core.CompileContext;
import thug.c.core.Constants;
import thug.c.core.Script;
import thug.c.language.ThugCLexer;
import thug.c.language.ThugCParser;

public class ThugC {

   private static Script compile(File file) throws FileNotFoundException, IOException {
      FileInputStream reading = new FileInputStream(file);
      try {
         ANTLRInputStream input = new ANTLRInputStream(reading);
         // ANTLRStringStream input = new ANTLRStringStream(s);
         CommonTokenStream tokens = new CommonTokenStream(new ThugCLexer(input));
         ThugCParser parser = new ThugCParser(tokens);
         try {
            Script script = parser.fle().script;
            script.filename = file.getName();
            return script;
         } catch (RecognitionException e) {
            return null;
         }
      } finally {
         reading.close();
      }
   }

   public static void main(String[] raw) throws Exception {
      Args args = new Args(raw);
      String input = new File(args.getRequiredArg("input")).getCanonicalPath();
      String output = new File(args.getRequiredArg("output")).getCanonicalPath();
      System.out.println("INPUT:" + input);
      System.out.println("OUTPUT:" + output);
      Constants.TAB.set("   ");

      File[] files = new File(input).listFiles(new FilenameFilter() {
         public boolean accept(File arg0, String filename) {
            return filename.endsWith(".gc");
         }
      });

      CompileContext cc = new CompileContext("ccccc");
      Script[] scripts = new Script[files.length];
      for (int k = 0; k < files.length; k++) {
         scripts[k] = compile(files[k]);
         scripts[k].register(cc);
      }

      for (Script script : scripts) {
         script.performTyping(cc.typing);
      }

      cc.typing.finish();

      FileOutputStream fos = new FileOutputStream(output + File.separatorChar + cc.packageName + ".h");
      fos.write(cc.typing.coreHeader.toString().getBytes());
      fos.flush();
      fos.close();

      for (Script script : scripts) {
         String pathOut = output + File.separatorChar + script.filename.replaceAll("\\.gc", ".h");
         fos = new FileOutputStream(pathOut);
         fos.write(script.compileH(cc).getBytes());
         fos.flush();
         fos.close();
      }

      for (Script script : scripts) {
         String pathOut = output + File.separatorChar + script.filename.replaceAll("\\.gc", ".c");
         fos = new FileOutputStream(pathOut);
         fos.write(script.compileC(script.filename.replaceAll("\\.gc", ".h"), cc).getBytes());
         fos.flush();
         fos.close();
      }

   }

   private static class Args {
      public final HashMap<String, String> map = new HashMap<>();

      public Args(String[] args) {
         for (int k = 0; k < args.length; k++) {
            String arg = args[k];
            if (k < args.length - 1 && arg.length() > 2) {
               String op = args[k + 1];
               if (arg.charAt(0) == '-' && arg.charAt(1) == '-') {
                  map.put(arg.substring(2).toLowerCase(), op);
               }
            }
         }
      }

      public String getRequiredArg(String name) {
         String value = map.get(name);
         if (value == null) {
            throw new NullPointerException();
         }
         return value;
      }
   }
}
