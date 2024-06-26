package se.kth.sponvisitors;


import japicmp.model.JApiCompatibilityChangeType;
import spoon.reflect.code.CtCatch;
import spoon.reflect.code.CtThrow;
import spoon.reflect.code.CtTry;
import spoon.reflect.declaration.CtMethod;
import spoon.reflect.reference.CtTypeReference;

import java.util.Optional;
import java.util.Set;

/**
 * Broken brokenChanges of CLASS_NOW_CHECKED_EXCEPTION are:
 * - All expression throwing the now-checked-exception or one of its subtypes unless:
 * - It is caught locally
 * - The enclosing method declares the exception
 */
public class ClassNowCheckedExceptionVisitor extends BreakingChangeVisitor {
	private final CtTypeReference<?> clsRef;

	public ClassNowCheckedExceptionVisitor(CtTypeReference<?> clsRef) {
		super(JApiCompatibilityChangeType.CLASS_NOW_CHECKED_EXCEPTION);
		this.clsRef = clsRef;
	}

	@Override
	public void visitCtThrow(CtThrow throwStatement) {
		CtTypeReference<? extends Throwable> thrownType = throwStatement.getThrownExpression().getType();
		if (thrownType != null && thrownType.isSubtypeOf(clsRef)) {
			boolean isCaught = false;
			boolean isDeclared = false;

			CtTry enclosingTry = throwStatement.getParent(CtTry.class);
			if (enclosingTry != null) {
				Optional<CtCatch> excCatcher =
					enclosingTry.getCatchers()
						.stream()
						.filter(c -> thrownType.isSubtypeOf(c.getParameter().getType()))
						.findAny();

				if (excCatcher.isPresent())
					isCaught = true;
			}

			@SuppressWarnings("unchecked")
			Set<CtTypeReference<? extends Throwable>> thrownTypes =
				throwStatement.getParent(CtMethod.class)
					.getThrownTypes();

			Optional<CtTypeReference<? extends Throwable>> compatibleThrows =
				thrownTypes
					.stream()
					.filter(thrownType::isSubtypeOf)
					.findAny();

			if (compatibleThrows.isPresent())
				isDeclared = true;

			if (!isCaught && !isDeclared)
				brokenUse(throwStatement, throwStatement.getThrownExpression().getType(), clsRef, APIUse.THROWS);
		}
	}
}
