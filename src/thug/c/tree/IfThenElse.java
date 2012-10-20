package thug.c.tree;

import thug.c.core.CompileContext;
import thug.c.core.Expression;
import thug.c.core.LatentFinal;
import thug.c.core.Statement;
import thug.c.core.Type;
import thug.c.core.TypingEnvironment;
import thug.c.core.TypingEnvironment.TypeException;
import thug.c.types.CoreTypes;

public class IfThenElse extends Statement {
   public final Expression             conditional;
   public final Statement              trueStatement;
   public final LatentFinal<Statement> falseStatement = new LatentFinal<>();

   public IfThenElse(Expression conditional, Statement trueStatement) {
      this.conditional = conditional;
      this.trueStatement = trueStatement;
   }

   public void setFalseStatement(Statement falseStatement) {
      this.falseStatement.set(falseStatement);
   }

   public void compileC(StringBuilder sb, CompileContext cc) {
      sb.append(cc.tabbify("if ("));
      conditional.compileC(sb, cc);
      sb.append(") ");
      cc.neuterNextTab();
      trueStatement.compileC(sb, cc);
      if (falseStatement.get() != null) {
         sb.append(cc.tabbify("else "));
         cc.neuterNextTab();
         falseStatement.get().compileC(sb, cc);
      }
   }

   public void compileSelf(StringBuilder sb, CompileContext cc) {
      sb.append(cc.tabbify("if ("));
      conditional.compileSelf(sb, cc);
      sb.append(") ");
      cc.neuterNextTab();
      trueStatement.compileSelf(sb, cc);
      if (falseStatement != null) {
         sb.append(cc.tabbify("else "));
         cc.neuterNextTab();
         falseStatement.get().compileSelf(sb, cc);
      }
   }

   public Type getTypeHard(TypingEnvironment e) throws TypeException {
      e.validateTypeAsBoolean(conditional.getType(e));
      trueStatement.getType(e);
      if (falseStatement.get() != null) {
         falseStatement.get().getType(e);
      }
      return new CoreTypes.VoidType();
   }
}
