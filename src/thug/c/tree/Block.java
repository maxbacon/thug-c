package thug.c.tree;

import java.util.ArrayList;
import java.util.List;
import thug.c.core.CompileContext;
import thug.c.core.Statement;
import thug.c.core.Type;
import thug.c.core.TypingEnvironment;
import thug.c.core.TypingEnvironment.TypeException;
import thug.c.types.CoreTypes;

public class Block extends Statement {
   public final List<Statement> statements = new ArrayList<>();

   public void add(Statement s) {
      statements.add(s);
   }

   public void compileC(StringBuilder sb, CompileContext cc) {
      sb.append(cc.tabbify("{\n"));
      cc.pushTab();
      for (Statement stmt : statements) {
         stmt.compileC(sb, cc);
      }
      cc.popTab();
      sb.append(cc.tabbify("}\n"));
   }

   public void compileSelf(StringBuilder sb, CompileContext cc) {
      sb.append(cc.tabbify("{\n"));
      cc.pushTab();
      for (Statement stmt : statements) {
         stmt.compileSelf(sb, cc);
      }
      cc.popTab();
      sb.append(cc.tabbify("}\n"));
   }

   public Type getTypeHard(TypingEnvironment e) throws TypeException {
      for (Statement stmt : statements) {
         stmt.getType(e);
      }
      return new CoreTypes.VoidType();
   }
}
