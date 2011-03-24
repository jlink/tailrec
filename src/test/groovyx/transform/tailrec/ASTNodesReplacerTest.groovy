package groovyx.transform.tailrec

import static org.junit.Assert.*

import org.codehaus.groovy.ast.expr.ArgumentListExpression
import org.codehaus.groovy.ast.expr.BinaryExpression
import org.codehaus.groovy.ast.expr.BooleanExpression
import org.codehaus.groovy.ast.expr.ConstantExpression
import org.codehaus.groovy.ast.expr.MethodCallExpression
import org.codehaus.groovy.ast.expr.TupleExpression
import org.codehaus.groovy.ast.expr.VariableExpression
import org.codehaus.groovy.ast.stmt.BlockStatement
import org.codehaus.groovy.ast.stmt.EmptyStatement
import org.codehaus.groovy.ast.stmt.IfStatement
import org.codehaus.groovy.ast.stmt.ReturnStatement
import org.codehaus.groovy.syntax.Token;
import org.junit.Test

class ASTNodesReplacerTest {

	static final Token ASSIGN = Token.newSymbol("=", -1, -1)

	@Test
	public void replaceSingleStatementInBlock() {
		def toReplace = aReturnStatement("old")
		def replacement = aReturnStatement("new")
		def block = new BlockStatement()
		block.addStatement(aReturnStatement("before"))
		block.addStatement(toReplace)
		block.addStatement(aReturnStatement("after"))

		def replacer = new ASTNodesReplacer(replace: [(toReplace):replacement])
		replacer.replaceIn(block) 

		assert block.statements[1] == replacement
		assert block.statements.size() == 3
	}

	@Test
	public void replaceByCondition() {
		def toReplace = aReturnStatement("old")
		def replacement = aReturnStatement("new")
		def block = new BlockStatement()
		block.addStatement(aReturnStatement("before"))
		block.addStatement(toReplace)
		block.addStatement(aReturnStatement("after"))

		def replacer = new ASTNodesReplacer(when: { it == toReplace }, replaceWith: {
			assert it == toReplace
			replacement
		})
		replacer.replaceIn(block) 

		assert block.statements[1] == replacement
		assert block.statements.size() == 3
	}

	@Test
	public void replaceTwoStatementsInBlock() {
		def toReplace1 = aReturnStatement("old1")
		def replacement1 = aReturnStatement("new1")
		def toReplace2 = aReturnStatement("old2")
		def replacement2 = aReturnStatement("new2")
		def block = new BlockStatement()
		block.addStatement(aReturnStatement("before"))
		block.addStatement(toReplace1)
		block.addStatement(toReplace2)
		block.addStatement(aReturnStatement("after"))

		def replacements = [(toReplace1):replacement1, (toReplace2):replacement2]
		def replacer = new ASTNodesReplacer(replace: replacements)
		replacer.replaceIn(block)

		assert block.statements[1] == replacement1
		assert block.statements[2] == replacement2
		assert block.statements.size() == 4
	}

	@Test
	public void replaceIfCondition() {
		def toReplace = aBooleanExpression(true)
		def replacement = aBooleanExpression(false)
		def ifStatement = new IfStatement(toReplace, new EmptyStatement(), new EmptyStatement())

		def replacements = [(toReplace):replacement]
		def replacer = new ASTNodesReplacer(replace: replacements)
		replacer.replaceIn(ifStatement)

		assert ifStatement.booleanExpression == replacement
	}

	@Test
	public void replaceMethodCallReceiver() {
		def toReplace = aVariable("old")
		def replacement = aVariable("new")
		def methodCall = new MethodCallExpression(toReplace, new ConstantExpression("method"), new TupleExpression())

		def replacements = [(toReplace):replacement]
		def replacer = new ASTNodesReplacer(replace: replacements)
		replacer.replaceIn(methodCall)

		assert methodCall.objectExpression == replacement
	}

	@Test
	public void replaceMethodCallArguments() {
		def toReplace1 = aVariable("old1")
		def replacement1 = aVariable("new1")
		def toReplace2 = aVariable("old2")
		def replacement2 = aVariable("new2")
		def args = new TupleExpression(toReplace1, new ConstantExpression("don't change"), toReplace2)
		def methodCall = new MethodCallExpression(aVariable("this"), new ConstantExpression("method"), args)

		def replacements = [(toReplace1):replacement1, (toReplace2):replacement2]
		def replacer = new ASTNodesReplacer(replace: replacements)
		replacer.replaceIn(methodCall)

		assert methodCall.arguments.getExpression(0) == replacement1
		assert methodCall.arguments.getExpression(1).value == "don't change"
		assert methodCall.arguments.getExpression(2) == replacement2
	}

	@Test
	public void replaceIfBlock() {
		def toReplace = aReturnStatement("old")
		def replacement = aReturnStatement("new")
		def ifStatement = new IfStatement(aBooleanExpression(true), toReplace, new EmptyStatement())

		def replacements = [(toReplace):replacement]
		def replacer = new ASTNodesReplacer(replace: replacements)
		replacer.replaceIn(ifStatement)

		assert ifStatement.ifBlock == replacement
	}

	@Test
	public void replaceElseBlock() {
		def toReplace = aReturnStatement("old")
		def replacement = aReturnStatement("new")
		def ifStatement = new IfStatement(aBooleanExpression(true), new EmptyStatement(), toReplace)

		def replacements = [(toReplace):replacement]
		def replacer = new ASTNodesReplacer(replace: replacements)
		replacer.replaceIn(ifStatement)

		assert ifStatement.elseBlock == replacement
	}

	@Test
	public void replaceInBinaryExpression() {
		def toReplaceLeft = aVariable("old")
		def replacementLeft = aVariable("new")
		def toReplaceRight = new ConstantExpression(1)
		def replacementRight = new ConstantExpression(2)
		def binary = new BinaryExpression(toReplaceLeft, ASSIGN, toReplaceRight)

		def replacements = [(toReplaceLeft):replacementLeft, (toReplaceRight): replacementRight]
		def replacer = new ASTNodesReplacer(replace: replacements)
		replacer.replaceIn(binary)

		assert binary.leftExpression == replacementLeft
		assert binary.rightExpression == replacementRight
	}

	@Test
	public void replaceInReturnStatemen() {
		def toReplace = new ConstantExpression("old")
		def replacement = new ConstantExpression("new")
		def returnStatement = new ReturnStatement(toReplace)

		def replacements = [(toReplace):replacement]
		def replacer = new ASTNodesReplacer(replace: replacements)
		replacer.replaceIn(returnStatement)

		assert returnStatement.expression == replacement
	}

	def aReturnStatement(value) {
		new ReturnStatement(new ConstantExpression(value))
	}

	def aBooleanExpression(value) {
		new BooleanExpression(new ConstantExpression(value))
	}

	def aVariable(name) {
		new VariableExpression(name)
	}
}
