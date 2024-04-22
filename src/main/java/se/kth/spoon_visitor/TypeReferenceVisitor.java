package se.kth.spoon_visitor;

import japicmp.model.JApiCompatibilityChangeType;
import japicmp.model.JApiCompatibilityChangeType;
import spoon.reflect.reference.CtTypeReference;

/**
 * Generic visitor in charge of gathering type reference issues in client code.
 * <p>
 * It creates a broken use for every reference to the supplied {@link #clsRef}.
 * The visitor detects the following cases:
 * <ul>
 * <li> Any reference to the referenced class. Example:
 *      <pre>
 *      public void method(RefClass cls) { }
 *      </pre>
 * </ul>
 */
public class TypeReferenceVisitor extends BreakingChangeVisitor {
    /**
     * Spoon reference to the modified type
     */
    protected final CtTypeReference<?> clsRef;

    /**
     * Creates a TypeReferenceVisitor instance.
     *
     * @param clsRef modified type
     * @param change kind of breaking change
     */
    public TypeReferenceVisitor(CtTypeReference<?> clsRef, JApiCompatibilityChangeType change) {
        super(change);
        this.clsRef = clsRef;
    }

    @Override
    public <T> void visitCtTypeReference(CtTypeReference<T> reference) {
        if (clsRef.equals(reference)) {
            brokenUse(reference.getParent(), reference, clsRef);
        }
    }

	/*
	 * Uncomment in case we also want to detect:
	 *   - Every reference to a field that it declares
     *   - Every reference to an executable that it declares
     *   - Every method that overrides one of its methods
     *
	@Override
	public <T> void visitCtFieldReference(CtFieldReference<T> reference) {
		if (clsRef.equals(reference.getDeclaringType()))
			brokenUse(reference.getParent(), reference.getFieldDeclaration(), clsRef);
	}

	@Override
	public <T> void visitCtExecutableReference(CtExecutableReference<T> reference) {
		if (clsRef.equals(reference.getDeclaringType()))
			brokenUse(reference.getParent(), reference.getExecutableDeclaration(), clsRef);
	}

	@Override
	public <T> void visitCtMethod(CtMethod<T> m) {
		if (m.hasAnnotation(java.lang.Override.class)) {
			Optional<CtMethod<?>> superMethod =
				m.getTopDefinitions()
					.stream()
					.filter(superM -> clsRef.equals(superM.getDeclaringType().getReference()))
					.findAny();

			if (superMethod.isPresent())
				brokenUse(m, superMethod.get(), clsRef);
		}
	}
	*/
}
