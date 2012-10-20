package thug.c.types;

import thug.c.core.Type;
import thug.c.core.TypingEnvironment;

public class PointerType implements Type {
  public final Type baseType;

  public PointerType(Type baseType) {
    this.baseType = baseType;
  }

  public String getCTypeString() {
    return this.baseType.getCTypeString() + "*";
  }

  public String getSelfTypeString() {
    return this.baseType.getSelfTypeString() + "*";
  }

  public void register(TypingEnvironment env) {
  }
}
