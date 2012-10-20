package thug.c.tree;

import thug.c.core.CompileContext;
import thug.c.core.Expression;
import thug.c.core.Type;
import thug.c.core.TypingEnvironment;
import thug.c.core.TypingEnvironment.TypeException;

public class Parenthesis extends Expression {
  private final Expression baseExpression;

  public Parenthesis(Expression baseExpression) {
    this.baseExpression = baseExpression;
  }

  public void compileC(StringBuilder sb, CompileContext cc) {
    sb.append("(");
    this.baseExpression.compileC(sb, cc);
    sb.append(")");
  }

  public void compileSelf(StringBuilder sb, CompileContext cc) {
    sb.append("(");
    this.baseExpression.compileSelf(sb, cc);
    sb.append(")");
  }
  
  public Type getTypeHard(TypingEnvironment e) throws TypeException {
    return baseExpression.getType(e);
  }
  
  
}
