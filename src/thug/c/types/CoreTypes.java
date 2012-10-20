package thug.c.types;

import thug.c.core.Type;
import thug.c.core.TypingEnvironment;

public class CoreTypes {
   public static class CoreNativeType implements Type {
      public final String cTypeString;
      public final String selfTypeString;

      public CoreNativeType(String cTypeString, String selfTypeString) {
         this.cTypeString = cTypeString;
         this.selfTypeString = selfTypeString;
      }

      public String getCTypeString() {
         return cTypeString;
      }

      public String getSelfTypeString() {
         return selfTypeString;
      }

      public void register(TypingEnvironment env) {
      }
   }

   public static class VoidType extends CoreNativeType {
      public VoidType() {
         super("void", "void");
      }
   }

   public static class BooleanType extends CoreNativeType {
      public BooleanType() {
         super("char", "bool");
      }
   }

   public static class Char8Type extends CoreNativeType {
      public Char8Type() {
         super("char", "char8");
      }
   }

   public static class UnsignedChar8Type extends CoreNativeType {
      public UnsignedChar8Type() {
         super("unsigned char", "unsigned char8");
      }
   }

   public static class Short16Type extends CoreNativeType {
      public Short16Type() {
         super("short", "short16");
      }
   }

   public static class UnsignedShort16Type extends CoreNativeType {
      public UnsignedShort16Type() {
         super("unsigned short", "unsigned short16");
      }
   }

   public static class Integer32Type extends CoreNativeType {
      public Integer32Type() {
         super("int", "int32");
      }
   }

   public static class UnsignedInteger32Type extends CoreNativeType {
      public UnsignedInteger32Type() {
         super("unsigned int", "unsigned int32");
      }
   }

   public static class Long64Type extends CoreNativeType {
      public Long64Type() {
         super("long", "long64");
      }
   }

   public static class UnsignedLong64Type extends CoreNativeType {
      public UnsignedLong64Type() {
         super("unsigned long", "unsigned long64");
      }
   }

   public static class Float32Type extends CoreNativeType {
      public Float32Type() {
         super("float", "float");
      }
   }

   public static class Double64Type extends CoreNativeType {
      public Double64Type() {
         super("double", "double");
      }
   }
}
