package thug.c.tree;

import java.util.ArrayList;
import thug.c.core.CompileContext;
import thug.c.core.Expression;
import thug.c.core.LatentFinal;
import thug.c.core.Type;
import thug.c.core.TypingEnvironment;
import thug.c.core.TypingEnvironment.TypeException;
import thug.c.types.FunctionalType;

public class CallFunction extends Expression {
  public final Expression            func;
  public final ArrayList<Expression> args = new ArrayList<>();
  public LatentFinal<FunctionalType> fType = new LatentFinal<>();

  public CallFunction(Expression func) {
    this.func = func;
  }

  public void add(Expression arg) {
    args.add(arg);
  }

  public void compileC(StringBuilder sb, CompileContext cc) {
    func.compileC(sb, cc);
    boolean first = true;
    if (!fType.get().isGlobal()) {
      sb.append(".func(");
      func.compileC(sb, cc);
      sb.append(".context");
      first = false;
    } else {
      sb.append("(");
    }
    for (Expression arg : args) {
      if (!first)
        sb.append(", ");
      first = false;
      arg.compileC(sb, cc);
    }
    sb.append(")");
  }

  public void compileSelf(StringBuilder sb, CompileContext cc) {
    func.compileSelf(sb, cc);
    sb.append("(");
    boolean first = true;
    for (Expression arg : args) {
      if (!first)
        sb.append(",");
      first = false;
      arg.compileSelf(sb, cc);
    }
    sb.append(")");
  }

  public Type getTypeHard(TypingEnvironment e) throws TypeException {
    for (Expression se : args)
      se.getType(e);
    
    Type returnType = e.getReturnType(func.getType(e));
    FunctionalType ft = (FunctionalType) func.getType(e);
    fType.set(ft);
    return returnType;
  }
}
