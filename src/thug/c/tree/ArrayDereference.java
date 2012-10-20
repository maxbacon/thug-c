package thug.c.tree;

import thug.c.core.CompileContext;
import thug.c.core.Expression;
import thug.c.core.Type;
import thug.c.core.TypingEnvironment;
import thug.c.core.TypingEnvironment.TypeException;

public class ArrayDereference extends Expression {
   public final Expression baseExpression;
   public final Expression index;

   public ArrayDereference(Expression baseExpression, Expression index) {
      this.baseExpression = baseExpression;
      this.index = index;
   }

   public void compileC(StringBuilder sb, CompileContext cc) {
      this.baseExpression.compileC(sb, cc);
      sb.append("[");
      this.index.compileC(sb, cc);
      sb.append("]");
   }

   public void compileSelf(StringBuilder sb, CompileContext cc) {
      this.baseExpression.compileSelf(sb, cc);
      sb.append("[");
      this.index.compileSelf(sb, cc);
      sb.append("]");
   }

   public Type getTypeHard(TypingEnvironment e) throws TypeException {
      e.validateTypeAsInteger(index.getType(e));
      return e.getPointersBaseType(baseExpression.getType(e));
   }
}
