package thug.c.tree;

import thug.c.core.CompileContext;
import thug.c.core.Expression;
import thug.c.core.LatentFinal;
import thug.c.core.Statement;
import thug.c.core.Type;
import thug.c.core.TypingEnvironment;
import thug.c.core.TypingEnvironment.TypeException;
import thug.c.types.CoreTypes;

public class DeclareType extends Statement {
   public final Type                    type;
   public final String                  name;
   public final LatentFinal<Expression> value = new LatentFinal<>();

   public DeclareType(Type type, String name) {
      this.type = type;
      this.name = name;
   }

   public DeclareType setValue(Expression value) {
      this.value.set(value);
      return this;
   }

   public void compileC(StringBuilder sb, CompileContext cc) {
      sb.append(cc.tabbify(type.getCTypeString() + " " + name));
      if (value.get() != null) {
         sb.append("=");
         value.get().compileC(sb, cc);
      }
      sb.append(";\n");
   }

   public void compileSelf(StringBuilder sb, CompileContext cc) {
      sb.append(cc.tabbify(type.getSelfTypeString() + " " + name));
      if (value.get() != null) {
         sb.append("=");
         value.get().compileSelf(sb, cc);
      }
      sb.append(";\n");
   }

   public Type getTypeHard(TypingEnvironment e) throws TypeException {
      type.register(e);
      e.declareType(name, type);
      if (value.get() != null) {
         e.validateAssignment(type, value.get().getType(e));
      }
      return new CoreTypes.VoidType();
   }
}
