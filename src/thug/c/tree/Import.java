package thug.c.tree;

import thug.c.core.CompileContext;
import thug.c.core.GlobalScopeDefn;
import thug.c.core.Type;
import thug.c.core.TypingEnvironment;
import thug.c.core.TypingEnvironment.TypeException;
import thug.c.types.CoreTypes;

public class Import extends GlobalScopeDefn {
   private final String importDef;

   public Import(String importDef) {
      this.importDef = importDef;
   }

   public Import extend(String ext) {
      return new Import(this.importDef + "." + ext);
   }

   public void compileC(StringBuilder sb, CompileContext cc) {
   }

   public void compileH(StringBuilder sb, CompileContext cc) {
      sb.append("#include \"" + importDef + ".h\"\n");
   }

   public void compileSelf(StringBuilder sb, CompileContext cc) {
      sb.append("import " + importDef + ";\n");
   }

   public void register(CompileContext cc) {
   }

   public Type getTypeHard(TypingEnvironment e) throws TypeException {
      return new CoreTypes.VoidType();
   }

   public Type getGlobalType() {
      return new CoreTypes.VoidType();
   }
}
