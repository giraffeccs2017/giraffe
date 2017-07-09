package gen;
import org.antlr.v4.runtime.tree.ParseTreeVisitor;

/**
 * This interface defines a complete generic visitor for a parse tree produced
 * by {@link pwsParser}.
 *
 * @param <T> The return type of the visit operation. Use {@link Void} for
 * operations with no return type.
 */
public interface pwsVisitor<T> extends ParseTreeVisitor<T> {
	/**
	 * Visit a parse tree produced by the {@code lident}
	 * labeled alternative in {@link pwsParser#lval}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitLident(pwsParser.LidentContext ctx);
	/**
	 * Visit a parse tree produced by the {@code elval}
	 * labeled alternative in {@link pwsParser#primaryExpr}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitElval(pwsParser.ElvalContext ctx);
	/**
	 * Visit a parse tree produced by the {@code const}
	 * labeled alternative in {@link pwsParser#primaryExpr}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitConst(pwsParser.ConstContext ctx);
	/**
	 * Visit a parse tree produced by the {@code eparen}
	 * labeled alternative in {@link pwsParser#primaryExpr}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitEparen(pwsParser.EparenContext ctx);
	/**
	 * Visit a parse tree produced by the {@code ePrePrim}
	 * labeled alternative in {@link pwsParser#preFixExpr}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitEPrePrim(pwsParser.EPrePrimContext ctx);
	/**
	 * Visit a parse tree produced by the {@code ePrePre}
	 * labeled alternative in {@link pwsParser#preFixExpr}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitEPrePre(pwsParser.EPrePreContext ctx);
	/**
	 * Visit a parse tree produced by the {@code eMultiBin}
	 * labeled alternative in {@link pwsParser#multiplicativeExpr}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitEMultiBin(pwsParser.EMultiBinContext ctx);
	/**
	 * Visit a parse tree produced by the {@code eMultiPre}
	 * labeled alternative in {@link pwsParser#multiplicativeExpr}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitEMultiPre(pwsParser.EMultiPreContext ctx);
	/**
	 * Visit a parse tree produced by the {@code eAddMult}
	 * labeled alternative in {@link pwsParser#additiveExpr}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitEAddMult(pwsParser.EAddMultContext ctx);
	/**
	 * Visit a parse tree produced by the {@code eAddBin}
	 * labeled alternative in {@link pwsParser#additiveExpr}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitEAddBin(pwsParser.EAddBinContext ctx);
	/**
	 * Visit a parse tree produced by the {@code pwsEmpty}
	 * labeled alternative in {@link pwsParser#onePws}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitPwsEmpty(pwsParser.PwsEmptyContext ctx);
	/**
	 * Visit a parse tree produced by the {@code pwsNonEmpty}
	 * labeled alternative in {@link pwsParser#onePws}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitPwsNonEmpty(pwsParser.PwsNonEmptyContext ctx);
	/**
	 * Visit a parse tree produced by the {@code pwsNEQ}
	 * labeled alternative in {@link pwsParser#onePws}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitPwsNEQ(pwsParser.PwsNEQContext ctx);
	/**
	 * Visit a parse tree produced by the {@code pwsLT}
	 * labeled alternative in {@link pwsParser#onePws}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitPwsLT(pwsParser.PwsLTContext ctx);
	/**
	 * Visit a parse tree produced by the {@code pwsOneOrMore}
	 * labeled alternative in {@link pwsParser#pws}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitPwsOneOrMore(pwsParser.PwsOneOrMoreContext ctx);
}
