package gen;
import org.antlr.v4.runtime.Lexer;
import org.antlr.v4.runtime.CharStream;
import org.antlr.v4.runtime.Token;
import org.antlr.v4.runtime.TokenStream;
import org.antlr.v4.runtime.*;
import org.antlr.v4.runtime.atn.*;
import org.antlr.v4.runtime.dfa.DFA;
import org.antlr.v4.runtime.misc.*;

@SuppressWarnings({"all", "warnings", "unchecked", "unused", "cast"})
public class cLexer extends Lexer {
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
	public static String[] modeNames = {
		"DEFAULT_MODE"
	};

	public static final String[] ruleNames = {
		"T__0", "T__1", "T__2", "T__3", "T__4", "T__5", "T__6", "T__7", "T__8", 
		"T__9", "T__10", "T__11", "T__12", "T__13", "T__14", "T__15", "T__16", 
		"T__17", "T__18", "T__19", "T__20", "T__21", "T__22", "T__23", "T__24", 
		"T__25", "T__26", "T__27", "T__28", "T__29", "T__30", "T__31", "T__32", 
		"T__33", "T__34", "T__35", "T__36", "T__37", "T__38", "T__39", "T__40", 
		"T__41", "T__42", "T__43", "T__44", "T__45", "T__46", "T__47", "T__48", 
		"T__49", "T__50", "T__51", "Ident", "COMMENT", "SCOMMENT", "INCLUDE", 
		"SHARPCOMMENT", "IntType", "Int", "WS"
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


	public cLexer(CharStream input) {
		super(input);
		_interp = new LexerATNSimulator(this,_ATN,_decisionToDFA,_sharedContextCache);
	}

	@Override
	public String getGrammarFileName() { return "c.g4"; }

	@Override
	public String[] getRuleNames() { return ruleNames; }

	@Override
	public String getSerializedATN() { return _serializedATN; }

	@Override
	public String[] getModeNames() { return modeNames; }

	@Override
	public ATN getATN() { return _ATN; }

	public static final String _serializedATN =
		"\3\u0430\ud6d1\u8206\uad2d\u4417\uaef1\u8d80\uaadd\2>\u01d1\b\1\4\2\t"+
		"\2\4\3\t\3\4\4\t\4\4\5\t\5\4\6\t\6\4\7\t\7\4\b\t\b\4\t\t\t\4\n\t\n\4\13"+
		"\t\13\4\f\t\f\4\r\t\r\4\16\t\16\4\17\t\17\4\20\t\20\4\21\t\21\4\22\t\22"+
		"\4\23\t\23\4\24\t\24\4\25\t\25\4\26\t\26\4\27\t\27\4\30\t\30\4\31\t\31"+
		"\4\32\t\32\4\33\t\33\4\34\t\34\4\35\t\35\4\36\t\36\4\37\t\37\4 \t \4!"+
		"\t!\4\"\t\"\4#\t#\4$\t$\4%\t%\4&\t&\4\'\t\'\4(\t(\4)\t)\4*\t*\4+\t+\4"+
		",\t,\4-\t-\4.\t.\4/\t/\4\60\t\60\4\61\t\61\4\62\t\62\4\63\t\63\4\64\t"+
		"\64\4\65\t\65\4\66\t\66\4\67\t\67\48\t8\49\t9\4:\t:\4;\t;\4<\t<\4=\t="+
		"\3\2\3\2\3\2\3\2\3\2\3\2\3\2\3\2\3\3\3\3\3\4\3\4\3\5\3\5\3\6\3\6\3\6\3"+
		"\7\3\7\3\7\3\b\3\b\3\t\3\t\3\n\3\n\3\13\3\13\3\f\3\f\3\r\3\r\3\16\3\16"+
		"\3\17\3\17\3\20\3\20\3\21\3\21\3\21\3\22\3\22\3\22\3\23\3\23\3\23\3\24"+
		"\3\24\3\24\3\25\3\25\3\25\3\26\3\26\3\26\3\27\3\27\3\30\3\30\3\31\3\31"+
		"\3\31\3\32\3\32\3\33\3\33\3\34\3\34\3\35\3\35\3\35\3\35\3\36\3\36\3\36"+
		"\3\36\3\36\3\37\3\37\3\37\3\37\3\37\3\37\3\37\3 \3 \3!\3!\3!\3\"\3\"\3"+
		"\"\3#\3#\3#\3$\3$\3$\3%\3%\3%\3&\3&\3\'\3\'\3(\3(\3)\3)\3)\3*\3*\3*\3"+
		"*\3*\3+\3+\3+\3+\3+\3+\3,\3,\3,\3,\3,\3,\3,\3,\3,\3,\3,\3,\3-\3-\3-\3"+
		"-\3.\3.\3.\3.\3.\3.\3/\3/\3/\3/\3\60\3\60\3\60\3\60\3\60\3\61\3\61\3\61"+
		"\3\61\3\61\3\61\3\61\3\62\3\62\3\62\3\62\3\62\3\62\3\63\3\63\3\63\3\63"+
		"\3\63\3\63\3\63\3\63\3\63\3\64\3\64\3\64\3\64\3\64\3\64\3\64\3\65\3\65"+
		"\3\65\3\65\3\65\3\66\3\66\7\66\u0139\n\66\f\66\16\66\u013c\13\66\3\67"+
		"\3\67\3\67\3\67\7\67\u0142\n\67\f\67\16\67\u0145\13\67\3\67\3\67\3\67"+
		"\3\67\3\67\38\38\38\38\78\u0150\n8\f8\168\u0153\138\38\38\39\39\39\39"+
		"\39\39\39\39\39\39\79\u0161\n9\f9\169\u0164\139\39\39\39\39\3:\3:\7:\u016c"+
		"\n:\f:\16:\u016f\13:\3:\3:\3;\3;\3;\3;\3;\3;\3;\3;\3;\3;\3;\3;\3;\3;\3"+
		";\3;\3;\3;\3;\3;\3;\3;\3;\3;\3;\3;\3;\3;\3;\3;\3;\3;\3;\3;\3;\3;\3;\3"+
		";\3;\3;\3;\3;\3;\3;\3;\3;\3;\3;\3;\3;\3;\3;\3;\3;\3;\3;\3;\3;\3;\3;\3"+
		";\3;\3;\3;\3;\3;\3;\3;\3;\3;\3;\3;\3;\3;\3;\3;\3;\3;\3;\3;\3;\5;\u01c4"+
		"\n;\3<\6<\u01c7\n<\r<\16<\u01c8\3=\6=\u01cc\n=\r=\16=\u01cd\3=\3=\4\u0143"+
		"\u0162\2>\3\3\5\4\7\5\t\6\13\7\r\b\17\t\21\n\23\13\25\f\27\r\31\16\33"+
		"\17\35\20\37\21!\22#\23%\24\'\25)\26+\27-\30/\31\61\32\63\33\65\34\67"+
		"\359\36;\37= ?!A\"C#E$G%I&K\'M(O)Q*S+U,W-Y.[/]\60_\61a\62c\63e\64g\65"+
		"i\66k\67m8o9q:s;u<w=y>\3\2\6\4\2C\\c|\6\2\62;C\\aac|\4\2\f\f\17\17\5\2"+
		"\13\f\17\17\"\"\u01e3\2\3\3\2\2\2\2\5\3\2\2\2\2\7\3\2\2\2\2\t\3\2\2\2"+
		"\2\13\3\2\2\2\2\r\3\2\2\2\2\17\3\2\2\2\2\21\3\2\2\2\2\23\3\2\2\2\2\25"+
		"\3\2\2\2\2\27\3\2\2\2\2\31\3\2\2\2\2\33\3\2\2\2\2\35\3\2\2\2\2\37\3\2"+
		"\2\2\2!\3\2\2\2\2#\3\2\2\2\2%\3\2\2\2\2\'\3\2\2\2\2)\3\2\2\2\2+\3\2\2"+
		"\2\2-\3\2\2\2\2/\3\2\2\2\2\61\3\2\2\2\2\63\3\2\2\2\2\65\3\2\2\2\2\67\3"+
		"\2\2\2\29\3\2\2\2\2;\3\2\2\2\2=\3\2\2\2\2?\3\2\2\2\2A\3\2\2\2\2C\3\2\2"+
		"\2\2E\3\2\2\2\2G\3\2\2\2\2I\3\2\2\2\2K\3\2\2\2\2M\3\2\2\2\2O\3\2\2\2\2"+
		"Q\3\2\2\2\2S\3\2\2\2\2U\3\2\2\2\2W\3\2\2\2\2Y\3\2\2\2\2[\3\2\2\2\2]\3"+
		"\2\2\2\2_\3\2\2\2\2a\3\2\2\2\2c\3\2\2\2\2e\3\2\2\2\2g\3\2\2\2\2i\3\2\2"+
		"\2\2k\3\2\2\2\2m\3\2\2\2\2o\3\2\2\2\2q\3\2\2\2\2s\3\2\2\2\2u\3\2\2\2\2"+
		"w\3\2\2\2\2y\3\2\2\2\3{\3\2\2\2\5\u0083\3\2\2\2\7\u0085\3\2\2\2\t\u0087"+
		"\3\2\2\2\13\u0089\3\2\2\2\r\u008c\3\2\2\2\17\u008f\3\2\2\2\21\u0091\3"+
		"\2\2\2\23\u0093\3\2\2\2\25\u0095\3\2\2\2\27\u0097\3\2\2\2\31\u0099\3\2"+
		"\2\2\33\u009b\3\2\2\2\35\u009d\3\2\2\2\37\u009f\3\2\2\2!\u00a1\3\2\2\2"+
		"#\u00a4\3\2\2\2%\u00a7\3\2\2\2\'\u00aa\3\2\2\2)\u00ad\3\2\2\2+\u00b0\3"+
		"\2\2\2-\u00b3\3\2\2\2/\u00b5\3\2\2\2\61\u00b7\3\2\2\2\63\u00ba\3\2\2\2"+
		"\65\u00bc\3\2\2\2\67\u00be\3\2\2\29\u00c0\3\2\2\2;\u00c4\3\2\2\2=\u00c9"+
		"\3\2\2\2?\u00d0\3\2\2\2A\u00d2\3\2\2\2C\u00d5\3\2\2\2E\u00d8\3\2\2\2G"+
		"\u00db\3\2\2\2I\u00de\3\2\2\2K\u00e1\3\2\2\2M\u00e3\3\2\2\2O\u00e5\3\2"+
		"\2\2Q\u00e7\3\2\2\2S\u00ea\3\2\2\2U\u00ef\3\2\2\2W\u00f5\3\2\2\2Y\u0101"+
		"\3\2\2\2[\u0105\3\2\2\2]\u010b\3\2\2\2_\u010f\3\2\2\2a\u0114\3\2\2\2c"+
		"\u011b\3\2\2\2e\u0121\3\2\2\2g\u012a\3\2\2\2i\u0131\3\2\2\2k\u0136\3\2"+
		"\2\2m\u013d\3\2\2\2o\u014b\3\2\2\2q\u0156\3\2\2\2s\u0169\3\2\2\2u\u01c3"+
		"\3\2\2\2w\u01c6\3\2\2\2y\u01cb\3\2\2\2{|\7%\2\2|}\7f\2\2}~\7g\2\2~\177"+
		"\7h\2\2\177\u0080\7k\2\2\u0080\u0081\7p\2\2\u0081\u0082\7g\2\2\u0082\4"+
		"\3\2\2\2\u0083\u0084\7*\2\2\u0084\6\3\2\2\2\u0085\u0086\7+\2\2\u0086\b"+
		"\3\2\2\2\u0087\u0088\7.\2\2\u0088\n\3\2\2\2\u0089\u008a\7/\2\2\u008a\u008b"+
		"\7/\2\2\u008b\f\3\2\2\2\u008c\u008d\7-\2\2\u008d\u008e\7-\2\2\u008e\16"+
		"\3\2\2\2\u008f\u0090\7/\2\2\u0090\20\3\2\2\2\u0091\u0092\7#\2\2\u0092"+
		"\22\3\2\2\2\u0093\u0094\7(\2\2\u0094\24\3\2\2\2\u0095\u0096\7,\2\2\u0096"+
		"\26\3\2\2\2\u0097\u0098\7\61\2\2\u0098\30\3\2\2\2\u0099\u009a\7\'\2\2"+
		"\u009a\32\3\2\2\2\u009b\u009c\7-\2\2\u009c\34\3\2\2\2\u009d\u009e\7>\2"+
		"\2\u009e\36\3\2\2\2\u009f\u00a0\7@\2\2\u00a0 \3\2\2\2\u00a1\u00a2\7>\2"+
		"\2\u00a2\u00a3\7?\2\2\u00a3\"\3\2\2\2\u00a4\u00a5\7@\2\2\u00a5\u00a6\7"+
		"?\2\2\u00a6$\3\2\2\2\u00a7\u00a8\7?\2\2\u00a8\u00a9\7?\2\2\u00a9&\3\2"+
		"\2\2\u00aa\u00ab\7#\2\2\u00ab\u00ac\7?\2\2\u00ac(\3\2\2\2\u00ad\u00ae"+
		"\7(\2\2\u00ae\u00af\7(\2\2\u00af*\3\2\2\2\u00b0\u00b1\7~\2\2\u00b1\u00b2"+
		"\7~\2\2\u00b2,\3\2\2\2\u00b3\u00b4\7A\2\2\u00b4.\3\2\2\2\u00b5\u00b6\7"+
		"<\2\2\u00b6\60\3\2\2\2\u00b7\u00b8\7/\2\2\u00b8\u00b9\7@\2\2\u00b9\62"+
		"\3\2\2\2\u00ba\u00bb\7\60\2\2\u00bb\64\3\2\2\2\u00bc\u00bd\7]\2\2\u00bd"+
		"\66\3\2\2\2\u00be\u00bf\7_\2\2\u00bf8\3\2\2\2\u00c0\u00c1\7k\2\2\u00c1"+
		"\u00c2\7p\2\2\u00c2\u00c3\7v\2\2\u00c3:\3\2\2\2\u00c4\u00c5\7x\2\2\u00c5"+
		"\u00c6\7q\2\2\u00c6\u00c7\7k\2\2\u00c7\u00c8\7f\2\2\u00c8<\3\2\2\2\u00c9"+
		"\u00ca\7u\2\2\u00ca\u00cb\7v\2\2\u00cb\u00cc\7t\2\2\u00cc\u00cd\7w\2\2"+
		"\u00cd\u00ce\7e\2\2\u00ce\u00cf\7v\2\2\u00cf>\3\2\2\2\u00d0\u00d1\7?\2"+
		"\2\u00d1@\3\2\2\2\u00d2\u00d3\7-\2\2\u00d3\u00d4\7?\2\2\u00d4B\3\2\2\2"+
		"\u00d5\u00d6\7,\2\2\u00d6\u00d7\7?\2\2\u00d7D\3\2\2\2\u00d8\u00d9\7\61"+
		"\2\2\u00d9\u00da\7?\2\2\u00daF\3\2\2\2\u00db\u00dc\7\'\2\2\u00dc\u00dd"+
		"\7?\2\2\u00ddH\3\2\2\2\u00de\u00df\7/\2\2\u00df\u00e0\7?\2\2\u00e0J\3"+
		"\2\2\2\u00e1\u00e2\7=\2\2\u00e2L\3\2\2\2\u00e3\u00e4\7}\2\2\u00e4N\3\2"+
		"\2\2\u00e5\u00e6\7\177\2\2\u00e6P\3\2\2\2\u00e7\u00e8\7k\2\2\u00e8\u00e9"+
		"\7h\2\2\u00e9R\3\2\2\2\u00ea\u00eb\7g\2\2\u00eb\u00ec\7n\2\2\u00ec\u00ed"+
		"\7u\2\2\u00ed\u00ee\7g\2\2\u00eeT\3\2\2\2\u00ef\u00f0\7q\2\2\u00f0\u00f1"+
		"\7w\2\2\u00f1\u00f2\7v\2\2\u00f2\u00f3\7\"\2\2\u00f3\u00f4\7}\2\2\u00f4"+
		"V\3\2\2\2\u00f5\u00f6\7q\2\2\u00f6\u00f7\7w\2\2\u00f7\u00f8\7v\2\2\u00f8"+
		"\u00f9\7u\2\2\u00f9\u00fa\7q\2\2\u00fa\u00fb\7w\2\2\u00fb\u00fc\7t\2\2"+
		"\u00fc\u00fd\7e\2\2\u00fd\u00fe\7g\2\2\u00fe\u00ff\7\"\2\2\u00ff\u0100"+
		"\7}\2\2\u0100X\3\2\2\2\u0101\u0102\7\177\2\2\u0102\u0103\7\"\2\2\u0103"+
		"\u0104\7}\2\2\u0104Z\3\2\2\2\u0105\u0106\7y\2\2\u0106\u0107\7j\2\2\u0107"+
		"\u0108\7k\2\2\u0108\u0109\7n\2\2\u0109\u010a\7g\2\2\u010a\\\3\2\2\2\u010b"+
		"\u010c\7h\2\2\u010c\u010d\7q\2\2\u010d\u010e\7t\2\2\u010e^\3\2\2\2\u010f"+
		"\u0110\7q\2\2\u0110\u0111\7h\2\2\u0111\u0112\7q\2\2\u0112\u0113\7t\2\2"+
		"\u0113`\3\2\2\2\u0114\u0115\7t\2\2\u0115\u0116\7g\2\2\u0116\u0117\7v\2"+
		"\2\u0117\u0118\7w\2\2\u0118\u0119\7t\2\2\u0119\u011a\7p\2\2\u011ab\3\2"+
		"\2\2\u011b\u011c\7d\2\2\u011c\u011d\7t\2\2\u011d\u011e\7g\2\2\u011e\u011f"+
		"\7c\2\2\u011f\u0120\7m\2\2\u0120d\3\2\2\2\u0121\u0122\7e\2\2\u0122\u0123"+
		"\7q\2\2\u0123\u0124\7p\2\2\u0124\u0125\7v\2\2\u0125\u0126\7k\2\2\u0126"+
		"\u0127\7p\2\2\u0127\u0128\7w\2\2\u0128\u0129\7g\2\2\u0129f\3\2\2\2\u012a"+
		"\u012b\7a\2\2\u012b\u012c\7d\2\2\u012c\u012d\7g\2\2\u012d\u012e\7i\2\2"+
		"\u012e\u012f\7k\2\2\u012f\u0130\7p\2\2\u0130h\3\2\2\2\u0131\u0132\7a\2"+
		"\2\u0132\u0133\7g\2\2\u0133\u0134\7p\2\2\u0134\u0135\7f\2\2\u0135j\3\2"+
		"\2\2\u0136\u013a\t\2\2\2\u0137\u0139\t\3\2\2\u0138\u0137\3\2\2\2\u0139"+
		"\u013c\3\2\2\2\u013a\u0138\3\2\2\2\u013a\u013b\3\2\2\2\u013bl\3\2\2\2"+
		"\u013c\u013a\3\2\2\2\u013d\u013e\7\61\2\2\u013e\u013f\7,\2\2\u013f\u0143"+
		"\3\2\2\2\u0140\u0142\13\2\2\2\u0141\u0140\3\2\2\2\u0142\u0145\3\2\2\2"+
		"\u0143\u0144\3\2\2\2\u0143\u0141\3\2\2\2\u0144\u0146\3\2\2\2\u0145\u0143"+
		"\3\2\2\2\u0146\u0147\7,\2\2\u0147\u0148\7\61\2\2\u0148\u0149\3\2\2\2\u0149"+
		"\u014a\b\67\2\2\u014an\3\2\2\2\u014b\u014c\7\61\2\2\u014c\u014d\7\61\2"+
		"\2\u014d\u0151\3\2\2\2\u014e\u0150\n\4\2\2\u014f\u014e\3\2\2\2\u0150\u0153"+
		"\3\2\2\2\u0151\u014f\3\2\2\2\u0151\u0152\3\2\2\2\u0152\u0154\3\2\2\2\u0153"+
		"\u0151\3\2\2\2\u0154\u0155\b8\2\2\u0155p\3\2\2\2\u0156\u0157\7%\2\2\u0157"+
		"\u0158\7k\2\2\u0158\u0159\7p\2\2\u0159\u015a\7e\2\2\u015a\u015b\7n\2\2"+
		"\u015b\u015c\7w\2\2\u015c\u015d\7f\2\2\u015d\u015e\7g\2\2\u015e\u0162"+
		"\3\2\2\2\u015f\u0161\13\2\2\2\u0160\u015f\3\2\2\2\u0161\u0164\3\2\2\2"+
		"\u0162\u0163\3\2\2\2\u0162\u0160\3\2\2\2\u0163\u0165\3\2\2\2\u0164\u0162"+
		"\3\2\2\2\u0165\u0166\7\f\2\2\u0166\u0167\3\2\2\2\u0167\u0168\b9\2\2\u0168"+
		"r\3\2\2\2\u0169\u016d\7%\2\2\u016a\u016c\n\4\2\2\u016b\u016a\3\2\2\2\u016c"+
		"\u016f\3\2\2\2\u016d\u016b\3\2\2\2\u016d\u016e\3\2\2\2\u016e\u0170\3\2"+
		"\2\2\u016f\u016d\3\2\2\2\u0170\u0171\b:\2\2\u0171t\3\2\2\2\u0172\u0173"+
		"\7k\2\2\u0173\u0174\7p\2\2\u0174\u01c4\7v\2\2\u0175\u0176\7w\2\2\u0176"+
		"\u0177\7k\2\2\u0177\u0178\7p\2\2\u0178\u0179\7v\2\2\u0179\u017a\7\65\2"+
		"\2\u017a\u017b\7\64\2\2\u017b\u017c\7a\2\2\u017c\u01c4\7v\2\2\u017d\u017e"+
		"\7w\2\2\u017e\u017f\7k\2\2\u017f\u0180\7p\2\2\u0180\u0181\7v\2\2\u0181"+
		"\u0182\7:\2\2\u0182\u0183\7a\2\2\u0183\u01c4\7v\2\2\u0184\u0185\7k\2\2"+
		"\u0185\u0186\7p\2\2\u0186\u0187\7v\2\2\u0187\u0188\7:\2\2\u0188\u0189"+
		"\7a\2\2\u0189\u01c4\7v\2\2\u018a\u018b\7k\2\2\u018b\u018c\7p\2\2\u018c"+
		"\u018d\7v\2\2\u018d\u018e\7\65\2\2\u018e\u018f\7\64\2\2\u018f\u0190\7"+
		"a\2\2\u0190\u01c4\7v\2\2\u0191\u0192\7w\2\2\u0192\u0193\7k\2\2\u0193\u0194"+
		"\7p\2\2\u0194\u0195\7v\2\2\u0195\u0196\7\63\2\2\u0196\u0197\78\2\2\u0197"+
		"\u0198\7a\2\2\u0198\u01c4\7v\2\2\u0199\u019a\7k\2\2\u019a\u019b\7p\2\2"+
		"\u019b\u019c\7v\2\2\u019c\u019d\7\63\2\2\u019d\u019e\78\2\2\u019e\u019f"+
		"\7a\2\2\u019f\u01c4\7v\2\2\u01a0\u01a1\7k\2\2\u01a1\u01a2\7p\2\2\u01a2"+
		"\u01a3\7v\2\2\u01a3\u01a4\78\2\2\u01a4\u01a5\7\66\2\2\u01a5\u01a6\7a\2"+
		"\2\u01a6\u01c4\7v\2\2\u01a7\u01a8\7w\2\2\u01a8\u01a9\7k\2\2\u01a9\u01aa"+
		"\7p\2\2\u01aa\u01ab\7v\2\2\u01ab\u01ac\78\2\2\u01ac\u01ad\7\66\2\2\u01ad"+
		"\u01ae\7a\2\2\u01ae\u01c4\7v\2\2\u01af\u01b0\7e\2\2\u01b0\u01b1\7j\2\2"+
		"\u01b1\u01b2\7c\2\2\u01b2\u01c4\7t\2\2\u01b3\u01b4\7d\2\2\u01b4\u01b5"+
		"\7q\2\2\u01b5\u01b6\7q\2\2\u01b6\u01c4\7n\2\2\u01b7\u01b8\7k\2\2\u01b8"+
		"\u01b9\7p\2\2\u01b9\u01ba\7v\2\2\u01ba\u01bb\7a\2\2\u01bb\u01bc\78\2\2"+
		"\u01bc\u01bd\7\66\2\2\u01bd\u01c4\7v\2\2\u01be\u01bf\7p\2\2\u01bf\u01c0"+
		"\7w\2\2\u01c0\u01c1\7o\2\2\u01c1\u01c2\7a\2\2\u01c2\u01c4\7v\2\2\u01c3"+
		"\u0172\3\2\2\2\u01c3\u0175\3\2\2\2\u01c3\u017d\3\2\2\2\u01c3\u0184\3\2"+
		"\2\2\u01c3\u018a\3\2\2\2\u01c3\u0191\3\2\2\2\u01c3\u0199\3\2\2\2\u01c3"+
		"\u01a0\3\2\2\2\u01c3\u01a7\3\2\2\2\u01c3\u01af\3\2\2\2\u01c3\u01b3\3\2"+
		"\2\2\u01c3\u01b7\3\2\2\2\u01c3\u01be\3\2\2\2\u01c4v\3\2\2\2\u01c5\u01c7"+
		"\4\62;\2\u01c6\u01c5\3\2\2\2\u01c7\u01c8\3\2\2\2\u01c8\u01c6\3\2\2\2\u01c8"+
		"\u01c9\3\2\2\2\u01c9x\3\2\2\2\u01ca\u01cc\t\5\2\2\u01cb\u01ca\3\2\2\2"+
		"\u01cc\u01cd\3\2\2\2\u01cd\u01cb\3\2\2\2\u01cd\u01ce\3\2\2\2\u01ce\u01cf"+
		"\3\2\2\2\u01cf\u01d0\b=\2\2\u01d0z\3\2\2\2\13\2\u013a\u0143\u0151\u0162"+
		"\u016d\u01c3\u01c8\u01cd\3\b\2\2";
	public static final ATN _ATN =
		new ATNDeserializer().deserialize(_serializedATN.toCharArray());
	static {
		_decisionToDFA = new DFA[_ATN.getNumberOfDecisions()];
		for (int i = 0; i < _ATN.getNumberOfDecisions(); i++) {
			_decisionToDFA[i] = new DFA(_ATN.getDecisionState(i), i);
		}
	}
}
