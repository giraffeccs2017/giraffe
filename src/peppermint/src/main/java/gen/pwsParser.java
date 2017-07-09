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
public class pwsParser extends Parser {
	static { RuntimeMetaData.checkVersion("4.5.3", RuntimeMetaData.VERSION); }

	protected static final DFA[] _decisionToDFA;
	protected static final PredictionContextCache _sharedContextCache =
		new PredictionContextCache();
	public static final int
		T__0=1, T__1=2, T__2=3, T__3=4, T__4=5, T__5=6, T__6=7, T__7=8, T__8=9, 
		T__9=10, T__10=11, T__11=12, T__12=13, T__13=14, T__14=15, T__15=16, T__16=17, 
		T__17=18, T__18=19, T__19=20, T__20=21, Ident=22, Int=23, WS=24;
	public static final int
		RULE_lval = 0, RULE_primaryExpr = 1, RULE_preFixExpr = 2, RULE_multiplicativeExpr = 3, 
		RULE_additiveExpr = 4, RULE_onePws = 5, RULE_pws = 6;
	public static final String[] ruleNames = {
		"lval", "primaryExpr", "preFixExpr", "multiplicativeExpr", "additiveExpr", 
		"onePws", "pws"
	};

	private static final String[] _LITERAL_NAMES = {
		null, "'('", "')'", "'-'", "'*'", "'/'", "'%'", "'+'", "'P'", "'='", "'E'", 
		"'!='", "'M'", "'X1'", "'X2'", "'Y'", "'<I'", "'N_0'", "'N'", "'Mlt'", 
		"'Meq'", "'Mgt'"
	};
	private static final String[] _SYMBOLIC_NAMES = {
		null, null, null, null, null, null, null, null, null, null, null, null, 
		null, null, null, null, null, null, null, null, null, null, "Ident", "Int", 
		"WS"
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
	public String getGrammarFileName() { return "pws.g4"; }

	@Override
	public String[] getRuleNames() { return ruleNames; }

	@Override
	public String getSerializedATN() { return _serializedATN; }

	@Override
	public ATN getATN() { return _ATN; }

	public pwsParser(TokenStream input) {
		super(input);
		_interp = new ParserATNSimulator(this,_ATN,_decisionToDFA,_sharedContextCache);
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
	public static class LidentContext extends LvalContext {
		public TerminalNode Ident() { return getToken(pwsParser.Ident, 0); }
		public LidentContext(LvalContext ctx) { copyFrom(ctx); }
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof pwsVisitor ) return ((pwsVisitor<? extends T>)visitor).visitLident(this);
			else return visitor.visitChildren(this);
		}
	}

	public final LvalContext lval() throws RecognitionException {
		LvalContext _localctx = new LvalContext(_ctx, getState());
		enterRule(_localctx, 0, RULE_lval);
		try {
			_localctx = new LidentContext(_localctx);
			enterOuterAlt(_localctx, 1);
			{
			setState(14);
			match(Ident);
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
		public TerminalNode Int() { return getToken(pwsParser.Int, 0); }
		public ConstContext(PrimaryExprContext ctx) { copyFrom(ctx); }
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof pwsVisitor ) return ((pwsVisitor<? extends T>)visitor).visitConst(this);
			else return visitor.visitChildren(this);
		}
	}
	public static class EparenContext extends PrimaryExprContext {
		public AdditiveExprContext additiveExpr() {
			return getRuleContext(AdditiveExprContext.class,0);
		}
		public EparenContext(PrimaryExprContext ctx) { copyFrom(ctx); }
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof pwsVisitor ) return ((pwsVisitor<? extends T>)visitor).visitEparen(this);
			else return visitor.visitChildren(this);
		}
	}
	public static class ElvalContext extends PrimaryExprContext {
		public LvalContext lval() {
			return getRuleContext(LvalContext.class,0);
		}
		public ElvalContext(PrimaryExprContext ctx) { copyFrom(ctx); }
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof pwsVisitor ) return ((pwsVisitor<? extends T>)visitor).visitElval(this);
			else return visitor.visitChildren(this);
		}
	}

	public final PrimaryExprContext primaryExpr() throws RecognitionException {
		PrimaryExprContext _localctx = new PrimaryExprContext(_ctx, getState());
		enterRule(_localctx, 2, RULE_primaryExpr);
		try {
			setState(22);
			switch (_input.LA(1)) {
			case Ident:
				_localctx = new ElvalContext(_localctx);
				enterOuterAlt(_localctx, 1);
				{
				setState(16);
				lval();
				}
				break;
			case Int:
				_localctx = new ConstContext(_localctx);
				enterOuterAlt(_localctx, 2);
				{
				setState(17);
				match(Int);
				}
				break;
			case T__0:
				_localctx = new EparenContext(_localctx);
				enterOuterAlt(_localctx, 3);
				{
				setState(18);
				match(T__0);
				setState(19);
				additiveExpr(0);
				setState(20);
				match(T__1);
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
	public static class EPrePrimContext extends PreFixExprContext {
		public PrimaryExprContext primaryExpr() {
			return getRuleContext(PrimaryExprContext.class,0);
		}
		public EPrePrimContext(PreFixExprContext ctx) { copyFrom(ctx); }
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof pwsVisitor ) return ((pwsVisitor<? extends T>)visitor).visitEPrePrim(this);
			else return visitor.visitChildren(this);
		}
	}
	public static class EPrePreContext extends PreFixExprContext {
		public Token prefix;
		public PrimaryExprContext primaryExpr() {
			return getRuleContext(PrimaryExprContext.class,0);
		}
		public EPrePreContext(PreFixExprContext ctx) { copyFrom(ctx); }
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof pwsVisitor ) return ((pwsVisitor<? extends T>)visitor).visitEPrePre(this);
			else return visitor.visitChildren(this);
		}
	}

	public final PreFixExprContext preFixExpr() throws RecognitionException {
		PreFixExprContext _localctx = new PreFixExprContext(_ctx, getState());
		enterRule(_localctx, 4, RULE_preFixExpr);
		try {
			setState(27);
			switch (_input.LA(1)) {
			case T__0:
			case Ident:
			case Int:
				_localctx = new EPrePrimContext(_localctx);
				enterOuterAlt(_localctx, 1);
				{
				setState(24);
				primaryExpr();
				}
				break;
			case T__2:
				_localctx = new EPrePreContext(_localctx);
				enterOuterAlt(_localctx, 2);
				{
				setState(25);
				((EPrePreContext)_localctx).prefix = match(T__2);
				setState(26);
				primaryExpr();
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
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof pwsVisitor ) return ((pwsVisitor<? extends T>)visitor).visitEMultiBin(this);
			else return visitor.visitChildren(this);
		}
	}
	public static class EMultiPreContext extends MultiplicativeExprContext {
		public PreFixExprContext preFixExpr() {
			return getRuleContext(PreFixExprContext.class,0);
		}
		public EMultiPreContext(MultiplicativeExprContext ctx) { copyFrom(ctx); }
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof pwsVisitor ) return ((pwsVisitor<? extends T>)visitor).visitEMultiPre(this);
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
		int _startState = 6;
		enterRecursionRule(_localctx, 6, RULE_multiplicativeExpr, _p);
		int _la;
		try {
			int _alt;
			enterOuterAlt(_localctx, 1);
			{
			{
			_localctx = new EMultiPreContext(_localctx);
			_ctx = _localctx;
			_prevctx = _localctx;

			setState(30);
			preFixExpr();
			}
			_ctx.stop = _input.LT(-1);
			setState(37);
			_errHandler.sync(this);
			_alt = getInterpreter().adaptivePredict(_input,2,_ctx);
			while ( _alt!=2 && _alt!=org.antlr.v4.runtime.atn.ATN.INVALID_ALT_NUMBER ) {
				if ( _alt==1 ) {
					if ( _parseListeners!=null ) triggerExitRuleEvent();
					_prevctx = _localctx;
					{
					{
					_localctx = new EMultiBinContext(new MultiplicativeExprContext(_parentctx, _parentState));
					pushNewRecursionContext(_localctx, _startState, RULE_multiplicativeExpr);
					setState(32);
					if (!(precpred(_ctx, 1))) throw new FailedPredicateException(this, "precpred(_ctx, 1)");
					setState(33);
					((EMultiBinContext)_localctx).op = _input.LT(1);
					_la = _input.LA(1);
					if ( !((((_la) & ~0x3f) == 0 && ((1L << _la) & ((1L << T__3) | (1L << T__4) | (1L << T__5))) != 0)) ) {
						((EMultiBinContext)_localctx).op = (Token)_errHandler.recoverInline(this);
					} else {
						consume();
					}
					setState(34);
					preFixExpr();
					}
					} 
				}
				setState(39);
				_errHandler.sync(this);
				_alt = getInterpreter().adaptivePredict(_input,2,_ctx);
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
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof pwsVisitor ) return ((pwsVisitor<? extends T>)visitor).visitEAddMult(this);
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
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof pwsVisitor ) return ((pwsVisitor<? extends T>)visitor).visitEAddBin(this);
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
		int _startState = 8;
		enterRecursionRule(_localctx, 8, RULE_additiveExpr, _p);
		int _la;
		try {
			int _alt;
			enterOuterAlt(_localctx, 1);
			{
			{
			_localctx = new EAddMultContext(_localctx);
			_ctx = _localctx;
			_prevctx = _localctx;

			setState(41);
			multiplicativeExpr(0);
			}
			_ctx.stop = _input.LT(-1);
			setState(48);
			_errHandler.sync(this);
			_alt = getInterpreter().adaptivePredict(_input,3,_ctx);
			while ( _alt!=2 && _alt!=org.antlr.v4.runtime.atn.ATN.INVALID_ALT_NUMBER ) {
				if ( _alt==1 ) {
					if ( _parseListeners!=null ) triggerExitRuleEvent();
					_prevctx = _localctx;
					{
					{
					_localctx = new EAddBinContext(new AdditiveExprContext(_parentctx, _parentState));
					pushNewRecursionContext(_localctx, _startState, RULE_additiveExpr);
					setState(43);
					if (!(precpred(_ctx, 1))) throw new FailedPredicateException(this, "precpred(_ctx, 1)");
					setState(44);
					((EAddBinContext)_localctx).op = _input.LT(1);
					_la = _input.LA(1);
					if ( !(_la==T__2 || _la==T__6) ) {
						((EAddBinContext)_localctx).op = (Token)_errHandler.recoverInline(this);
					} else {
						consume();
					}
					setState(45);
					multiplicativeExpr(0);
					}
					} 
				}
				setState(50);
				_errHandler.sync(this);
				_alt = getInterpreter().adaptivePredict(_input,3,_ctx);
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

	public static class OnePwsContext extends ParserRuleContext {
		public OnePwsContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_onePws; }
	 
		public OnePwsContext() { }
		public void copyFrom(OnePwsContext ctx) {
			super.copyFrom(ctx);
		}
	}
	public static class PwsLTContext extends OnePwsContext {
		public List<TerminalNode> Ident() { return getTokens(pwsParser.Ident); }
		public TerminalNode Ident(int i) {
			return getToken(pwsParser.Ident, i);
		}
		public TerminalNode Int() { return getToken(pwsParser.Int, 0); }
		public List<AdditiveExprContext> additiveExpr() {
			return getRuleContexts(AdditiveExprContext.class);
		}
		public AdditiveExprContext additiveExpr(int i) {
			return getRuleContext(AdditiveExprContext.class,i);
		}
		public PwsLTContext(OnePwsContext ctx) { copyFrom(ctx); }
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof pwsVisitor ) return ((pwsVisitor<? extends T>)visitor).visitPwsLT(this);
			else return visitor.visitChildren(this);
		}
	}
	public static class PwsEmptyContext extends OnePwsContext {
		public TerminalNode Ident() { return getToken(pwsParser.Ident, 0); }
		public PwsEmptyContext(OnePwsContext ctx) { copyFrom(ctx); }
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof pwsVisitor ) return ((pwsVisitor<? extends T>)visitor).visitPwsEmpty(this);
			else return visitor.visitChildren(this);
		}
	}
	public static class PwsNonEmptyContext extends OnePwsContext {
		public TerminalNode Ident() { return getToken(pwsParser.Ident, 0); }
		public AdditiveExprContext additiveExpr() {
			return getRuleContext(AdditiveExprContext.class,0);
		}
		public PwsNonEmptyContext(OnePwsContext ctx) { copyFrom(ctx); }
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof pwsVisitor ) return ((pwsVisitor<? extends T>)visitor).visitPwsNonEmpty(this);
			else return visitor.visitChildren(this);
		}
	}
	public static class PwsNEQContext extends OnePwsContext {
		public List<TerminalNode> Ident() { return getTokens(pwsParser.Ident); }
		public TerminalNode Ident(int i) {
			return getToken(pwsParser.Ident, i);
		}
		public List<AdditiveExprContext> additiveExpr() {
			return getRuleContexts(AdditiveExprContext.class);
		}
		public AdditiveExprContext additiveExpr(int i) {
			return getRuleContext(AdditiveExprContext.class,i);
		}
		public PwsNEQContext(OnePwsContext ctx) { copyFrom(ctx); }
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof pwsVisitor ) return ((pwsVisitor<? extends T>)visitor).visitPwsNEQ(this);
			else return visitor.visitChildren(this);
		}
	}

	public final OnePwsContext onePws() throws RecognitionException {
		OnePwsContext _localctx = new OnePwsContext(_ctx, getState());
		enterRule(_localctx, 10, RULE_onePws);
		try {
			setState(89);
			_errHandler.sync(this);
			switch ( getInterpreter().adaptivePredict(_input,4,_ctx) ) {
			case 1:
				_localctx = new PwsEmptyContext(_localctx);
				enterOuterAlt(_localctx, 1);
				{
				setState(51);
				match(T__7);
				setState(52);
				match(Ident);
				setState(53);
				match(T__8);
				setState(54);
				match(T__9);
				}
				break;
			case 2:
				_localctx = new PwsNonEmptyContext(_localctx);
				enterOuterAlt(_localctx, 2);
				{
				setState(55);
				match(T__7);
				setState(56);
				match(Ident);
				setState(57);
				match(T__8);
				setState(58);
				additiveExpr(0);
				setState(59);
				match(T__9);
				}
				break;
			case 3:
				_localctx = new PwsNEQContext(_localctx);
				enterOuterAlt(_localctx, 3);
				{
				setState(61);
				match(T__10);
				setState(62);
				match(T__11);
				setState(63);
				match(Ident);
				setState(64);
				match(T__12);
				setState(65);
				additiveExpr(0);
				setState(66);
				match(T__13);
				setState(67);
				additiveExpr(0);
				setState(68);
				match(T__14);
				setState(69);
				match(Ident);
				}
				break;
			case 4:
				_localctx = new PwsLTContext(_localctx);
				enterOuterAlt(_localctx, 4);
				{
				setState(71);
				match(T__15);
				setState(72);
				match(T__16);
				setState(73);
				match(Ident);
				setState(74);
				match(T__17);
				setState(75);
				match(Int);
				setState(76);
				match(T__18);
				setState(77);
				match(Ident);
				setState(78);
				match(T__19);
				setState(79);
				match(Ident);
				setState(80);
				match(T__20);
				setState(81);
				match(Ident);
				setState(82);
				match(T__12);
				setState(83);
				additiveExpr(0);
				setState(84);
				match(T__13);
				setState(85);
				additiveExpr(0);
				setState(86);
				match(T__14);
				setState(87);
				match(Ident);
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

	public static class PwsContext extends ParserRuleContext {
		public PwsContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_pws; }
	 
		public PwsContext() { }
		public void copyFrom(PwsContext ctx) {
			super.copyFrom(ctx);
		}
	}
	public static class PwsOneOrMoreContext extends PwsContext {
		public TerminalNode EOF() { return getToken(pwsParser.EOF, 0); }
		public List<OnePwsContext> onePws() {
			return getRuleContexts(OnePwsContext.class);
		}
		public OnePwsContext onePws(int i) {
			return getRuleContext(OnePwsContext.class,i);
		}
		public PwsOneOrMoreContext(PwsContext ctx) { copyFrom(ctx); }
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof pwsVisitor ) return ((pwsVisitor<? extends T>)visitor).visitPwsOneOrMore(this);
			else return visitor.visitChildren(this);
		}
	}

	public final PwsContext pws() throws RecognitionException {
		PwsContext _localctx = new PwsContext(_ctx, getState());
		enterRule(_localctx, 12, RULE_pws);
		int _la;
		try {
			_localctx = new PwsOneOrMoreContext(_localctx);
			enterOuterAlt(_localctx, 1);
			{
			setState(94);
			_errHandler.sync(this);
			_la = _input.LA(1);
			while ((((_la) & ~0x3f) == 0 && ((1L << _la) & ((1L << T__7) | (1L << T__10) | (1L << T__15))) != 0)) {
				{
				{
				setState(91);
				onePws();
				}
				}
				setState(96);
				_errHandler.sync(this);
				_la = _input.LA(1);
			}
			setState(97);
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

	public boolean sempred(RuleContext _localctx, int ruleIndex, int predIndex) {
		switch (ruleIndex) {
		case 3:
			return multiplicativeExpr_sempred((MultiplicativeExprContext)_localctx, predIndex);
		case 4:
			return additiveExpr_sempred((AdditiveExprContext)_localctx, predIndex);
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

	public static final String _serializedATN =
		"\3\u0430\ud6d1\u8206\uad2d\u4417\uaef1\u8d80\uaadd\3\32f\4\2\t\2\4\3\t"+
		"\3\4\4\t\4\4\5\t\5\4\6\t\6\4\7\t\7\4\b\t\b\3\2\3\2\3\3\3\3\3\3\3\3\3\3"+
		"\3\3\5\3\31\n\3\3\4\3\4\3\4\5\4\36\n\4\3\5\3\5\3\5\3\5\3\5\3\5\7\5&\n"+
		"\5\f\5\16\5)\13\5\3\6\3\6\3\6\3\6\3\6\3\6\7\6\61\n\6\f\6\16\6\64\13\6"+
		"\3\7\3\7\3\7\3\7\3\7\3\7\3\7\3\7\3\7\3\7\3\7\3\7\3\7\3\7\3\7\3\7\3\7\3"+
		"\7\3\7\3\7\3\7\3\7\3\7\3\7\3\7\3\7\3\7\3\7\3\7\3\7\3\7\3\7\3\7\3\7\3\7"+
		"\3\7\3\7\3\7\5\7\\\n\7\3\b\7\b_\n\b\f\b\16\bb\13\b\3\b\3\b\3\b\2\4\b\n"+
		"\t\2\4\6\b\n\f\16\2\4\3\2\6\b\4\2\5\5\t\tg\2\20\3\2\2\2\4\30\3\2\2\2\6"+
		"\35\3\2\2\2\b\37\3\2\2\2\n*\3\2\2\2\f[\3\2\2\2\16`\3\2\2\2\20\21\7\30"+
		"\2\2\21\3\3\2\2\2\22\31\5\2\2\2\23\31\7\31\2\2\24\25\7\3\2\2\25\26\5\n"+
		"\6\2\26\27\7\4\2\2\27\31\3\2\2\2\30\22\3\2\2\2\30\23\3\2\2\2\30\24\3\2"+
		"\2\2\31\5\3\2\2\2\32\36\5\4\3\2\33\34\7\5\2\2\34\36\5\4\3\2\35\32\3\2"+
		"\2\2\35\33\3\2\2\2\36\7\3\2\2\2\37 \b\5\1\2 !\5\6\4\2!\'\3\2\2\2\"#\f"+
		"\3\2\2#$\t\2\2\2$&\5\6\4\2%\"\3\2\2\2&)\3\2\2\2\'%\3\2\2\2\'(\3\2\2\2"+
		"(\t\3\2\2\2)\'\3\2\2\2*+\b\6\1\2+,\5\b\5\2,\62\3\2\2\2-.\f\3\2\2./\t\3"+
		"\2\2/\61\5\b\5\2\60-\3\2\2\2\61\64\3\2\2\2\62\60\3\2\2\2\62\63\3\2\2\2"+
		"\63\13\3\2\2\2\64\62\3\2\2\2\65\66\7\n\2\2\66\67\7\30\2\2\678\7\13\2\2"+
		"8\\\7\f\2\29:\7\n\2\2:;\7\30\2\2;<\7\13\2\2<=\5\n\6\2=>\7\f\2\2>\\\3\2"+
		"\2\2?@\7\r\2\2@A\7\16\2\2AB\7\30\2\2BC\7\17\2\2CD\5\n\6\2DE\7\20\2\2E"+
		"F\5\n\6\2FG\7\21\2\2GH\7\30\2\2H\\\3\2\2\2IJ\7\22\2\2JK\7\23\2\2KL\7\30"+
		"\2\2LM\7\24\2\2MN\7\31\2\2NO\7\25\2\2OP\7\30\2\2PQ\7\26\2\2QR\7\30\2\2"+
		"RS\7\27\2\2ST\7\30\2\2TU\7\17\2\2UV\5\n\6\2VW\7\20\2\2WX\5\n\6\2XY\7\21"+
		"\2\2YZ\7\30\2\2Z\\\3\2\2\2[\65\3\2\2\2[9\3\2\2\2[?\3\2\2\2[I\3\2\2\2\\"+
		"\r\3\2\2\2]_\5\f\7\2^]\3\2\2\2_b\3\2\2\2`^\3\2\2\2`a\3\2\2\2ac\3\2\2\2"+
		"b`\3\2\2\2cd\7\2\2\3d\17\3\2\2\2\b\30\35\'\62[`";
	public static final ATN _ATN =
		new ATNDeserializer().deserialize(_serializedATN.toCharArray());
	static {
		_decisionToDFA = new DFA[_ATN.getNumberOfDecisions()];
		for (int i = 0; i < _ATN.getNumberOfDecisions(); i++) {
			_decisionToDFA[i] = new DFA(_ATN.getDecisionState(i), i);
		}
	}
}
