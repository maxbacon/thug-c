package thug.c.parse;

import java.util.Map.Entry;

import junit.framework.Assert;

import org.junit.Test;
import thug.c.tree.StructDefinition;
import thug.c.types.PointerType;

public class StructureParsing extends BaseParsingTest{

	@Test
	public void testEmptyStructure() {
		StructDefinition sdef = (StructDefinition) parseGlobalScopeDefn("struct foo {}");
		Assert.assertEquals(0, sdef.members.size());
		Assert.assertEquals(0, sdef.membersAsSpecified.size());
	}
	
	@Test
	public void testAllBaseSingletons() {
		for(Entry<String, Class<?>> entry : TypeParsing.BASIC_TYPE_MAP.entrySet()) {
			StructDefinition sdef = (StructDefinition) parseGlobalScopeDefn("struct foo { "+entry.getKey()+" x; }");
			Assert.assertEquals(1, sdef.members.size());
			Assert.assertEquals(1, sdef.membersAsSpecified.size());
			Assert.assertEquals(entry.getValue(), sdef.members.get("x").getClass());
			Assert.assertEquals(entry.getValue(), sdef.membersAsSpecified.get(0).type.getClass());
			Assert.assertEquals("x", sdef.membersAsSpecified.get(0).name);
		}
	}

	@Test
	public void testAllBasePointerSingletons() {
		for(Entry<String, Class<?>> entry : TypeParsing.BASIC_TYPE_MAP.entrySet()) {
			StructDefinition sdef = (StructDefinition) parseGlobalScopeDefn("struct foo { "+entry.getKey()+"* x; }");
			Assert.assertEquals(1, sdef.members.size());
			Assert.assertEquals(1, sdef.membersAsSpecified.size());
			Assert.assertEquals(PointerType.class, sdef.members.get("x").getClass());
			Assert.assertEquals(PointerType.class, sdef.membersAsSpecified.get(0).type.getClass());
			Assert.assertEquals(entry.getValue(), ((PointerType)sdef.members.get("x")).baseType.getClass());
			Assert.assertEquals(entry.getValue(), ((PointerType)sdef.membersAsSpecified.get(0).type).baseType.getClass());
			Assert.assertEquals("x", sdef.membersAsSpecified.get(0).name);
		}
	}
	
	
	@Test
	public void testAllPairs() {
		for(Entry<String, Class<?>> entry1 : TypeParsing.BASIC_TYPE_MAP.entrySet()) {
			for(Entry<String, Class<?>> entry2 : TypeParsing.BASIC_TYPE_MAP.entrySet()) {
				StructDefinition sdef = (StructDefinition) parseGlobalScopeDefn("struct foo { "+entry1.getKey()+" x; "+entry2.getKey()+" y; }");
				Assert.assertEquals(2, sdef.members.size());
				Assert.assertEquals(2, sdef.membersAsSpecified.size());
				Assert.assertEquals(entry1.getValue(), sdef.members.get("x").getClass());
				Assert.assertEquals(entry1.getValue(), sdef.membersAsSpecified.get(0).type.getClass());
				Assert.assertEquals(entry2.getValue(), sdef.members.get("y").getClass());
				Assert.assertEquals(entry2.getValue(), sdef.membersAsSpecified.get(1).type.getClass());
				Assert.assertEquals("x", sdef.membersAsSpecified.get(0).name);
				Assert.assertEquals("y", sdef.membersAsSpecified.get(1).name);
			}
		}
	}	
}
