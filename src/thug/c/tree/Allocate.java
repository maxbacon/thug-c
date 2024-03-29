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
   public final Type                    type;
   public final LatentFinal<Expression> arity = new LatentFinal<>();

   public Allocate(Type type) {
      this.type = type;
   }

   public void setArity(Expression arity) {
      this.arity.set(arity);
   }

   public void compileC(StringBuilder sb, CompileContext cc) {
      if (type instanceof StructReferenceType) {
         String structName = ((StructReferenceType) type).name;
         sb.append("allocate_new_" + structName + "(");
         if (arity.get() != null) {
            arity.get().compileC(sb, cc);
         } else {
            sb.append("1");
         }
         sb.append(")");
      } else {
         throw new IllegalStateException("gosh, I'm lazy");
      }

   }

   public void compileSelf(StringBuilder sb, CompileContext cc) {
      boolean shouldWrap = type instanceof PointerType;
      sb.append("new ");
      if (shouldWrap)
         sb.append("[");
      sb.append(type.getSelfTypeString());
      if (shouldWrap)
         sb.append("]");
      if (arity.get() != null) {
         sb.append("[");
         arity.get().compileSelf(sb, cc);
         sb.append("]");
      }
   }

   public Type getTypeHard(TypingEnvironment e) throws TypeException {
      if (type instanceof StructReferenceType) {
         return new PointerType(type);
      }
      if (arity.get() != null) {
         return new PointerType(type);
      } else {
         throw new IllegalStateException("wrong!");
      }
   }
}
