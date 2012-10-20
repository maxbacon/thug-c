package thug.c.parse;

import java.io.ByteArrayInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import org.antlr.runtime.ANTLRInputStream;
import org.antlr.runtime.CommonTokenStream;
import org.antlr.runtime.RecognitionException;
import org.junit.Test;
import thug.c.core.CompileContext;
import thug.c.core.Script;
import thug.c.language.ThugCLexer;
import thug.c.language.ThugCParser;

public class ParseAnonymousFunction {
   private static Script compile(InputStream reading) throws FileNotFoundException, IOException {
      try {
         ANTLRInputStream input = new ANTLRInputStream(reading);
         // ANTLRStringStream input = new ANTLRStringStream(s);
         CommonTokenStream tokens = new CommonTokenStream(new ThugCLexer(input));
         ThugCParser parser = new ThugCParser(tokens);
         try {
            Script script = parser.fle().script;
            script.filename = "something";
            return script;
         } catch (RecognitionException e) {
            return null;
         }
      } finally {
         reading.close();
      }
   }

   @Test
   public void testX() throws Exception {
      Script script = compile(new ByteArrayInputStream("void foo() { int zzz = 1; # (int)->void # x; x = function ( int y) -> void { int k = zzz; }; x(123); }".getBytes()));
      CompileContext ctx = new CompileContext("foo");
      script.register(ctx);
      script.performTyping(ctx.typing);
      System.out.println(script.compileC("foo.h", ctx));
   }
}
