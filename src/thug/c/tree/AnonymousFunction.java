package thug.c.tree;

import java.util.ArrayList;
import java.util.concurrent.atomic.AtomicInteger;
import thug.c.core.CompileContext;
import thug.c.core.Constants;
import thug.c.core.Expression;
import thug.c.core.LatentFinal;
import thug.c.core.NameTypePair;
import thug.c.core.Statement;
import thug.c.core.Type;
import thug.c.core.TypingEnvironment;
import thug.c.core.TypingEnvironment.TypeException;
import thug.c.types.CoreTypes;
import thug.c.types.FunctionalType;
import thug.c.types.PointerType;
import thug.c.types.StructReferenceType;

public class AnonymousFunction extends Expression {
   private static final AtomicInteger           supportMethodCounter = new AtomicInteger();
   public final ArrayList<NameTypePair>         args                 = new ArrayList<>();
   public final LatentFinal<Type>               returnType           = new LatentFinal<>();
   public final LatentFinal<Block>              body                 = new LatentFinal<>();
   public final int                             methodIdx;
   public final LatentFinal<FunctionalType>     functionType         = new LatentFinal<>();
   public final ArrayList<NameTypePair>         freeVariables        = new ArrayList<>();
   public final LatentFinal<FunctionDefinition> fdefn                = new LatentFinal<>();

   public AnonymousFunction() {
      this.methodIdx = supportMethodCounter.incrementAndGet();
   }

   public void add(String name, Type type) {
      args.add(new NameTypePair(name, type));
   }

   public void setReturnType(Type returnType) {
      this.returnType.set(returnType);
   }

   public void setBody(Block body) {
      this.body.set(body);
   }

   private void writeClosureContext(StringBuilder supportBody, CompileContext cc) {
      if (freeVariables.size() > 0) {
         supportBody.append("typedef struct GC_STRUCT_CLOSURE_CONTEXT_" + methodIdx + " {\n");
         for (NameTypePair ntp : freeVariables) {
            supportBody.append(Constants.TAB.get() + ntp.type.getCTypeString() + " " + ntp.name + ";\n");
         }
         supportBody.append("} GC_STRUCT_CLOSURE_CONTEXT_" + methodIdx + ";\n");
      }
   }

   private String writeContextBuilder(StringBuilder supportBody) {
      final String TAB = Constants.TAB.get();
      StringBuilder sb = new StringBuilder();
      StringBuilder method = new StringBuilder();
      method.append("build_new_closure_" + methodIdx + "(");
      boolean first = true;
      for (NameTypePair ntp : freeVariables) {
         if (!first) {
            sb.append(", ");
            method.append(", ");
         }
         first = false;
         sb.append(ntp.type.getCTypeString() + " " + ntp.name);
         method.append(ntp.name);
      }
      method.append(")");
      String args = sb.toString();
      supportBody.append(functionType.get().getCTypeString() + " build_new_closure_" + methodIdx + "(" + args + ") {\n");
      supportBody.append(TAB + functionType.get().getCTypeString() + " closure;\n");
      if (freeVariables.size() > 0) {
         supportBody.append(TAB + "GC_STRUCT_CLOSURE_CONTEXT_" + methodIdx + "* context=(GC_STRUCT_CLOSURE_CONTEXT_" + methodIdx + "*) malloc(sizeof(GC_STRUCT_CLOSURE_CONTEXT_" + methodIdx + "));\n");
         for (NameTypePair ntp : freeVariables) {
            supportBody.append(TAB + "context->" + ntp.name + "=" + ntp.name + ";\n");
         }
         supportBody.append(TAB + "closure.context=(void*)context;\n");
      } else {
         supportBody.append(TAB + "closure.context=0;\n");
      }
      supportBody.append(TAB + "closure.func=" + "support_method_" + methodIdx + ";\n");
      supportBody.append(TAB + "return closure;\n");
      supportBody.append("}\n");
      return method.toString();
   }

   public void compileC(StringBuilder sb, CompileContext cc) {
      cc.backupAndResetTab();
      StringBuilder supportBody = new StringBuilder();
      cc.restoreTab();
      // write out the closure context
      writeClosureContext(supportBody, cc);
      // write out the function
      fdefn.get().compileC(supportBody, cc);
      // write out the function to construct the closure
      String expr = writeContextBuilder(supportBody);
      cc.supportC(supportBody.toString());
      sb.append(expr);
   }

   public void compileSelf(StringBuilder sb, CompileContext cc) {
      throw new RuntimeException();
   }

   public Type getTypeHard(TypingEnvironment e) throws TypeException {
      FunctionalType ft = new FunctionalType(true);
      for (NameTypePair ntp : args)
         ft.add(ntp.type);
      ft.setReturnType(returnType.get());
      functionType.set(ft);
      FunctionDefinition fdef = new FunctionDefinition("support_method_" + methodIdx, returnType.get());
      fdef.add("__CTX", new PointerType(new CoreTypes.VoidType()));
      for (NameTypePair ntp : args) {
         fdef.add(ntp.name, ntp.type);
      }
      fdef.setBody(body.get());
      e.pushScope();
      e.watchForFreeVariables(freeVariables);
      fdef.getType(e);
      e.unwatch();
      e.popScope();
      fdef = new FunctionDefinition("support_method_" + methodIdx, returnType.get());
      fdef.add("__CTX", new PointerType(new CoreTypes.VoidType()));
      for (NameTypePair ntp : args) {
         fdef.add(ntp.name, ntp.type);
      }
      Block augmentedBody = new Block();
      Type t = new PointerType(new StructReferenceType("CLOSURE_CONTEXT_" + methodIdx));
      augmentedBody.add(new DeclareType(t, "__CTX_REAL").setValue(new CastAs(new Variable("__CTX"), t)));
      for (NameTypePair ntp : freeVariables) {
         augmentedBody.add(new DeclareType(ntp.type, ntp.name).setValue(new MemberReference(new Variable("__CTX_REAL"), ntp.name, MemberReference.HowReference.INDIRECT_VIA_POINTER)));
      }
      for (Statement stmt : body.get().statements) {
         augmentedBody.add(stmt);
      }
      fdef.setBody(augmentedBody);
      fdefn.set(fdef);
      return ft;
   }
}
