package se.kth.sponvisitors;


import japicmp.model.JApiCompatibilityChangeType;
import spoon.reflect.code.CtNewClass;
import spoon.reflect.declaration.CtClass;
import spoon.reflect.reference.CtTypeReference;

/**
 * Broken brokenChanges of CLASS_NOW_FINAL are:
 * - Classes (regular and anonymous) extending the now-final class
 * <p>
 * Note that JApiCmp reports a CLASS_NOW_FINAL on types that go from {@code class}
 * to {@code enum}.
 */
public class ClassNowFinalVisitor extends BreakingChangeVisitor {
	private final CtTypeReference<?> clsRef;

	public ClassNowFinalVisitor(CtTypeReference<?> clsRef) {
		super(JApiCompatibilityChangeType.CLASS_NOW_FINAL);
		this.clsRef = clsRef;
	}

	@Override
	public <T> void visitCtClass(CtClass<T> ctClass) {
		if (clsRef.equals(ctClass.getSuperclass()))
			brokenUse(ctClass, ctClass.getSuperclass(), clsRef, APIUse.EXTENDS);
	}

	@Override
	public <T> void visitCtNewClass(CtNewClass<T> newClass) {
		// Anonymous classes (CtNewClass) also go through (CtClass)
		// -> don't count twice
	}
}
