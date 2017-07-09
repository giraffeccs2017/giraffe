package gen;
import org.antlr.v4.runtime.tree.ParseTreeListener;

/**
 * This interface defines a complete listener for a parse tree produced by
 * {@link cParser}.
 */
public interface cListener extends ParseTreeListener {
	/**
	 * Enter a parse tree produced by the {@code tlSingle}
	 * labeled alternative in {@link cParser#program}.
	 * @param ctx the parse tree
	 */
	void enterTlSingle(cParser.TlSingleContext ctx);
	/**
	 * Exit a parse tree produced by the {@code tlSingle}
	 * labeled alternative in {@link cParser#program}.
	 * @param ctx the parse tree
	 */
	void exitTlSingle(cParser.TlSingleContext ctx);
	/**
	 * Enter a parse tree produced by the {@code tldefFunc}
	 * labeled alternative in {@link cParser#topLevelDef}.
	 * @param ctx the parse tree
	 */
	void enterTldefFunc(cParser.TldefFuncContext ctx);
	/**
	 * Exit a parse tree produced by the {@code tldefFunc}
	 * labeled alternative in {@link cParser#topLevelDef}.
	 * @param ctx the parse tree
	 */
	void exitTldefFunc(cParser.TldefFuncContext ctx);
	/**
	 * Enter a parse tree produced by the {@code tldefDfn}
	 * labeled alternative in {@link cParser#topLevelDef}.
	 * @param ctx the parse tree
	 */
	void enterTldefDfn(cParser.TldefDfnContext ctx);
	/**
	 * Exit a parse tree produced by the {@code tldefDfn}
	 * labeled alternative in {@link cParser#topLevelDef}.
	 * @param ctx the parse tree
	 */
	void exitTldefDfn(cParser.TldefDfnContext ctx);
	/**
	 * Enter a parse tree produced by the {@code tldefStct}
	 * labeled alternative in {@link cParser#topLevelDef}.
	 * @param ctx the parse tree
	 */
	void enterTldefStct(cParser.TldefStctContext ctx);
	/**
	 * Exit a parse tree produced by the {@code tldefStct}
	 * labeled alternative in {@link cParser#topLevelDef}.
	 * @param ctx the parse tree
	 */
	void exitTldefStct(cParser.TldefStctContext ctx);
	/**
	 * Enter a parse tree produced by the {@code defineII}
	 * labeled alternative in {@link cParser#define}.
	 * @param ctx the parse tree
	 */
	void enterDefineII(cParser.DefineIIContext ctx);
	/**
	 * Exit a parse tree produced by the {@code defineII}
	 * labeled alternative in {@link cParser#define}.
	 * @param ctx the parse tree
	 */
	void exitDefineII(cParser.DefineIIContext ctx);
	/**
	 * Enter a parse tree produced by the {@code elval}
	 * labeled alternative in {@link cParser#primaryExpr}.
	 * @param ctx the parse tree
	 */
	void enterElval(cParser.ElvalContext ctx);
	/**
	 * Exit a parse tree produced by the {@code elval}
	 * labeled alternative in {@link cParser#primaryExpr}.
	 * @param ctx the parse tree
	 */
	void exitElval(cParser.ElvalContext ctx);
	/**
	 * Enter a parse tree produced by the {@code const}
	 * labeled alternative in {@link cParser#primaryExpr}.
	 * @param ctx the parse tree
	 */
	void enterConst(cParser.ConstContext ctx);
	/**
	 * Exit a parse tree produced by the {@code const}
	 * labeled alternative in {@link cParser#primaryExpr}.
	 * @param ctx the parse tree
	 */
	void exitConst(cParser.ConstContext ctx);
	/**
	 * Enter a parse tree produced by the {@code eparen}
	 * labeled alternative in {@link cParser#primaryExpr}.
	 * @param ctx the parse tree
	 */
	void enterEparen(cParser.EparenContext ctx);
	/**
	 * Exit a parse tree produced by the {@code eparen}
	 * labeled alternative in {@link cParser#primaryExpr}.
	 * @param ctx the parse tree
	 */
	void exitEparen(cParser.EparenContext ctx);
	/**
	 * Enter a parse tree produced by the {@code efunc}
	 * labeled alternative in {@link cParser#primaryExpr}.
	 * @param ctx the parse tree
	 */
	void enterEfunc(cParser.EfuncContext ctx);
	/**
	 * Exit a parse tree produced by the {@code efunc}
	 * labeled alternative in {@link cParser#primaryExpr}.
	 * @param ctx the parse tree
	 */
	void exitEfunc(cParser.EfuncContext ctx);
	/**
	 * Enter a parse tree produced by the {@code eprimary}
	 * labeled alternative in {@link cParser#postFixExpr}.
	 * @param ctx the parse tree
	 */
	void enterEprimary(cParser.EprimaryContext ctx);
	/**
	 * Exit a parse tree produced by the {@code eprimary}
	 * labeled alternative in {@link cParser#postFixExpr}.
	 * @param ctx the parse tree
	 */
	void exitEprimary(cParser.EprimaryContext ctx);
	/**
	 * Enter a parse tree produced by the {@code ePostFix}
	 * labeled alternative in {@link cParser#postFixExpr}.
	 * @param ctx the parse tree
	 */
	void enterEPostFix(cParser.EPostFixContext ctx);
	/**
	 * Exit a parse tree produced by the {@code ePostFix}
	 * labeled alternative in {@link cParser#postFixExpr}.
	 * @param ctx the parse tree
	 */
	void exitEPostFix(cParser.EPostFixContext ctx);
	/**
	 * Enter a parse tree produced by the {@code ePrePost}
	 * labeled alternative in {@link cParser#preFixExpr}.
	 * @param ctx the parse tree
	 */
	void enterEPrePost(cParser.EPrePostContext ctx);
	/**
	 * Exit a parse tree produced by the {@code ePrePost}
	 * labeled alternative in {@link cParser#preFixExpr}.
	 * @param ctx the parse tree
	 */
	void exitEPrePost(cParser.EPrePostContext ctx);
	/**
	 * Enter a parse tree produced by the {@code epre}
	 * labeled alternative in {@link cParser#preFixExpr}.
	 * @param ctx the parse tree
	 */
	void enterEpre(cParser.EpreContext ctx);
	/**
	 * Exit a parse tree produced by the {@code epre}
	 * labeled alternative in {@link cParser#preFixExpr}.
	 * @param ctx the parse tree
	 */
	void exitEpre(cParser.EpreContext ctx);
	/**
	 * Enter a parse tree produced by the {@code eMultiBin}
	 * labeled alternative in {@link cParser#multiplicativeExpr}.
	 * @param ctx the parse tree
	 */
	void enterEMultiBin(cParser.EMultiBinContext ctx);
	/**
	 * Exit a parse tree produced by the {@code eMultiBin}
	 * labeled alternative in {@link cParser#multiplicativeExpr}.
	 * @param ctx the parse tree
	 */
	void exitEMultiBin(cParser.EMultiBinContext ctx);
	/**
	 * Enter a parse tree produced by the {@code eMultiPre}
	 * labeled alternative in {@link cParser#multiplicativeExpr}.
	 * @param ctx the parse tree
	 */
	void enterEMultiPre(cParser.EMultiPreContext ctx);
	/**
	 * Exit a parse tree produced by the {@code eMultiPre}
	 * labeled alternative in {@link cParser#multiplicativeExpr}.
	 * @param ctx the parse tree
	 */
	void exitEMultiPre(cParser.EMultiPreContext ctx);
	/**
	 * Enter a parse tree produced by the {@code eAddMult}
	 * labeled alternative in {@link cParser#additiveExpr}.
	 * @param ctx the parse tree
	 */
	void enterEAddMult(cParser.EAddMultContext ctx);
	/**
	 * Exit a parse tree produced by the {@code eAddMult}
	 * labeled alternative in {@link cParser#additiveExpr}.
	 * @param ctx the parse tree
	 */
	void exitEAddMult(cParser.EAddMultContext ctx);
	/**
	 * Enter a parse tree produced by the {@code eAddBin}
	 * labeled alternative in {@link cParser#additiveExpr}.
	 * @param ctx the parse tree
	 */
	void enterEAddBin(cParser.EAddBinContext ctx);
	/**
	 * Exit a parse tree produced by the {@code eAddBin}
	 * labeled alternative in {@link cParser#additiveExpr}.
	 * @param ctx the parse tree
	 */
	void exitEAddBin(cParser.EAddBinContext ctx);
	/**
	 * Enter a parse tree produced by the {@code eRelBin}
	 * labeled alternative in {@link cParser#relationalExpr}.
	 * @param ctx the parse tree
	 */
	void enterERelBin(cParser.ERelBinContext ctx);
	/**
	 * Exit a parse tree produced by the {@code eRelBin}
	 * labeled alternative in {@link cParser#relationalExpr}.
	 * @param ctx the parse tree
	 */
	void exitERelBin(cParser.ERelBinContext ctx);
	/**
	 * Enter a parse tree produced by the {@code eRelAdd}
	 * labeled alternative in {@link cParser#relationalExpr}.
	 * @param ctx the parse tree
	 */
	void enterERelAdd(cParser.ERelAddContext ctx);
	/**
	 * Exit a parse tree produced by the {@code eRelAdd}
	 * labeled alternative in {@link cParser#relationalExpr}.
	 * @param ctx the parse tree
	 */
	void exitERelAdd(cParser.ERelAddContext ctx);
	/**
	 * Enter a parse tree produced by the {@code eAndBin}
	 * labeled alternative in {@link cParser#andExpr}.
	 * @param ctx the parse tree
	 */
	void enterEAndBin(cParser.EAndBinContext ctx);
	/**
	 * Exit a parse tree produced by the {@code eAndBin}
	 * labeled alternative in {@link cParser#andExpr}.
	 * @param ctx the parse tree
	 */
	void exitEAndBin(cParser.EAndBinContext ctx);
	/**
	 * Enter a parse tree produced by the {@code eAndRel}
	 * labeled alternative in {@link cParser#andExpr}.
	 * @param ctx the parse tree
	 */
	void enterEAndRel(cParser.EAndRelContext ctx);
	/**
	 * Exit a parse tree produced by the {@code eAndRel}
	 * labeled alternative in {@link cParser#andExpr}.
	 * @param ctx the parse tree
	 */
	void exitEAndRel(cParser.EAndRelContext ctx);
	/**
	 * Enter a parse tree produced by the {@code eAndOr}
	 * labeled alternative in {@link cParser#orExpr}.
	 * @param ctx the parse tree
	 */
	void enterEAndOr(cParser.EAndOrContext ctx);
	/**
	 * Exit a parse tree produced by the {@code eAndOr}
	 * labeled alternative in {@link cParser#orExpr}.
	 * @param ctx the parse tree
	 */
	void exitEAndOr(cParser.EAndOrContext ctx);
	/**
	 * Enter a parse tree produced by the {@code eOrBin}
	 * labeled alternative in {@link cParser#orExpr}.
	 * @param ctx the parse tree
	 */
	void enterEOrBin(cParser.EOrBinContext ctx);
	/**
	 * Exit a parse tree produced by the {@code eOrBin}
	 * labeled alternative in {@link cParser#orExpr}.
	 * @param ctx the parse tree
	 */
	void exitEOrBin(cParser.EOrBinContext ctx);
	/**
	 * Enter a parse tree produced by the {@code eOrExpr}
	 * labeled alternative in {@link cParser#expr}.
	 * @param ctx the parse tree
	 */
	void enterEOrExpr(cParser.EOrExprContext ctx);
	/**
	 * Exit a parse tree produced by the {@code eOrExpr}
	 * labeled alternative in {@link cParser#expr}.
	 * @param ctx the parse tree
	 */
	void exitEOrExpr(cParser.EOrExprContext ctx);
	/**
	 * Enter a parse tree produced by the {@code eSelect}
	 * labeled alternative in {@link cParser#expr}.
	 * @param ctx the parse tree
	 */
	void enterESelect(cParser.ESelectContext ctx);
	/**
	 * Exit a parse tree produced by the {@code eSelect}
	 * labeled alternative in {@link cParser#expr}.
	 * @param ctx the parse tree
	 */
	void exitESelect(cParser.ESelectContext ctx);
	/**
	 * Enter a parse tree produced by the {@code lindex}
	 * labeled alternative in {@link cParser#lval}.
	 * @param ctx the parse tree
	 */
	void enterLindex(cParser.LindexContext ctx);
	/**
	 * Exit a parse tree produced by the {@code lindex}
	 * labeled alternative in {@link cParser#lval}.
	 * @param ctx the parse tree
	 */
	void exitLindex(cParser.LindexContext ctx);
	/**
	 * Enter a parse tree produced by the {@code lident}
	 * labeled alternative in {@link cParser#lval}.
	 * @param ctx the parse tree
	 */
	void enterLident(cParser.LidentContext ctx);
	/**
	 * Exit a parse tree produced by the {@code lident}
	 * labeled alternative in {@link cParser#lval}.
	 * @param ctx the parse tree
	 */
	void exitLident(cParser.LidentContext ctx);
	/**
	 * Enter a parse tree produced by the {@code lderef}
	 * labeled alternative in {@link cParser#lval}.
	 * @param ctx the parse tree
	 */
	void enterLderef(cParser.LderefContext ctx);
	/**
	 * Exit a parse tree produced by the {@code lderef}
	 * labeled alternative in {@link cParser#lval}.
	 * @param ctx the parse tree
	 */
	void exitLderef(cParser.LderefContext ctx);
	/**
	 * Enter a parse tree produced by the {@code larrow}
	 * labeled alternative in {@link cParser#lval}.
	 * @param ctx the parse tree
	 */
	void enterLarrow(cParser.LarrowContext ctx);
	/**
	 * Exit a parse tree produced by the {@code larrow}
	 * labeled alternative in {@link cParser#lval}.
	 * @param ctx the parse tree
	 */
	void exitLarrow(cParser.LarrowContext ctx);
	/**
	 * Enter a parse tree produced by the {@code lparen}
	 * labeled alternative in {@link cParser#lval}.
	 * @param ctx the parse tree
	 */
	void enterLparen(cParser.LparenContext ctx);
	/**
	 * Exit a parse tree produced by the {@code lparen}
	 * labeled alternative in {@link cParser#lval}.
	 * @param ctx the parse tree
	 */
	void exitLparen(cParser.LparenContext ctx);
	/**
	 * Enter a parse tree produced by the {@code ldot}
	 * labeled alternative in {@link cParser#lval}.
	 * @param ctx the parse tree
	 */
	void enterLdot(cParser.LdotContext ctx);
	/**
	 * Exit a parse tree produced by the {@code ldot}
	 * labeled alternative in {@link cParser#lval}.
	 * @param ctx the parse tree
	 */
	void exitLdot(cParser.LdotContext ctx);
	/**
	 * Enter a parse tree produced by the {@code intType}
	 * labeled alternative in {@link cParser#nonPtrType}.
	 * @param ctx the parse tree
	 */
	void enterIntType(cParser.IntTypeContext ctx);
	/**
	 * Exit a parse tree produced by the {@code intType}
	 * labeled alternative in {@link cParser#nonPtrType}.
	 * @param ctx the parse tree
	 */
	void exitIntType(cParser.IntTypeContext ctx);
	/**
	 * Enter a parse tree produced by the {@code voidType}
	 * labeled alternative in {@link cParser#nonPtrType}.
	 * @param ctx the parse tree
	 */
	void enterVoidType(cParser.VoidTypeContext ctx);
	/**
	 * Exit a parse tree produced by the {@code voidType}
	 * labeled alternative in {@link cParser#nonPtrType}.
	 * @param ctx the parse tree
	 */
	void exitVoidType(cParser.VoidTypeContext ctx);
	/**
	 * Enter a parse tree produced by the {@code structType}
	 * labeled alternative in {@link cParser#nonPtrType}.
	 * @param ctx the parse tree
	 */
	void enterStructType(cParser.StructTypeContext ctx);
	/**
	 * Exit a parse tree produced by the {@code structType}
	 * labeled alternative in {@link cParser#nonPtrType}.
	 * @param ctx the parse tree
	 */
	void exitStructType(cParser.StructTypeContext ctx);
	/**
	 * Enter a parse tree produced by the {@code nonPointerType}
	 * labeled alternative in {@link cParser#type}.
	 * @param ctx the parse tree
	 */
	void enterNonPointerType(cParser.NonPointerTypeContext ctx);
	/**
	 * Exit a parse tree produced by the {@code nonPointerType}
	 * labeled alternative in {@link cParser#type}.
	 * @param ctx the parse tree
	 */
	void exitNonPointerType(cParser.NonPointerTypeContext ctx);
	/**
	 * Enter a parse tree produced by the {@code pointerType}
	 * labeled alternative in {@link cParser#type}.
	 * @param ctx the parse tree
	 */
	void enterPointerType(cParser.PointerTypeContext ctx);
	/**
	 * Exit a parse tree produced by the {@code pointerType}
	 * labeled alternative in {@link cParser#type}.
	 * @param ctx the parse tree
	 */
	void exitPointerType(cParser.PointerTypeContext ctx);
	/**
	 * Enter a parse tree produced by the {@code inputListCons}
	 * labeled alternative in {@link cParser#inputList}.
	 * @param ctx the parse tree
	 */
	void enterInputListCons(cParser.InputListConsContext ctx);
	/**
	 * Exit a parse tree produced by the {@code inputListCons}
	 * labeled alternative in {@link cParser#inputList}.
	 * @param ctx the parse tree
	 */
	void exitInputListCons(cParser.InputListConsContext ctx);
	/**
	 * Enter a parse tree produced by the {@code inputListSingleton}
	 * labeled alternative in {@link cParser#inputList}.
	 * @param ctx the parse tree
	 */
	void enterInputListSingleton(cParser.InputListSingletonContext ctx);
	/**
	 * Exit a parse tree produced by the {@code inputListSingleton}
	 * labeled alternative in {@link cParser#inputList}.
	 * @param ctx the parse tree
	 */
	void exitInputListSingleton(cParser.InputListSingletonContext ctx);
	/**
	 * Enter a parse tree produced by the {@code inputListEmpty}
	 * labeled alternative in {@link cParser#inputList}.
	 * @param ctx the parse tree
	 */
	void enterInputListEmpty(cParser.InputListEmptyContext ctx);
	/**
	 * Exit a parse tree produced by the {@code inputListEmpty}
	 * labeled alternative in {@link cParser#inputList}.
	 * @param ctx the parse tree
	 */
	void exitInputListEmpty(cParser.InputListEmptyContext ctx);
	/**
	 * Enter a parse tree produced by the {@code stmtsCons}
	 * labeled alternative in {@link cParser#statements}.
	 * @param ctx the parse tree
	 */
	void enterStmtsCons(cParser.StmtsConsContext ctx);
	/**
	 * Exit a parse tree produced by the {@code stmtsCons}
	 * labeled alternative in {@link cParser#statements}.
	 * @param ctx the parse tree
	 */
	void exitStmtsCons(cParser.StmtsConsContext ctx);
	/**
	 * Enter a parse tree produced by the {@code stmtsEmpty}
	 * labeled alternative in {@link cParser#statements}.
	 * @param ctx the parse tree
	 */
	void enterStmtsEmpty(cParser.StmtsEmptyContext ctx);
	/**
	 * Exit a parse tree produced by the {@code stmtsEmpty}
	 * labeled alternative in {@link cParser#statements}.
	 * @param ctx the parse tree
	 */
	void exitStmtsEmpty(cParser.StmtsEmptyContext ctx);
	/**
	 * Enter a parse tree produced by the {@code decUSingle}
	 * labeled alternative in {@link cParser#decSingle}.
	 * @param ctx the parse tree
	 */
	void enterDecUSingle(cParser.DecUSingleContext ctx);
	/**
	 * Exit a parse tree produced by the {@code decUSingle}
	 * labeled alternative in {@link cParser#decSingle}.
	 * @param ctx the parse tree
	 */
	void exitDecUSingle(cParser.DecUSingleContext ctx);
	/**
	 * Enter a parse tree produced by the {@code decASingle}
	 * labeled alternative in {@link cParser#decSingle}.
	 * @param ctx the parse tree
	 */
	void enterDecASingle(cParser.DecASingleContext ctx);
	/**
	 * Exit a parse tree produced by the {@code decASingle}
	 * labeled alternative in {@link cParser#decSingle}.
	 * @param ctx the parse tree
	 */
	void exitDecASingle(cParser.DecASingleContext ctx);
	/**
	 * Enter a parse tree produced by the {@code declSingle}
	 * labeled alternative in {@link cParser#declist}.
	 * @param ctx the parse tree
	 */
	void enterDeclSingle(cParser.DeclSingleContext ctx);
	/**
	 * Exit a parse tree produced by the {@code declSingle}
	 * labeled alternative in {@link cParser#declist}.
	 * @param ctx the parse tree
	 */
	void exitDeclSingle(cParser.DeclSingleContext ctx);
	/**
	 * Enter a parse tree produced by the {@code declCons}
	 * labeled alternative in {@link cParser#declist}.
	 * @param ctx the parse tree
	 */
	void enterDeclCons(cParser.DeclConsContext ctx);
	/**
	 * Exit a parse tree produced by the {@code declCons}
	 * labeled alternative in {@link cParser#declist}.
	 * @param ctx the parse tree
	 */
	void exitDeclCons(cParser.DeclConsContext ctx);
	/**
	 * Enter a parse tree produced by the {@code blankStmt}
	 * labeled alternative in {@link cParser#simpleStatement}.
	 * @param ctx the parse tree
	 */
	void enterBlankStmt(cParser.BlankStmtContext ctx);
	/**
	 * Exit a parse tree produced by the {@code blankStmt}
	 * labeled alternative in {@link cParser#simpleStatement}.
	 * @param ctx the parse tree
	 */
	void exitBlankStmt(cParser.BlankStmtContext ctx);
	/**
	 * Enter a parse tree produced by the {@code assnStmt}
	 * labeled alternative in {@link cParser#simpleStatement}.
	 * @param ctx the parse tree
	 */
	void enterAssnStmt(cParser.AssnStmtContext ctx);
	/**
	 * Exit a parse tree produced by the {@code assnStmt}
	 * labeled alternative in {@link cParser#simpleStatement}.
	 * @param ctx the parse tree
	 */
	void exitAssnStmt(cParser.AssnStmtContext ctx);
	/**
	 * Enter a parse tree produced by the {@code arithAssnStmt}
	 * labeled alternative in {@link cParser#simpleStatement}.
	 * @param ctx the parse tree
	 */
	void enterArithAssnStmt(cParser.ArithAssnStmtContext ctx);
	/**
	 * Exit a parse tree produced by the {@code arithAssnStmt}
	 * labeled alternative in {@link cParser#simpleStatement}.
	 * @param ctx the parse tree
	 */
	void exitArithAssnStmt(cParser.ArithAssnStmtContext ctx);
	/**
	 * Enter a parse tree produced by the {@code exprStmt}
	 * labeled alternative in {@link cParser#simpleStatement}.
	 * @param ctx the parse tree
	 */
	void enterExprStmt(cParser.ExprStmtContext ctx);
	/**
	 * Exit a parse tree produced by the {@code exprStmt}
	 * labeled alternative in {@link cParser#simpleStatement}.
	 * @param ctx the parse tree
	 */
	void exitExprStmt(cParser.ExprStmtContext ctx);
	/**
	 * Enter a parse tree produced by the {@code lvalHead}
	 * labeled alternative in {@link cParser#lvals}.
	 * @param ctx the parse tree
	 */
	void enterLvalHead(cParser.LvalHeadContext ctx);
	/**
	 * Exit a parse tree produced by the {@code lvalHead}
	 * labeled alternative in {@link cParser#lvals}.
	 * @param ctx the parse tree
	 */
	void exitLvalHead(cParser.LvalHeadContext ctx);
	/**
	 * Enter a parse tree produced by the {@code lvalCons}
	 * labeled alternative in {@link cParser#lvals}.
	 * @param ctx the parse tree
	 */
	void enterLvalCons(cParser.LvalConsContext ctx);
	/**
	 * Exit a parse tree produced by the {@code lvalCons}
	 * labeled alternative in {@link cParser#lvals}.
	 * @param ctx the parse tree
	 */
	void exitLvalCons(cParser.LvalConsContext ctx);
	/**
	 * Enter a parse tree produced by the {@code simpleStmt}
	 * labeled alternative in {@link cParser#statement}.
	 * @param ctx the parse tree
	 */
	void enterSimpleStmt(cParser.SimpleStmtContext ctx);
	/**
	 * Exit a parse tree produced by the {@code simpleStmt}
	 * labeled alternative in {@link cParser#statement}.
	 * @param ctx the parse tree
	 */
	void exitSimpleStmt(cParser.SimpleStmtContext ctx);
	/**
	 * Enter a parse tree produced by the {@code groupStmt}
	 * labeled alternative in {@link cParser#statement}.
	 * @param ctx the parse tree
	 */
	void enterGroupStmt(cParser.GroupStmtContext ctx);
	/**
	 * Exit a parse tree produced by the {@code groupStmt}
	 * labeled alternative in {@link cParser#statement}.
	 * @param ctx the parse tree
	 */
	void exitGroupStmt(cParser.GroupStmtContext ctx);
	/**
	 * Enter a parse tree produced by the {@code ifSingle}
	 * labeled alternative in {@link cParser#statement}.
	 * @param ctx the parse tree
	 */
	void enterIfSingle(cParser.IfSingleContext ctx);
	/**
	 * Exit a parse tree produced by the {@code ifSingle}
	 * labeled alternative in {@link cParser#statement}.
	 * @param ctx the parse tree
	 */
	void exitIfSingle(cParser.IfSingleContext ctx);
	/**
	 * Enter a parse tree produced by the {@code ifElse}
	 * labeled alternative in {@link cParser#statement}.
	 * @param ctx the parse tree
	 */
	void enterIfElse(cParser.IfElseContext ctx);
	/**
	 * Exit a parse tree produced by the {@code ifElse}
	 * labeled alternative in {@link cParser#statement}.
	 * @param ctx the parse tree
	 */
	void exitIfElse(cParser.IfElseContext ctx);
	/**
	 * Enter a parse tree produced by the {@code sout}
	 * labeled alternative in {@link cParser#statement}.
	 * @param ctx the parse tree
	 */
	void enterSout(cParser.SoutContext ctx);
	/**
	 * Exit a parse tree produced by the {@code sout}
	 * labeled alternative in {@link cParser#statement}.
	 * @param ctx the parse tree
	 */
	void exitSout(cParser.SoutContext ctx);
	/**
	 * Enter a parse tree produced by the {@code sOutsource}
	 * labeled alternative in {@link cParser#statement}.
	 * @param ctx the parse tree
	 */
	void enterSOutsource(cParser.SOutsourceContext ctx);
	/**
	 * Exit a parse tree produced by the {@code sOutsource}
	 * labeled alternative in {@link cParser#statement}.
	 * @param ctx the parse tree
	 */
	void exitSOutsource(cParser.SOutsourceContext ctx);
	/**
	 * Enter a parse tree produced by the {@code while}
	 * labeled alternative in {@link cParser#statement}.
	 * @param ctx the parse tree
	 */
	void enterWhile(cParser.WhileContext ctx);
	/**
	 * Exit a parse tree produced by the {@code while}
	 * labeled alternative in {@link cParser#statement}.
	 * @param ctx the parse tree
	 */
	void exitWhile(cParser.WhileContext ctx);
	/**
	 * Enter a parse tree produced by the {@code forLoop}
	 * labeled alternative in {@link cParser#statement}.
	 * @param ctx the parse tree
	 */
	void enterForLoop(cParser.ForLoopContext ctx);
	/**
	 * Exit a parse tree produced by the {@code forLoop}
	 * labeled alternative in {@link cParser#statement}.
	 * @param ctx the parse tree
	 */
	void exitForLoop(cParser.ForLoopContext ctx);
	/**
	 * Enter a parse tree produced by the {@code oforLoop}
	 * labeled alternative in {@link cParser#statement}.
	 * @param ctx the parse tree
	 */
	void enterOforLoop(cParser.OforLoopContext ctx);
	/**
	 * Exit a parse tree produced by the {@code oforLoop}
	 * labeled alternative in {@link cParser#statement}.
	 * @param ctx the parse tree
	 */
	void exitOforLoop(cParser.OforLoopContext ctx);
	/**
	 * Enter a parse tree produced by the {@code decStmt}
	 * labeled alternative in {@link cParser#statement}.
	 * @param ctx the parse tree
	 */
	void enterDecStmt(cParser.DecStmtContext ctx);
	/**
	 * Exit a parse tree produced by the {@code decStmt}
	 * labeled alternative in {@link cParser#statement}.
	 * @param ctx the parse tree
	 */
	void exitDecStmt(cParser.DecStmtContext ctx);
	/**
	 * Enter a parse tree produced by the {@code decArrayIdent}
	 * labeled alternative in {@link cParser#statement}.
	 * @param ctx the parse tree
	 */
	void enterDecArrayIdent(cParser.DecArrayIdentContext ctx);
	/**
	 * Exit a parse tree produced by the {@code decArrayIdent}
	 * labeled alternative in {@link cParser#statement}.
	 * @param ctx the parse tree
	 */
	void exitDecArrayIdent(cParser.DecArrayIdentContext ctx);
	/**
	 * Enter a parse tree produced by the {@code decArrayInt}
	 * labeled alternative in {@link cParser#statement}.
	 * @param ctx the parse tree
	 */
	void enterDecArrayInt(cParser.DecArrayIntContext ctx);
	/**
	 * Exit a parse tree produced by the {@code decArrayInt}
	 * labeled alternative in {@link cParser#statement}.
	 * @param ctx the parse tree
	 */
	void exitDecArrayInt(cParser.DecArrayIntContext ctx);
	/**
	 * Enter a parse tree produced by the {@code decSingleDef}
	 * labeled alternative in {@link cParser#statement}.
	 * @param ctx the parse tree
	 */
	void enterDecSingleDef(cParser.DecSingleDefContext ctx);
	/**
	 * Exit a parse tree produced by the {@code decSingleDef}
	 * labeled alternative in {@link cParser#statement}.
	 * @param ctx the parse tree
	 */
	void exitDecSingleDef(cParser.DecSingleDefContext ctx);
	/**
	 * Enter a parse tree produced by the {@code retStmt}
	 * labeled alternative in {@link cParser#statement}.
	 * @param ctx the parse tree
	 */
	void enterRetStmt(cParser.RetStmtContext ctx);
	/**
	 * Exit a parse tree produced by the {@code retStmt}
	 * labeled alternative in {@link cParser#statement}.
	 * @param ctx the parse tree
	 */
	void exitRetStmt(cParser.RetStmtContext ctx);
	/**
	 * Enter a parse tree produced by the {@code retStmtNone}
	 * labeled alternative in {@link cParser#statement}.
	 * @param ctx the parse tree
	 */
	void enterRetStmtNone(cParser.RetStmtNoneContext ctx);
	/**
	 * Exit a parse tree produced by the {@code retStmtNone}
	 * labeled alternative in {@link cParser#statement}.
	 * @param ctx the parse tree
	 */
	void exitRetStmtNone(cParser.RetStmtNoneContext ctx);
	/**
	 * Enter a parse tree produced by the {@code breakStmt}
	 * labeled alternative in {@link cParser#statement}.
	 * @param ctx the parse tree
	 */
	void enterBreakStmt(cParser.BreakStmtContext ctx);
	/**
	 * Exit a parse tree produced by the {@code breakStmt}
	 * labeled alternative in {@link cParser#statement}.
	 * @param ctx the parse tree
	 */
	void exitBreakStmt(cParser.BreakStmtContext ctx);
	/**
	 * Enter a parse tree produced by the {@code continueStmt}
	 * labeled alternative in {@link cParser#statement}.
	 * @param ctx the parse tree
	 */
	void enterContinueStmt(cParser.ContinueStmtContext ctx);
	/**
	 * Exit a parse tree produced by the {@code continueStmt}
	 * labeled alternative in {@link cParser#statement}.
	 * @param ctx the parse tree
	 */
	void exitContinueStmt(cParser.ContinueStmtContext ctx);
	/**
	 * Enter a parse tree produced by the {@code beginStmt}
	 * labeled alternative in {@link cParser#statement}.
	 * @param ctx the parse tree
	 */
	void enterBeginStmt(cParser.BeginStmtContext ctx);
	/**
	 * Exit a parse tree produced by the {@code beginStmt}
	 * labeled alternative in {@link cParser#statement}.
	 * @param ctx the parse tree
	 */
	void exitBeginStmt(cParser.BeginStmtContext ctx);
	/**
	 * Enter a parse tree produced by the {@code endStmt}
	 * labeled alternative in {@link cParser#statement}.
	 * @param ctx the parse tree
	 */
	void enterEndStmt(cParser.EndStmtContext ctx);
	/**
	 * Exit a parse tree produced by the {@code endStmt}
	 * labeled alternative in {@link cParser#statement}.
	 * @param ctx the parse tree
	 */
	void exitEndStmt(cParser.EndStmtContext ctx);
	/**
	 * Enter a parse tree produced by the {@code funcdefSimple}
	 * labeled alternative in {@link cParser#funcDef}.
	 * @param ctx the parse tree
	 */
	void enterFuncdefSimple(cParser.FuncdefSimpleContext ctx);
	/**
	 * Exit a parse tree produced by the {@code funcdefSimple}
	 * labeled alternative in {@link cParser#funcDef}.
	 * @param ctx the parse tree
	 */
	void exitFuncdefSimple(cParser.FuncdefSimpleContext ctx);
	/**
	 * Enter a parse tree produced by {@link cParser#arrayLength}.
	 * @param ctx the parse tree
	 */
	void enterArrayLength(cParser.ArrayLengthContext ctx);
	/**
	 * Exit a parse tree produced by {@link cParser#arrayLength}.
	 * @param ctx the parse tree
	 */
	void exitArrayLength(cParser.ArrayLengthContext ctx);
	/**
	 * Enter a parse tree produced by the {@code singleDefNormal}
	 * labeled alternative in {@link cParser#singleDef}.
	 * @param ctx the parse tree
	 */
	void enterSingleDefNormal(cParser.SingleDefNormalContext ctx);
	/**
	 * Exit a parse tree produced by the {@code singleDefNormal}
	 * labeled alternative in {@link cParser#singleDef}.
	 * @param ctx the parse tree
	 */
	void exitSingleDefNormal(cParser.SingleDefNormalContext ctx);
	/**
	 * Enter a parse tree produced by the {@code singleDefArrayConst}
	 * labeled alternative in {@link cParser#singleDef}.
	 * @param ctx the parse tree
	 */
	void enterSingleDefArrayConst(cParser.SingleDefArrayConstContext ctx);
	/**
	 * Exit a parse tree produced by the {@code singleDefArrayConst}
	 * labeled alternative in {@link cParser#singleDef}.
	 * @param ctx the parse tree
	 */
	void exitSingleDefArrayConst(cParser.SingleDefArrayConstContext ctx);
	/**
	 * Enter a parse tree produced by {@link cParser#structDef}.
	 * @param ctx the parse tree
	 */
	void enterStructDef(cParser.StructDefContext ctx);
	/**
	 * Exit a parse tree produced by {@link cParser#structDef}.
	 * @param ctx the parse tree
	 */
	void exitStructDef(cParser.StructDefContext ctx);
}
