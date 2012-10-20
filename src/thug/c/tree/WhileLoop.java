package thug.c.tree;

import thug.c.core.CompileContext;
import thug.c.core.Expression;
import thug.c.core.Statement;
import thug.c.core.Type;
import thug.c.core.TypingEnvironment;
import thug.c.core.TypingEnvironment.TypeException;

public class WhileLoop extends Statement {
   public final Expression conditional;
   public final Statement  statement;

   public WhileLoop(Expression conditional, Statement statement) {
      this.conditional = conditional;
      this.statement = statement;
   }

   public void compileC(StringBuilder sb, CompileContext cc) {
      sb.append(cc.tabbify("while ("));
      conditional.compileC(sb, cc);
      sb.append(") ");
      cc.neuterNextTab();
      statement.compileC(sb, cc);
   }

   public void compileSelf(StringBuilder sb, CompileContext cc) {
      sb.append(cc.tabbify("while ("));
      conditional.compileSelf(sb, cc);
      sb.append(") ");
      cc.neuterNextTab();
      statement.compileSelf(sb, cc);
   }

   public Type getTypeHard(TypingEnvironment e) throws TypeException {
      e.validateTypeAsBoolean(conditional.getType(e));
      return statement.getType(e);
   }
}
