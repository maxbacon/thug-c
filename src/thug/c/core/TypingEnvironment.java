package thug.c.core;

import java.util.ArrayList;
import java.util.HashMap;
import thug.c.tree.FunctionDefinition;
import thug.c.tree.StructDefinition;
import thug.c.types.FunctionalType;
import thug.c.types.PointerType;
import thug.c.types.CoreTypes.Integer32Type;

public class TypingEnvironment {
  public class Scope {
    public final Scope                 parent;
    public final HashMap<String, Type> variables = new HashMap<>();

    public Scope() {
      this.parent = null;
    }

    public Scope(Scope parent) {
      this.parent = parent;
    }
  }

  private Scope              scope;
  public final StringBuilder coreHeader = new StringBuilder();

  public TypingEnvironment() {
    // open the global scope
    pushScope();
    coreHeader.append("/* header stuff here */\n\n");
  }

  public void registerCoreHeader(String header) {
    coreHeader.append(header);
  }

  public void finish() {
  }

  public class TypeException extends Exception {
    private static final long serialVersionUID = -1536470064822807847L;
  }

  public Type getPointersBaseType(Type t) throws TypeException {
    PointerType pt = (PointerType) t;
    return pt.baseType;
  }

  public void validateTypeAsInteger(Type t) throws TypeException {
    // do something here
    boolean isInteger = t instanceof Integer32Type;
    if (!isInteger) {
      throw new TypeException();
    }
  }

  public void validateTypeAsBoolean(Type t) {
  }

  public void pushScope() {
    scope = new Scope(scope);
  }

  public void popScope() {
    scope = scope.parent;
  }

  public void declareType(String name, Type type) {
    scope.variables.put(name, type);
  }

  private ArrayList<NameTypePair> watching = null;

  public void watchForFreeVariables(ArrayList<NameTypePair> watch) {
    this.watching = watch;
  }

  public void unwatch() {
    this.watching = null;
  }

  public Type lookup(String name) {
    Scope head = scope;
    int skip = 0;
    Type val = null;
    while (head != null && val == null) {
      skip++;
      val = head.variables.get(name);
      head = head.parent;
    }
    if (skip > 1 && this.watching != null) {
      this.watching.add(new NameTypePair(name, val));
    }
    if (val == null) {
      throw new NullPointerException(name + " not found");
    }
    return val;
  }

  public void validateAssignment(Type dst, Type src) {
  }

  public void add(FunctionDefinition fdef) {
    declareType(fdef.name, fdef.getGlobalType());
  }

  public void add(StructDefinition sdef) {
  }

  public Type getReturnType(Type t) {
    FunctionalType ft = (FunctionalType) t;
    return ft.returnType.get();
  }
}
