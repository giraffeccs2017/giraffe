package peppermint.ILAST
import peppermint.Analysis.CPrinter
import peppermint.CircuitCompilation.ProverWorkItem

import scala.util.parsing.input.Positional


object AST {
  private var current = 0
  def nextIdentity():Int = {
    current = current + 1
    current
  }
}

abstract class AST {
  import AST._
  var lineNumber: Int = 0
  var identity : Int = nextIdentity()
  def setLNumber(x : Int) : AST.this.type  = {
    lineNumber = x
    this
  }
}



abstract class LValPath extends AST
case class LVPArrow(child : String) extends LValPath
case class LVPDot(child : String) extends LValPath
case class LVPArray(index : Expression) extends LValPath
case class LVPDeref() extends LValPath


abstract class LValue extends AST {
  override def toString : String = {
    CPrinter.printLVal(this)
  }
}
case class VIdent(name : String, path: Vector[LValPath]) extends LValue
case class VIdentSSA(ident : LValue, version : Int) extends LValue
case class VIdentName(name : String, idx : Option[Int]) extends LValue


abstract class Expression extends AST {
  def -(expr : Expression) : Expression = {
    EBinOp(BArithOp(BMinus()), this, expr)
  }
  def *(expr : Expression) : Expression = {
    EBinOp(BArithOp(BMul()), this, expr)
  }
  def +(expr : Expression) : Expression = {
    EBinOp(BArithOp(BPlus()), this, expr)
  }
}
case class ELValue(value : LValue) extends Expression
case class EBinOp(op : BinOp, x : Expression, y : Expression) extends Expression
case class EUniOp(op : UniOp, x : Expression) extends Expression
case class ESelect(cond : Expression, x : Expression, y : Expression) extends Expression // cond?x:y
case class EConstant(value : BigInt) extends Expression
case class EFunctionCall(funcName : String, args : List[Expression]) extends Expression

abstract class Type extends AST
case class TInt() extends Type
case class TArr(elemType : Type, size : Int) extends Type
case class TPtr(elemType : Type) extends Type
case class TStruct(name : String) extends Type
case class TVoid() extends Type

abstract class ArithBinOp extends AST
case class BMinus() extends ArithBinOp
case class BPlus() extends ArithBinOp
case class BMul() extends ArithBinOp
case class BDiv() extends ArithBinOp
case class BMod() extends ArithBinOp

abstract class BinOp extends AST with Positional
case class BArithOp(op : ArithBinOp) extends BinOp
case class BAnd() extends BinOp
case class BOr() extends BinOp
case class BEqual() extends BinOp
case class BNEqual() extends BinOp
case class BLT() extends BinOp
case class BGT() extends BinOp
case class BLE() extends BinOp
case class BGE() extends BinOp

abstract class UniOp extends AST
case class UInc() extends UniOp
case class UDec() extends UniOp
case class UIncPre() extends UniOp
case class UDecPre() extends UniOp
case class UDeref() extends UniOp  // *p
case class UGetRef() extends UniOp // &i
case class ULNegate() extends UniOp // !b
case class UANegate() extends UniOp // minus sign


abstract class Statement extends AST with Positional {
  var comment : String = ""
}
case class SBlank() extends Statement // For convenience
case class SAssign(lval : LValue, rval : Expression) extends Statement
case class SArithAssign(lval : LValue, op : ArithBinOp, rval : Expression) extends Statement
case class SExpression(expr : Expression) extends Statement
case class SIf(bool : Expression, thenS : Vector[Statement], elseS : Vector[Statement]) extends Statement
case class SLoop(pre : Statement, condition : Expression, post : Statement, body : Vector[Statement]) extends Statement
case class SWhile(condition : Expression, body : Vector[Statement]) extends Statement
case class SVarDec(vdType : Type, nameValList : Vector[(String, Option[Expression])]) extends Statement
case class SReturn(oexpr : Option[Expression]) extends Statement
case class SBreak() extends Statement
case class SContinue() extends Statement
case class SBegin() extends Statement
case class SEnd() extends Statement
case class SOut(body : Vector[Statement]) extends Statement
case class SGrouped(body : Vector[Statement]) extends Statement
case class SOutsource(input : Vector[LValue], output : Vector[LValue],
                      pepperCName : String, pepperInputFileName : String,
                      pepperOutputFileName : String,
                      typeMap : Map[LValue, Type] = Map() // useful when generating C code
                     ) extends Statement
case class SOFor(oCount:Int, pre : Statement, condition : Expression, post : Statement, body: Vector[Statement]) extends  Statement
// expanded from SFor
case class SForGrouped(stmts:Vector[Statement]) extends Statement
case class SReadInput(lval : LValue, typ : Type) extends Statement
case class SWriteLVal(lval : LValue, typ : Type) extends Statement
case class SLiteral(str : String) extends Statement // this will be translated verbatim when generating C code
case class SZebraOutsource(orig:Vector[Statement],
                           pwis : Vector[ProverWorkItem],
                           singleSubcircuit : Vector[SAssign],
                           numOfCopies : Int,
                           originalInputSet : Set[VIdentSSA],
                           originalOutputSet : Set[VIdentSSA],
                           inputSet : Vector[VIdentSSA],
                           outputSet : Vector[VIdentSSA],
                           indexMapping : Map[VIdentSSA, Int]
                          ) extends Statement {
  var pwsFileName = ""
  var costForLocalExec : Double = 0
  var costForOutsource : Double = 0
  def costSavings:Double = costForLocalExec - costForOutsource
}

/*
* Produced by SSATransformation
 *  */
case class EFetch(lval : LValue, indices : Vector[Expression]) extends Expression
case class SCondStore(lval : VIdent, indices : Vector[Expression], value : Expression, cond : Option[Expression]) extends Statement

case class FunctionDef(funcName : String, retType : Type, args : Vector[(String, Type)], body : Vector[Statement]
                      ) extends AST

case class ConstantDef(name : String, value : BigInt) extends AST

case class TypeDef(name : String, members : Vector[(String, Type)]) extends AST

case class Program(constantDefs: List[ConstantDef], funcDefs: List[FunctionDef], typeDefs : List[TypeDef]) extends AST
