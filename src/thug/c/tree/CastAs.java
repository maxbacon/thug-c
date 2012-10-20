package thug.c.tree;

import thug.c.core.CompileContext;
import thug.c.core.Expression;
import thug.c.core.Type;
import thug.c.core.TypingEnvironment;
import thug.c.core.TypingEnvironment.TypeException;

public class CastAs extends Expression {
  public final Expression baseExpression;
  public final Type       castedType;

  public CastAs(Expression baseExpression, Type castedType) {
    this.baseExpression = baseExpression;
    this.castedType = castedType;
  }

  public void compileC(StringBuilder sb, CompileContext cc) {
    sb.append("((" + castedType.getCTypeString() + ")");
    baseExpression.compileC(sb, cc);
    sb.append(")");
  }

  public void compileSelf(StringBuilder sb, CompileContext cc) {
    baseExpression.compileSelf(sb, cc);
    sb.append(" as (" + castedType.getSelfTypeString() + ")");
  }

  public Type getTypeHard(TypingEnvironment e) throws TypeException {
    castedType.register(e);
    e.validateAssignment(castedType, baseExpression.getType(e));
    return castedType;
  }
}
