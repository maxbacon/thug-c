package thug.c.tree;

import thug.c.core.CompileContext;
import thug.c.core.Expression;
import thug.c.core.Type;
import thug.c.core.TypingEnvironment;
import thug.c.core.TypingEnvironment.TypeException;
import thug.c.types.PointerType;

public class DereferencePointer extends Expression {
  public final Expression expr;

  public DereferencePointer(Expression expr) {
    this.expr = expr;
  }

  public void compileC(StringBuilder sb, CompileContext cc) {
    sb.append("&");
    this.expr.compileC(sb, cc);
  }

  public void compileSelf(StringBuilder sb, CompileContext cc) {
    sb.append("&");
    this.expr.compileSelf(sb, cc);
  }
  
  public Type getTypeHard(TypingEnvironment e) throws TypeException {
    return new PointerType(expr.getType(e));
  }
}
