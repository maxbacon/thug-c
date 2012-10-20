package thug.c;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FilenameFilter;
import java.io.IOException;

import org.antlr.runtime.ANTLRInputStream;
import org.antlr.runtime.CommonTokenStream;
import org.antlr.runtime.RecognitionException;
import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.OptionBuilder;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;
import org.apache.commons.cli.PosixParser;

import thug.c.core.CompileContext;
import thug.c.core.Constants;
import thug.c.core.Script;
import thug.c.language.ThugCLexer;
import thug.c.language.ThugCParser;

public class ThugC {

   public final static class Args {
      public final String input;
      public final String output;
      public final String name;

      public Args(String[] args) throws ParseException {
         String _tab = "  ";
         String _name = "a";
         Options options = new Options();
         OptionBuilder.withLongOpt("input");
         OptionBuilder.withArgName("inputPath");
         OptionBuilder.withDescription("The input path which contain the *.tc source files");
         OptionBuilder.hasArg();
         OptionBuilder.isRequired();
         options.addOption(OptionBuilder.create("i"));
         
         OptionBuilder.withLongOpt("output");
         OptionBuilder.withArgName("outputPath");
         OptionBuilder.withDescription("The output path where the compiler will write all the C code");
         OptionBuilder.hasArg();
         OptionBuilder.isRequired();
         options.addOption(OptionBuilder.create("o"));

         OptionBuilder.withLongOpt("name");
         OptionBuilder.withArgName("name");
         OptionBuilder.withDescription("The name of the project");
         OptionBuilder.hasArg();
         options.addOption(OptionBuilder.create("n"));
         
         // options.addOption(OptionBuilder.withLongOpt("platform").withArgName("platformCodePath").withDescription("The code that we are going to copy into the output path").hasArg().isRequired().create("p"));
         
         OptionBuilder.withLongOpt("tab");
         OptionBuilder.withArgName("number of spaces");
         OptionBuilder.withDescription("The number of spaces per tab");
         OptionBuilder.hasArg();
         options.addOption(OptionBuilder.create("t"));

         CommandLineParser parser = new PosixParser();
         CommandLine cmd = parser.parse(options, args);

         this.input = cmd.getOptionValue("input");
         this.output = cmd.getOptionValue("output");
         if (cmd.hasOption("t")) {
            int spaces = Integer.parseInt(cmd.getOptionValue("t"));
            _tab = "";
            while (spaces > 0) {
               _tab += " ";
               spaces--;
            }
         }
         Constants.TAB.set(_tab);
         if (cmd.hasOption("n")) {
            _name = cmd.getOptionValue("n");
         }
         this.name = _name;
      }
   }

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
      System.out.println("INPUT:" + args.input);
      System.out.println("OUTPUT:" + args.output);

      File[] files = new File(args.input).listFiles(new FilenameFilter() {
         public boolean accept(File arg0, String filename) {
            return filename.endsWith(".tc");
         }
      });

      CompileContext cc = new CompileContext(args.name);
      Script[] scripts = new Script[files.length];
      for (int k = 0; k < files.length; k++) {
         scripts[k] = compile(files[k]);
         scripts[k].register(cc);
      }

      for (Script script : scripts) {
         script.performTyping(cc.typing);
      }

      cc.typing.finish();

      FileOutputStream fos = new FileOutputStream(args.output + File.separatorChar + cc.packageName + ".h");
      fos.write(cc.typing.coreHeader.toString().getBytes());
      fos.flush();
      fos.close();

      for (Script script : scripts) {
         String pathOut = args.output + File.separatorChar + script.filename.replaceAll("\\.tc", ".h");
         fos = new FileOutputStream(pathOut);
         fos.write(script.compileH(cc).getBytes());
         fos.flush();
         fos.close();
      }

      for (Script script : scripts) {
         String pathOut = args.output + File.separatorChar + script.filename.replaceAll("\\.tc", ".c");
         fos = new FileOutputStream(pathOut);
         fos.write(script.compileC(script.filename.replaceAll("\\.tc", ".h"), cc).getBytes());
         fos.flush();
         fos.close();
      }

   }
}
