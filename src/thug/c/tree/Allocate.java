package thug.c.tree;

import thug.c.core.CompileContext;
import thug.c.core.Expression;
import thug.c.core.LatentFinal;
import thug.c.core.Type;
import thug.c.core.TypingEnvironment;
import thug.c.core.TypingEnvironment.TypeException;
import thug.c.types.PointerType;
import thug.c.types.StructReferenceType;

public class Allocate extends Expression {
  public final String               structName;
  public final LatentFinal<Expression> arity = new LatentFinal<>();

  public Allocate(String structName) {
    this.structName = structName;
  }

  public void setArity(Expression arity) {
    this.arity.set(arity);
  }

  public void compileC(StringBuilder sb, CompileContext cc) {
    sb.append("allocate_new_" + structName + "(");
    if (arity.get() != null) {
      arity.get().compileC(sb, cc);
    } else {
      sb.append("1");
    }
    sb.append(")");
  }

  public void compileSelf(StringBuilder sb, CompileContext cc) {
    sb.append("new " + structName);
    if (arity.get() != null) {
      sb.append("[");
      arity.get().compileSelf(sb, cc);
      sb.append("]");
    }
  }

  public Type getTypeHard(TypingEnvironment e) throws TypeException {
    return new PointerType(new StructReferenceType(structName));
  }
}
