
package peppermint.Parser
import java.io.FileInputStream

import scala.collection.JavaConversions._
import scala.util.parsing.combinator._
import scala.util.parsing.combinator._
import scala.util.parsing.combinator.lexical._
import scala.util.parsing.combinator.token._
import scala.util.parsing.combinator.syntactical._
import scala.util.parsing.input._
import org.antlr.v4.runtime._
import peppermint.ILAST._
import gen._
//import gen.cParser.{_}
import org.antlr.v4.runtime.tree.{ErrorNode, ParseTree, RuleNode, TerminalNode}

class PWSV extends pwsBaseVisitor[Any] {
  import gen.pwsParser.{_}
  override def visitPwsEmpty(ctx : PwsEmptyContext) : SAssign = {
    SAssign(VIdent(ctx.Ident().getText,Vector()), EConstant(0))
  }

  override def visitPwsNonEmpty(ctx : PwsNonEmptyContext) : SAssign = {
    SAssign(VIdent(ctx.Ident().getText,Vector()), visit(ctx.additiveExpr()).asInstanceOf[Expression])
  }

  override def visitPwsNEQ(ctx : PwsNEQContext) : SAssign = {
    SAssign(VIdent(ctx.Ident(1).getText, Vector()),
      EBinOp(BNEqual(),
        visit(ctx.additiveExpr(0)).asInstanceOf[Expression],
        visit(ctx.additiveExpr(1)).asInstanceOf[Expression]))
  }

  override def visitPwsLT(ctx : PwsLTContext) : SAssign = {
    SAssign(VIdent(ctx.Ident(4).getText, Vector()),
      EBinOp(BLT(),
        visit(ctx.additiveExpr(0)).asInstanceOf[Expression],
        visit(ctx.additiveExpr(1)).asInstanceOf[Expression]))
  }


  override def visitPwsOneOrMore(ctx:PwsOneOrMoreContext) : Vector[SAssign] = {
    ctx.onePws().map(visit).to[Vector].asInstanceOf[Vector[SAssign]]
  }

  override def visitLident(ctx : LidentContext) : LValue = {
    VIdent(ctx.Ident().getText, Vector()).setLNumber(ctx.start.getLine)
  }


  override def visitElval(ctx : ElvalContext) : Expression = {
    ELValue(visit(ctx.lval()).asInstanceOf[LValue]).setLNumber(ctx.start.getLine)
  }

  override def visitConst(ctx : ConstContext) : Expression = {
    EConstant(BigInt(ctx.Int().getText)).setLNumber(ctx.start.getLine)
  }

  override def visitEparen(ctx : EparenContext) : Expression = visit(ctx.additiveExpr()).asInstanceOf[Expression]

  override def visitEPrePre(ctx: EPrePreContext): Expression = {
    EUniOp(UANegate(), visit(ctx.primaryExpr()).asInstanceOf[Expression]).setLNumber(ctx.start.getLine)
  }

  override def visitEPrePrim(ctx : EPrePrimContext) : Expression = {
    visit(ctx.primaryExpr()).asInstanceOf[Expression]
  }

  override def visitEMultiPre(ctx:EMultiPreContext) : Expression = {
    visit(ctx.preFixExpr()).asInstanceOf[Expression]
  }

  override def visitEMultiBin(ctx: EMultiBinContext): AnyRef = {
    val x = visit(ctx.multiplicativeExpr()).asInstanceOf[Expression]
    val y = visit(ctx.preFixExpr()).asInstanceOf[Expression]
    val op : ArithBinOp = ctx.op.getText match {
      case "*" => BMul()
      case "/" => BDiv()
      case "%" => BMod()
    }
    EBinOp(BArithOp(op), x, y).setLNumber(ctx.start.getLine)
  }

  override def visitEAddMult(ctx: EAddMultContext): Expression = {
    visit(ctx.multiplicativeExpr()).asInstanceOf[Expression].setLNumber(ctx.start.getLine)
  }

  override def visitEAddBin(ctx: EAddBinContext): Expression = {
    val x = visit(ctx.additiveExpr()).asInstanceOf[Expression]
    val y = visit(ctx.multiplicativeExpr()).asInstanceOf[Expression]
    val op : ArithBinOp = ctx.op.getText match {
      case "-" => BMinus()
      case "+" => BPlus()
    }
    EBinOp(BArithOp(op), x, y).setLNumber(ctx.start.getLine)
  }

}

class CL extends cBaseVisitor[Any] {
  import gen.cParser.{_}

  //def visitChildren(node:RuleNode) = Unit

  //def visit(tree : ParseTree) : Any = super.visit(tree)


  var constantsDefined : Map[String, BigInt] = Map()

  //  override def visitPwsEmpty(ctx : PwsEmptyContext) : SAssign = {
  //    SAssign(VIdent(ctx.Ident(1).getText,Vector()), EConstant(0))
  //  }
  //
  //  override def visitPwsNonEmpty(ctx : PwsNonEmptyContext) : SAssign = {
  //    SAssign(VIdent(ctx.Ident(1).getText,Vector()), visit(ctx.additiveExpr()).asInstanceOf[Expression])
  //  }
  //
  //  override def visitPwsNEQ(ctx : PwsNEQContext) : SAssign = {
  //    SAssign(VIdent(ctx.Ident(7).getText, Vector()),
  //      EBinOp(BNEqual(),
  //        ELValue(VIdent(ctx.Ident(3).getText,Vector())),
  //        ELValue(VIdent(ctx.Ident(5).getText,Vector())))
  //    )
  //  }
  //
  //  override def visitPwsLT(ctx : PwsLTContext) : SAssign = {
  //    SAssign(VIdent(ctx.Ident(13).getText, Vector()),
  //      EBinOp(BLT(),
  //        ELValue(VIdent(ctx.Ident(9).getText,Vector())),
  //        ELValue(VIdent(ctx.Ident(11).getText,Vector())))
  //    )
  //  }
  //
  //
  //  override def visitPwsOneOrMore(ctx:PwsOneOrMoreContext) : Vector[SAssign] = {
  //    ctx.onePws().map(visit).to[Vector].asInstanceOf[Vector[SAssign]]
  //  }


  override def visitTlSingle(ctx : TlSingleContext): Program = {
    ctx.topLevelDef.foldLeft(Program(List(),List(),List()))( (p, tld) => {
      p match {
        case Program(cons,funs,typs) => {
          val h = visit(tld).asInstanceOf[AST]
          h match {
            case a@FunctionDef(_, _, _, _) => Program(cons, a::funs, typs)
            case a@ConstantDef(_, _) => Program(a::cons, funs, typs)
            case a@TypeDef(_, _) => Program(cons, funs, a::typs)
          }
        }
      }
    })
  }

  //  override def visitTlCons(ctx : TlConsContext) : Program = {
  //    val h = visit(ctx.topLevelDef()).asInstanceOf[AST]
  //    val t = visit(ctx.program())
  //    t match {
  //      case Program(cons,funs,typs) =>
  //        h match {
  //          case a@FunctionDef(_,_,_,_) => Program(cons, a::funs, typs)
  //          case a@ConstantDef(_,_) => Program(a::cons,funs,typs)
  //          case a@TypeDef(_,_) => Program(cons,funs,a::typs)
  //        }
  //    }
  //  }

  override def visitTldefFunc(ctx : TldefFuncContext) : FunctionDef = {
    visit(ctx.funcDef()).asInstanceOf[FunctionDef]
  }

  override def visitTldefDfn(ctx : cParser.TldefDfnContext ) : ConstantDef = {
    val dfn = visit(ctx.define()).asInstanceOf[ConstantDef]
    dfn
  }

  override def visitTldefStct(ctx : TldefStctContext) : TypeDef = {
    val d = visit(ctx.structDef()).asInstanceOf[TypeDef]
    d
  }

  override def visitDefineII(ctx : DefineIIContext) : ConstantDef = {
    val n = ctx.Ident().getText
    val nn = BigInt(ctx.Int().getText)
    constantsDefined = constantsDefined.updated(n,nn)
    ConstantDef(n,nn)
  }

  override def visitEpre(ctx : EpreContext) : Expression = {
    val pre = ctx.prefix.getText
    val op : UniOp = pre match {
      case "++" => UIncPre()
      case "--" => UDecPre()
      case "-" => UANegate()
      case "!" => ULNegate()
      case "&" => UGetRef()
    }
    EUniOp(op, visit(ctx.postFixExpr()).asInstanceOf[Expression]).setLNumber(ctx.start.getLine)
  }

  override def visitConst(ctx : ConstContext) : Expression = {
    EConstant(BigInt(ctx.Int().getText)).setLNumber(ctx.start.getLine)
  }

  override def visitEparen(ctx : EparenContext) : Expression = visit(ctx.expr()).asInstanceOf[Expression]

  override def visitESelect(ctx : ESelectContext) : Expression = {
    val b = visit(ctx.orExpr(0)).asInstanceOf[Expression]
    val x = visit(ctx.orExpr(1)).asInstanceOf[Expression]
    val y = visit(ctx.orExpr(2)).asInstanceOf[Expression]
    ESelect(b,x,y).setLNumber(ctx.start.getLine)
  }

  override def visitEfunc(ctx : EfuncContext) : Expression = {
    val n = ctx.Ident().getText
    val k = ctx.expr().map(visit(_).asInstanceOf[Expression]).to[List]
    EFunctionCall(n,k).setLNumber(ctx.start.getLine)
  }

  override def visitEprimary(ctx: EprimaryContext): Expression = {
    visit(ctx.primaryExpr()).asInstanceOf[Expression].setLNumber(ctx.start.getLine)
  }

  override def visitEPostFix(ctx: EPostFixContext): Expression = {
    val l = visit(ctx.primaryExpr()).asInstanceOf[Expression]
    val op : UniOp = ctx.op.getText match {
      case "++" => UInc()
      case "--" => UDec()
    }
    EUniOp(op, l).setLNumber(ctx.start.getLine)
  }

  override def visitEPrePost(ctx: EPrePostContext): Expression = {
    visit(ctx.postFixExpr()).asInstanceOf[Expression].setLNumber(ctx.start.getLine)
  }


  override def visitElval(ctx : ElvalContext) : Expression = {
    ELValue(visit(ctx.lval()).asInstanceOf[LValue]).setLNumber(ctx.start.getLine)
  }

  override def visitEMultiPre(ctx: EMultiPreContext): Expression = {
    visit(ctx.preFixExpr()).asInstanceOf[Expression].setLNumber(ctx.start.getLine)
  }

  override def visitEMultiBin(ctx: EMultiBinContext): AnyRef = {
    val x = visit(ctx.multiplicativeExpr()).asInstanceOf[Expression]
    val y = visit(ctx.preFixExpr()).asInstanceOf[Expression]
    val op : ArithBinOp = ctx.op.getText match {
      case "*" => BMul()
      case "/" => BDiv()
      case "%" => BMod()
    }
    EBinOp(BArithOp(op), x, y).setLNumber(ctx.start.getLine)
  }

  override def visitEAddMult(ctx: EAddMultContext): Expression = {
    visit(ctx.multiplicativeExpr()).asInstanceOf[Expression].setLNumber(ctx.start.getLine)
  }

  override def visitEAddBin(ctx: EAddBinContext): Expression = {
    val x = visit(ctx.additiveExpr()).asInstanceOf[Expression]
    val y = visit(ctx.multiplicativeExpr()).asInstanceOf[Expression]
    val op : ArithBinOp = ctx.op.getText match {
      case "-" => BMinus()
      case "+" => BPlus()
    }
    EBinOp(BArithOp(op), x, y).setLNumber(ctx.start.getLine)
  }

  override def visitERelAdd(ctx: ERelAddContext): Expression = {
    visit(ctx.additiveExpr()).asInstanceOf[Expression].setLNumber(ctx.start.getLine)
  }

  override def visitERelBin(ctx: ERelBinContext): Expression = {
    val x = visit(ctx.relationalExpr()).asInstanceOf[Expression]
    val y = visit(ctx.additiveExpr()).asInstanceOf[Expression]
    val op : BinOp = ctx.op.getText match {
      case "<" => BLT()
      case ">" => BGT()
      case ">=" => BGE()
      case "<=" => BLE()
      case "==" => BEqual()
      case "!=" => BNEqual()
    }
    EBinOp(op, x, y).setLNumber(ctx.start.getLine)
  }

  override def visitEAndRel(ctx: EAndRelContext): Expression = {
    visit(ctx.relationalExpr()).asInstanceOf[Expression].setLNumber(ctx.start.getLine)
  }

  override def visitEAndBin(ctx: EAndBinContext): Expression = {
    val x = visit(ctx.andExpr()).asInstanceOf[Expression]
    val y = visit(ctx.relationalExpr()).asInstanceOf[Expression]
    EBinOp(BAnd(), x, y).setLNumber(ctx.start.getLine)
  }

  override def visitEAndOr(ctx: EAndOrContext): Expression = {
    visit(ctx.andExpr()).asInstanceOf[Expression].setLNumber(ctx.start.getLine)
  }

  override def visitEOrBin(ctx: EOrBinContext): Expression = {
    val x = visit(ctx.orExpr()).asInstanceOf[Expression]
    val y = visit(ctx.andExpr()).asInstanceOf[Expression]
    EBinOp(BOr(), x, y).setLNumber(ctx.start.getLine)
  }

  override def visitEOrExpr(ctx: EOrExprContext): Expression = {
    visit(ctx.orExpr()).asInstanceOf[Expression].setLNumber(ctx.start.getLine)
  }

  override def visitLindex(ctx : LindexContext) : LValue = {
    val l = visit(ctx.lval()).asInstanceOf[LValue]
    val e = visit(ctx.expr()).asInstanceOf[Expression]
    l match {
      case VIdent(n, pl) => VIdent(n, pl:+ LVPArray(e)).setLNumber(ctx.start.getLine)
    }
  }

  override def visitLident(ctx : LidentContext) : LValue = {
    VIdent(ctx.Ident().getText, Vector()).setLNumber(ctx.start.getLine)
  }

  override def visitLderef(ctx : LderefContext) : LValue = {
    val l = visit(ctx.lval()).asInstanceOf[LValue]
    l match {
      case VIdent(n, pl) => VIdent(n, pl:+ LVPDeref()).setLNumber(ctx.start.getLine)
    }
  }

  override def visitLarrow(ctx : LarrowContext) : LValue = {
    val l = visit(ctx.lval()).asInstanceOf[LValue]
    l match {
      case VIdent(n, pl) => VIdent(n, pl:+ LVPArrow(ctx.Ident().getText)).setLNumber(ctx.start.getLine)
    }
  }

  override def visitLparen(ctx : LparenContext) : LValue = {
    visit(ctx.lval()).asInstanceOf[LValue].setLNumber(ctx.start.getLine)
  }

  override def visitLdot(ctx : LdotContext) : LValue = {
    val l = visit(ctx.lval()).asInstanceOf[LValue]
    l match {
      case VIdent(n, pl) => VIdent(n, pl:+ LVPDot(ctx.Ident().getText)).setLNumber(ctx.start.getLine)
    }
  }

  override def visitNonPointerType(ctx : NonPointerTypeContext) : Type = {
    visit(ctx.nonPtrType()).asInstanceOf[Type]
  }

  override def visitPointerType(ctx : PointerTypeContext) : Type = {
    val ty = visit(ctx.nonPtrType()).asInstanceOf[Type]
    TPtr(ty).setLNumber(ctx.start.getLine)
  }

  override def visitStructType(ctx : StructTypeContext) : Type = {
    TStruct(ctx.Ident().getText).setLNumber(ctx.start.getLine)
  }

  override def visitIntType(ctx : IntTypeContext) : Type = TInt().setLNumber(ctx.start.getLine)

  override def visitVoidType(ctx : VoidTypeContext) : Type = TVoid().setLNumber(ctx.start.getLine)

  override def visitInputListCons(ctx : InputListConsContext) : Vector[(String, Type)] = {
    val typ = visit(ctx.`type`()).asInstanceOf[Type]
    val n = ctx.Ident().getText
    (n, typ) +: visit(ctx.inputList()).asInstanceOf[Vector[(String, Type)]]
  }

  override def visitInputListSingleton(ctx : InputListSingletonContext) : Vector[(String,Type)] = {
    val typ = visit(ctx.`type`()).asInstanceOf[Type]
    val n = ctx.Ident().getText
    Vector((n,typ))
  }

  override def visitInputListEmpty(ctx : InputListEmptyContext) : Vector[(String,Type)] = {
    Vector()
  }

  override def visitStmtsCons(stmtsConsContext: StmtsConsContext) : Vector[Statement] = {
    visit(stmtsConsContext.statement()).asInstanceOf[Statement] +:
      visit(stmtsConsContext.statements()).asInstanceOf[Vector[Statement]]
  }

  override def visitStmtsEmpty(stmtsEmptyContext: StmtsEmptyContext) : Vector[Statement] = Vector()

  override def visitDecUSingle(decUSingleContext: DecUSingleContext) : (String, Option[Expression]) =
    (decUSingleContext.Ident().getText, None)

  override def visitDecASingle(decASingleContext: DecASingleContext) : (String, Option[Expression]) =
    (decASingleContext.Ident().getText, Some(visit(decASingleContext.expr()).asInstanceOf[Expression]))

  override def visitDeclSingle(declSingleContext: DeclSingleContext) : Vector[(String, Option[Expression])] = {
    Vector(visit(declSingleContext.decSingle()).asInstanceOf[(String,Option[Expression])])
  }

  override def visitDeclCons(declConsContext: DeclConsContext) : Vector[(String, Option[Expression])] = {
    val h = visit(declConsContext.decSingle()).asInstanceOf[(String,Option[Expression])]
    val t = visit(declConsContext.declist()).asInstanceOf[Vector[(String,Option[Expression])]]
    h +: t
  }

  override def visitAssnStmt(assnStmtContext: AssnStmtContext) : Statement = {
    val l = visit(assnStmtContext.lval()).asInstanceOf[LValue]
    val r = visit(assnStmtContext.expr()).asInstanceOf[Expression]
    val res = SAssign(l,r)
    res.setLNumber(assnStmtContext.start.getLine)
  }

  override def visitArithAssnStmt(arithAssnStmtContext: ArithAssnStmtContext) : Statement = {
    val l = visit(arithAssnStmtContext.lval()).asInstanceOf[LValue]
    val r = visit(arithAssnStmtContext.expr()).asInstanceOf[Expression]
    val op = arithAssnStmtContext.op.getText match {
      case "+=" => BPlus()
      case "*=" => BMul()
      case "/=" => BDiv()
      case "%=" => BMod()
      case "-=" => BMinus()
    }
    val res = SArithAssign(l,op,r)
    res.setLNumber(arithAssnStmtContext.start.getLine)
  }

  override def visitExprStmt(exprStmtContext: ExprStmtContext) : Statement = {
    val res = SExpression(visit(exprStmtContext.expr()).asInstanceOf[Expression])
    res.setLNumber(exprStmtContext.start.getLine)
  }

  override def visitIfSingle(ctx : IfSingleContext) : Statement = {
    val cond = visit(ctx.expr()).asInstanceOf[Expression]
    val stmts = visit(ctx.statements()).asInstanceOf[Vector[Statement]]
    val res = SIf(cond, stmts, Vector())
    res.setLNumber(ctx.start.getLine)
  }

  override def visitIfElse(ctx : IfElseContext) : Statement = {
    val cond = visit(ctx.expr()).asInstanceOf[Expression]
    val stmts = visit(ctx.statements(0)).asInstanceOf[Vector[Statement]]
    val stmts2 = visit(ctx.statements(1)).asInstanceOf[Vector[Statement]]
    val res = SIf(cond, stmts, stmts2)
    res.setLNumber(ctx.start.getLine).setLNumber(ctx.start.getLine)
  }

  override def visitWhile(ctx : WhileContext) : Statement = {
    val cond = visit(ctx.expr()).asInstanceOf[Expression]
    val stmts = visit(ctx.statements()).asInstanceOf[Vector[Statement]]
    SWhile(cond, stmts).setLNumber(ctx.start.getLine)
  }

  override def visitForLoop(ctx : ForLoopContext) : Statement = {
    val pre = visit(ctx.simpleStatement(0)).asInstanceOf[Statement]
    val cond = visit(ctx.expr()).asInstanceOf[Expression]
    val post = visit(ctx.simpleStatement(1)).asInstanceOf[Statement]
    val body = visit(ctx.statements()).asInstanceOf[Vector[Statement]]
    SLoop(pre, cond, post, body).setLNumber(ctx.start.getLine)
  }

  override def visitGroupStmt(ctx : GroupStmtContext) : Statement = {
    SGrouped(visit(ctx.statements()).asInstanceOf[Vector[Statement]])
  }

  override def visitOforLoop(ctx : OforLoopContext) : Statement = {
    val iterCount = ctx.Int().getText.toInt
    val pre = visit(ctx.simpleStatement(0)).asInstanceOf[Statement]
    val cond = visit(ctx.expr()).asInstanceOf[Expression]
    val post = visit(ctx.simpleStatement(1)).asInstanceOf[Statement]
    val body = visit(ctx.statements()).asInstanceOf[Vector[Statement]]
    SOFor(iterCount, pre, cond, post, body).setLNumber(ctx.start.getLine)
  }

  override def visitDecStmt(ctx : DecStmtContext) : Statement = {
    val t = visit(ctx.`type`()).asInstanceOf[Type]
    val l = visit(ctx.declist()).asInstanceOf[Vector[(String,Option[Expression])]]
    SVarDec(t, l).setLNumber(ctx.start.getLine)
  }

  override def visitRetStmt(ctx : RetStmtContext) : Statement = {
    SReturn(Some(visit(ctx.expr()).asInstanceOf[Expression])).setLNumber(ctx.start.getLine)
  }

  override def visitRetStmtNone(ctx : RetStmtNoneContext) : Statement = {
    SReturn(None).setLNumber(ctx.start.getLine)
  }

  override def visitBreakStmt(ctx : BreakStmtContext) : Statement = SBreak()

  override def visitContinueStmt(ctx : ContinueStmtContext) : Statement = SContinue()

  override def visitBeginStmt(ctx : BeginStmtContext) : Statement = SBegin()

  override def visitEndStmt(ctx : EndStmtContext) : Statement = SEnd()

  override def visitFuncdefSimple(ctx : FuncdefSimpleContext) : FunctionDef = {
    val t = visit(ctx.`type`()).asInstanceOf[Type]
    val n = ctx.Ident().getText
    val il = visit(ctx.inputList()).asInstanceOf[Vector[(String, Type)]]
    val stmts = visit(ctx.statements()).asInstanceOf[Vector[Statement]]
    FunctionDef(n,t,il,stmts).setLNumber(ctx.start.getLine)
  }

  override def visitSingleDefNormal(ctx : SingleDefNormalContext) : (String, Type) = {
    val ty = visit(ctx.`type`()).asInstanceOf[Type]
    val n = ctx.Ident().getText
    (n, ty)
  }

  override def visitBlankStmt(ctx: BlankStmtContext): Statement = {
    SBlank().setLNumber(ctx.start.getLine)
  }

  override def visitSimpleStmt(ctx : SimpleStmtContext) : Statement = {
    visit(ctx.simpleStatement()).asInstanceOf[Statement]
  }

  //  override def visitSingleDefArray(ctx : SingleDefArrayContext) : (String, Type) = {
  //    val ty = visit(ctx.`type`()).asInstanceOf[Type]
  //    val n = ctx.Ident(0).getText
  //    val l = constantsDefined.get(ctx.Ident(1).getText)
  //    (n, TArr(ty, l.get))
  //  }

  def stringToArrayLength(x : String) : Int = {
    if (x.head.isDigit) x.toInt else constantsDefined(x).toInt
  }

  override def visitDecSingleDef(ctx : DecSingleDefContext) : Statement = {
    val x = visit(ctx.singleDef()).asInstanceOf[(String,Type)]
    SVarDec(x._2,Vector((x._1,None)))
  }

  override def visitSingleDefArrayConst(ctx : SingleDefArrayConstContext) : (String, Type) = {
    val ty = visit(ctx.`type`()).asInstanceOf[Type]
    val ns = ctx.arrayLength.map(x => stringToArrayLength(x.getText))
    val n = ctx.Ident().getText
    val tyArr = ns.foldRight(ty)((l, t) => TArr(t, l))
    (n, tyArr)
  }

  override def visitStructDef(ctx : StructDefContext) : TypeDef = {
    val typs = ctx.singleDef().map(visit(_).asInstanceOf[(String,Type)])
    TypeDef(ctx.Ident().getText, typs.to[Vector])
  }

  override def visitDecArrayInt(ctx : DecArrayIntContext) : Statement = {
    val ty = visit(ctx.`type`()).asInstanceOf[Type]
    val n = ctx.Ident().getText
    val nn = ctx.Int().getText.toInt
    SVarDec(TArr(ty, nn), Vector((n,None)))
  }

  override def visitDecArrayIdent(ctx : DecArrayIdentContext) : Statement = {
    val ty = visit(ctx.`type`()).asInstanceOf[Type]
    val n = ctx.Ident(0).getText
    val nn = constantsDefined(ctx.Ident(1).getText)
    SVarDec(TArr(ty, nn.toInt), Vector((n,None)))
  }

  override def visitSout(soutContext: SoutContext) : Statement = {
    val stmts = visit(soutContext.statements()).asInstanceOf[Vector[Statement]]
    SOut(stmts)
  }

  override def visitLvalHead(lvalHeadContext: LvalHeadContext) : Vector[LValue] = {
    Vector(visit(lvalHeadContext.lval()).asInstanceOf[LValue])
  }

  override def visitLvalCons(lvalConsContext: LvalConsContext) : Vector[LValue] = {
    visit(lvalConsContext.lval()).asInstanceOf[LValue] +:
      visit(lvalConsContext.lvals()).asInstanceOf[Vector[LValue]]
  }

  override def visitSOutsource(sOutsourceContext: SOutsourceContext) : Statement = {
    val invals = visit(sOutsourceContext.lvals(0)).asInstanceOf[Vector[LValue]]
    val outvals = visit(sOutsourceContext.lvals(1)).asInstanceOf[Vector[LValue]]
    SOutsource(invals, outvals, sOutsourceContext.Ident(0).getText,
      sOutsourceContext.Ident(1).getText,sOutsourceContext.Ident(2).getText)
  }
}

object CParserPlus extends JavaTokenParsers {

  def parsePWSFile(filename : String) : Vector[SAssign] = {
    val fin = new FileInputStream(filename)
    val input = new ANTLRInputStream(fin)
    val lexer = new pwsLexer(input)
    val tokens = new CommonTokenStream(lexer)
    val parser = new pwsParser(tokens)
    val tree = parser.pws()
    val visitor = new PWSV()
    visitor.visit(tree).asInstanceOf[Vector[SAssign]]
  }

  def parseFilePreprocess(fileName : String) : Program = {
    import scala.sys.process._
    import java.io.File
    val preprocessed = "%s_tmp".format (fileName)
    Seq("gcc", "-E", fileName) #> new File(preprocessed) !
    val prog = parseFile(preprocessed)
    Seq("rm", "-rf", preprocessed).!
    prog
  }

  def parseFile(filename : String): Program = {
    val fin = new FileInputStream(filename)
    val input = new ANTLRInputStream(fin)
    val lexer = new cLexer(input)
    val tokens = new CommonTokenStream(lexer)
    val parser = new cParser(tokens)
    val tree = parser.program()
    val visitor = new CL()
    visitor.visit(tree).asInstanceOf[Program]
  }

  def parseTest() = {
    val input = new ANTLRInputStream(System.in)
    val lexer = new cLexer(input)
    val tokens = new CommonTokenStream(lexer)
    val parser = new cParser(tokens)
    val tree = parser.statement()
    val visitor = new CL()

    println(visitor.visit(tree))
  }

  def divide(start:Int, end:Int, vs: Vector[Statement]) : (Vector[Statement], Vector[Statement], Vector[Statement]) = {
    // assume everything is in range
    val b : Int = vs.indexWhere(x => x.lineNumber == start && x.lineNumber != 0) match {
      case -1 => vs.lastIndexWhere(x => x.lineNumber < start && x .lineNumber != 0)
      case a => a
    }

    val e = vs.indexWhere(x => x.lineNumber != 0 && x.lineNumber == end) match {
      case -1 => vs.lastIndexWhere(x => x.lineNumber != 0 && x.lineNumber < end)
      case a => a
    }
    (vs.slice(0, b), vs.slice(b, e+1), vs.slice(e+1, vs.size))
  }


  def main_(args : Array[String]) {
    //println(tree.toStringTree(parser))
    // val prog = parseFile(filename)
    parseTest()
  }

  //  def constantDef : Parser[ConstantDef] =
  //    "#define" ~> ident ~ wholeNumber ^^ {case n ~ v => ConstantDef(n, v.toInt) }
  //
  //  def arithBOp : Parser[ArithBinOp] =
  //  "+" ^^^ BPlus() |
  //  "-" ^^^ BMinus() |
  //  "*" ^^^ BMul() |
  //  "/" ^^^ BDiv() |
  //  "%" ^^^ BMod()
  //
  //  def lvalArrHead : Parser[String] =
  //    ident <~ "->"
  //
  //  def lvalDotHead : Parser[String] =
  //    ident <~ "."
  //
  //  def lvalArrayOrIdent : Parser[(String, Expression)] =
  //    (ident <~ "[") ~ (expression <~ "]") ^^ {
  //      case i ~ e => (i,e)
  //    }
  //
  //  def expression : Parser[Expression] = ???
  //
  //  def lval : Parser[LValue] = ident ^^ {VIdent(_, Vector())} |
  //    (lval <~ "->") ~ ident ^^ {case (VIdent(n, l)) ~ i => VIdent(n, l:+LVPArrow(i))} /*|||
  ///*    "(" ~> lval <~ ")" |||
  //    "*" ~> lval ^^ {case VIdent(i, l) => VIdent(i, LVPDeref()::l)} |||
  //    lvalArrHead ~ lval ^^ {case i ~ (VIdent(n, l)) => VIdent(i, LVPArrow(n)::l) } |||
  //    lvalDotHead ~ lval ^^ {case i ~ (VIdent(n, l)) => VIdent(i, LVPDot(n)::l)}*/*/
  //
  //
  //  def binop: Parser[BinOp] =
  //  "<=" ^^^ BLE() |
  //  "<"  ^^^ BLT() |
  //  ">=" ^^^ BGE() |
  //  ">"  ^^^ BGT() |
  //  "&&" ^^^ BAnd() |
  //  "||" ^^^ BOr() |
  //  "==" ^^^ BEqual() |
  //  "!=" ^^^ BNEqual() |
  //  arithBOp ^^ (BArithOp(_))


}

