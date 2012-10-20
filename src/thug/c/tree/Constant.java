package thug.c.tree;

import thug.c.core.CompileContext;
import thug.c.core.Expression;
import thug.c.core.Type;
import thug.c.core.TypingEnvironment;
import thug.c.core.TypingEnvironment.TypeException;
import thug.c.types.CoreTypes;

public class Constant extends Expression {
  public final String       str;
  public final ConstantType type;

  public static enum ConstantType {
    Character(new CoreTypes.Char8Type()),
    Integer(new CoreTypes.Integer32Type()),
    Long(new CoreTypes.Long64Type()),
    FloatingPoint(new CoreTypes.Double64Type());
    public final Type type;

    private ConstantType(Type type) {
      this.type = type;
    }
  }

  public Constant(final String str, final ConstantType type) {
    this.str = str;
    this.type = type;
  }

  public void compileC(final StringBuilder sb, final CompileContext cc) {
    sb.append(str);
  }

  public void compileSelf(final StringBuilder sb, final CompileContext cc) {
    sb.append(str);
  }

  public Type getTypeHard(TypingEnvironment e) throws TypeException {
    this.type.type.register(e);
    return this.type.type;
  }
}
