package thug.c.tree;

import thug.c.core.CompileContext;
import thug.c.core.Expression;
import thug.c.core.LatentFinal;
import thug.c.core.Statement;
import thug.c.core.Type;
import thug.c.core.TypingEnvironment;
import thug.c.core.TypingEnvironment.TypeException;
import thug.c.types.CoreTypes;

public class Return extends Statement {
  public final LatentFinal<Expression> returnValue = new LatentFinal<>();

  public Return() {
  }

  public void setReturnValue(Expression returnValue) {
    this.returnValue.set(returnValue);
  }

  public void compileC(StringBuilder sb, CompileContext cc) {
    sb.append(cc.tabbify("return"));
    if (returnValue.get() != null) {
      sb.append(" ");
      returnValue.get().compileC(sb, cc);
    }
    sb.append(";\n");
  }

  public void compileSelf(StringBuilder sb, CompileContext cc) {
    sb.append(cc.tabbify("return"));
    if (returnValue.get() != null) {
      sb.append(" ");
      returnValue.get().compileSelf(sb, cc);
    }
    sb.append(";\n");
  }

  public Type getTypeHard(TypingEnvironment e) throws TypeException {
    if (returnValue.get() != null) {
      returnValue.get().getType(e);
    }
    return new CoreTypes.VoidType();
  }
}
