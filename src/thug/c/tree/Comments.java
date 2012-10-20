package thug.c.tree;

import thug.c.core.CompileContext;
import thug.c.core.GlobalScopeDefn;
import thug.c.core.Statement;
import thug.c.core.Type;
import thug.c.core.TypingEnvironment;
import thug.c.core.TypingEnvironment.TypeException;
import thug.c.types.CoreTypes;

public class Comments {
  private static String commentify(String comment, CompileContext cc) {
    String[] lines = comment.split("\n");
    for (int k = 0; k < lines.length; k++) {
      lines[k] = lines[k].trim();
      boolean isEnd = lines[k].startsWith("*/");
      if (!isEnd && k > 0) {
        while (lines[k].length() > 0 && lines[k].charAt(0) == '*') {
          lines[k] = lines[k].substring(1);
        }
        lines[k] = " * " + lines[k].trim();
      } else if (isEnd) {
        lines[k] = " " + lines[k];
      }
    }
    StringBuilder sb = new StringBuilder();
    sb.append(cc.tabbify(lines[0]) + "\n");
    for (int k = 1; k < lines.length; k++) {
      sb.append(cc.tabbify(lines[k]) + "\n");
    }
    return sb.toString();
  }

  public static class GlobalComment extends GlobalScopeDefn {
    public final String comment;

    public GlobalComment(String comment) {
      this.comment = comment;
    }

    public void compileC(StringBuilder sb, CompileContext cc) {
      sb.append(commentify(comment, cc));
    }

    public void compileSelf(StringBuilder sb, CompileContext cc) {
      sb.append(commentify(comment, cc));
    }

    public void compileH(StringBuilder sb, CompileContext cc) {
      sb.append(commentify(comment, cc));
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

  public static class StatementComment extends Statement {
    public final String comment;

    public StatementComment(String comment) {
      this.comment = comment;
    }

    public void compileC(StringBuilder sb, CompileContext cc) {
      sb.append(commentify(comment, cc));
    }

    public void compileSelf(StringBuilder sb, CompileContext cc) {
      sb.append(commentify(comment, cc));
    }

    public void compileH(StringBuilder sb, CompileContext cc) {
    }

    public Type getTypeHard(TypingEnvironment e) throws TypeException {
      return new CoreTypes.VoidType();
    }
  }
}
