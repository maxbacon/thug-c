package thug.c.types;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.concurrent.atomic.AtomicInteger;
import thug.c.core.CompileContext;
import thug.c.core.LatentFinal;
import thug.c.core.Type;
import thug.c.core.TypingEnvironment;

public class FunctionalType implements Type {
  private static final AtomicInteger            COUNT         = new AtomicInteger();
  private static final HashMap<String, Integer> SIGNATURE_MAP = new HashMap<>();
  private static final HashSet<Integer>         hasRegistered = new HashSet<>();
  public final ArrayList<Type>                  inputs        = new ArrayList<>();
  public final LatentFinal<Type>                returnType    = new LatentFinal<>();
  public final LatentFinal<Boolean>             isGlobal      = new LatentFinal<>();
  public final boolean                          isSpecifiedAndRequiresClosureStruct;

  public FunctionalType(boolean isSpecifiedAndRequiresClosureStruct) {
    this.isSpecifiedAndRequiresClosureStruct = isSpecifiedAndRequiresClosureStruct;
  }

  public int computeSignature() {
    StringBuilder sb = new StringBuilder();
    for (Type input : inputs) {
      sb.append("!");
      sb.append(input.getSelfTypeString());
    }
    sb.append("->");
    sb.append(returnType.get().getSelfTypeString());
    String signature = sb.toString();
    if (SIGNATURE_MAP.containsKey(signature)) {
      return SIGNATURE_MAP.get(signature);
    } else {
      int id = COUNT.incrementAndGet();
      SIGNATURE_MAP.put(signature, id);
      return id;
    }
  }

  public void add(Type input) {
    inputs.add(input);
  }

  public void setReturnType(Type rt) {
    returnType.set(rt);
  }

  public boolean isGlobal() {
    return isGlobal.get() != null;
  }

  public void markAsGlobal() {
    isGlobal.set(true);
  }

  public String getCTypeString() {
    return "GC_CLOSURE_" + computeSignature();
  }

  public String getSelfTypeString() {
    StringBuilder sb = new StringBuilder();
    sb.append("#(");
    boolean first = true;
    for (Type input : inputs) {
      if (!first) {
        sb.append(",");
      }
      first = false;
      sb.append(input.getSelfTypeString());
    }
    sb.append(")->");
    sb.append(returnType.get().getSelfTypeString());
    sb.append("#");
    return sb.toString();
  }

  public void register(TypingEnvironment env) {
    int id = computeSignature();
    if (hasRegistered.contains(id))
      return;
    hasRegistered.add(id);
    if (isSpecifiedAndRequiresClosureStruct) {
      StringBuilder closureH = new StringBuilder();
      closureH.append("typedef struct " + getCTypeString() + " {\n");
      closureH.append(CompileContext.TAB + returnType.get().getCTypeString() + " (*func)(");
      closureH.append("void*");
      for (Type argType : inputs) {
        closureH.append(", ");
        closureH.append(argType.getCTypeString());
      }
      closureH.append(");\n");
      closureH.append(CompileContext.TAB + "void* context;\n");
      closureH.append("} " + getCTypeString() + ";\n");
      env.registerCoreHeader(closureH.toString());
    }
  }
}
