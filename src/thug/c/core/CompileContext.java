package thug.c.core;

import java.util.HashMap;
import java.util.Stack;
import thug.c.tree.FunctionDefinition;
import thug.c.tree.StructDefinition;

public class CompileContext {
	private final String _TAB;
	private String tab = "";
	private boolean _neuterNextTab = false;
	public final TypingEnvironment typing = new TypingEnvironment();
	public final String packageName;

	public CompileContext(String packageName) {
		this.packageName = packageName;
		_TAB = Constants.TAB.get();
	}

	public void neuterNextTab() {
		_neuterNextTab = true;
	}

	public void pushTab() {
		tab += _TAB;
	}

	public void popTab() {
		tab = tab.substring(0, tab.length() - _TAB.length());
	}

	public String tabbify(String x) {
		if (_neuterNextTab) {
			_neuterNextTab = false;
			return x;
		}
		return tab + x;
	}

	private final Stack<String> tabHistory = new Stack<>();

	public void backupAndResetTab() {
		tabHistory.push(tab);
		tab = "";
	}

	public void restoreTab() {
		tab = tabHistory.pop();
	}

	private final Stack<String> supportingC = new Stack<>();

	public void supportC(String CCode) {
		supportingC.push(CCode);
	}

	public void applySupport(StringBuilder sb) {
		while (supportingC.size() > 0) {
			sb.append(supportingC.pop());
		}
	}

	private final HashMap<String, StructDefinition> structs = new HashMap<>();
	private final HashMap<String, FunctionDefinition> functions = new HashMap<>();

	public StructDefinition getStruct(String name) {
		return structs.get(name);
	}

	public void register(StructDefinition struct) {
		if (functions.containsKey(struct.name)) {
			throw new IllegalStateException("Already defined:" + struct.name);
		}
		structs.put(struct.name, struct);
	}

	public void register(FunctionDefinition func) {
		if (functions.containsKey(func.name)) {
			throw new IllegalStateException("Already defined:" + func.name);
		}
		functions.put(func.name, func);
		System.out.println("DEFN:" + func.name);
		typing.add(func);
	}
}
