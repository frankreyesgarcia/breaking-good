package se.kth.sponvisitors;


import japicmp.model.JApiCompatibilityChangeType;
import spoon.SpoonException;
import spoon.reflect.declaration.CtMethod;
import spoon.reflect.reference.CtExecutableReference;

import java.util.Optional;

/**
 * Broken brokenChanges of METHOD_NOW_FINAL are:
 * - Methods overriding the now-final method (with or w/o explicit @Override)
 */
public class MethodNowFinalVisitor extends BreakingChangeVisitor {
	private final CtExecutableReference<?> mRef;

	public MethodNowFinalVisitor(CtExecutableReference<?> mRef) {
		super(JApiCompatibilityChangeType.METHOD_NOW_FINAL);
		this.mRef = mRef;
	}

	@Override
	public <T> void visitCtMethod(CtMethod<T> m) {
		try {
			Optional<CtMethod<?>> superMethod =
				m.getTopDefinitions()
					.stream()
					.filter(superM -> mRef.equals(superM.getReference()))
					.findAny();

			superMethod.ifPresent(ctMethod ->
				brokenUse(m, ctMethod, mRef, APIUse.METHOD_OVERRIDE));
		} catch (SpoonException e) {
			// FIXME: Find fancier solution. A declaration cannot be resolved
		}
	}
}
