package thug.c.tree;

import thug.c.core.CompileContext;
import thug.c.core.Expression;
import thug.c.core.Type;
import thug.c.core.TypingEnvironment;
import thug.c.core.TypingEnvironment.TypeException;

public class Variable extends Expression {
   public final String variable;

   public Variable(String variable) {
      this.variable = variable;
   }

   public void compileC(StringBuilder sb, CompileContext cc) {
      sb.append(variable);
   }

   public void compileSelf(StringBuilder sb, CompileContext cc) {
      sb.append(variable);
   }

   public Type getTypeHard(TypingEnvironment e) throws TypeException {
      return e.lookup(variable);
   }
}
