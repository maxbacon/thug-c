package thug.c.tree;

import thug.c.core.CompileContext;
import thug.c.core.Expression;
import thug.c.core.Statement;
import thug.c.core.Type;
import thug.c.core.TypingEnvironment;
import thug.c.core.TypingEnvironment.TypeException;

public class Evaluate extends Statement {
  public final Expression expr;

  public Evaluate(Expression expr) {
    this.expr = expr;
  }

  public void compileC(StringBuilder sb, CompileContext cc) {
    sb.append(cc.tabbify(""));
    expr.compileC(sb, cc);
    sb.append(";\n");
  }

  public void compileSelf(StringBuilder sb, CompileContext cc) {
    sb.append(cc.tabbify(""));
    expr.compileSelf(sb, cc);
    sb.append(";\n");
  }

  public Type getTypeHard(TypingEnvironment e) throws TypeException {
    return expr.getType(e);
  }
}
