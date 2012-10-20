package thug.c.tree;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map.Entry;
import java.util.concurrent.atomic.AtomicInteger;
import thug.c.core.CompileContext;
import thug.c.core.Expression;
import thug.c.core.GlobalScopeDefn;
import thug.c.core.LatentFinal;
import thug.c.core.NameTypePair;
import thug.c.core.Type;
import thug.c.core.TypingEnvironment;
import thug.c.core.TypingEnvironment.TypeException;
import thug.c.types.CoreTypes;
import thug.c.types.PointerType;
import thug.c.types.StructReferenceType;

public class StructDefinition extends GlobalScopeDefn {
  private static AtomicInteger             TYPE_UID           = new AtomicInteger();
  public final int                         uid;
  public final String                      name;
  public final ArrayList<NameTypePair>     membersAsSpecified = new ArrayList<>();
  public final HashMap<String, Type>       members            = new HashMap<>();
  public final HashMap<String, Expression> defaultValues      = new HashMap<>();
  public final StructReferenceType         refType;
  public LatentFinal<String>               parent             = new LatentFinal<>();

  public StructDefinition(String name) {
    this.name = name;
    this.refType = new StructReferenceType(name);
    this.uid = TYPE_UID.incrementAndGet();
  }
  
  public void addGenericUnknown(String arg) {
	  throw new IllegalStateException("not supported yet");
  }

  public void setParent(String parent) {
    this.parent.set(parent);
  }

  public void add(String memberName, Type memberType) {
    members.put(memberName, memberType);
    membersAsSpecified.add(new NameTypePair(memberName, memberType));
  }

  public void addDefault(String memberName, Expression defaultValue) {
    defaultValues.put(memberName, defaultValue);
  }

  public void compileC(StringBuilder sb, CompileContext cc) {
    sb.append(cc.tabbify("") + this.refType.getCTypeString() + " __DEFAULT_" + this.refType.getCTypeString() + ";\n");
    sb.append(cc.tabbify("unsigned char __IS_NEW_") + this.refType.getCTypeString() + "=0xFF;\n");
    // apply defaults
    sb.append(cc.tabbify("") + this.refType.getCTypeString() + "* allocate_new_" + this.name + "(int arity) {\n");
    cc.pushTab();
    sb.append(cc.tabbify("int _IDX=0;\n"));
    sb.append(cc.tabbify("") + this.refType.getCTypeString() + "* ptr;\n");
    sb.append(cc.tabbify("if(__IS_NEW_" + this.refType.getCTypeString() + ") {\n"));
    cc.pushTab();
    sb.append(cc.tabbify("__DEFAULT_") + this.refType.getCTypeString() + ".__type_code=" + uid + ";\n");
    for (Entry<String, Expression> ent : this.defaultValues.entrySet()) {
      sb.append(cc.tabbify("__DEFAULT_") + this.refType.getCTypeString() + "." + ent.getKey() + "=");
      ent.getValue().compileC(sb, cc);
      sb.append(";\n");
    }
    sb.append(cc.tabbify("__IS_NEW_") + this.refType.getCTypeString() + "=0x00;\n");
    cc.popTab();
    sb.append(cc.tabbify("}\n"));
    sb.append(cc.tabbify("ptr = (") + this.refType.getCTypeString() + "*)malloc(arity*sizeof(" + this.refType.getCTypeString() + "));\n");
    sb.append(cc.tabbify("for(;_IDX<arity;_IDX++) {\n"));
    cc.pushTab();
    sb.append(cc.tabbify("ptr[_IDX]=__DEFAULT_" + this.refType.getCTypeString() + ";\n"));
    cc.popTab();
    sb.append(cc.tabbify("}\n"));
    sb.append(cc.tabbify("return ptr;\n"));
    cc.popTab();
    sb.append(cc.tabbify("}\n\n"));
  }

  private void compileMembers(StringBuilder sb, CompileContext cc) {
    for (NameTypePair ntp : membersAsSpecified) {
      String prefix = "";
      if (ntp.type instanceof StructReferenceType)
        prefix = "struct ";
      if (ntp.type instanceof PointerType) {
        if (((PointerType) ntp.type).baseType instanceof StructReferenceType)
          prefix = "struct ";
      }
      sb.append(cc.tabbify(prefix + ntp.type.getCTypeString() + " " + ntp.name + ";\n"));
    }
  }

  public void compileH(StringBuilder sb, CompileContext cc) {
    sb.append(cc.tabbify("typedef struct " + this.refType.getCTypeString() + " {\n"));
    cc.pushTab();
    sb.append(cc.tabbify("unsigned int __type_code;\n"));
    if (parent.get() != null) {
      cc.getStruct(parent.get()).compileMembers(sb, cc);
    }
    compileMembers(sb, cc);
    cc.popTab();
    sb.append(cc.tabbify("} " + this.refType.getCTypeString() + ";\n\n"));
    sb.append(cc.tabbify("") + this.refType.getCTypeString() + "* allocate_new_" + this.name + "(int arity);\n");
  }

  public void compileSelf(StringBuilder sb, CompileContext cc) {
    sb.append(cc.tabbify("struct " + this.name));
    if (parent.get() != null) {
      sb.append(" telescopes " + parent.get());
    }
    sb.append(" {\n");
    cc.pushTab();
    for (NameTypePair ntp : membersAsSpecified) {
      sb.append(cc.tabbify(ntp.type.getSelfTypeString() + " " + ntp.name + ";\n"));
    }
    cc.popTab();
    sb.append(cc.tabbify("}\n"));
  }

  public void register(CompileContext cc) {
    cc.register(this);
  }

  public Type getTypeHard(TypingEnvironment e) throws TypeException {
    for (NameTypePair ntp : membersAsSpecified) {
      ntp.type.register(e);
    }
    return new CoreTypes.VoidType();
  }

  public Type getGlobalType() {
    return new StructReferenceType(name);
  }
}
