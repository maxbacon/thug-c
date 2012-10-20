package thug.c.core;

import thug.c.core.TypingEnvironment.TypeException;

public abstract class Tree {
  public abstract void compileC(StringBuilder sb, CompileContext cc);

  public abstract void compileSelf(StringBuilder sb, CompileContext cc);

  private final LatentFinal<Type> type = new LatentFinal<>();

  public abstract Type getTypeHard(TypingEnvironment e) throws TypeException;

  public Type getType(TypingEnvironment e) throws TypeException {
    if (type.get() == null) {
      Type val = this.getTypeHard(e);
      type.set(val);
      val.register(e);
    }
    return type.get();
  }
}
