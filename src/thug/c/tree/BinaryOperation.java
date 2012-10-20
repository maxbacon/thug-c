package thug.c.tree;

import thug.c.core.CompileContext;
import thug.c.core.Expression;
import thug.c.core.Type;
import thug.c.core.TypingEnvironment;
import thug.c.core.TypingEnvironment.TypeException;

public class BinaryOperation extends Expression {
  public final Operation  op;
  public final Expression left;
  public final Expression right;

  public static enum Operation {
    ADD("+"),
    SUB("-"),
    MULT("*"),
    DIV("/"),
    MOD("%"),
    EQUALITY("=="),
    LESSTHAN("<"),
    LESSTHANEQ("<="),
    GREATERTHAN(">"),
    GREATERTHANEQ(">="),
    NOTEQUAL("!="),
    AND("&&"),
    OR("||");
    public final String code;

    private Operation(String code) {
      this.code = code;
    }
  }

  public BinaryOperation(Operation op, Expression left, Expression right) {
    this.op = op;
    this.left = left;
    this.right = right;
  }

  public void compileC(StringBuilder sb, CompileContext cc) {
    left.compileC(sb, cc);
    sb.append(op.code);
    right.compileC(sb, cc);
  }

  public void compileSelf(StringBuilder sb, CompileContext cc) {
    left.compileSelf(sb, cc);
    sb.append(op.code);
    right.compileSelf(sb, cc);
  }

  public Type getTypeHard(TypingEnvironment e) throws TypeException {
    left.getType(e);
    // TODO: Really fix this, as this is terrible, but... lazy
    return right.getType(e);
  }
}
