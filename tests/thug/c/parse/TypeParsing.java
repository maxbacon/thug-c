package thug.c.parse;

import java.util.HashMap;

import org.junit.Test;
import thug.c.core.Type;
import thug.c.types.PointerType;
import thug.c.types.StructReferenceType;
import thug.c.types.CoreTypes.BooleanType;
import thug.c.types.CoreTypes.Char8Type;
import thug.c.types.CoreTypes.Double64Type;
import thug.c.types.CoreTypes.Float32Type;
import thug.c.types.CoreTypes.Integer32Type;
import thug.c.types.CoreTypes.Long64Type;
import thug.c.types.CoreTypes.Short16Type;
import thug.c.types.CoreTypes.UnsignedChar8Type;
import thug.c.types.CoreTypes.UnsignedInteger32Type;
import thug.c.types.CoreTypes.UnsignedLong64Type;
import thug.c.types.CoreTypes.UnsignedShort16Type;
import thug.c.types.CoreTypes.VoidType;

import junit.framework.Assert;

public class TypeParsing extends BaseParsingTest {

   public final static HashMap<String, Class<?>> BASIC_TYPE_MAP = generateBasicTypeMap();

   private static HashMap<String, Class<?>> generateBasicTypeMap() {
      HashMap<String, Class<?>> canon = new HashMap<>();
      canon.put("char8", Char8Type.class);
      canon.put("char", Char8Type.class);
      canon.put("bool", BooleanType.class);
      canon.put("unsigned char8", UnsignedChar8Type.class);
      canon.put("unsigned char", UnsignedChar8Type.class);
      canon.put("short16", Short16Type.class);
      canon.put("short", Short16Type.class);
      canon.put("unsigned short16", UnsignedShort16Type.class);
      canon.put("unsigned short", UnsignedShort16Type.class);
      canon.put("int32", Integer32Type.class);
      canon.put("int", Integer32Type.class);
      canon.put("unsigned int32", UnsignedInteger32Type.class);
      canon.put("unsigned int", UnsignedInteger32Type.class);
      canon.put("long64", Long64Type.class);
      canon.put("long", Long64Type.class);
      canon.put("unsigned long64", UnsignedLong64Type.class);
      canon.put("unsigned long", UnsignedLong64Type.class);
      canon.put("float", Float32Type.class);
      canon.put("double", Double64Type.class);
      canon.put("foo17", StructReferenceType.class);
      canon.put("Foo", StructReferenceType.class);
      canon.put("Foogly", StructReferenceType.class);
      return canon;
   }

   private void assertBasicType(String c, Class<?> cls) {
      Type parsedType = parseType(c);
      Assert.assertEquals(cls, parsedType.getClass());
      parsedType = parseType(c + "*");
      Assert.assertTrue(parsedType instanceof PointerType);
      PointerType pt = (PointerType) parsedType;
      Assert.assertEquals(cls, pt.baseType.getClass());
   }

   @Test
   public void testVoidType() {
      assertBasicType("void", VoidType.class);
   }

   @Test
   public void testBooleanType() {
      assertBasicType("bool", BooleanType.class);
   }

   @Test
   public void testChar8() {
      assertBasicType("char8", Char8Type.class);
      assertBasicType("char", Char8Type.class);
   }

   @Test
   public void testUnsignedChar8Type() {
      assertBasicType("unsigned char8", UnsignedChar8Type.class);
      assertBasicType("unsigned char", UnsignedChar8Type.class);
   }

   @Test
   public void testShort16Type() {
      assertBasicType("short16", Short16Type.class);
      assertBasicType("short", Short16Type.class);
   }

   @Test
   public void testUnsignedShort16Type() {
      assertBasicType("unsigned short16", UnsignedShort16Type.class);
      assertBasicType("unsigned short", UnsignedShort16Type.class);
   }

   @Test
   public void testInteger32Type() {
      assertBasicType("int32", Integer32Type.class);
      assertBasicType("int", Integer32Type.class);
   }

   @Test
   public void testUnsignedInteger32Type() {
      assertBasicType("unsigned int32", UnsignedInteger32Type.class);
      assertBasicType("unsigned int", UnsignedInteger32Type.class);
   }

   @Test
   public void testLong64Type() {
      assertBasicType("long64", Long64Type.class);
      assertBasicType("long", Long64Type.class);
   }

   @Test
   public void testUnsignedLong64Type() {
      assertBasicType("unsigned long64", UnsignedLong64Type.class);
      assertBasicType("unsigned long", UnsignedLong64Type.class);
   }

   @Test
   public void testFloat32Type() {
      assertBasicType("float", Float32Type.class);
   }

   @Test
   public void testDouble64Type() {
      assertBasicType("double", Double64Type.class);
   }

   @Test
   public void testStructureTest() {
      assertBasicType("foo17", StructReferenceType.class);
      assertBasicType("Foo", StructReferenceType.class);
      assertBasicType("Foogly", StructReferenceType.class);
   }
}
