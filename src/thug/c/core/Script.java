package thug.c.core;

import java.util.ArrayList;
import thug.c.core.TypingEnvironment.TypeException;

public class Script {
  private final ArrayList<GlobalScopeDefn> defns = new ArrayList<>();
  public String                            filename;

  public void register(CompileContext cc) {
    for (GlobalScopeDefn g : defns) {
      g.register(cc);
    }
  }

  public void add(GlobalScopeDefn defn) {
    defns.add(defn);
  }

  public String compileH(CompileContext cc) {
    StringBuilder sb = new StringBuilder();
    sb.append("#include \"" + cc.packageName + ".h\"\n\n");
    for (GlobalScopeDefn defn : defns) {
      defn.compileH(sb, cc);
    }
    return sb.toString();
  }

  public String compileC(String headerFilename, CompileContext cc) {
    StringBuilder sb = new StringBuilder();
    sb.append("#include \"platform.h\"\n");
    sb.append("#include \"" + headerFilename + "\"\n\n");
    for (GlobalScopeDefn defn : defns) {
      StringBuilder local = new StringBuilder();
      defn.compileC(local, cc);
      cc.applySupport(sb);
      sb.append(local.toString());
    }
    return sb.toString();
  }

  public String compileSelf(CompileContext cc) {
    StringBuilder sb = new StringBuilder();
    for (GlobalScopeDefn defn : defns) {
      defn.compileSelf(sb, cc);
    }
    return sb.toString();
  }

  public void performTyping(TypingEnvironment env) throws TypeException {
    for (GlobalScopeDefn defn : defns) {
      defn.getType(env);
    }
  }
}
