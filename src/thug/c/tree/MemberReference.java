package thug.c.tree;

import thug.c.core.CompileContext;
import thug.c.core.Expression;
import thug.c.core.Type;
import thug.c.core.TypingEnvironment;
import thug.c.core.TypingEnvironment.TypeException;
import thug.c.types.CoreTypes;

public class MemberReference extends Expression {
  private final Expression   baseExpression;
  private final String       member;
  private final HowReference op;

  public static enum HowReference {
    DIRECT("."),
    INDIRECT_VIA_POINTER("->");
    public final String code;

    private HowReference(String code) {
      this.code = code;
    }
  }

  public MemberReference(Expression baseExpression, String member, HowReference op) {
    this.baseExpression = baseExpression;
    this.member = member;
    this.op = op;
  }

  public void compileC(StringBuilder sb, CompileContext cc) {
    this.baseExpression.compileC(sb, cc);
    sb.append(op.code);
    sb.append(member);
  }

  public void compileSelf(StringBuilder sb, CompileContext cc) {
    this.baseExpression.compileSelf(sb, cc);
    sb.append(op.code);
    sb.append(member);
  }
  
  public Type getTypeHard(TypingEnvironment e) throws TypeException {
    baseExpression.getType(e);
    System.out.println("#TODO:" + this.getClass().toString());
    return new CoreTypes.Char8Type();
  }
}
