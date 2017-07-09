grammar c;

options
{
   language = Java;
}

Ident
: ('a'..'z' | 'A'..'Z')('a'..'z' | 'A'..'Z' | '_' | '0'..'9')*
;


program
: topLevelDef* EOF #  tlSingle
;

topLevelDef
: funcDef     # tldefFunc
| define      # tldefDfn
| structDef   # tldefStct
;


COMMENT
: '/*' .*? '*/' -> skip
;

SCOMMENT
: '//' ~('\r' | '\n')* -> skip
;


INCLUDE
: '#include' .*? '\n' -> skip
;

define
: '#define' Ident Int # defineII
;

SHARPCOMMENT
: '#' ~('\r' | '\n')* -> skip
;

primaryExpr
: lval # elval
| Int # const
| '(' expr ')' # eparen
| Ident '(' (expr (',' expr)*)? ')' # efunc
;


postFixExpr
: primaryExpr # eprimary
| primaryExpr op=('--'|'++') # ePostFix
;

preFixExpr
: postFixExpr # ePrePost
| prefix=('-'|'++'|'--'|'!'|'&') postFixExpr # epre
;

multiplicativeExpr
: preFixExpr          # eMultiPre
| multiplicativeExpr op=('*' | '/' | '%') preFixExpr # eMultiBin
;

additiveExpr
: multiplicativeExpr # eAddMult
| additiveExpr op=('+' | '-') multiplicativeExpr # eAddBin
;

relationalExpr
: additiveExpr # eRelAdd
| relationalExpr op=('<' | '>' | '<=' | '>=' | '==' | '!=') additiveExpr # eRelBin
;

andExpr
: relationalExpr # eAndRel
| andExpr '&&' relationalExpr # eAndBin
;

orExpr
: andExpr # eAndOr
| orExpr '||' andExpr # eOrBin
;

expr
: orExpr      # eOrExpr
| orExpr '?' orExpr ':' orExpr # eSelect
;


lval
: Ident          # lident
| lval '->' Ident # larrow
| '(' lval ')' #    lparen
| lval '.' Ident #  ldot
| lval '[' expr ']' # lindex
| '*' lval  # lderef
;

nonPtrType
: 'int'         # intType
| 'void'            # voidType
| 'struct' Ident  # structType
;

type
/* : IntType         # intType
| Void            # voidType
| 'struct' Ident  # structType
*/
: nonPtrType      # nonPointerType
| nonPtrType '*'  # pointerType
;

inputList
: type Ident ',' inputList # inputListCons
| type Ident               # inputListSingleton
| /* empty */              # inputListEmpty
;

statements
: statement statements # stmtsCons
| /* empty */   # stmtsEmpty
;

decSingle
: Ident # decUSingle
| Ident '=' expr # decASingle
;

declist
: decSingle # declSingle
| decSingle ',' declist # declCons
;

simpleStatement
: /* blank */     # blankStmt
| lval '=' expr     # assnStmt
| lval op=('+=' | '*=' | '/=' | '%=' | '-=') expr  # arithAssnStmt
| expr            # exprStmt
;

lvals
: lval       # lvalHead
| lval lvals # lvalCons
;

statement
: simpleStatement ';' # simpleStmt
| '{' statements '}'   # groupStmt
|'if' '(' expr ')' '{' statements '}' # ifSingle
| 'if' '(' expr ')' '{' statements '}' 'else' '{' statements '}' # ifElse
| 'out {' statements '}' # sout
| 'outsource {' lvals '} {' lvals '} {' Ident Ident Ident '}' # sOutsource
| 'while' '(' expr ')' '{' statements '}'  # while
| 'for' '(' simpleStatement ';' expr ';' simpleStatement ')' '{' statements '}' # forLoop
| 'ofor' '(' Int ';' simpleStatement ';' expr ';' simpleStatement ')' '{' statements '}' # oforLoop
| type declist ';' # decStmt
| type Ident '[' Ident ']' ';' # decArrayIdent
| type Ident '[' Int ']' ';' # decArrayInt
| singleDef ';' # decSingleDef
| 'return' expr ';'# retStmt
| 'return' ';'    # retStmtNone
| 'break'  ';'# breakStmt
| 'continue' ';'# continueStmt
| '_begin' ';'# beginStmt
| '_end' ';'# endStmt
;

funcDef
: type Ident '(' inputList ')' '{' statements '}' # funcdefSimple
;

arrayLength
: Int
| Ident
;

singleDef
: type Ident # singleDefNormal
//| type Ident '[' Ident ']' # singleDefArray
| type Ident ('['arrayLength']')+ # singleDefArrayConst
;

structDef
: 'struct' Ident '{' (singleDef ';')+ '}' ';'
;


IntType
: 'int'
| 'uint32_t'
| 'uint8_t'
| 'int8_t'
| 'int32_t'
| 'uint16_t'
| 'int16_t'
| 'int64_t'
| 'uint64_t'
| 'char'
| 'bool'
| 'int_64t'
| 'num_t'
;



Int
: ('0'..'9')+
;


WS: [ \n\t\r]+ -> skip;


//onePws
//: Ident Ident '=' Ident  # pwsEmpty
//// : 'P' Ident '=' 'E' # pwsEmpty
//| Ident Ident '=' additiveExpr Ident  # pwsNonEmpty
//// 'P' Ident '=' additiveExpr 'E' # pwsNonEmpty
//| '!=' Ident Ident Ident Ident Ident Ident Ident Ident  # pwsNEQ
////  '!=' 'M' Ident 'X1' Ident 'X2' Ident 'Y' Ident # pwsNEQ
//| '<I' Ident Ident Ident Int Ident Ident Ident Ident Ident Ident Ident Ident Ident Ident Ident Ident # pwsLT
//// '<I' 'N_0' Ident 'N' Int 'Mlt' Ident 'Meq' Ident 'Mgt' Ident 'X1' Ident 'X2' Ident 'Y' Ident # pwsLT
//;
//
//pws
//: onePws* EOF # pwsOneOrMore
//;
