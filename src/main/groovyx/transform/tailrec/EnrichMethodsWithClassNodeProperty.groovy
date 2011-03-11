package groovyx.transform.tailrec

import org.codehaus.groovy.ast.ClassCodeVisitorSupport
import org.codehaus.groovy.ast.ClassNode
import org.codehaus.groovy.ast.MethodNode
import org.codehaus.groovy.ast.ModuleNode
import org.codehaus.groovy.control.SourceUnit

/*
 * Enricher works only in a single thread.
 * And the classNode property is only available in this thread
 */
class EnrichMethodsWithClassNodeProperty extends ClassCodeVisitorSupport {

	ModuleNode ast
	private SourceUnit sourceUnit
	private ClassNode current

	void setSourceUnit(SourceUnit sourceUnit) {
		this.sourceUnit = sourceUnit
		ast = sourceUnit.ast	
	}
	
	@Override
	protected SourceUnit getSourceUnit() {
		sourceUnit
	}
	
	@Override
	public void visitMethod(MethodNode node) {
		node.metaClass.classNode = current
	}
	
	def enrich() {
		ast.classes.each {
			current = it
			it.visitContents(this)
		}
	}
}
