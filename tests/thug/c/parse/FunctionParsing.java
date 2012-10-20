package thug.c.parse;


import java.util.Map.Entry;

import junit.framework.Assert;

import org.junit.Test;
import thug.c.tree.FunctionDefinition;
import thug.c.types.CoreTypes.VoidType;

public class FunctionParsing extends BaseParsingTest {
	
	@Test
	public void testReturnType() {
		for(Entry<String, Class<?>> rt : TypeParsing.BASIC_TYPE_MAP.entrySet()) {
			FunctionDefinition fdef = (FunctionDefinition) this.parseGlobalScopeDefn(rt.getKey() + " foo() {}");
			Assert.assertEquals(rt.getValue(), fdef.returnType.getClass());
			Assert.assertEquals(0, fdef.args.size());
		}
	}
	
	@Test
	public void testArg1() {
		for(Entry<String, Class<?>> at : TypeParsing.BASIC_TYPE_MAP.entrySet()) {
			FunctionDefinition fdef = (FunctionDefinition) this.parseGlobalScopeDefn("void foo("+at.getKey()+" x) {}");
			Assert.assertEquals(VoidType.class, fdef.returnType.getClass());
			Assert.assertEquals(1, fdef.args.size());
			Assert.assertEquals("x", fdef.args.get(0).name);
			Assert.assertEquals(at.getValue(), fdef.args.get(0).type.getClass());
		}
	}
	
	
	@Test
	public void testArg2() {
		for(Entry<String, Class<?>> at1 : TypeParsing.BASIC_TYPE_MAP.entrySet()) {
			for(Entry<String, Class<?>> at2 : TypeParsing.BASIC_TYPE_MAP.entrySet()) {
				FunctionDefinition fdef = (FunctionDefinition) this.parseGlobalScopeDefn("void foo("+at1.getKey()+" x, "+at2.getKey()+" y) {}");
				Assert.assertEquals(VoidType.class, fdef.returnType.getClass());
				Assert.assertEquals(2, fdef.args.size());
				Assert.assertEquals("x", fdef.args.get(0).name);
				Assert.assertEquals("y", fdef.args.get(1).name);
				Assert.assertEquals(at1.getValue(), fdef.args.get(0).type.getClass());
				Assert.assertEquals(at2.getValue(), fdef.args.get(1).type.getClass());
			}
		}
	}
	
	
	@Test
	public void testArg3() {
		for(Entry<String, Class<?>> at1 : TypeParsing.BASIC_TYPE_MAP.entrySet()) {
			for(Entry<String, Class<?>> at2 : TypeParsing.BASIC_TYPE_MAP.entrySet()) {
				for(Entry<String, Class<?>> at3 : TypeParsing.BASIC_TYPE_MAP.entrySet()) {
					FunctionDefinition fdef = (FunctionDefinition) this.parseGlobalScopeDefn("void foo("+at1.getKey()+" x, "+at2.getKey()+" y, "+at3.getKey()+" z) {}");
					Assert.assertEquals(VoidType.class, fdef.returnType.getClass());
					Assert.assertEquals(3, fdef.args.size());
					Assert.assertEquals("x", fdef.args.get(0).name);
					Assert.assertEquals("y", fdef.args.get(1).name);
					Assert.assertEquals("z", fdef.args.get(2).name);
					Assert.assertEquals(at1.getValue(), fdef.args.get(0).type.getClass());
					Assert.assertEquals(at2.getValue(), fdef.args.get(1).type.getClass());
					Assert.assertEquals(at3.getValue(), fdef.args.get(2).type.getClass());
				}
			}
		}
	}
}
