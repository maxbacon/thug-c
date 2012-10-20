package thug.c.parse;

import org.antlr.runtime.ANTLRStringStream;
import org.antlr.runtime.CommonTokenStream;
import org.antlr.runtime.RecognitionException;
import thug.c.core.GlobalScopeDefn;
import thug.c.core.Type;
import thug.c.language.ThugCLexer;
import thug.c.language.ThugCParser;

public class BaseParsingTest {
	protected Type parseType(String s) {
		ANTLRStringStream input = new ANTLRStringStream(s);
		CommonTokenStream tokens = new CommonTokenStream(new ThugCLexer(input));
		ThugCParser parser = new ThugCParser(tokens);
		try {
			return parser.typ().typ;
		} catch (RecognitionException e) {
			return null;
		}
	}

	protected GlobalScopeDefn parseGlobalScopeDefn(String s) {
		ANTLRStringStream input = new ANTLRStringStream(s);
		CommonTokenStream tokens = new CommonTokenStream(new ThugCLexer(input));
		ThugCParser parser = new ThugCParser(tokens);
		try {
			return parser.glb().g;
		} catch (RecognitionException e) {
			return null;
		}
	}
}
