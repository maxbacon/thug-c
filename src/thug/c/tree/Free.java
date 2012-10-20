package thug.c.tree;

import thug.c.core.CompileContext;
import thug.c.core.Expression;
import thug.c.core.LatentFinal;
import thug.c.core.Statement;
import thug.c.core.Type;
import thug.c.core.TypingEnvironment;
import thug.c.core.TypingEnvironment.TypeException;
import thug.c.types.CoreTypes;
import thug.c.types.FunctionalType;

public class Free extends Statement {

   public final Expression           expression;
   public final LatentFinal<Boolean> isFunctional = new LatentFinal<>();

   public Free(Expression expression) {
      this.expression = expression;
   }

   @Override
   public void compileC(StringBuilder sb, CompileContext cc) {
      sb.append(cc.tabbify("free()"));
      expression.compileSelf(sb, cc);
      sb.append(")");
      if (isFunctional.get()) {
         sb.append(".context");
      }
      sb.append(");");
   }

   @Override
   public void compileSelf(StringBuilder sb, CompileContext cc) {
      sb.append(cc.tabbify("free "));
      expression.compileSelf(sb, cc);
      sb.append(";");
   }

   @Override
   public Type getTypeHard(TypingEnvironment e) throws TypeException {
      isFunctional.set(expression.getType(e) instanceof FunctionalType);
      return new CoreTypes.VoidType();
   }

}
