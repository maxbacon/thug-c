grammar ThugC;

options {
  language=Java;
  output=AST;
  ASTLabelType = CommonTree;
}

@header {package thug.c.language;
import thug.c.core.*;
import thug.c.tree.*;
import thug.c.types.*;}
@lexer::header {package thug.c.language;}

typF returns [FunctionalType typ]
    : '#' '(' {$typ = new FunctionalType(true);}
       (
          t=typ {$typ.add($t.typ);}
          (',' t=typ {$typ.add($t.typ);})*
       )?
       ')' '->' tr=typ {$typ.setReturnType($tr.typ);}
       '#'
    ;
    
/*
  The Core Types
*/

typ0 returns [Type typ]
	: 'bool' {$typ = new CoreTypes.BooleanType();}
	| 'char' {$typ = new CoreTypes.Char8Type();}
	| 'short' {$typ = new CoreTypes.Short16Type();}
	| 'int' {$typ = new CoreTypes.Integer32Type();}
	| 'long' {$typ = new CoreTypes.Long64Type();}
	| 'char8' {$typ = new CoreTypes.Char8Type();}
	| 'short16' {$typ = new CoreTypes.Short16Type();}
	| 'int32' {$typ = new CoreTypes.Integer32Type();}
	| 'long64' {$typ = new CoreTypes.Long64Type();}
	| 'float' {$typ = new CoreTypes.Float32Type();}
	| 'double' {$typ = new CoreTypes.Double64Type();}
	| 'unsigned' ( 'char8' {$typ = new CoreTypes.UnsignedChar8Type();}
	             | 'short16' {$typ = new CoreTypes.UnsignedShort16Type();}
	             | 'int32' {$typ = new CoreTypes.UnsignedInteger32Type();}
	             | 'long64' {$typ = new CoreTypes.UnsignedLong64Type();}
	             | 'char' {$typ = new CoreTypes.UnsignedChar8Type();}
	             | 'short' {$typ = new CoreTypes.UnsignedShort16Type();}
	             | 'int' {$typ = new CoreTypes.UnsignedInteger32Type();}
	             | 'long' {$typ = new CoreTypes.UnsignedLong64Type();}
	             )
	| t=typF {$typ=$t.typ;}
	| (n=ID {$typ = new StructReferenceType($n.text);}
	   ('<'
	      gt=typ { ((StructReferenceType)$typ).addGenericTypeInstance($gt.typ); }
	   '>')?)
    | '[' gt=typ ']' {$typ=$gt.typ;}
    ;
    
typ1 returns [Type typ]
	:  'void' {$typ = new CoreTypes.VoidType();}
	   | a=typ0 {$typ=$a.typ;}
	;

typ returns [Type typ]
     	: t=typ1 { $typ=$t.typ; } 
     	  ('*' {$typ = new PointerType($typ);})*
     	;

structDef returns [StructDefinition sdef]
        : 'struct' n=ID {$sdef = new StructDefinition($n.text);}
           ('<'
              arg=ID {$sdef.addGenericUnknown($arg.text);}
           '>')?
           ('telescopes' ex=ID {$sdef.setParent($ex.text);})?
        '{'
            (t=typ lbl=ID  {$sdef.add($lbl.text, $t.typ);}
            ('=' e=expr {$sdef.addDefault($lbl.text,$e.expr);})?
            ';')*
          '}'
          ;

expr0 returns [Expression expr]
	:  id=ID  {$expr = new Variable($id.text);}

	| '('  e=expr ')' {$expr = new Parenthesis($e.expr);}
	;
	 
expr1 returns [Expression expr]
        : e=expr0 {$expr=$e.expr;}
        (
          '.' f=ID {$expr=new MemberReference($expr, $f.text, MemberReference.HowReference.DIRECT);}
        | '->' f=ID {$expr=new MemberReference($expr, $f.text, MemberReference.HowReference.INDIRECT_VIA_POINTER);}
        | 'as' '(' t=typ ')' {$expr=new CastAs($expr, $t.typ);}
        | '[' e2=expr ']' {$expr = new ArrayDereference($expr, $e2.expr); }
	| '(' {$expr = new CallFunction($expr);}
	      e2=expr {((CallFunction)$expr).add($e2.expr);}
	      (',' e2=expr {((CallFunction)$expr).add($e2.expr);})*
	   ')'
        )*
        ;

closur returns [AnonymousFunction af]
   :  'function' '(' {$af = new AnonymousFunction();}
      (
          t=typ n=ID {$af.add($n.text, $t.typ);}
         (',' t=typ n=ID {$af.add($n.text, $t.typ);})*
      )?
      ')' '->' rt=typ {$af.setReturnType($rt.typ);} 
      b=block {$af.setBody($b.b);}
   ;
expr2 returns [Expression expr]
 	: n=INT {
             if ($n.text.indexOf('L') > 0) { $expr = new Constant($n.text.replace("L", ""), Constant.ConstantType.Long);
 	     } else { $expr = new Constant($n.text, Constant.ConstantType.Integer); }
 	  }
	| f=FLOAT   {$expr = new Constant($f.text, Constant.ConstantType.FloatingPoint);}
	| c=CHAR   {$expr = new Constant($c.text, Constant.ConstantType.Character);}
	| 'true' {$expr = new Constant("1", Constant.ConstantType.Character); }
	| 'false' {$expr = new Constant("0", Constant.ConstantType.Character); }
	| 'new' at=typ0 { $expr=new Allocate($at.typ); }
	   ('[' e2=expr ']' { ((Allocate)$expr).setArity($e2.expr);  })?
	| cc=closur {$expr=$cc.af;}
	| '&' e=expr1 {$expr=new DereferencePointer($e.expr);}
	| '*' e=expr1 {$expr=new ReferencePointer($e.expr);}
        | e=expr1 {$expr=$e.expr;}
	;
	
expr3 returns [Expression expr]
	: e=expr2 {$expr=$e.expr;}
	;
	
expr4 returns [Expression expr]
	:	e1=expr3		{$expr=$e1.expr;}
		(o=operMultiplicative e2=expr3	{$expr=new BinaryOperation($o.op,$expr,$e2.expr);})*
	;

expr5 returns [Expression expr]
	:	
	 e1=expr4		{$expr=$e1.expr;}
        (o=operAdditive e2=expr4	{$expr=new BinaryOperation($o.op,$expr,$e2.expr);})*
	;

expr6 returns [Expression expr]
	:	e1=expr5		{$expr=$e1.expr;}
		(o=operRelat e2=expr5	{$expr=new BinaryOperation($o.op,$expr,$e2.expr);})?
	;
	
expr7 returns [Expression expr]
	:	e1=expr6		{$expr=$e1.expr;}
		(o=operLogicAnd e2=expr6	{$expr=new BinaryOperation($o.op,$expr,$e2.expr);})?
	;	

expr8 returns [Expression expr]
	:	e1=expr7		{$expr=$e1.expr;}
		(o=operLogicOr e2=expr7	{$expr=new BinaryOperation($o.op,$expr,$e2.expr);})?
	;	

operAdditive returns [BinaryOperation.Operation op]
	: '+' { $op = BinaryOperation.Operation.ADD; }
	| '-' { $op = BinaryOperation.Operation.SUB; }
	;

operMultiplicative returns [BinaryOperation.Operation op]
	: '*' { $op = BinaryOperation.Operation.MULT; }
	| '/' { $op = BinaryOperation.Operation.DIV; }
	| '%' { $op = BinaryOperation.Operation.MOD; }
	;

operRelat returns [BinaryOperation.Operation op]
	: '==' { $op = BinaryOperation.Operation.EQUALITY; }
	| '<' { $op = BinaryOperation.Operation.LESSTHAN; }
	| '<=' { $op = BinaryOperation.Operation.LESSTHANEQ; }
	| '>' { $op = BinaryOperation.Operation.GREATERTHAN; }
	| '>=' { $op = BinaryOperation.Operation.GREATERTHANEQ; }
	| '!=' { $op = BinaryOperation.Operation.NOTEQUAL; }	
	;

 operLogicAnd returns [BinaryOperation.Operation op]
	: '&&' { $op = BinaryOperation.Operation.AND; }
	;

 operLogicOr returns [BinaryOperation.Operation op]
	: '||' { $op = BinaryOperation.Operation.OR; }
	;
	
expr returns [Expression expr]
  : e=expr8 {$expr=$e.expr ;}
  ;

block returns [Block b]
  	: '{' {$b = new Block();}
  	  (s=stmt {$b.add($s.s);})*
  	 '}'
  	;

sComment returns [Statement cm]
    	:	c=COMMENT {$cm=new Comments.StatementComment($c.text);}
        ;
        
stmt returns [Statement s]
 	: s0=block {$s=$s0.b;}
 	| c=sComment {$s=$c.cm;}
 	| lhs=expr1 {$s = new Evaluate($lhs.expr); } ('=' rhs=expr { $s = new Assignment($lhs.expr, $rhs.expr);})? ';'
 	| ty=typ id=ID { $s = new DeclareType($ty.typ, $id.text); }
 	    ('=' v=expr {  ((DeclareType)$s).setValue($v.expr); })?
 	    ';'
 	| 'return' {$s=new Return();}
 	    (v=expr {((Return)$s).setReturnValue($v.expr);} )? ';'
 	| 'if' '(' e=expr ')' bT=block {$s = new IfThenElse($e.expr, $bT.b);}
 	  ('else' bF=block {((IfThenElse)$s).setFalseStatement($bF.b);})?
 	| 'while' '(' e=expr ')' bT=block {$s = new WhileLoop($e.expr, $bT.b);}
 	;
          
funcDef returns [FunctionDefinition fdef]
        : rt=typ n=ID '(' {$fdef = new FunctionDefinition($n.text,$rt.typ);}
            (t=typ lbl=ID {$fdef.add($lbl.text, $t.typ);}
            (',' t=typ lbl=ID {$fdef.add($lbl.text, $t.typ);})*
            )?
        ')' b=block {$fdef.setBody($b.b);}
        ;

importDef returns [Import imp]
  	: 'import' t=ID  {$imp = new Import($t.text);}
  	('.' t=ID {$imp = $imp.extend($t.text);})* 
  	';'
  	;

gComment returns [GlobalScopeDefn cm]
    	:	c=COMMENT {$cm=new Comments.GlobalComment($c.text);}
        ;

glb returns [GlobalScopeDefn g]
  	: g0=structDef {$g=$g0.sdef;}
  	| g1=importDef {$g=$g1.imp;}
  	| g2=funcDef {$g=$g2.fdef;}
  	| g3=gComment {$g=$g3.cm;}
  	;

fle returns [Script script]
 	:	{$script=new Script();}
 	    (g=glb {$script.add($g.g);})*
 	;

ID  :	('a'..'z'|'A'..'Z'|'_'|'$'|'@') ('a'..'z'|'A'..'Z'|'0'..'9'|'_'|'$'|'@')*
    ;

INT :	'0'..'9'+ 'L'?
    ;

FLOAT
    :	('0'..'9')+ '.' ('0'..'9')* EXPONENT?
    |   '.' ('0'..'9')+ EXPONENT?
    |   ('0'..'9')+ EXPONENT
    ;

COMMENT
    :   '//' ~('\n'|'\r')* '\r'? '\n'
    |   '/*' ( options {greedy=false;} : . )* '*/'
    ;

WS  :   ( ' '
        | '\t'
        | '\r'
        | '\n'
        ) {$channel=HIDDEN;}
    ;

STRING
    :  '"' ( ESC_SEQ | ~('\\'|'"') )* '"'
    ;

CHAR:  '\'' ( ESC_SEQ | ~('\''|'\\') ) '\''
    ;

fragment
EXPONENT : ('e'|'E') ('+'|'-')? ('0'..'9')+ ;

fragment
HEX_DIGIT : ('0'..'9'|'a'..'f'|'A'..'F') ;

fragment
ESC_SEQ
    :   '\\' ('b'|'t'|'n'|'f'|'r'|'\"'|'\''|'\\')
    |   UNICODE_ESC
    |   OCTAL_ESC
    ;

fragment
OCTAL_ESC
    :   '\\' ('0'..'3') ('0'..'7') ('0'..'7')
    |   '\\' ('0'..'7') ('0'..'7')
    |   '\\' ('0'..'7')
    ;

fragment
UNICODE_ESC
    :   '\\' 'u' HEX_DIGIT HEX_DIGIT HEX_DIGIT HEX_DIGIT
    ;

