package thug.c.tree;

import java.util.ArrayList;
import thug.c.core.CompileContext;
import thug.c.core.GlobalScopeDefn;
import thug.c.core.LatentFinal;
import thug.c.core.NameTypePair;
import thug.c.core.Type;
import thug.c.core.TypingEnvironment;
import thug.c.core.TypingEnvironment.TypeException;
import thug.c.types.CoreTypes;
import thug.c.types.FunctionalType;

public class FunctionDefinition extends GlobalScopeDefn {
   public final String                  name;
   public final ArrayList<NameTypePair> args = new ArrayList<>();
   public final Type                    returnType;
   public final LatentFinal<Block>      body = new LatentFinal<>();

   public FunctionDefinition(String name, Type returnType) {
      this.name = name;
      this.returnType = returnType;
   }

   public void setBody(Block body) {
      this.body.set(body);
   }

   public void add(String argName, Type argTyp) {
      this.args.add(new NameTypePair(argName, argTyp));
   }

   public void compileC(StringBuilder sb, CompileContext cc) {
      sb.append(returnType.getCTypeString() + " ");
      sb.append(name);
      sb.append("(");
      boolean first = true;
      for (NameTypePair arg : args) {
         if (!first)
            sb.append(", ");
         first = false;
         sb.append(arg.type.getCTypeString() + " " + arg.name);
      }
      sb.append(") ");
      cc.neuterNextTab();
      body.get().compileC(sb, cc);
   }

   public void compileH(StringBuilder sb, CompileContext cc) {
      sb.append(returnType.getCTypeString() + " ");
      sb.append(name);
      sb.append("(");
      boolean first = true;
      for (NameTypePair arg : args) {
         if (!first)
            sb.append(", ");
         first = false;
         sb.append(arg.type.getCTypeString() + " " + arg.name);
      }
      sb.append(");\n");
   }

   public void compileSelf(StringBuilder sb, CompileContext cc) {
      sb.append(returnType.getSelfTypeString() + " ");
      sb.append(name);
      sb.append("(");
      boolean first = true;
      for (NameTypePair arg : args) {
         if (!first)
            sb.append(", ");
         first = false;
         sb.append(arg.type.getSelfTypeString() + " " + arg.name);
      }
      sb.append(") ");
      cc.neuterNextTab();
      body.get().compileSelf(sb, cc);
   }

   public void register(CompileContext cc) {
      cc.register(this);
   }

   public Type getTypeHard(TypingEnvironment e) throws TypeException {
      e.pushScope();
      for (NameTypePair arg : args) {
         arg.type.register(e);
         e.declareType(arg.name, arg.type);
      }
      body.get().getType(e);
      e.popScope();
      return new CoreTypes.VoidType();
   }

   public Type getGlobalType() {
      FunctionalType ft = new FunctionalType(false);
      for (NameTypePair arg : args) {
         ft.add(arg.type);
      }
      ft.setReturnType(returnType);
      ft.markAsGlobal();
      return ft;
   }
}
