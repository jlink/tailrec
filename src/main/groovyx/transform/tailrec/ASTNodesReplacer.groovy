package groovyx.transform.tailrec

import java.util.List

import org.codehaus.groovy.ast.*
import org.codehaus.groovy.ast.expr.*
import org.codehaus.groovy.ast.stmt.*
import org.codehaus.groovy.classgen.BytecodeExpression

class ASTNodesReplacer extends CodeVisitorSupport {

	Map<ASTNode, ASTNode> replace = [:]
	Closure when = { replace.containsKey it}
	Closure replaceWith = { replace[it] }
	
	void replaceIn(ASTNode root) {
		root.visit(this)
	}

	public void visitBlockStatement(BlockStatement block) {
		block.statements.clone().eachWithIndex { Statement statement, index ->
			replaceIfNecessary(statement) {block.statements[index] = it}
		}
		super.visitBlockStatement(block);
	}

	public void visitIfElse(IfStatement ifElse) {
		replaceIfNecessary(ifElse.booleanExpression) { ifElse.booleanExpression = it }
		replaceIfNecessary(ifElse.ifBlock) { ifElse.ifBlock = it }
		replaceIfNecessary(ifElse.elseBlock) { ifElse.elseBlock = it }
		super.visitIfElse(ifElse);
	}

	public void visitBinaryExpression(BinaryExpression expression) {
		replaceIfNecessary(expression.leftExpression) {expression.leftExpression = it}
		replaceIfNecessary(expression.rightExpression) {expression.rightExpression = it}
		super.visitBinaryExpression(expression);
	}

	public void visitReturnStatement(ReturnStatement statement) {
		replaceInnerExpressionIfNecessary(statement)
		super.visitReturnStatement(statement);
	}

	public void visitMethodCallExpression(MethodCallExpression call) {
		replaceIfNecessary(call.objectExpression) {call.objectExpression = it}
		super.visitMethodCallExpression(call);
	}

	protected void visitListOfExpressions(List<? extends Expression> list) {
		list.clone().eachWithIndex { Expression expression, index ->
			replaceIfNecessary(expression) {list[index] = it}
		}
		super.visitListOfExpressions(list)
	}

	private replaceIfNecessary(ASTNode nodeToCheck, Closure replacementCode) {
		if (when(nodeToCheck)) {
			def replacement = replaceWith(nodeToCheck)
			replacementCode(replacement)
		}
	}

	private void replaceInnerExpressionIfNecessary(statement) {
		replaceIfNecessary(statement.expression) {statement.expression = it}
	}

	//todo: test
	public void visitExpressionStatement(ExpressionStatement statement) {
		replaceIfNecessary(statement.expression) {statement.expression = it}
		super.visitExpressionStatement(statement);
	}

	//todo: test
	public void visitForLoop(ForStatement forLoop) {
		replaceIfNecessary(forLoop.collectionExpression) {forLoop.collectionExpression = it}
		replaceIfNecessary(forLoop.loopBlock) {forLoop.loopBlock = it}
		super.visitForLoop(forLoop);
	}

	//todo: test
	public void visitWhileLoop(WhileStatement loop) {
		replaceIfNecessary(loop.booleanExpression) {loop.booleanExpression = it}
		replaceIfNecessary(loop.loopBlock) {loop.loopBlock = it}
		super.visitWhileLoop(loop);
	}

	//todo: test
	public void visitDoWhileLoop(DoWhileStatement loop) {
		replaceIfNecessary(loop.booleanExpression) {loop.booleanExpression = it}
		replaceIfNecessary(loop.loopBlock) {loop.loopBlock = it}
		super.visitDoWhileLoop(loop);
	}

	//todo: test
	public void visitThrowStatement(ThrowStatement statement) {
		replaceInnerExpressionIfNecessary(statement)
		super.visitThrowStatement(statement)
	}

	//todo: test
	public void visitPostfixExpression(PostfixExpression expression) {
		replaceInnerExpressionIfNecessary(expression)
		super.visitPostfixExpression(expression);
	}

	//todo: test
	public void visitPrefixExpression(PrefixExpression expression) {
		replaceInnerExpressionIfNecessary(expression)
		super.visitPrefixExpression(expression);
	}

	//todo: test
	public void visitBooleanExpression(BooleanExpression expression) {
		replaceInnerExpressionIfNecessary(expression)
		super.visitBooleanExpression(expression);
	}

	//todo: test
	public void visitNotExpression(NotExpression expression) {
		replaceInnerExpressionIfNecessary(expression)
		super.visitNotExpression(expression);
	}

	//todo: test
	public void visitSpreadExpression(SpreadExpression expression) {
		replaceInnerExpressionIfNecessary(expression)
		super.visitSpreadExpression(expression);
	}

	//todo: test
	public void visitSpreadMapExpression(SpreadMapExpression expression) {
		replaceInnerExpressionIfNecessary(expression)
		super.visitSpreadMapExpression(expression);
	}

	//todo: test
	public void visitUnaryMinusExpression(UnaryMinusExpression expression) {
		replaceInnerExpressionIfNecessary(expression)
		super.visitUnaryMinusExpression(expression);
	}

	//todo: test
	public void visitUnaryPlusExpression(UnaryPlusExpression expression) {
		replaceInnerExpressionIfNecessary(expression)
		super.visitUnaryPlusExpression(expression);
	}

	//todo: test
	public void visitBitwiseNegationExpression(BitwiseNegationExpression expression) {
		replaceInnerExpressionIfNecessary(expression)
		super.visitBitwiseNegationExpression(expression);
	}

	//todo: test
	public void visitCastExpression(CastExpression expression) {
		replaceInnerExpressionIfNecessary(expression)
		super.visitCastExpression(expression);
	}

	//todo: test
	public void visitMapEntryExpression(MapEntryExpression expression) {
		replaceIfNecessary(expression.keyExpression) {expression.keyExpression = it}
		replaceIfNecessary(expression.valueExpression) {expression.valueExpression = it}
		super.visitMapEntryExpression(expression);
	}

	//todo: test
	public void visitTernaryExpression(TernaryExpression expression) {
		replaceIfNecessary(expression.booleanExpression) {expression.booleanExpression = it}
		replaceIfNecessary(expression.trueExpression) {expression.trueExpression = it}
		replaceIfNecessary(expression.falseExpression) {expression.falseExpression = it}
		super.visitTernaryExpression(expression);
	}

	//todo
	public void visitSwitch(SwitchStatement statement) {
		statement.getExpression().visit(this);
		for (CaseStatement caseStatement : statement.getCaseStatements()) {
			caseStatement.visit(this);
		}
		statement.getDefaultStatement().visit(this);
	}

	//todo
	public void visitCaseStatement(CaseStatement statement) {
		statement.getExpression().visit(this);
		statement.getCode().visit(this);
	}

	//todo
	public void visitAssertStatement(AssertStatement statement) {
		statement.getBooleanExpression().visit(this);
		statement.getMessageExpression().visit(this);
	}

	//todo
	public void visitSynchronizedStatement(SynchronizedStatement statement) {
		statement.getExpression().visit(this);
		statement.getCode().visit(this);
	}

	//todo
	public void visitRangeExpression(RangeExpression expression) {
		expression.getFrom().visit(this);
		expression.getTo().visit(this);
	}

	//todo
	public void visitMethodPointerExpression(MethodPointerExpression expression) {
		expression.getExpression().visit(this);
		expression.getMethodName().visit(this);
	}

	//todo
	public void visitPropertyExpression(PropertyExpression expression) {
		expression.getObjectExpression().visit(this);
		expression.getProperty().visit(this);
	}

	//todo
	public void visitAttributeExpression(AttributeExpression expression) {
		expression.getObjectExpression().visit(this);
		expression.getProperty().visit(this);
	}

	//todo
	public void visitCatchStatement(CatchStatement statement) {
		statement.getCode().visit(this);
	}
}
