package gen;
import org.antlr.v4.runtime.atn.*;
import org.antlr.v4.runtime.dfa.DFA;
import org.antlr.v4.runtime.*;
import org.antlr.v4.runtime.misc.*;
import org.antlr.v4.runtime.tree.*;
import java.util.List;
import java.util.Iterator;
import java.util.ArrayList;

@SuppressWarnings({"all", "warnings", "unchecked", "unused", "cast"})
public class cParser extends Parser {
	static { RuntimeMetaData.checkVersion("4.5.3", RuntimeMetaData.VERSION); }

	protected static final DFA[] _decisionToDFA;
	protected static final PredictionContextCache _sharedContextCache =
		new PredictionContextCache();
	public static final int
		T__0=1, T__1=2, T__2=3, T__3=4, T__4=5, T__5=6, T__6=7, T__7=8, T__8=9, 
		T__9=10, T__10=11, T__11=12, T__12=13, T__13=14, T__14=15, T__15=16, T__16=17, 
		T__17=18, T__18=19, T__19=20, T__20=21, T__21=22, T__22=23, T__23=24, 
		T__24=25, T__25=26, T__26=27, T__27=28, T__28=29, T__29=30, T__30=31, 
		T__31=32, T__32=33, T__33=34, T__34=35, T__35=36, T__36=37, T__37=38, 
		T__38=39, T__39=40, T__40=41, T__41=42, T__42=43, T__43=44, T__44=45, 
		T__45=46, T__46=47, T__47=48, T__48=49, T__49=50, T__50=51, T__51=52, 
		Ident=53, COMMENT=54, SCOMMENT=55, INCLUDE=56, SHARPCOMMENT=57, IntType=58, 
		Int=59, WS=60;
	public static final int
		RULE_program = 0, RULE_topLevelDef = 1, RULE_define = 2, RULE_primaryExpr = 3, 
		RULE_postFixExpr = 4, RULE_preFixExpr = 5, RULE_multiplicativeExpr = 6, 
		RULE_additiveExpr = 7, RULE_relationalExpr = 8, RULE_andExpr = 9, RULE_orExpr = 10, 
		RULE_expr = 11, RULE_lval = 12, RULE_nonPtrType = 13, RULE_type = 14, 
		RULE_inputList = 15, RULE_statements = 16, RULE_decSingle = 17, RULE_declist = 18, 
		RULE_simpleStatement = 19, RULE_lvals = 20, RULE_statement = 21, RULE_funcDef = 22, 
		RULE_arrayLength = 23, RULE_singleDef = 24, RULE_structDef = 25;
	public static final String[] ruleNames = {
		"program", "topLevelDef", "define", "primaryExpr", "postFixExpr", "preFixExpr", 
		"multiplicativeExpr", "additiveExpr", "relationalExpr", "andExpr", "orExpr", 
		"expr", "lval", "nonPtrType", "type", "inputList", "statements", "decSingle", 
		"declist", "simpleStatement", "lvals", "statement", "funcDef", "arrayLength", 
		"singleDef", "structDef"
	};

	private static final String[] _LITERAL_NAMES = {
		null, "'#define'", "'('", "')'", "','", "'--'", "'++'", "'-'", "'!'", 
		"'&'", "'*'", "'/'", "'%'", "'+'", "'<'", "'>'", "'<='", "'>='", "'=='", 
		"'!='", "'&&'", "'||'", "'?'", "':'", "'->'", "'.'", "'['", "']'", "'int'", 
		"'void'", "'struct'", "'='", "'+='", "'*='", "'/='", "'%='", "'-='", "';'", 
		"'{'", "'}'", "'if'", "'else'", "'out {'", "'outsource {'", "'} {'", "'while'", 
		"'for'", "'ofor'", "'return'", "'break'", "'continue'", "'_begin'", "'_end'"
	};
	private static final String[] _SYMBOLIC_NAMES = {
		null, null, null, null, null, null, null, null, null, null, null, null, 
		null, null, null, null, null, null, null, null, null, null, null, null, 
		null, null, null, null, null, null, null, null, null, null, null, null, 
		null, null, null, null, null, null, null, null, null, null, null, null, 
		null, null, null, null, null, "Ident", "COMMENT", "SCOMMENT", "INCLUDE", 
		"SHARPCOMMENT", "IntType", "Int", "WS"
	};
	public static final Vocabulary VOCABULARY = new VocabularyImpl(_LITERAL_NAMES, _SYMBOLIC_NAMES);

	/**
	 * @deprecated Use {@link #VOCABULARY} instead.
	 */
	@Deprecated
	public static final String[] tokenNames;
	static {
		tokenNames = new String[_SYMBOLIC_NAMES.length];
		for (int i = 0; i < tokenNames.length; i++) {
			tokenNames[i] = VOCABULARY.getLiteralName(i);
			if (tokenNames[i] == null) {
				tokenNames[i] = VOCABULARY.getSymbolicName(i);
			}

			if (tokenNames[i] == null) {
				tokenNames[i] = "<INVALID>";
			}
		}
	}

	@Override
	@Deprecated
	public String[] getTokenNames() {
		return tokenNames;
	}

	@Override

	public Vocabulary getVocabulary() {
		return VOCABULARY;
	}

	@Override
	public String getGrammarFileName() { return "c.g4"; }

	@Override
	public String[] getRuleNames() { return ruleNames; }

	@Override
	public String getSerializedATN() { return _serializedATN; }

	@Override
	public ATN getATN() { return _ATN; }

	public cParser(TokenStream input) {
		super(input);
		_interp = new ParserATNSimulator(this,_ATN,_decisionToDFA,_sharedContextCache);
	}
	public static class ProgramContext extends ParserRuleContext {
		public ProgramContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_program; }
	 
		public ProgramContext() { }
		public void copyFrom(ProgramContext ctx) {
			super.copyFrom(ctx);
		}
	}
	public static class TlSingleContext extends ProgramContext {
		public TerminalNode EOF() { return getToken(cParser.EOF, 0); }
		public List<TopLevelDefContext> topLevelDef() {
			return getRuleContexts(TopLevelDefContext.class);
		}
		public TopLevelDefContext topLevelDef(int i) {
			return getRuleContext(TopLevelDefContext.class,i);
		}
		public TlSingleContext(ProgramContext ctx) { copyFrom(ctx); }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof cListener ) ((cListener)listener).enterTlSingle(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof cListener ) ((cListener)listener).exitTlSingle(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof cVisitor ) return ((cVisitor<? extends T>)visitor).visitTlSingle(this);
			else return visitor.visitChildren(this);
		}
	}

	public final ProgramContext program() throws RecognitionException {
		ProgramContext _localctx = new ProgramContext(_ctx, getState());
		enterRule(_localctx, 0, RULE_program);
		int _la;
		try {
			_localctx = new TlSingleContext(_localctx);
			enterOuterAlt(_localctx, 1);
			{
			setState(55);
			_errHandler.sync(this);
			_la = _input.LA(1);
			while ((((_la) & ~0x3f) == 0 && ((1L << _la) & ((1L << T__0) | (1L << T__27) | (1L << T__28) | (1L << T__29))) != 0)) {
				{
				{
				setState(52);
				topLevelDef();
				}
				}
				setState(57);
				_errHandler.sync(this);
				_la = _input.LA(1);
			}
			setState(58);
			match(EOF);
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class TopLevelDefContext extends ParserRuleContext {
		public TopLevelDefContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_topLevelDef; }
	 
		public TopLevelDefContext() { }
		public void copyFrom(TopLevelDefContext ctx) {
			super.copyFrom(ctx);
		}
	}
	public static class TldefStctContext extends TopLevelDefContext {
		public StructDefContext structDef() {
			return getRuleContext(StructDefContext.class,0);
		}
		public TldefStctContext(TopLevelDefContext ctx) { copyFrom(ctx); }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof cListener ) ((cListener)listener).enterTldefStct(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof cListener ) ((cListener)listener).exitTldefStct(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof cVisitor ) return ((cVisitor<? extends T>)visitor).visitTldefStct(this);
			else return visitor.visitChildren(this);
		}
	}
	public static class TldefDfnContext extends TopLevelDefContext {
		public DefineContext define() {
			return getRuleContext(DefineContext.class,0);
		}
		public TldefDfnContext(TopLevelDefContext ctx) { copyFrom(ctx); }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof cListener ) ((cListener)listener).enterTldefDfn(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof cListener ) ((cListener)listener).exitTldefDfn(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof cVisitor ) return ((cVisitor<? extends T>)visitor).visitTldefDfn(this);
			else return visitor.visitChildren(this);
		}
	}
	public static class TldefFuncContext extends TopLevelDefContext {
		public FuncDefContext funcDef() {
			return getRuleContext(FuncDefContext.class,0);
		}
		public TldefFuncContext(TopLevelDefContext ctx) { copyFrom(ctx); }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof cListener ) ((cListener)listener).enterTldefFunc(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof cListener ) ((cListener)listener).exitTldefFunc(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof cVisitor ) return ((cVisitor<? extends T>)visitor).visitTldefFunc(this);
			else return visitor.visitChildren(this);
		}
	}

	public final TopLevelDefContext topLevelDef() throws RecognitionException {
		TopLevelDefContext _localctx = new TopLevelDefContext(_ctx, getState());
		enterRule(_localctx, 2, RULE_topLevelDef);
		try {
			setState(63);
			_errHandler.sync(this);
			switch ( getInterpreter().adaptivePredict(_input,1,_ctx) ) {
			case 1:
				_localctx = new TldefFuncContext(_localctx);
				enterOuterAlt(_localctx, 1);
				{
				setState(60);
				funcDef();
				}
				break;
			case 2:
				_localctx = new TldefDfnContext(_localctx);
				enterOuterAlt(_localctx, 2);
				{
				setState(61);
				define();
				}
				break;
			case 3:
				_localctx = new TldefStctContext(_localctx);
				enterOuterAlt(_localctx, 3);
				{
				setState(62);
				structDef();
				}
				break;
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class DefineContext extends ParserRuleContext {
		public DefineContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_define; }
	 
		public DefineContext() { }
		public void copyFrom(DefineContext ctx) {
			super.copyFrom(ctx);
		}
	}
	public static class DefineIIContext extends DefineContext {
		public TerminalNode Ident() { return getToken(cParser.Ident, 0); }
		public TerminalNode Int() { return getToken(cParser.Int, 0); }
		public DefineIIContext(DefineContext ctx) { copyFrom(ctx); }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof cListener ) ((cListener)listener).enterDefineII(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof cListener ) ((cListener)listener).exitDefineII(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof cVisitor ) return ((cVisitor<? extends T>)visitor).visitDefineII(this);
			else return visitor.visitChildren(this);
		}
	}

	public final DefineContext define() throws RecognitionException {
		DefineContext _localctx = new DefineContext(_ctx, getState());
		enterRule(_localctx, 4, RULE_define);
		try {
			_localctx = new DefineIIContext(_localctx);
			enterOuterAlt(_localctx, 1);
			{
			setState(65);
			match(T__0);
			setState(66);
			match(Ident);
			setState(67);
			match(Int);
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class PrimaryExprContext extends ParserRuleContext {
		public PrimaryExprContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_primaryExpr; }
	 
		public PrimaryExprContext() { }
		public void copyFrom(PrimaryExprContext ctx) {
			super.copyFrom(ctx);
		}
	}
	public static class ConstContext extends PrimaryExprContext {
		public TerminalNode Int() { return getToken(cParser.Int, 0); }
		public ConstContext(PrimaryExprContext ctx) { copyFrom(ctx); }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof cListener ) ((cListener)listener).enterConst(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof cListener ) ((cListener)listener).exitConst(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof cVisitor ) return ((cVisitor<? extends T>)visitor).visitConst(this);
			else return visitor.visitChildren(this);
		}
	}
	public static class EparenContext extends PrimaryExprContext {
		public ExprContext expr() {
			return getRuleContext(ExprContext.class,0);
		}
		public EparenContext(PrimaryExprContext ctx) { copyFrom(ctx); }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof cListener ) ((cListener)listener).enterEparen(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof cListener ) ((cListener)listener).exitEparen(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof cVisitor ) return ((cVisitor<? extends T>)visitor).visitEparen(this);
			else return visitor.visitChildren(this);
		}
	}
	public static class EfuncContext extends PrimaryExprContext {
		public TerminalNode Ident() { return getToken(cParser.Ident, 0); }
		public List<ExprContext> expr() {
			return getRuleContexts(ExprContext.class);
		}
		public ExprContext expr(int i) {
			return getRuleContext(ExprContext.class,i);
		}
		public EfuncContext(PrimaryExprContext ctx) { copyFrom(ctx); }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof cListener ) ((cListener)listener).enterEfunc(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof cListener ) ((cListener)listener).exitEfunc(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof cVisitor ) return ((cVisitor<? extends T>)visitor).visitEfunc(this);
			else return visitor.visitChildren(this);
		}
	}
	public static class ElvalContext extends PrimaryExprContext {
		public LvalContext lval() {
			return getRuleContext(LvalContext.class,0);
		}
		public ElvalContext(PrimaryExprContext ctx) { copyFrom(ctx); }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof cListener ) ((cListener)listener).enterElval(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof cListener ) ((cListener)listener).exitElval(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof cVisitor ) return ((cVisitor<? extends T>)visitor).visitElval(this);
			else return visitor.visitChildren(this);
		}
	}

	public final PrimaryExprContext primaryExpr() throws RecognitionException {
		PrimaryExprContext _localctx = new PrimaryExprContext(_ctx, getState());
		enterRule(_localctx, 6, RULE_primaryExpr);
		int _la;
		try {
			setState(88);
			_errHandler.sync(this);
			switch ( getInterpreter().adaptivePredict(_input,4,_ctx) ) {
			case 1:
				_localctx = new ElvalContext(_localctx);
				enterOuterAlt(_localctx, 1);
				{
				setState(69);
				lval(0);
				}
				break;
			case 2:
				_localctx = new ConstContext(_localctx);
				enterOuterAlt(_localctx, 2);
				{
				setState(70);
				match(Int);
				}
				break;
			case 3:
				_localctx = new EparenContext(_localctx);
				enterOuterAlt(_localctx, 3);
				{
				setState(71);
				match(T__1);
				setState(72);
				expr();
				setState(73);
				match(T__2);
				}
				break;
			case 4:
				_localctx = new EfuncContext(_localctx);
				enterOuterAlt(_localctx, 4);
				{
				setState(75);
				match(Ident);
				setState(76);
				match(T__1);
				setState(85);
				_la = _input.LA(1);
				if ((((_la) & ~0x3f) == 0 && ((1L << _la) & ((1L << T__1) | (1L << T__4) | (1L << T__5) | (1L << T__6) | (1L << T__7) | (1L << T__8) | (1L << T__9) | (1L << Ident) | (1L << Int))) != 0)) {
					{
					setState(77);
					expr();
					setState(82);
					_errHandler.sync(this);
					_la = _input.LA(1);
					while (_la==T__3) {
						{
						{
						setState(78);
						match(T__3);
						setState(79);
						expr();
						}
						}
						setState(84);
						_errHandler.sync(this);
						_la = _input.LA(1);
					}
					}
				}

				setState(87);
				match(T__2);
				}
				break;
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class PostFixExprContext extends ParserRuleContext {
		public PostFixExprContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_postFixExpr; }
	 
		public PostFixExprContext() { }
		public void copyFrom(PostFixExprContext ctx) {
			super.copyFrom(ctx);
		}
	}
	public static class EPostFixContext extends PostFixExprContext {
		public Token op;
		public PrimaryExprContext primaryExpr() {
			return getRuleContext(PrimaryExprContext.class,0);
		}
		public EPostFixContext(PostFixExprContext ctx) { copyFrom(ctx); }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof cListener ) ((cListener)listener).enterEPostFix(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof cListener ) ((cListener)listener).exitEPostFix(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof cVisitor ) return ((cVisitor<? extends T>)visitor).visitEPostFix(this);
			else return visitor.visitChildren(this);
		}
	}
	public static class EprimaryContext extends PostFixExprContext {
		public PrimaryExprContext primaryExpr() {
			return getRuleContext(PrimaryExprContext.class,0);
		}
		public EprimaryContext(PostFixExprContext ctx) { copyFrom(ctx); }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof cListener ) ((cListener)listener).enterEprimary(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof cListener ) ((cListener)listener).exitEprimary(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof cVisitor ) return ((cVisitor<? extends T>)visitor).visitEprimary(this);
			else return visitor.visitChildren(this);
		}
	}

	public final PostFixExprContext postFixExpr() throws RecognitionException {
		PostFixExprContext _localctx = new PostFixExprContext(_ctx, getState());
		enterRule(_localctx, 8, RULE_postFixExpr);
		int _la;
		try {
			setState(94);
			_errHandler.sync(this);
			switch ( getInterpreter().adaptivePredict(_input,5,_ctx) ) {
			case 1:
				_localctx = new EprimaryContext(_localctx);
				enterOuterAlt(_localctx, 1);
				{
				setState(90);
				primaryExpr();
				}
				break;
			case 2:
				_localctx = new EPostFixContext(_localctx);
				enterOuterAlt(_localctx, 2);
				{
				setState(91);
				primaryExpr();
				setState(92);
				((EPostFixContext)_localctx).op = _input.LT(1);
				_la = _input.LA(1);
				if ( !(_la==T__4 || _la==T__5) ) {
					((EPostFixContext)_localctx).op = (Token)_errHandler.recoverInline(this);
				} else {
					consume();
				}
				}
				break;
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class PreFixExprContext extends ParserRuleContext {
		public PreFixExprContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_preFixExpr; }
	 
		public PreFixExprContext() { }
		public void copyFrom(PreFixExprContext ctx) {
			super.copyFrom(ctx);
		}
	}
	public static class EpreContext extends PreFixExprContext {
		public Token prefix;
		public PostFixExprContext postFixExpr() {
			return getRuleContext(PostFixExprContext.class,0);
		}
		public EpreContext(PreFixExprContext ctx) { copyFrom(ctx); }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof cListener ) ((cListener)listener).enterEpre(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof cListener ) ((cListener)listener).exitEpre(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof cVisitor ) return ((cVisitor<? extends T>)visitor).visitEpre(this);
			else return visitor.visitChildren(this);
		}
	}
	public static class EPrePostContext extends PreFixExprContext {
		public PostFixExprContext postFixExpr() {
			return getRuleContext(PostFixExprContext.class,0);
		}
		public EPrePostContext(PreFixExprContext ctx) { copyFrom(ctx); }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof cListener ) ((cListener)listener).enterEPrePost(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof cListener ) ((cListener)listener).exitEPrePost(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof cVisitor ) return ((cVisitor<? extends T>)visitor).visitEPrePost(this);
			else return visitor.visitChildren(this);
		}
	}

	public final PreFixExprContext preFixExpr() throws RecognitionException {
		PreFixExprContext _localctx = new PreFixExprContext(_ctx, getState());
		enterRule(_localctx, 10, RULE_preFixExpr);
		int _la;
		try {
			setState(99);
			switch (_input.LA(1)) {
			case T__1:
			case T__9:
			case Ident:
			case Int:
				_localctx = new EPrePostContext(_localctx);
				enterOuterAlt(_localctx, 1);
				{
				setState(96);
				postFixExpr();
				}
				break;
			case T__4:
			case T__5:
			case T__6:
			case T__7:
			case T__8:
				_localctx = new EpreContext(_localctx);
				enterOuterAlt(_localctx, 2);
				{
				setState(97);
				((EpreContext)_localctx).prefix = _input.LT(1);
				_la = _input.LA(1);
				if ( !((((_la) & ~0x3f) == 0 && ((1L << _la) & ((1L << T__4) | (1L << T__5) | (1L << T__6) | (1L << T__7) | (1L << T__8))) != 0)) ) {
					((EpreContext)_localctx).prefix = (Token)_errHandler.recoverInline(this);
				} else {
					consume();
				}
				setState(98);
				postFixExpr();
				}
				break;
			default:
				throw new NoViableAltException(this);
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class MultiplicativeExprContext extends ParserRuleContext {
		public MultiplicativeExprContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_multiplicativeExpr; }
	 
		public MultiplicativeExprContext() { }
		public void copyFrom(MultiplicativeExprContext ctx) {
			super.copyFrom(ctx);
		}
	}
	public static class EMultiBinContext extends MultiplicativeExprContext {
		public Token op;
		public MultiplicativeExprContext multiplicativeExpr() {
			return getRuleContext(MultiplicativeExprContext.class,0);
		}
		public PreFixExprContext preFixExpr() {
			return getRuleContext(PreFixExprContext.class,0);
		}
		public EMultiBinContext(MultiplicativeExprContext ctx) { copyFrom(ctx); }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof cListener ) ((cListener)listener).enterEMultiBin(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof cListener ) ((cListener)listener).exitEMultiBin(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof cVisitor ) return ((cVisitor<? extends T>)visitor).visitEMultiBin(this);
			else return visitor.visitChildren(this);
		}
	}
	public static class EMultiPreContext extends MultiplicativeExprContext {
		public PreFixExprContext preFixExpr() {
			return getRuleContext(PreFixExprContext.class,0);
		}
		public EMultiPreContext(MultiplicativeExprContext ctx) { copyFrom(ctx); }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof cListener ) ((cListener)listener).enterEMultiPre(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof cListener ) ((cListener)listener).exitEMultiPre(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof cVisitor ) return ((cVisitor<? extends T>)visitor).visitEMultiPre(this);
			else return visitor.visitChildren(this);
		}
	}

	public final MultiplicativeExprContext multiplicativeExpr() throws RecognitionException {
		return multiplicativeExpr(0);
	}

	private MultiplicativeExprContext multiplicativeExpr(int _p) throws RecognitionException {
		ParserRuleContext _parentctx = _ctx;
		int _parentState = getState();
		MultiplicativeExprContext _localctx = new MultiplicativeExprContext(_ctx, _parentState);
		MultiplicativeExprContext _prevctx = _localctx;
		int _startState = 12;
		enterRecursionRule(_localctx, 12, RULE_multiplicativeExpr, _p);
		int _la;
		try {
			int _alt;
			enterOuterAlt(_localctx, 1);
			{
			{
			_localctx = new EMultiPreContext(_localctx);
			_ctx = _localctx;
			_prevctx = _localctx;

			setState(102);
			preFixExpr();
			}
			_ctx.stop = _input.LT(-1);
			setState(109);
			_errHandler.sync(this);
			_alt = getInterpreter().adaptivePredict(_input,7,_ctx);
			while ( _alt!=2 && _alt!=org.antlr.v4.runtime.atn.ATN.INVALID_ALT_NUMBER ) {
				if ( _alt==1 ) {
					if ( _parseListeners!=null ) triggerExitRuleEvent();
					_prevctx = _localctx;
					{
					{
					_localctx = new EMultiBinContext(new MultiplicativeExprContext(_parentctx, _parentState));
					pushNewRecursionContext(_localctx, _startState, RULE_multiplicativeExpr);
					setState(104);
					if (!(precpred(_ctx, 1))) throw new FailedPredicateException(this, "precpred(_ctx, 1)");
					setState(105);
					((EMultiBinContext)_localctx).op = _input.LT(1);
					_la = _input.LA(1);
					if ( !((((_la) & ~0x3f) == 0 && ((1L << _la) & ((1L << T__9) | (1L << T__10) | (1L << T__11))) != 0)) ) {
						((EMultiBinContext)_localctx).op = (Token)_errHandler.recoverInline(this);
					} else {
						consume();
					}
					setState(106);
					preFixExpr();
					}
					} 
				}
				setState(111);
				_errHandler.sync(this);
				_alt = getInterpreter().adaptivePredict(_input,7,_ctx);
			}
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			unrollRecursionContexts(_parentctx);
		}
		return _localctx;
	}

	public static class AdditiveExprContext extends ParserRuleContext {
		public AdditiveExprContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_additiveExpr; }
	 
		public AdditiveExprContext() { }
		public void copyFrom(AdditiveExprContext ctx) {
			super.copyFrom(ctx);
		}
	}
	public static class EAddMultContext extends AdditiveExprContext {
		public MultiplicativeExprContext multiplicativeExpr() {
			return getRuleContext(MultiplicativeExprContext.class,0);
		}
		public EAddMultContext(AdditiveExprContext ctx) { copyFrom(ctx); }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof cListener ) ((cListener)listener).enterEAddMult(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof cListener ) ((cListener)listener).exitEAddMult(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof cVisitor ) return ((cVisitor<? extends T>)visitor).visitEAddMult(this);
			else return visitor.visitChildren(this);
		}
	}
	public static class EAddBinContext extends AdditiveExprContext {
		public Token op;
		public AdditiveExprContext additiveExpr() {
			return getRuleContext(AdditiveExprContext.class,0);
		}
		public MultiplicativeExprContext multiplicativeExpr() {
			return getRuleContext(MultiplicativeExprContext.class,0);
		}
		public EAddBinContext(AdditiveExprContext ctx) { copyFrom(ctx); }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof cListener ) ((cListener)listener).enterEAddBin(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof cListener ) ((cListener)listener).exitEAddBin(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof cVisitor ) return ((cVisitor<? extends T>)visitor).visitEAddBin(this);
			else return visitor.visitChildren(this);
		}
	}

	public final AdditiveExprContext additiveExpr() throws RecognitionException {
		return additiveExpr(0);
	}

	private AdditiveExprContext additiveExpr(int _p) throws RecognitionException {
		ParserRuleContext _parentctx = _ctx;
		int _parentState = getState();
		AdditiveExprContext _localctx = new AdditiveExprContext(_ctx, _parentState);
		AdditiveExprContext _prevctx = _localctx;
		int _startState = 14;
		enterRecursionRule(_localctx, 14, RULE_additiveExpr, _p);
		int _la;
		try {
			int _alt;
			enterOuterAlt(_localctx, 1);
			{
			{
			_localctx = new EAddMultContext(_localctx);
			_ctx = _localctx;
			_prevctx = _localctx;

			setState(113);
			multiplicativeExpr(0);
			}
			_ctx.stop = _input.LT(-1);
			setState(120);
			_errHandler.sync(this);
			_alt = getInterpreter().adaptivePredict(_input,8,_ctx);
			while ( _alt!=2 && _alt!=org.antlr.v4.runtime.atn.ATN.INVALID_ALT_NUMBER ) {
				if ( _alt==1 ) {
					if ( _parseListeners!=null ) triggerExitRuleEvent();
					_prevctx = _localctx;
					{
					{
					_localctx = new EAddBinContext(new AdditiveExprContext(_parentctx, _parentState));
					pushNewRecursionContext(_localctx, _startState, RULE_additiveExpr);
					setState(115);
					if (!(precpred(_ctx, 1))) throw new FailedPredicateException(this, "precpred(_ctx, 1)");
					setState(116);
					((EAddBinContext)_localctx).op = _input.LT(1);
					_la = _input.LA(1);
					if ( !(_la==T__6 || _la==T__12) ) {
						((EAddBinContext)_localctx).op = (Token)_errHandler.recoverInline(this);
					} else {
						consume();
					}
					setState(117);
					multiplicativeExpr(0);
					}
					} 
				}
				setState(122);
				_errHandler.sync(this);
				_alt = getInterpreter().adaptivePredict(_input,8,_ctx);
			}
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			unrollRecursionContexts(_parentctx);
		}
		return _localctx;
	}

	public static class RelationalExprContext extends ParserRuleContext {
		public RelationalExprContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_relationalExpr; }
	 
		public RelationalExprContext() { }
		public void copyFrom(RelationalExprContext ctx) {
			super.copyFrom(ctx);
		}
	}
	public static class ERelBinContext extends RelationalExprContext {
		public Token op;
		public RelationalExprContext relationalExpr() {
			return getRuleContext(RelationalExprContext.class,0);
		}
		public AdditiveExprContext additiveExpr() {
			return getRuleContext(AdditiveExprContext.class,0);
		}
		public ERelBinContext(RelationalExprContext ctx) { copyFrom(ctx); }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof cListener ) ((cListener)listener).enterERelBin(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof cListener ) ((cListener)listener).exitERelBin(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof cVisitor ) return ((cVisitor<? extends T>)visitor).visitERelBin(this);
			else return visitor.visitChildren(this);
		}
	}
	public static class ERelAddContext extends RelationalExprContext {
		public AdditiveExprContext additiveExpr() {
			return getRuleContext(AdditiveExprContext.class,0);
		}
		public ERelAddContext(RelationalExprContext ctx) { copyFrom(ctx); }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof cListener ) ((cListener)listener).enterERelAdd(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof cListener ) ((cListener)listener).exitERelAdd(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof cVisitor ) return ((cVisitor<? extends T>)visitor).visitERelAdd(this);
			else return visitor.visitChildren(this);
		}
	}

	public final RelationalExprContext relationalExpr() throws RecognitionException {
		return relationalExpr(0);
	}

	private RelationalExprContext relationalExpr(int _p) throws RecognitionException {
		ParserRuleContext _parentctx = _ctx;
		int _parentState = getState();
		RelationalExprContext _localctx = new RelationalExprContext(_ctx, _parentState);
		RelationalExprContext _prevctx = _localctx;
		int _startState = 16;
		enterRecursionRule(_localctx, 16, RULE_relationalExpr, _p);
		int _la;
		try {
			int _alt;
			enterOuterAlt(_localctx, 1);
			{
			{
			_localctx = new ERelAddContext(_localctx);
			_ctx = _localctx;
			_prevctx = _localctx;

			setState(124);
			additiveExpr(0);
			}
			_ctx.stop = _input.LT(-1);
			setState(131);
			_errHandler.sync(this);
			_alt = getInterpreter().adaptivePredict(_input,9,_ctx);
			while ( _alt!=2 && _alt!=org.antlr.v4.runtime.atn.ATN.INVALID_ALT_NUMBER ) {
				if ( _alt==1 ) {
					if ( _parseListeners!=null ) triggerExitRuleEvent();
					_prevctx = _localctx;
					{
					{
					_localctx = new ERelBinContext(new RelationalExprContext(_parentctx, _parentState));
					pushNewRecursionContext(_localctx, _startState, RULE_relationalExpr);
					setState(126);
					if (!(precpred(_ctx, 1))) throw new FailedPredicateException(this, "precpred(_ctx, 1)");
					setState(127);
					((ERelBinContext)_localctx).op = _input.LT(1);
					_la = _input.LA(1);
					if ( !((((_la) & ~0x3f) == 0 && ((1L << _la) & ((1L << T__13) | (1L << T__14) | (1L << T__15) | (1L << T__16) | (1L << T__17) | (1L << T__18))) != 0)) ) {
						((ERelBinContext)_localctx).op = (Token)_errHandler.recoverInline(this);
					} else {
						consume();
					}
					setState(128);
					additiveExpr(0);
					}
					} 
				}
				setState(133);
				_errHandler.sync(this);
				_alt = getInterpreter().adaptivePredict(_input,9,_ctx);
			}
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			unrollRecursionContexts(_parentctx);
		}
		return _localctx;
	}

	public static class AndExprContext extends ParserRuleContext {
		public AndExprContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_andExpr; }
	 
		public AndExprContext() { }
		public void copyFrom(AndExprContext ctx) {
			super.copyFrom(ctx);
		}
	}
	public static class EAndBinContext extends AndExprContext {
		public AndExprContext andExpr() {
			return getRuleContext(AndExprContext.class,0);
		}
		public RelationalExprContext relationalExpr() {
			return getRuleContext(RelationalExprContext.class,0);
		}
		public EAndBinContext(AndExprContext ctx) { copyFrom(ctx); }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof cListener ) ((cListener)listener).enterEAndBin(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof cListener ) ((cListener)listener).exitEAndBin(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof cVisitor ) return ((cVisitor<? extends T>)visitor).visitEAndBin(this);
			else return visitor.visitChildren(this);
		}
	}
	public static class EAndRelContext extends AndExprContext {
		public RelationalExprContext relationalExpr() {
			return getRuleContext(RelationalExprContext.class,0);
		}
		public EAndRelContext(AndExprContext ctx) { copyFrom(ctx); }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof cListener ) ((cListener)listener).enterEAndRel(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof cListener ) ((cListener)listener).exitEAndRel(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof cVisitor ) return ((cVisitor<? extends T>)visitor).visitEAndRel(this);
			else return visitor.visitChildren(this);
		}
	}

	public final AndExprContext andExpr() throws RecognitionException {
		return andExpr(0);
	}

	private AndExprContext andExpr(int _p) throws RecognitionException {
		ParserRuleContext _parentctx = _ctx;
		int _parentState = getState();
		AndExprContext _localctx = new AndExprContext(_ctx, _parentState);
		AndExprContext _prevctx = _localctx;
		int _startState = 18;
		enterRecursionRule(_localctx, 18, RULE_andExpr, _p);
		try {
			int _alt;
			enterOuterAlt(_localctx, 1);
			{
			{
			_localctx = new EAndRelContext(_localctx);
			_ctx = _localctx;
			_prevctx = _localctx;

			setState(135);
			relationalExpr(0);
			}
			_ctx.stop = _input.LT(-1);
			setState(142);
			_errHandler.sync(this);
			_alt = getInterpreter().adaptivePredict(_input,10,_ctx);
			while ( _alt!=2 && _alt!=org.antlr.v4.runtime.atn.ATN.INVALID_ALT_NUMBER ) {
				if ( _alt==1 ) {
					if ( _parseListeners!=null ) triggerExitRuleEvent();
					_prevctx = _localctx;
					{
					{
					_localctx = new EAndBinContext(new AndExprContext(_parentctx, _parentState));
					pushNewRecursionContext(_localctx, _startState, RULE_andExpr);
					setState(137);
					if (!(precpred(_ctx, 1))) throw new FailedPredicateException(this, "precpred(_ctx, 1)");
					setState(138);
					match(T__19);
					setState(139);
					relationalExpr(0);
					}
					} 
				}
				setState(144);
				_errHandler.sync(this);
				_alt = getInterpreter().adaptivePredict(_input,10,_ctx);
			}
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			unrollRecursionContexts(_parentctx);
		}
		return _localctx;
	}

	public static class OrExprContext extends ParserRuleContext {
		public OrExprContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_orExpr; }
	 
		public OrExprContext() { }
		public void copyFrom(OrExprContext ctx) {
			super.copyFrom(ctx);
		}
	}
	public static class EAndOrContext extends OrExprContext {
		public AndExprContext andExpr() {
			return getRuleContext(AndExprContext.class,0);
		}
		public EAndOrContext(OrExprContext ctx) { copyFrom(ctx); }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof cListener ) ((cListener)listener).enterEAndOr(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof cListener ) ((cListener)listener).exitEAndOr(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof cVisitor ) return ((cVisitor<? extends T>)visitor).visitEAndOr(this);
			else return visitor.visitChildren(this);
		}
	}
	public static class EOrBinContext extends OrExprContext {
		public OrExprContext orExpr() {
			return getRuleContext(OrExprContext.class,0);
		}
		public AndExprContext andExpr() {
			return getRuleContext(AndExprContext.class,0);
		}
		public EOrBinContext(OrExprContext ctx) { copyFrom(ctx); }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof cListener ) ((cListener)listener).enterEOrBin(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof cListener ) ((cListener)listener).exitEOrBin(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof cVisitor ) return ((cVisitor<? extends T>)visitor).visitEOrBin(this);
			else return visitor.visitChildren(this);
		}
	}

	public final OrExprContext orExpr() throws RecognitionException {
		return orExpr(0);
	}

	private OrExprContext orExpr(int _p) throws RecognitionException {
		ParserRuleContext _parentctx = _ctx;
		int _parentState = getState();
		OrExprContext _localctx = new OrExprContext(_ctx, _parentState);
		OrExprContext _prevctx = _localctx;
		int _startState = 20;
		enterRecursionRule(_localctx, 20, RULE_orExpr, _p);
		try {
			int _alt;
			enterOuterAlt(_localctx, 1);
			{
			{
			_localctx = new EAndOrContext(_localctx);
			_ctx = _localctx;
			_prevctx = _localctx;

			setState(146);
			andExpr(0);
			}
			_ctx.stop = _input.LT(-1);
			setState(153);
			_errHandler.sync(this);
			_alt = getInterpreter().adaptivePredict(_input,11,_ctx);
			while ( _alt!=2 && _alt!=org.antlr.v4.runtime.atn.ATN.INVALID_ALT_NUMBER ) {
				if ( _alt==1 ) {
					if ( _parseListeners!=null ) triggerExitRuleEvent();
					_prevctx = _localctx;
					{
					{
					_localctx = new EOrBinContext(new OrExprContext(_parentctx, _parentState));
					pushNewRecursionContext(_localctx, _startState, RULE_orExpr);
					setState(148);
					if (!(precpred(_ctx, 1))) throw new FailedPredicateException(this, "precpred(_ctx, 1)");
					setState(149);
					match(T__20);
					setState(150);
					andExpr(0);
					}
					} 
				}
				setState(155);
				_errHandler.sync(this);
				_alt = getInterpreter().adaptivePredict(_input,11,_ctx);
			}
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			unrollRecursionContexts(_parentctx);
		}
		return _localctx;
	}

	public static class ExprContext extends ParserRuleContext {
		public ExprContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_expr; }
	 
		public ExprContext() { }
		public void copyFrom(ExprContext ctx) {
			super.copyFrom(ctx);
		}
	}
	public static class EOrExprContext extends ExprContext {
		public OrExprContext orExpr() {
			return getRuleContext(OrExprContext.class,0);
		}
		public EOrExprContext(ExprContext ctx) { copyFrom(ctx); }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof cListener ) ((cListener)listener).enterEOrExpr(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof cListener ) ((cListener)listener).exitEOrExpr(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof cVisitor ) return ((cVisitor<? extends T>)visitor).visitEOrExpr(this);
			else return visitor.visitChildren(this);
		}
	}
	public static class ESelectContext extends ExprContext {
		public List<OrExprContext> orExpr() {
			return getRuleContexts(OrExprContext.class);
		}
		public OrExprContext orExpr(int i) {
			return getRuleContext(OrExprContext.class,i);
		}
		public ESelectContext(ExprContext ctx) { copyFrom(ctx); }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof cListener ) ((cListener)listener).enterESelect(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof cListener ) ((cListener)listener).exitESelect(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof cVisitor ) return ((cVisitor<? extends T>)visitor).visitESelect(this);
			else return visitor.visitChildren(this);
		}
	}

	public final ExprContext expr() throws RecognitionException {
		ExprContext _localctx = new ExprContext(_ctx, getState());
		enterRule(_localctx, 22, RULE_expr);
		try {
			setState(163);
			_errHandler.sync(this);
			switch ( getInterpreter().adaptivePredict(_input,12,_ctx) ) {
			case 1:
				_localctx = new EOrExprContext(_localctx);
				enterOuterAlt(_localctx, 1);
				{
				setState(156);
				orExpr(0);
				}
				break;
			case 2:
				_localctx = new ESelectContext(_localctx);
				enterOuterAlt(_localctx, 2);
				{
				setState(157);
				orExpr(0);
				setState(158);
				match(T__21);
				setState(159);
				orExpr(0);
				setState(160);
				match(T__22);
				setState(161);
				orExpr(0);
				}
				break;
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class LvalContext extends ParserRuleContext {
		public LvalContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_lval; }
	 
		public LvalContext() { }
		public void copyFrom(LvalContext ctx) {
			super.copyFrom(ctx);
		}
	}
	public static class LindexContext extends LvalContext {
		public LvalContext lval() {
			return getRuleContext(LvalContext.class,0);
		}
		public ExprContext expr() {
			return getRuleContext(ExprContext.class,0);
		}
		public LindexContext(LvalContext ctx) { copyFrom(ctx); }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof cListener ) ((cListener)listener).enterLindex(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof cListener ) ((cListener)listener).exitLindex(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof cVisitor ) return ((cVisitor<? extends T>)visitor).visitLindex(this);
			else return visitor.visitChildren(this);
		}
	}
	public static class LidentContext extends LvalContext {
		public TerminalNode Ident() { return getToken(cParser.Ident, 0); }
		public LidentContext(LvalContext ctx) { copyFrom(ctx); }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof cListener ) ((cListener)listener).enterLident(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof cListener ) ((cListener)listener).exitLident(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof cVisitor ) return ((cVisitor<? extends T>)visitor).visitLident(this);
			else return visitor.visitChildren(this);
		}
	}
	public static class LderefContext extends LvalContext {
		public LvalContext lval() {
			return getRuleContext(LvalContext.class,0);
		}
		public LderefContext(LvalContext ctx) { copyFrom(ctx); }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof cListener ) ((cListener)listener).enterLderef(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof cListener ) ((cListener)listener).exitLderef(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof cVisitor ) return ((cVisitor<? extends T>)visitor).visitLderef(this);
			else return visitor.visitChildren(this);
		}
	}
	public static class LarrowContext extends LvalContext {
		public LvalContext lval() {
			return getRuleContext(LvalContext.class,0);
		}
		public TerminalNode Ident() { return getToken(cParser.Ident, 0); }
		public LarrowContext(LvalContext ctx) { copyFrom(ctx); }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof cListener ) ((cListener)listener).enterLarrow(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof cListener ) ((cListener)listener).exitLarrow(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof cVisitor ) return ((cVisitor<? extends T>)visitor).visitLarrow(this);
			else return visitor.visitChildren(this);
		}
	}
	public static class LparenContext extends LvalContext {
		public LvalContext lval() {
			return getRuleContext(LvalContext.class,0);
		}
		public LparenContext(LvalContext ctx) { copyFrom(ctx); }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof cListener ) ((cListener)listener).enterLparen(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof cListener ) ((cListener)listener).exitLparen(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof cVisitor ) return ((cVisitor<? extends T>)visitor).visitLparen(this);
			else return visitor.visitChildren(this);
		}
	}
	public static class LdotContext extends LvalContext {
		public LvalContext lval() {
			return getRuleContext(LvalContext.class,0);
		}
		public TerminalNode Ident() { return getToken(cParser.Ident, 0); }
		public LdotContext(LvalContext ctx) { copyFrom(ctx); }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof cListener ) ((cListener)listener).enterLdot(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof cListener ) ((cListener)listener).exitLdot(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof cVisitor ) return ((cVisitor<? extends T>)visitor).visitLdot(this);
			else return visitor.visitChildren(this);
		}
	}

	public final LvalContext lval() throws RecognitionException {
		return lval(0);
	}

	private LvalContext lval(int _p) throws RecognitionException {
		ParserRuleContext _parentctx = _ctx;
		int _parentState = getState();
		LvalContext _localctx = new LvalContext(_ctx, _parentState);
		LvalContext _prevctx = _localctx;
		int _startState = 24;
		enterRecursionRule(_localctx, 24, RULE_lval, _p);
		try {
			int _alt;
			enterOuterAlt(_localctx, 1);
			{
			setState(173);
			switch (_input.LA(1)) {
			case Ident:
				{
				_localctx = new LidentContext(_localctx);
				_ctx = _localctx;
				_prevctx = _localctx;

				setState(166);
				match(Ident);
				}
				break;
			case T__1:
				{
				_localctx = new LparenContext(_localctx);
				_ctx = _localctx;
				_prevctx = _localctx;
				setState(167);
				match(T__1);
				setState(168);
				lval(0);
				setState(169);
				match(T__2);
				}
				break;
			case T__9:
				{
				_localctx = new LderefContext(_localctx);
				_ctx = _localctx;
				_prevctx = _localctx;
				setState(171);
				match(T__9);
				setState(172);
				lval(1);
				}
				break;
			default:
				throw new NoViableAltException(this);
			}
			_ctx.stop = _input.LT(-1);
			setState(188);
			_errHandler.sync(this);
			_alt = getInterpreter().adaptivePredict(_input,15,_ctx);
			while ( _alt!=2 && _alt!=org.antlr.v4.runtime.atn.ATN.INVALID_ALT_NUMBER ) {
				if ( _alt==1 ) {
					if ( _parseListeners!=null ) triggerExitRuleEvent();
					_prevctx = _localctx;
					{
					setState(186);
					_errHandler.sync(this);
					switch ( getInterpreter().adaptivePredict(_input,14,_ctx) ) {
					case 1:
						{
						_localctx = new LarrowContext(new LvalContext(_parentctx, _parentState));
						pushNewRecursionContext(_localctx, _startState, RULE_lval);
						setState(175);
						if (!(precpred(_ctx, 5))) throw new FailedPredicateException(this, "precpred(_ctx, 5)");
						setState(176);
						match(T__23);
						setState(177);
						match(Ident);
						}
						break;
					case 2:
						{
						_localctx = new LdotContext(new LvalContext(_parentctx, _parentState));
						pushNewRecursionContext(_localctx, _startState, RULE_lval);
						setState(178);
						if (!(precpred(_ctx, 3))) throw new FailedPredicateException(this, "precpred(_ctx, 3)");
						setState(179);
						match(T__24);
						setState(180);
						match(Ident);
						}
						break;
					case 3:
						{
						_localctx = new LindexContext(new LvalContext(_parentctx, _parentState));
						pushNewRecursionContext(_localctx, _startState, RULE_lval);
						setState(181);
						if (!(precpred(_ctx, 2))) throw new FailedPredicateException(this, "precpred(_ctx, 2)");
						setState(182);
						match(T__25);
						setState(183);
						expr();
						setState(184);
						match(T__26);
						}
						break;
					}
					} 
				}
				setState(190);
				_errHandler.sync(this);
				_alt = getInterpreter().adaptivePredict(_input,15,_ctx);
			}
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			unrollRecursionContexts(_parentctx);
		}
		return _localctx;
	}

	public static class NonPtrTypeContext extends ParserRuleContext {
		public NonPtrTypeContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_nonPtrType; }
	 
		public NonPtrTypeContext() { }
		public void copyFrom(NonPtrTypeContext ctx) {
			super.copyFrom(ctx);
		}
	}
	public static class StructTypeContext extends NonPtrTypeContext {
		public TerminalNode Ident() { return getToken(cParser.Ident, 0); }
		public StructTypeContext(NonPtrTypeContext ctx) { copyFrom(ctx); }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof cListener ) ((cListener)listener).enterStructType(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof cListener ) ((cListener)listener).exitStructType(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof cVisitor ) return ((cVisitor<? extends T>)visitor).visitStructType(this);
			else return visitor.visitChildren(this);
		}
	}
	public static class IntTypeContext extends NonPtrTypeContext {
		public IntTypeContext(NonPtrTypeContext ctx) { copyFrom(ctx); }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof cListener ) ((cListener)listener).enterIntType(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof cListener ) ((cListener)listener).exitIntType(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof cVisitor ) return ((cVisitor<? extends T>)visitor).visitIntType(this);
			else return visitor.visitChildren(this);
		}
	}
	public static class VoidTypeContext extends NonPtrTypeContext {
		public VoidTypeContext(NonPtrTypeContext ctx) { copyFrom(ctx); }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof cListener ) ((cListener)listener).enterVoidType(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof cListener ) ((cListener)listener).exitVoidType(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof cVisitor ) return ((cVisitor<? extends T>)visitor).visitVoidType(this);
			else return visitor.visitChildren(this);
		}
	}

	public final NonPtrTypeContext nonPtrType() throws RecognitionException {
		NonPtrTypeContext _localctx = new NonPtrTypeContext(_ctx, getState());
		enterRule(_localctx, 26, RULE_nonPtrType);
		try {
			setState(195);
			switch (_input.LA(1)) {
			case T__27:
				_localctx = new IntTypeContext(_localctx);
				enterOuterAlt(_localctx, 1);
				{
				setState(191);
				match(T__27);
				}
				break;
			case T__28:
				_localctx = new VoidTypeContext(_localctx);
				enterOuterAlt(_localctx, 2);
				{
				setState(192);
				match(T__28);
				}
				break;
			case T__29:
				_localctx = new StructTypeContext(_localctx);
				enterOuterAlt(_localctx, 3);
				{
				setState(193);
				match(T__29);
				setState(194);
				match(Ident);
				}
				break;
			default:
				throw new NoViableAltException(this);
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class TypeContext extends ParserRuleContext {
		public TypeContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_type; }
	 
		public TypeContext() { }
		public void copyFrom(TypeContext ctx) {
			super.copyFrom(ctx);
		}
	}
	public static class PointerTypeContext extends TypeContext {
		public NonPtrTypeContext nonPtrType() {
			return getRuleContext(NonPtrTypeContext.class,0);
		}
		public PointerTypeContext(TypeContext ctx) { copyFrom(ctx); }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof cListener ) ((cListener)listener).enterPointerType(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof cListener ) ((cListener)listener).exitPointerType(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof cVisitor ) return ((cVisitor<? extends T>)visitor).visitPointerType(this);
			else return visitor.visitChildren(this);
		}
	}
	public static class NonPointerTypeContext extends TypeContext {
		public NonPtrTypeContext nonPtrType() {
			return getRuleContext(NonPtrTypeContext.class,0);
		}
		public NonPointerTypeContext(TypeContext ctx) { copyFrom(ctx); }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof cListener ) ((cListener)listener).enterNonPointerType(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof cListener ) ((cListener)listener).exitNonPointerType(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof cVisitor ) return ((cVisitor<? extends T>)visitor).visitNonPointerType(this);
			else return visitor.visitChildren(this);
		}
	}

	public final TypeContext type() throws RecognitionException {
		TypeContext _localctx = new TypeContext(_ctx, getState());
		enterRule(_localctx, 28, RULE_type);
		try {
			setState(201);
			_errHandler.sync(this);
			switch ( getInterpreter().adaptivePredict(_input,17,_ctx) ) {
			case 1:
				_localctx = new NonPointerTypeContext(_localctx);
				enterOuterAlt(_localctx, 1);
				{
				setState(197);
				nonPtrType();
				}
				break;
			case 2:
				_localctx = new PointerTypeContext(_localctx);
				enterOuterAlt(_localctx, 2);
				{
				setState(198);
				nonPtrType();
				setState(199);
				match(T__9);
				}
				break;
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class InputListContext extends ParserRuleContext {
		public InputListContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_inputList; }
	 
		public InputListContext() { }
		public void copyFrom(InputListContext ctx) {
			super.copyFrom(ctx);
		}
	}
	public static class InputListEmptyContext extends InputListContext {
		public InputListEmptyContext(InputListContext ctx) { copyFrom(ctx); }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof cListener ) ((cListener)listener).enterInputListEmpty(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof cListener ) ((cListener)listener).exitInputListEmpty(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof cVisitor ) return ((cVisitor<? extends T>)visitor).visitInputListEmpty(this);
			else return visitor.visitChildren(this);
		}
	}
	public static class InputListConsContext extends InputListContext {
		public TypeContext type() {
			return getRuleContext(TypeContext.class,0);
		}
		public TerminalNode Ident() { return getToken(cParser.Ident, 0); }
		public InputListContext inputList() {
			return getRuleContext(InputListContext.class,0);
		}
		public InputListConsContext(InputListContext ctx) { copyFrom(ctx); }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof cListener ) ((cListener)listener).enterInputListCons(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof cListener ) ((cListener)listener).exitInputListCons(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof cVisitor ) return ((cVisitor<? extends T>)visitor).visitInputListCons(this);
			else return visitor.visitChildren(this);
		}
	}
	public static class InputListSingletonContext extends InputListContext {
		public TypeContext type() {
			return getRuleContext(TypeContext.class,0);
		}
		public TerminalNode Ident() { return getToken(cParser.Ident, 0); }
		public InputListSingletonContext(InputListContext ctx) { copyFrom(ctx); }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof cListener ) ((cListener)listener).enterInputListSingleton(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof cListener ) ((cListener)listener).exitInputListSingleton(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof cVisitor ) return ((cVisitor<? extends T>)visitor).visitInputListSingleton(this);
			else return visitor.visitChildren(this);
		}
	}

	public final InputListContext inputList() throws RecognitionException {
		InputListContext _localctx = new InputListContext(_ctx, getState());
		enterRule(_localctx, 30, RULE_inputList);
		try {
			setState(212);
			_errHandler.sync(this);
			switch ( getInterpreter().adaptivePredict(_input,18,_ctx) ) {
			case 1:
				_localctx = new InputListConsContext(_localctx);
				enterOuterAlt(_localctx, 1);
				{
				setState(203);
				type();
				setState(204);
				match(Ident);
				setState(205);
				match(T__3);
				setState(206);
				inputList();
				}
				break;
			case 2:
				_localctx = new InputListSingletonContext(_localctx);
				enterOuterAlt(_localctx, 2);
				{
				setState(208);
				type();
				setState(209);
				match(Ident);
				}
				break;
			case 3:
				_localctx = new InputListEmptyContext(_localctx);
				enterOuterAlt(_localctx, 3);
				{
				}
				break;
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class StatementsContext extends ParserRuleContext {
		public StatementsContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_statements; }
	 
		public StatementsContext() { }
		public void copyFrom(StatementsContext ctx) {
			super.copyFrom(ctx);
		}
	}
	public static class StmtsConsContext extends StatementsContext {
		public StatementContext statement() {
			return getRuleContext(StatementContext.class,0);
		}
		public StatementsContext statements() {
			return getRuleContext(StatementsContext.class,0);
		}
		public StmtsConsContext(StatementsContext ctx) { copyFrom(ctx); }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof cListener ) ((cListener)listener).enterStmtsCons(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof cListener ) ((cListener)listener).exitStmtsCons(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof cVisitor ) return ((cVisitor<? extends T>)visitor).visitStmtsCons(this);
			else return visitor.visitChildren(this);
		}
	}
	public static class StmtsEmptyContext extends StatementsContext {
		public StmtsEmptyContext(StatementsContext ctx) { copyFrom(ctx); }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof cListener ) ((cListener)listener).enterStmtsEmpty(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof cListener ) ((cListener)listener).exitStmtsEmpty(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof cVisitor ) return ((cVisitor<? extends T>)visitor).visitStmtsEmpty(this);
			else return visitor.visitChildren(this);
		}
	}

	public final StatementsContext statements() throws RecognitionException {
		StatementsContext _localctx = new StatementsContext(_ctx, getState());
		enterRule(_localctx, 32, RULE_statements);
		try {
			setState(218);
			switch (_input.LA(1)) {
			case T__1:
			case T__4:
			case T__5:
			case T__6:
			case T__7:
			case T__8:
			case T__9:
			case T__27:
			case T__28:
			case T__29:
			case T__36:
			case T__37:
			case T__39:
			case T__41:
			case T__42:
			case T__44:
			case T__45:
			case T__46:
			case T__47:
			case T__48:
			case T__49:
			case T__50:
			case T__51:
			case Ident:
			case Int:
				_localctx = new StmtsConsContext(_localctx);
				enterOuterAlt(_localctx, 1);
				{
				setState(214);
				statement();
				setState(215);
				statements();
				}
				break;
			case T__38:
				_localctx = new StmtsEmptyContext(_localctx);
				enterOuterAlt(_localctx, 2);
				{
				}
				break;
			default:
				throw new NoViableAltException(this);
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class DecSingleContext extends ParserRuleContext {
		public DecSingleContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_decSingle; }
	 
		public DecSingleContext() { }
		public void copyFrom(DecSingleContext ctx) {
			super.copyFrom(ctx);
		}
	}
	public static class DecASingleContext extends DecSingleContext {
		public TerminalNode Ident() { return getToken(cParser.Ident, 0); }
		public ExprContext expr() {
			return getRuleContext(ExprContext.class,0);
		}
		public DecASingleContext(DecSingleContext ctx) { copyFrom(ctx); }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof cListener ) ((cListener)listener).enterDecASingle(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof cListener ) ((cListener)listener).exitDecASingle(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof cVisitor ) return ((cVisitor<? extends T>)visitor).visitDecASingle(this);
			else return visitor.visitChildren(this);
		}
	}
	public static class DecUSingleContext extends DecSingleContext {
		public TerminalNode Ident() { return getToken(cParser.Ident, 0); }
		public DecUSingleContext(DecSingleContext ctx) { copyFrom(ctx); }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof cListener ) ((cListener)listener).enterDecUSingle(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof cListener ) ((cListener)listener).exitDecUSingle(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof cVisitor ) return ((cVisitor<? extends T>)visitor).visitDecUSingle(this);
			else return visitor.visitChildren(this);
		}
	}

	public final DecSingleContext decSingle() throws RecognitionException {
		DecSingleContext _localctx = new DecSingleContext(_ctx, getState());
		enterRule(_localctx, 34, RULE_decSingle);
		try {
			setState(224);
			_errHandler.sync(this);
			switch ( getInterpreter().adaptivePredict(_input,20,_ctx) ) {
			case 1:
				_localctx = new DecUSingleContext(_localctx);
				enterOuterAlt(_localctx, 1);
				{
				setState(220);
				match(Ident);
				}
				break;
			case 2:
				_localctx = new DecASingleContext(_localctx);
				enterOuterAlt(_localctx, 2);
				{
				setState(221);
				match(Ident);
				setState(222);
				match(T__30);
				setState(223);
				expr();
				}
				break;
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class DeclistContext extends ParserRuleContext {
		public DeclistContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_declist; }
	 
		public DeclistContext() { }
		public void copyFrom(DeclistContext ctx) {
			super.copyFrom(ctx);
		}
	}
	public static class DeclSingleContext extends DeclistContext {
		public DecSingleContext decSingle() {
			return getRuleContext(DecSingleContext.class,0);
		}
		public DeclSingleContext(DeclistContext ctx) { copyFrom(ctx); }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof cListener ) ((cListener)listener).enterDeclSingle(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof cListener ) ((cListener)listener).exitDeclSingle(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof cVisitor ) return ((cVisitor<? extends T>)visitor).visitDeclSingle(this);
			else return visitor.visitChildren(this);
		}
	}
	public static class DeclConsContext extends DeclistContext {
		public DecSingleContext decSingle() {
			return getRuleContext(DecSingleContext.class,0);
		}
		public DeclistContext declist() {
			return getRuleContext(DeclistContext.class,0);
		}
		public DeclConsContext(DeclistContext ctx) { copyFrom(ctx); }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof cListener ) ((cListener)listener).enterDeclCons(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof cListener ) ((cListener)listener).exitDeclCons(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof cVisitor ) return ((cVisitor<? extends T>)visitor).visitDeclCons(this);
			else return visitor.visitChildren(this);
		}
	}

	public final DeclistContext declist() throws RecognitionException {
		DeclistContext _localctx = new DeclistContext(_ctx, getState());
		enterRule(_localctx, 36, RULE_declist);
		try {
			setState(231);
			_errHandler.sync(this);
			switch ( getInterpreter().adaptivePredict(_input,21,_ctx) ) {
			case 1:
				_localctx = new DeclSingleContext(_localctx);
				enterOuterAlt(_localctx, 1);
				{
				setState(226);
				decSingle();
				}
				break;
			case 2:
				_localctx = new DeclConsContext(_localctx);
				enterOuterAlt(_localctx, 2);
				{
				setState(227);
				decSingle();
				setState(228);
				match(T__3);
				setState(229);
				declist();
				}
				break;
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class SimpleStatementContext extends ParserRuleContext {
		public SimpleStatementContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_simpleStatement; }
	 
		public SimpleStatementContext() { }
		public void copyFrom(SimpleStatementContext ctx) {
			super.copyFrom(ctx);
		}
	}
	public static class BlankStmtContext extends SimpleStatementContext {
		public BlankStmtContext(SimpleStatementContext ctx) { copyFrom(ctx); }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof cListener ) ((cListener)listener).enterBlankStmt(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof cListener ) ((cListener)listener).exitBlankStmt(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof cVisitor ) return ((cVisitor<? extends T>)visitor).visitBlankStmt(this);
			else return visitor.visitChildren(this);
		}
	}
	public static class ExprStmtContext extends SimpleStatementContext {
		public ExprContext expr() {
			return getRuleContext(ExprContext.class,0);
		}
		public ExprStmtContext(SimpleStatementContext ctx) { copyFrom(ctx); }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof cListener ) ((cListener)listener).enterExprStmt(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof cListener ) ((cListener)listener).exitExprStmt(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof cVisitor ) return ((cVisitor<? extends T>)visitor).visitExprStmt(this);
			else return visitor.visitChildren(this);
		}
	}
	public static class AssnStmtContext extends SimpleStatementContext {
		public LvalContext lval() {
			return getRuleContext(LvalContext.class,0);
		}
		public ExprContext expr() {
			return getRuleContext(ExprContext.class,0);
		}
		public AssnStmtContext(SimpleStatementContext ctx) { copyFrom(ctx); }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof cListener ) ((cListener)listener).enterAssnStmt(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof cListener ) ((cListener)listener).exitAssnStmt(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof cVisitor ) return ((cVisitor<? extends T>)visitor).visitAssnStmt(this);
			else return visitor.visitChildren(this);
		}
	}
	public static class ArithAssnStmtContext extends SimpleStatementContext {
		public Token op;
		public LvalContext lval() {
			return getRuleContext(LvalContext.class,0);
		}
		public ExprContext expr() {
			return getRuleContext(ExprContext.class,0);
		}
		public ArithAssnStmtContext(SimpleStatementContext ctx) { copyFrom(ctx); }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof cListener ) ((cListener)listener).enterArithAssnStmt(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof cListener ) ((cListener)listener).exitArithAssnStmt(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof cVisitor ) return ((cVisitor<? extends T>)visitor).visitArithAssnStmt(this);
			else return visitor.visitChildren(this);
		}
	}

	public final SimpleStatementContext simpleStatement() throws RecognitionException {
		SimpleStatementContext _localctx = new SimpleStatementContext(_ctx, getState());
		enterRule(_localctx, 38, RULE_simpleStatement);
		int _la;
		try {
			setState(243);
			_errHandler.sync(this);
			switch ( getInterpreter().adaptivePredict(_input,22,_ctx) ) {
			case 1:
				_localctx = new BlankStmtContext(_localctx);
				enterOuterAlt(_localctx, 1);
				{
				}
				break;
			case 2:
				_localctx = new AssnStmtContext(_localctx);
				enterOuterAlt(_localctx, 2);
				{
				setState(234);
				lval(0);
				setState(235);
				match(T__30);
				setState(236);
				expr();
				}
				break;
			case 3:
				_localctx = new ArithAssnStmtContext(_localctx);
				enterOuterAlt(_localctx, 3);
				{
				setState(238);
				lval(0);
				setState(239);
				((ArithAssnStmtContext)_localctx).op = _input.LT(1);
				_la = _input.LA(1);
				if ( !((((_la) & ~0x3f) == 0 && ((1L << _la) & ((1L << T__31) | (1L << T__32) | (1L << T__33) | (1L << T__34) | (1L << T__35))) != 0)) ) {
					((ArithAssnStmtContext)_localctx).op = (Token)_errHandler.recoverInline(this);
				} else {
					consume();
				}
				setState(240);
				expr();
				}
				break;
			case 4:
				_localctx = new ExprStmtContext(_localctx);
				enterOuterAlt(_localctx, 4);
				{
				setState(242);
				expr();
				}
				break;
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class LvalsContext extends ParserRuleContext {
		public LvalsContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_lvals; }
	 
		public LvalsContext() { }
		public void copyFrom(LvalsContext ctx) {
			super.copyFrom(ctx);
		}
	}
	public static class LvalConsContext extends LvalsContext {
		public LvalContext lval() {
			return getRuleContext(LvalContext.class,0);
		}
		public LvalsContext lvals() {
			return getRuleContext(LvalsContext.class,0);
		}
		public LvalConsContext(LvalsContext ctx) { copyFrom(ctx); }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof cListener ) ((cListener)listener).enterLvalCons(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof cListener ) ((cListener)listener).exitLvalCons(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof cVisitor ) return ((cVisitor<? extends T>)visitor).visitLvalCons(this);
			else return visitor.visitChildren(this);
		}
	}
	public static class LvalHeadContext extends LvalsContext {
		public LvalContext lval() {
			return getRuleContext(LvalContext.class,0);
		}
		public LvalHeadContext(LvalsContext ctx) { copyFrom(ctx); }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof cListener ) ((cListener)listener).enterLvalHead(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof cListener ) ((cListener)listener).exitLvalHead(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof cVisitor ) return ((cVisitor<? extends T>)visitor).visitLvalHead(this);
			else return visitor.visitChildren(this);
		}
	}

	public final LvalsContext lvals() throws RecognitionException {
		LvalsContext _localctx = new LvalsContext(_ctx, getState());
		enterRule(_localctx, 40, RULE_lvals);
		try {
			setState(249);
			_errHandler.sync(this);
			switch ( getInterpreter().adaptivePredict(_input,23,_ctx) ) {
			case 1:
				_localctx = new LvalHeadContext(_localctx);
				enterOuterAlt(_localctx, 1);
				{
				setState(245);
				lval(0);
				}
				break;
			case 2:
				_localctx = new LvalConsContext(_localctx);
				enterOuterAlt(_localctx, 2);
				{
				setState(246);
				lval(0);
				setState(247);
				lvals();
				}
				break;
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class StatementContext extends ParserRuleContext {
		public StatementContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_statement; }
	 
		public StatementContext() { }
		public void copyFrom(StatementContext ctx) {
			super.copyFrom(ctx);
		}
	}
	public static class RetStmtContext extends StatementContext {
		public ExprContext expr() {
			return getRuleContext(ExprContext.class,0);
		}
		public RetStmtContext(StatementContext ctx) { copyFrom(ctx); }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof cListener ) ((cListener)listener).enterRetStmt(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof cListener ) ((cListener)listener).exitRetStmt(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof cVisitor ) return ((cVisitor<? extends T>)visitor).visitRetStmt(this);
			else return visitor.visitChildren(this);
		}
	}
	public static class DecArrayIntContext extends StatementContext {
		public TypeContext type() {
			return getRuleContext(TypeContext.class,0);
		}
		public TerminalNode Ident() { return getToken(cParser.Ident, 0); }
		public TerminalNode Int() { return getToken(cParser.Int, 0); }
		public DecArrayIntContext(StatementContext ctx) { copyFrom(ctx); }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof cListener ) ((cListener)listener).enterDecArrayInt(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof cListener ) ((cListener)listener).exitDecArrayInt(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof cVisitor ) return ((cVisitor<? extends T>)visitor).visitDecArrayInt(this);
			else return visitor.visitChildren(this);
		}
	}
	public static class DecStmtContext extends StatementContext {
		public TypeContext type() {
			return getRuleContext(TypeContext.class,0);
		}
		public DeclistContext declist() {
			return getRuleContext(DeclistContext.class,0);
		}
		public DecStmtContext(StatementContext ctx) { copyFrom(ctx); }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof cListener ) ((cListener)listener).enterDecStmt(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof cListener ) ((cListener)listener).exitDecStmt(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof cVisitor ) return ((cVisitor<? extends T>)visitor).visitDecStmt(this);
			else return visitor.visitChildren(this);
		}
	}
	public static class WhileContext extends StatementContext {
		public ExprContext expr() {
			return getRuleContext(ExprContext.class,0);
		}
		public StatementsContext statements() {
			return getRuleContext(StatementsContext.class,0);
		}
		public WhileContext(StatementContext ctx) { copyFrom(ctx); }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof cListener ) ((cListener)listener).enterWhile(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof cListener ) ((cListener)listener).exitWhile(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof cVisitor ) return ((cVisitor<? extends T>)visitor).visitWhile(this);
			else return visitor.visitChildren(this);
		}
	}
	public static class OforLoopContext extends StatementContext {
		public TerminalNode Int() { return getToken(cParser.Int, 0); }
		public List<SimpleStatementContext> simpleStatement() {
			return getRuleContexts(SimpleStatementContext.class);
		}
		public SimpleStatementContext simpleStatement(int i) {
			return getRuleContext(SimpleStatementContext.class,i);
		}
		public ExprContext expr() {
			return getRuleContext(ExprContext.class,0);
		}
		public StatementsContext statements() {
			return getRuleContext(StatementsContext.class,0);
		}
		public OforLoopContext(StatementContext ctx) { copyFrom(ctx); }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof cListener ) ((cListener)listener).enterOforLoop(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof cListener ) ((cListener)listener).exitOforLoop(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof cVisitor ) return ((cVisitor<? extends T>)visitor).visitOforLoop(this);
			else return visitor.visitChildren(this);
		}
	}
	public static class SoutContext extends StatementContext {
		public StatementsContext statements() {
			return getRuleContext(StatementsContext.class,0);
		}
		public SoutContext(StatementContext ctx) { copyFrom(ctx); }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof cListener ) ((cListener)listener).enterSout(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof cListener ) ((cListener)listener).exitSout(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof cVisitor ) return ((cVisitor<? extends T>)visitor).visitSout(this);
			else return visitor.visitChildren(this);
		}
	}
	public static class RetStmtNoneContext extends StatementContext {
		public RetStmtNoneContext(StatementContext ctx) { copyFrom(ctx); }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof cListener ) ((cListener)listener).enterRetStmtNone(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof cListener ) ((cListener)listener).exitRetStmtNone(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof cVisitor ) return ((cVisitor<? extends T>)visitor).visitRetStmtNone(this);
			else return visitor.visitChildren(this);
		}
	}
	public static class EndStmtContext extends StatementContext {
		public EndStmtContext(StatementContext ctx) { copyFrom(ctx); }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof cListener ) ((cListener)listener).enterEndStmt(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof cListener ) ((cListener)listener).exitEndStmt(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof cVisitor ) return ((cVisitor<? extends T>)visitor).visitEndStmt(this);
			else return visitor.visitChildren(this);
		}
	}
	public static class SimpleStmtContext extends StatementContext {
		public SimpleStatementContext simpleStatement() {
			return getRuleContext(SimpleStatementContext.class,0);
		}
		public SimpleStmtContext(StatementContext ctx) { copyFrom(ctx); }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof cListener ) ((cListener)listener).enterSimpleStmt(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof cListener ) ((cListener)listener).exitSimpleStmt(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof cVisitor ) return ((cVisitor<? extends T>)visitor).visitSimpleStmt(this);
			else return visitor.visitChildren(this);
		}
	}
	public static class IfSingleContext extends StatementContext {
		public ExprContext expr() {
			return getRuleContext(ExprContext.class,0);
		}
		public StatementsContext statements() {
			return getRuleContext(StatementsContext.class,0);
		}
		public IfSingleContext(StatementContext ctx) { copyFrom(ctx); }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof cListener ) ((cListener)listener).enterIfSingle(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof cListener ) ((cListener)listener).exitIfSingle(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof cVisitor ) return ((cVisitor<? extends T>)visitor).visitIfSingle(this);
			else return visitor.visitChildren(this);
		}
	}
	public static class BreakStmtContext extends StatementContext {
		public BreakStmtContext(StatementContext ctx) { copyFrom(ctx); }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof cListener ) ((cListener)listener).enterBreakStmt(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof cListener ) ((cListener)listener).exitBreakStmt(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof cVisitor ) return ((cVisitor<? extends T>)visitor).visitBreakStmt(this);
			else return visitor.visitChildren(this);
		}
	}
	public static class DecSingleDefContext extends StatementContext {
		public SingleDefContext singleDef() {
			return getRuleContext(SingleDefContext.class,0);
		}
		public DecSingleDefContext(StatementContext ctx) { copyFrom(ctx); }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof cListener ) ((cListener)listener).enterDecSingleDef(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof cListener ) ((cListener)listener).exitDecSingleDef(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof cVisitor ) return ((cVisitor<? extends T>)visitor).visitDecSingleDef(this);
			else return visitor.visitChildren(this);
		}
	}
	public static class GroupStmtContext extends StatementContext {
		public StatementsContext statements() {
			return getRuleContext(StatementsContext.class,0);
		}
		public GroupStmtContext(StatementContext ctx) { copyFrom(ctx); }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof cListener ) ((cListener)listener).enterGroupStmt(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof cListener ) ((cListener)listener).exitGroupStmt(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof cVisitor ) return ((cVisitor<? extends T>)visitor).visitGroupStmt(this);
			else return visitor.visitChildren(this);
		}
	}
	public static class DecArrayIdentContext extends StatementContext {
		public TypeContext type() {
			return getRuleContext(TypeContext.class,0);
		}
		public List<TerminalNode> Ident() { return getTokens(cParser.Ident); }
		public TerminalNode Ident(int i) {
			return getToken(cParser.Ident, i);
		}
		public DecArrayIdentContext(StatementContext ctx) { copyFrom(ctx); }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof cListener ) ((cListener)listener).enterDecArrayIdent(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof cListener ) ((cListener)listener).exitDecArrayIdent(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof cVisitor ) return ((cVisitor<? extends T>)visitor).visitDecArrayIdent(this);
			else return visitor.visitChildren(this);
		}
	}
	public static class ContinueStmtContext extends StatementContext {
		public ContinueStmtContext(StatementContext ctx) { copyFrom(ctx); }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof cListener ) ((cListener)listener).enterContinueStmt(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof cListener ) ((cListener)listener).exitContinueStmt(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof cVisitor ) return ((cVisitor<? extends T>)visitor).visitContinueStmt(this);
			else return visitor.visitChildren(this);
		}
	}
	public static class BeginStmtContext extends StatementContext {
		public BeginStmtContext(StatementContext ctx) { copyFrom(ctx); }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof cListener ) ((cListener)listener).enterBeginStmt(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof cListener ) ((cListener)listener).exitBeginStmt(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof cVisitor ) return ((cVisitor<? extends T>)visitor).visitBeginStmt(this);
			else return visitor.visitChildren(this);
		}
	}
	public static class IfElseContext extends StatementContext {
		public ExprContext expr() {
			return getRuleContext(ExprContext.class,0);
		}
		public List<StatementsContext> statements() {
			return getRuleContexts(StatementsContext.class);
		}
		public StatementsContext statements(int i) {
			return getRuleContext(StatementsContext.class,i);
		}
		public IfElseContext(StatementContext ctx) { copyFrom(ctx); }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof cListener ) ((cListener)listener).enterIfElse(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof cListener ) ((cListener)listener).exitIfElse(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof cVisitor ) return ((cVisitor<? extends T>)visitor).visitIfElse(this);
			else return visitor.visitChildren(this);
		}
	}
	public static class ForLoopContext extends StatementContext {
		public List<SimpleStatementContext> simpleStatement() {
			return getRuleContexts(SimpleStatementContext.class);
		}
		public SimpleStatementContext simpleStatement(int i) {
			return getRuleContext(SimpleStatementContext.class,i);
		}
		public ExprContext expr() {
			return getRuleContext(ExprContext.class,0);
		}
		public StatementsContext statements() {
			return getRuleContext(StatementsContext.class,0);
		}
		public ForLoopContext(StatementContext ctx) { copyFrom(ctx); }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof cListener ) ((cListener)listener).enterForLoop(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof cListener ) ((cListener)listener).exitForLoop(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof cVisitor ) return ((cVisitor<? extends T>)visitor).visitForLoop(this);
			else return visitor.visitChildren(this);
		}
	}
	public static class SOutsourceContext extends StatementContext {
		public List<LvalsContext> lvals() {
			return getRuleContexts(LvalsContext.class);
		}
		public LvalsContext lvals(int i) {
			return getRuleContext(LvalsContext.class,i);
		}
		public List<TerminalNode> Ident() { return getTokens(cParser.Ident); }
		public TerminalNode Ident(int i) {
			return getToken(cParser.Ident, i);
		}
		public SOutsourceContext(StatementContext ctx) { copyFrom(ctx); }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof cListener ) ((cListener)listener).enterSOutsource(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof cListener ) ((cListener)listener).exitSOutsource(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof cVisitor ) return ((cVisitor<? extends T>)visitor).visitSOutsource(this);
			else return visitor.visitChildren(this);
		}
	}

	public final StatementContext statement() throws RecognitionException {
		StatementContext _localctx = new StatementContext(_ctx, getState());
		enterRule(_localctx, 42, RULE_statement);
		try {
			setState(361);
			_errHandler.sync(this);
			switch ( getInterpreter().adaptivePredict(_input,24,_ctx) ) {
			case 1:
				_localctx = new SimpleStmtContext(_localctx);
				enterOuterAlt(_localctx, 1);
				{
				setState(251);
				simpleStatement();
				setState(252);
				match(T__36);
				}
				break;
			case 2:
				_localctx = new GroupStmtContext(_localctx);
				enterOuterAlt(_localctx, 2);
				{
				setState(254);
				match(T__37);
				setState(255);
				statements();
				setState(256);
				match(T__38);
				}
				break;
			case 3:
				_localctx = new IfSingleContext(_localctx);
				enterOuterAlt(_localctx, 3);
				{
				setState(258);
				match(T__39);
				setState(259);
				match(T__1);
				setState(260);
				expr();
				setState(261);
				match(T__2);
				setState(262);
				match(T__37);
				setState(263);
				statements();
				setState(264);
				match(T__38);
				}
				break;
			case 4:
				_localctx = new IfElseContext(_localctx);
				enterOuterAlt(_localctx, 4);
				{
				setState(266);
				match(T__39);
				setState(267);
				match(T__1);
				setState(268);
				expr();
				setState(269);
				match(T__2);
				setState(270);
				match(T__37);
				setState(271);
				statements();
				setState(272);
				match(T__38);
				setState(273);
				match(T__40);
				setState(274);
				match(T__37);
				setState(275);
				statements();
				setState(276);
				match(T__38);
				}
				break;
			case 5:
				_localctx = new SoutContext(_localctx);
				enterOuterAlt(_localctx, 5);
				{
				setState(278);
				match(T__41);
				setState(279);
				statements();
				setState(280);
				match(T__38);
				}
				break;
			case 6:
				_localctx = new SOutsourceContext(_localctx);
				enterOuterAlt(_localctx, 6);
				{
				setState(282);
				match(T__42);
				setState(283);
				lvals();
				setState(284);
				match(T__43);
				setState(285);
				lvals();
				setState(286);
				match(T__43);
				setState(287);
				match(Ident);
				setState(288);
				match(Ident);
				setState(289);
				match(Ident);
				setState(290);
				match(T__38);
				}
				break;
			case 7:
				_localctx = new WhileContext(_localctx);
				enterOuterAlt(_localctx, 7);
				{
				setState(292);
				match(T__44);
				setState(293);
				match(T__1);
				setState(294);
				expr();
				setState(295);
				match(T__2);
				setState(296);
				match(T__37);
				setState(297);
				statements();
				setState(298);
				match(T__38);
				}
				break;
			case 8:
				_localctx = new ForLoopContext(_localctx);
				enterOuterAlt(_localctx, 8);
				{
				setState(300);
				match(T__45);
				setState(301);
				match(T__1);
				setState(302);
				simpleStatement();
				setState(303);
				match(T__36);
				setState(304);
				expr();
				setState(305);
				match(T__36);
				setState(306);
				simpleStatement();
				setState(307);
				match(T__2);
				setState(308);
				match(T__37);
				setState(309);
				statements();
				setState(310);
				match(T__38);
				}
				break;
			case 9:
				_localctx = new OforLoopContext(_localctx);
				enterOuterAlt(_localctx, 9);
				{
				setState(312);
				match(T__46);
				setState(313);
				match(T__1);
				setState(314);
				match(Int);
				setState(315);
				match(T__36);
				setState(316);
				simpleStatement();
				setState(317);
				match(T__36);
				setState(318);
				expr();
				setState(319);
				match(T__36);
				setState(320);
				simpleStatement();
				setState(321);
				match(T__2);
				setState(322);
				match(T__37);
				setState(323);
				statements();
				setState(324);
				match(T__38);
				}
				break;
			case 10:
				_localctx = new DecStmtContext(_localctx);
				enterOuterAlt(_localctx, 10);
				{
				setState(326);
				type();
				setState(327);
				declist();
				setState(328);
				match(T__36);
				}
				break;
			case 11:
				_localctx = new DecArrayIdentContext(_localctx);
				enterOuterAlt(_localctx, 11);
				{
				setState(330);
				type();
				setState(331);
				match(Ident);
				setState(332);
				match(T__25);
				setState(333);
				match(Ident);
				setState(334);
				match(T__26);
				setState(335);
				match(T__36);
				}
				break;
			case 12:
				_localctx = new DecArrayIntContext(_localctx);
				enterOuterAlt(_localctx, 12);
				{
				setState(337);
				type();
				setState(338);
				match(Ident);
				setState(339);
				match(T__25);
				setState(340);
				match(Int);
				setState(341);
				match(T__26);
				setState(342);
				match(T__36);
				}
				break;
			case 13:
				_localctx = new DecSingleDefContext(_localctx);
				enterOuterAlt(_localctx, 13);
				{
				setState(344);
				singleDef();
				setState(345);
				match(T__36);
				}
				break;
			case 14:
				_localctx = new RetStmtContext(_localctx);
				enterOuterAlt(_localctx, 14);
				{
				setState(347);
				match(T__47);
				setState(348);
				expr();
				setState(349);
				match(T__36);
				}
				break;
			case 15:
				_localctx = new RetStmtNoneContext(_localctx);
				enterOuterAlt(_localctx, 15);
				{
				setState(351);
				match(T__47);
				setState(352);
				match(T__36);
				}
				break;
			case 16:
				_localctx = new BreakStmtContext(_localctx);
				enterOuterAlt(_localctx, 16);
				{
				setState(353);
				match(T__48);
				setState(354);
				match(T__36);
				}
				break;
			case 17:
				_localctx = new ContinueStmtContext(_localctx);
				enterOuterAlt(_localctx, 17);
				{
				setState(355);
				match(T__49);
				setState(356);
				match(T__36);
				}
				break;
			case 18:
				_localctx = new BeginStmtContext(_localctx);
				enterOuterAlt(_localctx, 18);
				{
				setState(357);
				match(T__50);
				setState(358);
				match(T__36);
				}
				break;
			case 19:
				_localctx = new EndStmtContext(_localctx);
				enterOuterAlt(_localctx, 19);
				{
				setState(359);
				match(T__51);
				setState(360);
				match(T__36);
				}
				break;
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class FuncDefContext extends ParserRuleContext {
		public FuncDefContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_funcDef; }
	 
		public FuncDefContext() { }
		public void copyFrom(FuncDefContext ctx) {
			super.copyFrom(ctx);
		}
	}
	public static class FuncdefSimpleContext extends FuncDefContext {
		public TypeContext type() {
			return getRuleContext(TypeContext.class,0);
		}
		public TerminalNode Ident() { return getToken(cParser.Ident, 0); }
		public InputListContext inputList() {
			return getRuleContext(InputListContext.class,0);
		}
		public StatementsContext statements() {
			return getRuleContext(StatementsContext.class,0);
		}
		public FuncdefSimpleContext(FuncDefContext ctx) { copyFrom(ctx); }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof cListener ) ((cListener)listener).enterFuncdefSimple(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof cListener ) ((cListener)listener).exitFuncdefSimple(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof cVisitor ) return ((cVisitor<? extends T>)visitor).visitFuncdefSimple(this);
			else return visitor.visitChildren(this);
		}
	}

	public final FuncDefContext funcDef() throws RecognitionException {
		FuncDefContext _localctx = new FuncDefContext(_ctx, getState());
		enterRule(_localctx, 44, RULE_funcDef);
		try {
			_localctx = new FuncdefSimpleContext(_localctx);
			enterOuterAlt(_localctx, 1);
			{
			setState(363);
			type();
			setState(364);
			match(Ident);
			setState(365);
			match(T__1);
			setState(366);
			inputList();
			setState(367);
			match(T__2);
			setState(368);
			match(T__37);
			setState(369);
			statements();
			setState(370);
			match(T__38);
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class ArrayLengthContext extends ParserRuleContext {
		public TerminalNode Int() { return getToken(cParser.Int, 0); }
		public TerminalNode Ident() { return getToken(cParser.Ident, 0); }
		public ArrayLengthContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_arrayLength; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof cListener ) ((cListener)listener).enterArrayLength(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof cListener ) ((cListener)listener).exitArrayLength(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof cVisitor ) return ((cVisitor<? extends T>)visitor).visitArrayLength(this);
			else return visitor.visitChildren(this);
		}
	}

	public final ArrayLengthContext arrayLength() throws RecognitionException {
		ArrayLengthContext _localctx = new ArrayLengthContext(_ctx, getState());
		enterRule(_localctx, 46, RULE_arrayLength);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(372);
			_la = _input.LA(1);
			if ( !(_la==Ident || _la==Int) ) {
			_errHandler.recoverInline(this);
			} else {
				consume();
			}
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class SingleDefContext extends ParserRuleContext {
		public SingleDefContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_singleDef; }
	 
		public SingleDefContext() { }
		public void copyFrom(SingleDefContext ctx) {
			super.copyFrom(ctx);
		}
	}
	public static class SingleDefArrayConstContext extends SingleDefContext {
		public TypeContext type() {
			return getRuleContext(TypeContext.class,0);
		}
		public TerminalNode Ident() { return getToken(cParser.Ident, 0); }
		public List<ArrayLengthContext> arrayLength() {
			return getRuleContexts(ArrayLengthContext.class);
		}
		public ArrayLengthContext arrayLength(int i) {
			return getRuleContext(ArrayLengthContext.class,i);
		}
		public SingleDefArrayConstContext(SingleDefContext ctx) { copyFrom(ctx); }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof cListener ) ((cListener)listener).enterSingleDefArrayConst(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof cListener ) ((cListener)listener).exitSingleDefArrayConst(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof cVisitor ) return ((cVisitor<? extends T>)visitor).visitSingleDefArrayConst(this);
			else return visitor.visitChildren(this);
		}
	}
	public static class SingleDefNormalContext extends SingleDefContext {
		public TypeContext type() {
			return getRuleContext(TypeContext.class,0);
		}
		public TerminalNode Ident() { return getToken(cParser.Ident, 0); }
		public SingleDefNormalContext(SingleDefContext ctx) { copyFrom(ctx); }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof cListener ) ((cListener)listener).enterSingleDefNormal(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof cListener ) ((cListener)listener).exitSingleDefNormal(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof cVisitor ) return ((cVisitor<? extends T>)visitor).visitSingleDefNormal(this);
			else return visitor.visitChildren(this);
		}
	}

	public final SingleDefContext singleDef() throws RecognitionException {
		SingleDefContext _localctx = new SingleDefContext(_ctx, getState());
		enterRule(_localctx, 48, RULE_singleDef);
		int _la;
		try {
			setState(387);
			_errHandler.sync(this);
			switch ( getInterpreter().adaptivePredict(_input,26,_ctx) ) {
			case 1:
				_localctx = new SingleDefNormalContext(_localctx);
				enterOuterAlt(_localctx, 1);
				{
				setState(374);
				type();
				setState(375);
				match(Ident);
				}
				break;
			case 2:
				_localctx = new SingleDefArrayConstContext(_localctx);
				enterOuterAlt(_localctx, 2);
				{
				setState(377);
				type();
				setState(378);
				match(Ident);
				setState(383); 
				_errHandler.sync(this);
				_la = _input.LA(1);
				do {
					{
					{
					setState(379);
					match(T__25);
					setState(380);
					arrayLength();
					setState(381);
					match(T__26);
					}
					}
					setState(385); 
					_errHandler.sync(this);
					_la = _input.LA(1);
				} while ( _la==T__25 );
				}
				break;
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class StructDefContext extends ParserRuleContext {
		public TerminalNode Ident() { return getToken(cParser.Ident, 0); }
		public List<SingleDefContext> singleDef() {
			return getRuleContexts(SingleDefContext.class);
		}
		public SingleDefContext singleDef(int i) {
			return getRuleContext(SingleDefContext.class,i);
		}
		public StructDefContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_structDef; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof cListener ) ((cListener)listener).enterStructDef(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof cListener ) ((cListener)listener).exitStructDef(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof cVisitor ) return ((cVisitor<? extends T>)visitor).visitStructDef(this);
			else return visitor.visitChildren(this);
		}
	}

	public final StructDefContext structDef() throws RecognitionException {
		StructDefContext _localctx = new StructDefContext(_ctx, getState());
		enterRule(_localctx, 50, RULE_structDef);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(389);
			match(T__29);
			setState(390);
			match(Ident);
			setState(391);
			match(T__37);
			setState(395); 
			_errHandler.sync(this);
			_la = _input.LA(1);
			do {
				{
				{
				setState(392);
				singleDef();
				setState(393);
				match(T__36);
				}
				}
				setState(397); 
				_errHandler.sync(this);
				_la = _input.LA(1);
			} while ( (((_la) & ~0x3f) == 0 && ((1L << _la) & ((1L << T__27) | (1L << T__28) | (1L << T__29))) != 0) );
			setState(399);
			match(T__38);
			setState(400);
			match(T__36);
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public boolean sempred(RuleContext _localctx, int ruleIndex, int predIndex) {
		switch (ruleIndex) {
		case 6:
			return multiplicativeExpr_sempred((MultiplicativeExprContext)_localctx, predIndex);
		case 7:
			return additiveExpr_sempred((AdditiveExprContext)_localctx, predIndex);
		case 8:
			return relationalExpr_sempred((RelationalExprContext)_localctx, predIndex);
		case 9:
			return andExpr_sempred((AndExprContext)_localctx, predIndex);
		case 10:
			return orExpr_sempred((OrExprContext)_localctx, predIndex);
		case 12:
			return lval_sempred((LvalContext)_localctx, predIndex);
		}
		return true;
	}
	private boolean multiplicativeExpr_sempred(MultiplicativeExprContext _localctx, int predIndex) {
		switch (predIndex) {
		case 0:
			return precpred(_ctx, 1);
		}
		return true;
	}
	private boolean additiveExpr_sempred(AdditiveExprContext _localctx, int predIndex) {
		switch (predIndex) {
		case 1:
			return precpred(_ctx, 1);
		}
		return true;
	}
	private boolean relationalExpr_sempred(RelationalExprContext _localctx, int predIndex) {
		switch (predIndex) {
		case 2:
			return precpred(_ctx, 1);
		}
		return true;
	}
	private boolean andExpr_sempred(AndExprContext _localctx, int predIndex) {
		switch (predIndex) {
		case 3:
			return precpred(_ctx, 1);
		}
		return true;
	}
	private boolean orExpr_sempred(OrExprContext _localctx, int predIndex) {
		switch (predIndex) {
		case 4:
			return precpred(_ctx, 1);
		}
		return true;
	}
	private boolean lval_sempred(LvalContext _localctx, int predIndex) {
		switch (predIndex) {
		case 5:
			return precpred(_ctx, 5);
		case 6:
			return precpred(_ctx, 3);
		case 7:
			return precpred(_ctx, 2);
		}
		return true;
	}

	public static final String _serializedATN =
		"\3\u0430\ud6d1\u8206\uad2d\u4417\uaef1\u8d80\uaadd\3>\u0195\4\2\t\2\4"+
		"\3\t\3\4\4\t\4\4\5\t\5\4\6\t\6\4\7\t\7\4\b\t\b\4\t\t\t\4\n\t\n\4\13\t"+
		"\13\4\f\t\f\4\r\t\r\4\16\t\16\4\17\t\17\4\20\t\20\4\21\t\21\4\22\t\22"+
		"\4\23\t\23\4\24\t\24\4\25\t\25\4\26\t\26\4\27\t\27\4\30\t\30\4\31\t\31"+
		"\4\32\t\32\4\33\t\33\3\2\7\28\n\2\f\2\16\2;\13\2\3\2\3\2\3\3\3\3\3\3\5"+
		"\3B\n\3\3\4\3\4\3\4\3\4\3\5\3\5\3\5\3\5\3\5\3\5\3\5\3\5\3\5\3\5\3\5\7"+
		"\5S\n\5\f\5\16\5V\13\5\5\5X\n\5\3\5\5\5[\n\5\3\6\3\6\3\6\3\6\5\6a\n\6"+
		"\3\7\3\7\3\7\5\7f\n\7\3\b\3\b\3\b\3\b\3\b\3\b\7\bn\n\b\f\b\16\bq\13\b"+
		"\3\t\3\t\3\t\3\t\3\t\3\t\7\ty\n\t\f\t\16\t|\13\t\3\n\3\n\3\n\3\n\3\n\3"+
		"\n\7\n\u0084\n\n\f\n\16\n\u0087\13\n\3\13\3\13\3\13\3\13\3\13\3\13\7\13"+
		"\u008f\n\13\f\13\16\13\u0092\13\13\3\f\3\f\3\f\3\f\3\f\3\f\7\f\u009a\n"+
		"\f\f\f\16\f\u009d\13\f\3\r\3\r\3\r\3\r\3\r\3\r\3\r\5\r\u00a6\n\r\3\16"+
		"\3\16\3\16\3\16\3\16\3\16\3\16\3\16\5\16\u00b0\n\16\3\16\3\16\3\16\3\16"+
		"\3\16\3\16\3\16\3\16\3\16\3\16\3\16\7\16\u00bd\n\16\f\16\16\16\u00c0\13"+
		"\16\3\17\3\17\3\17\3\17\5\17\u00c6\n\17\3\20\3\20\3\20\3\20\5\20\u00cc"+
		"\n\20\3\21\3\21\3\21\3\21\3\21\3\21\3\21\3\21\3\21\5\21\u00d7\n\21\3\22"+
		"\3\22\3\22\3\22\5\22\u00dd\n\22\3\23\3\23\3\23\3\23\5\23\u00e3\n\23\3"+
		"\24\3\24\3\24\3\24\3\24\5\24\u00ea\n\24\3\25\3\25\3\25\3\25\3\25\3\25"+
		"\3\25\3\25\3\25\3\25\5\25\u00f6\n\25\3\26\3\26\3\26\3\26\5\26\u00fc\n"+
		"\26\3\27\3\27\3\27\3\27\3\27\3\27\3\27\3\27\3\27\3\27\3\27\3\27\3\27\3"+
		"\27\3\27\3\27\3\27\3\27\3\27\3\27\3\27\3\27\3\27\3\27\3\27\3\27\3\27\3"+
		"\27\3\27\3\27\3\27\3\27\3\27\3\27\3\27\3\27\3\27\3\27\3\27\3\27\3\27\3"+
		"\27\3\27\3\27\3\27\3\27\3\27\3\27\3\27\3\27\3\27\3\27\3\27\3\27\3\27\3"+
		"\27\3\27\3\27\3\27\3\27\3\27\3\27\3\27\3\27\3\27\3\27\3\27\3\27\3\27\3"+
		"\27\3\27\3\27\3\27\3\27\3\27\3\27\3\27\3\27\3\27\3\27\3\27\3\27\3\27\3"+
		"\27\3\27\3\27\3\27\3\27\3\27\3\27\3\27\3\27\3\27\3\27\3\27\3\27\3\27\3"+
		"\27\3\27\3\27\3\27\3\27\3\27\3\27\3\27\3\27\3\27\3\27\3\27\3\27\5\27\u016c"+
		"\n\27\3\30\3\30\3\30\3\30\3\30\3\30\3\30\3\30\3\30\3\31\3\31\3\32\3\32"+
		"\3\32\3\32\3\32\3\32\3\32\3\32\3\32\6\32\u0182\n\32\r\32\16\32\u0183\5"+
		"\32\u0186\n\32\3\33\3\33\3\33\3\33\3\33\3\33\6\33\u018e\n\33\r\33\16\33"+
		"\u018f\3\33\3\33\3\33\3\33\2\b\16\20\22\24\26\32\34\2\4\6\b\n\f\16\20"+
		"\22\24\26\30\32\34\36 \"$&(*,.\60\62\64\2\t\3\2\7\b\3\2\7\13\3\2\f\16"+
		"\4\2\t\t\17\17\3\2\20\25\3\2\"&\4\2\67\67==\u01b0\29\3\2\2\2\4A\3\2\2"+
		"\2\6C\3\2\2\2\bZ\3\2\2\2\n`\3\2\2\2\fe\3\2\2\2\16g\3\2\2\2\20r\3\2\2\2"+
		"\22}\3\2\2\2\24\u0088\3\2\2\2\26\u0093\3\2\2\2\30\u00a5\3\2\2\2\32\u00af"+
		"\3\2\2\2\34\u00c5\3\2\2\2\36\u00cb\3\2\2\2 \u00d6\3\2\2\2\"\u00dc\3\2"+
		"\2\2$\u00e2\3\2\2\2&\u00e9\3\2\2\2(\u00f5\3\2\2\2*\u00fb\3\2\2\2,\u016b"+
		"\3\2\2\2.\u016d\3\2\2\2\60\u0176\3\2\2\2\62\u0185\3\2\2\2\64\u0187\3\2"+
		"\2\2\668\5\4\3\2\67\66\3\2\2\28;\3\2\2\29\67\3\2\2\29:\3\2\2\2:<\3\2\2"+
		"\2;9\3\2\2\2<=\7\2\2\3=\3\3\2\2\2>B\5.\30\2?B\5\6\4\2@B\5\64\33\2A>\3"+
		"\2\2\2A?\3\2\2\2A@\3\2\2\2B\5\3\2\2\2CD\7\3\2\2DE\7\67\2\2EF\7=\2\2F\7"+
		"\3\2\2\2G[\5\32\16\2H[\7=\2\2IJ\7\4\2\2JK\5\30\r\2KL\7\5\2\2L[\3\2\2\2"+
		"MN\7\67\2\2NW\7\4\2\2OT\5\30\r\2PQ\7\6\2\2QS\5\30\r\2RP\3\2\2\2SV\3\2"+
		"\2\2TR\3\2\2\2TU\3\2\2\2UX\3\2\2\2VT\3\2\2\2WO\3\2\2\2WX\3\2\2\2XY\3\2"+
		"\2\2Y[\7\5\2\2ZG\3\2\2\2ZH\3\2\2\2ZI\3\2\2\2ZM\3\2\2\2[\t\3\2\2\2\\a\5"+
		"\b\5\2]^\5\b\5\2^_\t\2\2\2_a\3\2\2\2`\\\3\2\2\2`]\3\2\2\2a\13\3\2\2\2"+
		"bf\5\n\6\2cd\t\3\2\2df\5\n\6\2eb\3\2\2\2ec\3\2\2\2f\r\3\2\2\2gh\b\b\1"+
		"\2hi\5\f\7\2io\3\2\2\2jk\f\3\2\2kl\t\4\2\2ln\5\f\7\2mj\3\2\2\2nq\3\2\2"+
		"\2om\3\2\2\2op\3\2\2\2p\17\3\2\2\2qo\3\2\2\2rs\b\t\1\2st\5\16\b\2tz\3"+
		"\2\2\2uv\f\3\2\2vw\t\5\2\2wy\5\16\b\2xu\3\2\2\2y|\3\2\2\2zx\3\2\2\2z{"+
		"\3\2\2\2{\21\3\2\2\2|z\3\2\2\2}~\b\n\1\2~\177\5\20\t\2\177\u0085\3\2\2"+
		"\2\u0080\u0081\f\3\2\2\u0081\u0082\t\6\2\2\u0082\u0084\5\20\t\2\u0083"+
		"\u0080\3\2\2\2\u0084\u0087\3\2\2\2\u0085\u0083\3\2\2\2\u0085\u0086\3\2"+
		"\2\2\u0086\23\3\2\2\2\u0087\u0085\3\2\2\2\u0088\u0089\b\13\1\2\u0089\u008a"+
		"\5\22\n\2\u008a\u0090\3\2\2\2\u008b\u008c\f\3\2\2\u008c\u008d\7\26\2\2"+
		"\u008d\u008f\5\22\n\2\u008e\u008b\3\2\2\2\u008f\u0092\3\2\2\2\u0090\u008e"+
		"\3\2\2\2\u0090\u0091\3\2\2\2\u0091\25\3\2\2\2\u0092\u0090\3\2\2\2\u0093"+
		"\u0094\b\f\1\2\u0094\u0095\5\24\13\2\u0095\u009b\3\2\2\2\u0096\u0097\f"+
		"\3\2\2\u0097\u0098\7\27\2\2\u0098\u009a\5\24\13\2\u0099\u0096\3\2\2\2"+
		"\u009a\u009d\3\2\2\2\u009b\u0099\3\2\2\2\u009b\u009c\3\2\2\2\u009c\27"+
		"\3\2\2\2\u009d\u009b\3\2\2\2\u009e\u00a6\5\26\f\2\u009f\u00a0\5\26\f\2"+
		"\u00a0\u00a1\7\30\2\2\u00a1\u00a2\5\26\f\2\u00a2\u00a3\7\31\2\2\u00a3"+
		"\u00a4\5\26\f\2\u00a4\u00a6\3\2\2\2\u00a5\u009e\3\2\2\2\u00a5\u009f\3"+
		"\2\2\2\u00a6\31\3\2\2\2\u00a7\u00a8\b\16\1\2\u00a8\u00b0\7\67\2\2\u00a9"+
		"\u00aa\7\4\2\2\u00aa\u00ab\5\32\16\2\u00ab\u00ac\7\5\2\2\u00ac\u00b0\3"+
		"\2\2\2\u00ad\u00ae\7\f\2\2\u00ae\u00b0\5\32\16\3\u00af\u00a7\3\2\2\2\u00af"+
		"\u00a9\3\2\2\2\u00af\u00ad\3\2\2\2\u00b0\u00be\3\2\2\2\u00b1\u00b2\f\7"+
		"\2\2\u00b2\u00b3\7\32\2\2\u00b3\u00bd\7\67\2\2\u00b4\u00b5\f\5\2\2\u00b5"+
		"\u00b6\7\33\2\2\u00b6\u00bd\7\67\2\2\u00b7\u00b8\f\4\2\2\u00b8\u00b9\7"+
		"\34\2\2\u00b9\u00ba\5\30\r\2\u00ba\u00bb\7\35\2\2\u00bb\u00bd\3\2\2\2"+
		"\u00bc\u00b1\3\2\2\2\u00bc\u00b4\3\2\2\2\u00bc\u00b7\3\2\2\2\u00bd\u00c0"+
		"\3\2\2\2\u00be\u00bc\3\2\2\2\u00be\u00bf\3\2\2\2\u00bf\33\3\2\2\2\u00c0"+
		"\u00be\3\2\2\2\u00c1\u00c6\7\36\2\2\u00c2\u00c6\7\37\2\2\u00c3\u00c4\7"+
		" \2\2\u00c4\u00c6\7\67\2\2\u00c5\u00c1\3\2\2\2\u00c5\u00c2\3\2\2\2\u00c5"+
		"\u00c3\3\2\2\2\u00c6\35\3\2\2\2\u00c7\u00cc\5\34\17\2\u00c8\u00c9\5\34"+
		"\17\2\u00c9\u00ca\7\f\2\2\u00ca\u00cc\3\2\2\2\u00cb\u00c7\3\2\2\2\u00cb"+
		"\u00c8\3\2\2\2\u00cc\37\3\2\2\2\u00cd\u00ce\5\36\20\2\u00ce\u00cf\7\67"+
		"\2\2\u00cf\u00d0\7\6\2\2\u00d0\u00d1\5 \21\2\u00d1\u00d7\3\2\2\2\u00d2"+
		"\u00d3\5\36\20\2\u00d3\u00d4\7\67\2\2\u00d4\u00d7\3\2\2\2\u00d5\u00d7"+
		"\3\2\2\2\u00d6\u00cd\3\2\2\2\u00d6\u00d2\3\2\2\2\u00d6\u00d5\3\2\2\2\u00d7"+
		"!\3\2\2\2\u00d8\u00d9\5,\27\2\u00d9\u00da\5\"\22\2\u00da\u00dd\3\2\2\2"+
		"\u00db\u00dd\3\2\2\2\u00dc\u00d8\3\2\2\2\u00dc\u00db\3\2\2\2\u00dd#\3"+
		"\2\2\2\u00de\u00e3\7\67\2\2\u00df\u00e0\7\67\2\2\u00e0\u00e1\7!\2\2\u00e1"+
		"\u00e3\5\30\r\2\u00e2\u00de\3\2\2\2\u00e2\u00df\3\2\2\2\u00e3%\3\2\2\2"+
		"\u00e4\u00ea\5$\23\2\u00e5\u00e6\5$\23\2\u00e6\u00e7\7\6\2\2\u00e7\u00e8"+
		"\5&\24\2\u00e8\u00ea\3\2\2\2\u00e9\u00e4\3\2\2\2\u00e9\u00e5\3\2\2\2\u00ea"+
		"\'\3\2\2\2\u00eb\u00f6\3\2\2\2\u00ec\u00ed\5\32\16\2\u00ed\u00ee\7!\2"+
		"\2\u00ee\u00ef\5\30\r\2\u00ef\u00f6\3\2\2\2\u00f0\u00f1\5\32\16\2\u00f1"+
		"\u00f2\t\7\2\2\u00f2\u00f3\5\30\r\2\u00f3\u00f6\3\2\2\2\u00f4\u00f6\5"+
		"\30\r\2\u00f5\u00eb\3\2\2\2\u00f5\u00ec\3\2\2\2\u00f5\u00f0\3\2\2\2\u00f5"+
		"\u00f4\3\2\2\2\u00f6)\3\2\2\2\u00f7\u00fc\5\32\16\2\u00f8\u00f9\5\32\16"+
		"\2\u00f9\u00fa\5*\26\2\u00fa\u00fc\3\2\2\2\u00fb\u00f7\3\2\2\2\u00fb\u00f8"+
		"\3\2\2\2\u00fc+\3\2\2\2\u00fd\u00fe\5(\25\2\u00fe\u00ff\7\'\2\2\u00ff"+
		"\u016c\3\2\2\2\u0100\u0101\7(\2\2\u0101\u0102\5\"\22\2\u0102\u0103\7)"+
		"\2\2\u0103\u016c\3\2\2\2\u0104\u0105\7*\2\2\u0105\u0106\7\4\2\2\u0106"+
		"\u0107\5\30\r\2\u0107\u0108\7\5\2\2\u0108\u0109\7(\2\2\u0109\u010a\5\""+
		"\22\2\u010a\u010b\7)\2\2\u010b\u016c\3\2\2\2\u010c\u010d\7*\2\2\u010d"+
		"\u010e\7\4\2\2\u010e\u010f\5\30\r\2\u010f\u0110\7\5\2\2\u0110\u0111\7"+
		"(\2\2\u0111\u0112\5\"\22\2\u0112\u0113\7)\2\2\u0113\u0114\7+\2\2\u0114"+
		"\u0115\7(\2\2\u0115\u0116\5\"\22\2\u0116\u0117\7)\2\2\u0117\u016c\3\2"+
		"\2\2\u0118\u0119\7,\2\2\u0119\u011a\5\"\22\2\u011a\u011b\7)\2\2\u011b"+
		"\u016c\3\2\2\2\u011c\u011d\7-\2\2\u011d\u011e\5*\26\2\u011e\u011f\7.\2"+
		"\2\u011f\u0120\5*\26\2\u0120\u0121\7.\2\2\u0121\u0122\7\67\2\2\u0122\u0123"+
		"\7\67\2\2\u0123\u0124\7\67\2\2\u0124\u0125\7)\2\2\u0125\u016c\3\2\2\2"+
		"\u0126\u0127\7/\2\2\u0127\u0128\7\4\2\2\u0128\u0129\5\30\r\2\u0129\u012a"+
		"\7\5\2\2\u012a\u012b\7(\2\2\u012b\u012c\5\"\22\2\u012c\u012d\7)\2\2\u012d"+
		"\u016c\3\2\2\2\u012e\u012f\7\60\2\2\u012f\u0130\7\4\2\2\u0130\u0131\5"+
		"(\25\2\u0131\u0132\7\'\2\2\u0132\u0133\5\30\r\2\u0133\u0134\7\'\2\2\u0134"+
		"\u0135\5(\25\2\u0135\u0136\7\5\2\2\u0136\u0137\7(\2\2\u0137\u0138\5\""+
		"\22\2\u0138\u0139\7)\2\2\u0139\u016c\3\2\2\2\u013a\u013b\7\61\2\2\u013b"+
		"\u013c\7\4\2\2\u013c\u013d\7=\2\2\u013d\u013e\7\'\2\2\u013e\u013f\5(\25"+
		"\2\u013f\u0140\7\'\2\2\u0140\u0141\5\30\r\2\u0141\u0142\7\'\2\2\u0142"+
		"\u0143\5(\25\2\u0143\u0144\7\5\2\2\u0144\u0145\7(\2\2\u0145\u0146\5\""+
		"\22\2\u0146\u0147\7)\2\2\u0147\u016c\3\2\2\2\u0148\u0149\5\36\20\2\u0149"+
		"\u014a\5&\24\2\u014a\u014b\7\'\2\2\u014b\u016c\3\2\2\2\u014c\u014d\5\36"+
		"\20\2\u014d\u014e\7\67\2\2\u014e\u014f\7\34\2\2\u014f\u0150\7\67\2\2\u0150"+
		"\u0151\7\35\2\2\u0151\u0152\7\'\2\2\u0152\u016c\3\2\2\2\u0153\u0154\5"+
		"\36\20\2\u0154\u0155\7\67\2\2\u0155\u0156\7\34\2\2\u0156\u0157\7=\2\2"+
		"\u0157\u0158\7\35\2\2\u0158\u0159\7\'\2\2\u0159\u016c\3\2\2\2\u015a\u015b"+
		"\5\62\32\2\u015b\u015c\7\'\2\2\u015c\u016c\3\2\2\2\u015d\u015e\7\62\2"+
		"\2\u015e\u015f\5\30\r\2\u015f\u0160\7\'\2\2\u0160\u016c\3\2\2\2\u0161"+
		"\u0162\7\62\2\2\u0162\u016c\7\'\2\2\u0163\u0164\7\63\2\2\u0164\u016c\7"+
		"\'\2\2\u0165\u0166\7\64\2\2\u0166\u016c\7\'\2\2\u0167\u0168\7\65\2\2\u0168"+
		"\u016c\7\'\2\2\u0169\u016a\7\66\2\2\u016a\u016c\7\'\2\2\u016b\u00fd\3"+
		"\2\2\2\u016b\u0100\3\2\2\2\u016b\u0104\3\2\2\2\u016b\u010c\3\2\2\2\u016b"+
		"\u0118\3\2\2\2\u016b\u011c\3\2\2\2\u016b\u0126\3\2\2\2\u016b\u012e\3\2"+
		"\2\2\u016b\u013a\3\2\2\2\u016b\u0148\3\2\2\2\u016b\u014c\3\2\2\2\u016b"+
		"\u0153\3\2\2\2\u016b\u015a\3\2\2\2\u016b\u015d\3\2\2\2\u016b\u0161\3\2"+
		"\2\2\u016b\u0163\3\2\2\2\u016b\u0165\3\2\2\2\u016b\u0167\3\2\2\2\u016b"+
		"\u0169\3\2\2\2\u016c-\3\2\2\2\u016d\u016e\5\36\20\2\u016e\u016f\7\67\2"+
		"\2\u016f\u0170\7\4\2\2\u0170\u0171\5 \21\2\u0171\u0172\7\5\2\2\u0172\u0173"+
		"\7(\2\2\u0173\u0174\5\"\22\2\u0174\u0175\7)\2\2\u0175/\3\2\2\2\u0176\u0177"+
		"\t\b\2\2\u0177\61\3\2\2\2\u0178\u0179\5\36\20\2\u0179\u017a\7\67\2\2\u017a"+
		"\u0186\3\2\2\2\u017b\u017c\5\36\20\2\u017c\u0181\7\67\2\2\u017d\u017e"+
		"\7\34\2\2\u017e\u017f\5\60\31\2\u017f\u0180\7\35\2\2\u0180\u0182\3\2\2"+
		"\2\u0181\u017d\3\2\2\2\u0182\u0183\3\2\2\2\u0183\u0181\3\2\2\2\u0183\u0184"+
		"\3\2\2\2\u0184\u0186\3\2\2\2\u0185\u0178\3\2\2\2\u0185\u017b\3\2\2\2\u0186"+
		"\63\3\2\2\2\u0187\u0188\7 \2\2\u0188\u0189\7\67\2\2\u0189\u018d\7(\2\2"+
		"\u018a\u018b\5\62\32\2\u018b\u018c\7\'\2\2\u018c\u018e\3\2\2\2\u018d\u018a"+
		"\3\2\2\2\u018e\u018f\3\2\2\2\u018f\u018d\3\2\2\2\u018f\u0190\3\2\2\2\u0190"+
		"\u0191\3\2\2\2\u0191\u0192\7)\2\2\u0192\u0193\7\'\2\2\u0193\65\3\2\2\2"+
		"\369ATWZ`eoz\u0085\u0090\u009b\u00a5\u00af\u00bc\u00be\u00c5\u00cb\u00d6"+
		"\u00dc\u00e2\u00e9\u00f5\u00fb\u016b\u0183\u0185\u018f";
	public static final ATN _ATN =
		new ATNDeserializer().deserialize(_serializedATN.toCharArray());
	static {
		_decisionToDFA = new DFA[_ATN.getNumberOfDecisions()];
		for (int i = 0; i < _ATN.getNumberOfDecisions(); i++) {
			_decisionToDFA[i] = new DFA(_ATN.getDecisionState(i), i);
		}
	}
}
