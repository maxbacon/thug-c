package thug.c.core;

public abstract class GlobalScopeDefn extends Tree {
   public abstract void compileH(StringBuilder sb, CompileContext cc);

   public abstract void register(CompileContext cc);

   public abstract Type getGlobalType();
}
