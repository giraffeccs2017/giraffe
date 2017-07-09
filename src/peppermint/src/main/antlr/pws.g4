grammar pws;

options
 {
 language = Java;
 }



Ident
: ('a'..'z' | 'A'..'Z')('a'..'z' | 'A'..'Z' | '_' | '0'..'9')*
;

Int
: ('0'..'9')+
;



lval
: Ident          # lident
;

primaryExpr
: lval # elval
| Int # const
| '(' additiveExpr ')' # eparen
;

preFixExpr
: primaryExpr # ePrePrim
| prefix='-' primaryExpr # ePrePre
;


multiplicativeExpr
: preFixExpr          # eMultiPre
| multiplicativeExpr op=('*' | '/' | '%') preFixExpr # eMultiBin
;

additiveExpr
: multiplicativeExpr # eAddMult
| additiveExpr op=('+' | '-') multiplicativeExpr # eAddBin
;

onePws
: 'P' Ident '=' 'E' # pwsEmpty
| 'P' Ident '=' additiveExpr 'E' # pwsNonEmpty
// '!=' Ident Ident Ident Ident Ident Ident Ident Ident  # pwsNEQ
|  '!=' 'M' Ident 'X1' additiveExpr 'X2' additiveExpr 'Y' Ident # pwsNEQ
// '<I' Ident Ident Ident Int Ident Ident Ident Ident Ident Ident Ident Ident Ident Ident Ident Ident # pwsLT
| '<I' 'N_0' Ident 'N' Int 'Mlt' Ident 'Meq' Ident 'Mgt' Ident 'X1' additiveExpr 'X2' additiveExpr 'Y' Ident # pwsLT
;

pws
: onePws* EOF # pwsOneOrMore
;

WS: [ \n\t\r]+ -> skip;
