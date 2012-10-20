package thug.c.types;

import thug.c.core.Type;
import thug.c.core.TypingEnvironment;

public class StructReferenceType implements Type {
  final String name;

  public StructReferenceType(String name) {
    this.name = name;
  }

  public String getCTypeString() {
    return "GC_STRUCT_" + name.toUpperCase();
  }

  public String getSelfTypeString() {
    return this.name;
  }

  public void register(TypingEnvironment env) {
  }
}
