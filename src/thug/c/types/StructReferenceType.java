package thug.c.types;

import java.util.ArrayList;

import thug.c.core.Type;
import thug.c.core.TypingEnvironment;

public class StructReferenceType implements Type {
	public final String name;

	public final ArrayList<Type> genericTypeParameters = new ArrayList<>();

	public StructReferenceType(String name) {
		this.name = name;
	}

	public void addGenericTypeInstance(Type t) {
		throw new IllegalStateException("Not supported... yet");
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
