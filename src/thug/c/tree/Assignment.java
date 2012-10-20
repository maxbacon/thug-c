package thug.c.tree;

import thug.c.core.CompileContext;
import thug.c.core.Expression;
import thug.c.core.Statement;
import thug.c.core.Type;
import thug.c.core.TypingEnvironment;
import thug.c.core.TypingEnvironment.TypeException;
import thug.c.types.CoreTypes;

public class Assignment extends Statement {
   public final Expression leftHand;
   public final Expression rightHand;

   public Assignment(Expression leftHand, Expression rightHand) {
      this.leftHand = leftHand;
      this.rightHand = rightHand;
   }

   public void compileC(StringBuilder sb, CompileContext cc) {
      sb.append(cc.tabbify(""));
      leftHand.compileC(sb, cc);
      sb.append("=");
      rightHand.compileC(sb, cc);
      sb.append(";\n");
   }

   public void compileSelf(StringBuilder sb, CompileContext cc) {
      sb.append(cc.tabbify(""));
      leftHand.compileSelf(sb, cc);
      sb.append("=");
      rightHand.compileSelf(sb, cc);
      sb.append(";\n");
   }

   public Type getTypeHard(TypingEnvironment e) throws TypeException {
      Type leftHandType = leftHand.getType(e);
      Type rightHandType = rightHand.getType(e);
      e.validateAssignment(leftHandType, rightHandType);
      return new CoreTypes.VoidType();
   }
}
