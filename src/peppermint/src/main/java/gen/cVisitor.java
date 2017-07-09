package gen;
import org.antlr.v4.runtime.tree.ParseTreeVisitor;

/**
 * This interface defines a complete generic visitor for a parse tree produced
 * by {@link cParser}.
 *
 * @param <T> The return type of the visit operation. Use {@link Void} for
 * operations with no return type.
 */
public interface cVisitor<T> extends ParseTreeVisitor<T> {
	/**
	 * Visit a parse tree produced by the {@code tlSingle}
	 * labeled alternative in {@link cParser#program}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitTlSingle(cParser.TlSingleContext ctx);
	/**
	 * Visit a parse tree produced by the {@code tldefFunc}
	 * labeled alternative in {@link cParser#topLevelDef}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitTldefFunc(cParser.TldefFuncContext ctx);
	/**
	 * Visit a parse tree produced by the {@code tldefDfn}
	 * labeled alternative in {@link cParser#topLevelDef}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitTldefDfn(cParser.TldefDfnContext ctx);
	/**
	 * Visit a parse tree produced by the {@code tldefStct}
	 * labeled alternative in {@link cParser#topLevelDef}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitTldefStct(cParser.TldefStctContext ctx);
	/**
	 * Visit a parse tree produced by the {@code defineII}
	 * labeled alternative in {@link cParser#define}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitDefineII(cParser.DefineIIContext ctx);
	/**
	 * Visit a parse tree produced by the {@code elval}
	 * labeled alternative in {@link cParser#primaryExpr}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitElval(cParser.ElvalContext ctx);
	/**
	 * Visit a parse tree produced by the {@code const}
	 * labeled alternative in {@link cParser#primaryExpr}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitConst(cParser.ConstContext ctx);
	/**
	 * Visit a parse tree produced by the {@code eparen}
	 * labeled alternative in {@link cParser#primaryExpr}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitEparen(cParser.EparenContext ctx);
	/**
	 * Visit a parse tree produced by the {@code efunc}
	 * labeled alternative in {@link cParser#primaryExpr}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitEfunc(cParser.EfuncContext ctx);
	/**
	 * Visit a parse tree produced by the {@code eprimary}
	 * labeled alternative in {@link cParser#postFixExpr}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitEprimary(cParser.EprimaryContext ctx);
	/**
	 * Visit a parse tree produced by the {@code ePostFix}
	 * labeled alternative in {@link cParser#postFixExpr}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitEPostFix(cParser.EPostFixContext ctx);
	/**
	 * Visit a parse tree produced by the {@code ePrePost}
	 * labeled alternative in {@link cParser#preFixExpr}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitEPrePost(cParser.EPrePostContext ctx);
	/**
	 * Visit a parse tree produced by the {@code epre}
	 * labeled alternative in {@link cParser#preFixExpr}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitEpre(cParser.EpreContext ctx);
	/**
	 * Visit a parse tree produced by the {@code eMultiBin}
	 * labeled alternative in {@link cParser#multiplicativeExpr}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitEMultiBin(cParser.EMultiBinContext ctx);
	/**
	 * Visit a parse tree produced by the {@code eMultiPre}
	 * labeled alternative in {@link cParser#multiplicativeExpr}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitEMultiPre(cParser.EMultiPreContext ctx);
	/**
	 * Visit a parse tree produced by the {@code eAddMult}
	 * labeled alternative in {@link cParser#additiveExpr}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitEAddMult(cParser.EAddMultContext ctx);
	/**
	 * Visit a parse tree produced by the {@code eAddBin}
	 * labeled alternative in {@link cParser#additiveExpr}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitEAddBin(cParser.EAddBinContext ctx);
	/**
	 * Visit a parse tree produced by the {@code eRelBin}
	 * labeled alternative in {@link cParser#relationalExpr}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitERelBin(cParser.ERelBinContext ctx);
	/**
	 * Visit a parse tree produced by the {@code eRelAdd}
	 * labeled alternative in {@link cParser#relationalExpr}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitERelAdd(cParser.ERelAddContext ctx);
	/**
	 * Visit a parse tree produced by the {@code eAndBin}
	 * labeled alternative in {@link cParser#andExpr}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitEAndBin(cParser.EAndBinContext ctx);
	/**
	 * Visit a parse tree produced by the {@code eAndRel}
	 * labeled alternative in {@link cParser#andExpr}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitEAndRel(cParser.EAndRelContext ctx);
	/**
	 * Visit a parse tree produced by the {@code eAndOr}
	 * labeled alternative in {@link cParser#orExpr}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitEAndOr(cParser.EAndOrContext ctx);
	/**
	 * Visit a parse tree produced by the {@code eOrBin}
	 * labeled alternative in {@link cParser#orExpr}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitEOrBin(cParser.EOrBinContext ctx);
	/**
	 * Visit a parse tree produced by the {@code eOrExpr}
	 * labeled alternative in {@link cParser#expr}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitEOrExpr(cParser.EOrExprContext ctx);
	/**
	 * Visit a parse tree produced by the {@code eSelect}
	 * labeled alternative in {@link cParser#expr}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitESelect(cParser.ESelectContext ctx);
	/**
	 * Visit a parse tree produced by the {@code lindex}
	 * labeled alternative in {@link cParser#lval}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitLindex(cParser.LindexContext ctx);
	/**
	 * Visit a parse tree produced by the {@code lident}
	 * labeled alternative in {@link cParser#lval}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitLident(cParser.LidentContext ctx);
	/**
	 * Visit a parse tree produced by the {@code lderef}
	 * labeled alternative in {@link cParser#lval}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitLderef(cParser.LderefContext ctx);
	/**
	 * Visit a parse tree produced by the {@code larrow}
	 * labeled alternative in {@link cParser#lval}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitLarrow(cParser.LarrowContext ctx);
	/**
	 * Visit a parse tree produced by the {@code lparen}
	 * labeled alternative in {@link cParser#lval}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitLparen(cParser.LparenContext ctx);
	/**
	 * Visit a parse tree produced by the {@code ldot}
	 * labeled alternative in {@link cParser#lval}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitLdot(cParser.LdotContext ctx);
	/**
	 * Visit a parse tree produced by the {@code intType}
	 * labeled alternative in {@link cParser#nonPtrType}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitIntType(cParser.IntTypeContext ctx);
	/**
	 * Visit a parse tree produced by the {@code voidType}
	 * labeled alternative in {@link cParser#nonPtrType}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitVoidType(cParser.VoidTypeContext ctx);
	/**
	 * Visit a parse tree produced by the {@code structType}
	 * labeled alternative in {@link cParser#nonPtrType}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitStructType(cParser.StructTypeContext ctx);
	/**
	 * Visit a parse tree produced by the {@code nonPointerType}
	 * labeled alternative in {@link cParser#type}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitNonPointerType(cParser.NonPointerTypeContext ctx);
	/**
	 * Visit a parse tree produced by the {@code pointerType}
	 * labeled alternative in {@link cParser#type}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitPointerType(cParser.PointerTypeContext ctx);
	/**
	 * Visit a parse tree produced by the {@code inputListCons}
	 * labeled alternative in {@link cParser#inputList}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitInputListCons(cParser.InputListConsContext ctx);
	/**
	 * Visit a parse tree produced by the {@code inputListSingleton}
	 * labeled alternative in {@link cParser#inputList}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitInputListSingleton(cParser.InputListSingletonContext ctx);
	/**
	 * Visit a parse tree produced by the {@code inputListEmpty}
	 * labeled alternative in {@link cParser#inputList}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitInputListEmpty(cParser.InputListEmptyContext ctx);
	/**
	 * Visit a parse tree produced by the {@code stmtsCons}
	 * labeled alternative in {@link cParser#statements}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitStmtsCons(cParser.StmtsConsContext ctx);
	/**
	 * Visit a parse tree produced by the {@code stmtsEmpty}
	 * labeled alternative in {@link cParser#statements}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitStmtsEmpty(cParser.StmtsEmptyContext ctx);
	/**
	 * Visit a parse tree produced by the {@code decUSingle}
	 * labeled alternative in {@link cParser#decSingle}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitDecUSingle(cParser.DecUSingleContext ctx);
	/**
	 * Visit a parse tree produced by the {@code decASingle}
	 * labeled alternative in {@link cParser#decSingle}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitDecASingle(cParser.DecASingleContext ctx);
	/**
	 * Visit a parse tree produced by the {@code declSingle}
	 * labeled alternative in {@link cParser#declist}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitDeclSingle(cParser.DeclSingleContext ctx);
	/**
	 * Visit a parse tree produced by the {@code declCons}
	 * labeled alternative in {@link cParser#declist}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitDeclCons(cParser.DeclConsContext ctx);
	/**
	 * Visit a parse tree produced by the {@code blankStmt}
	 * labeled alternative in {@link cParser#simpleStatement}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitBlankStmt(cParser.BlankStmtContext ctx);
	/**
	 * Visit a parse tree produced by the {@code assnStmt}
	 * labeled alternative in {@link cParser#simpleStatement}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitAssnStmt(cParser.AssnStmtContext ctx);
	/**
	 * Visit a parse tree produced by the {@code arithAssnStmt}
	 * labeled alternative in {@link cParser#simpleStatement}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitArithAssnStmt(cParser.ArithAssnStmtContext ctx);
	/**
	 * Visit a parse tree produced by the {@code exprStmt}
	 * labeled alternative in {@link cParser#simpleStatement}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitExprStmt(cParser.ExprStmtContext ctx);
	/**
	 * Visit a parse tree produced by the {@code lvalHead}
	 * labeled alternative in {@link cParser#lvals}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitLvalHead(cParser.LvalHeadContext ctx);
	/**
	 * Visit a parse tree produced by the {@code lvalCons}
	 * labeled alternative in {@link cParser#lvals}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitLvalCons(cParser.LvalConsContext ctx);
	/**
	 * Visit a parse tree produced by the {@code simpleStmt}
	 * labeled alternative in {@link cParser#statement}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitSimpleStmt(cParser.SimpleStmtContext ctx);
	/**
	 * Visit a parse tree produced by the {@code groupStmt}
	 * labeled alternative in {@link cParser#statement}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitGroupStmt(cParser.GroupStmtContext ctx);
	/**
	 * Visit a parse tree produced by the {@code ifSingle}
	 * labeled alternative in {@link cParser#statement}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitIfSingle(cParser.IfSingleContext ctx);
	/**
	 * Visit a parse tree produced by the {@code ifElse}
	 * labeled alternative in {@link cParser#statement}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitIfElse(cParser.IfElseContext ctx);
	/**
	 * Visit a parse tree produced by the {@code sout}
	 * labeled alternative in {@link cParser#statement}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitSout(cParser.SoutContext ctx);
	/**
	 * Visit a parse tree produced by the {@code sOutsource}
	 * labeled alternative in {@link cParser#statement}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitSOutsource(cParser.SOutsourceContext ctx);
	/**
	 * Visit a parse tree produced by the {@code while}
	 * labeled alternative in {@link cParser#statement}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitWhile(cParser.WhileContext ctx);
	/**
	 * Visit a parse tree produced by the {@code forLoop}
	 * labeled alternative in {@link cParser#statement}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitForLoop(cParser.ForLoopContext ctx);
	/**
	 * Visit a parse tree produced by the {@code oforLoop}
	 * labeled alternative in {@link cParser#statement}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitOforLoop(cParser.OforLoopContext ctx);
	/**
	 * Visit a parse tree produced by the {@code decStmt}
	 * labeled alternative in {@link cParser#statement}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitDecStmt(cParser.DecStmtContext ctx);
	/**
	 * Visit a parse tree produced by the {@code decArrayIdent}
	 * labeled alternative in {@link cParser#statement}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitDecArrayIdent(cParser.DecArrayIdentContext ctx);
	/**
	 * Visit a parse tree produced by the {@code decArrayInt}
	 * labeled alternative in {@link cParser#statement}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitDecArrayInt(cParser.DecArrayIntContext ctx);
	/**
	 * Visit a parse tree produced by the {@code decSingleDef}
	 * labeled alternative in {@link cParser#statement}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitDecSingleDef(cParser.DecSingleDefContext ctx);
	/**
	 * Visit a parse tree produced by the {@code retStmt}
	 * labeled alternative in {@link cParser#statement}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitRetStmt(cParser.RetStmtContext ctx);
	/**
	 * Visit a parse tree produced by the {@code retStmtNone}
	 * labeled alternative in {@link cParser#statement}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitRetStmtNone(cParser.RetStmtNoneContext ctx);
	/**
	 * Visit a parse tree produced by the {@code breakStmt}
	 * labeled alternative in {@link cParser#statement}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitBreakStmt(cParser.BreakStmtContext ctx);
	/**
	 * Visit a parse tree produced by the {@code continueStmt}
	 * labeled alternative in {@link cParser#statement}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitContinueStmt(cParser.ContinueStmtContext ctx);
	/**
	 * Visit a parse tree produced by the {@code beginStmt}
	 * labeled alternative in {@link cParser#statement}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitBeginStmt(cParser.BeginStmtContext ctx);
	/**
	 * Visit a parse tree produced by the {@code endStmt}
	 * labeled alternative in {@link cParser#statement}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitEndStmt(cParser.EndStmtContext ctx);
	/**
	 * Visit a parse tree produced by the {@code funcdefSimple}
	 * labeled alternative in {@link cParser#funcDef}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitFuncdefSimple(cParser.FuncdefSimpleContext ctx);
	/**
	 * Visit a parse tree produced by {@link cParser#arrayLength}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitArrayLength(cParser.ArrayLengthContext ctx);
	/**
	 * Visit a parse tree produced by the {@code singleDefNormal}
	 * labeled alternative in {@link cParser#singleDef}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitSingleDefNormal(cParser.SingleDefNormalContext ctx);
	/**
	 * Visit a parse tree produced by the {@code singleDefArrayConst}
	 * labeled alternative in {@link cParser#singleDef}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitSingleDefArrayConst(cParser.SingleDefArrayConstContext ctx);
	/**
	 * Visit a parse tree produced by {@link cParser#structDef}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitStructDef(cParser.StructDefContext ctx);
}
