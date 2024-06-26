package se.kth.sponvisitors;



import japicmp.model.JApiCompatibilityChangeType;
import spoon.reflect.declaration.CtClass;
import spoon.reflect.reference.CtTypeReference;
import util.SpoonTypeHelpers;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

/**
 * Visitor in charge of gathering all method added to interface issues in
 * client code.
 * <p>
 * The visitor detects the following cases:
 * <ul>
 * <li> Concrete classes implementing the modified interface. Example:
 *      <pre>
 *      public class MyClass implements ModifiedInterface { }
 *      </pre>
 * </ul>
 */
public class MethodAddedToInterfaceVisitor extends BreakingChangeVisitor {
	/**
	 * Spoon reference to the interface where the new method has been added
	 */
	private final CtTypeReference<?> clsRef;

	/**
	 * Creates a MethodAddedToInterfaceVisitor instance.
	 *
	 * @param clsRef the interface where the new method has been added
	 */
	public MethodAddedToInterfaceVisitor(CtTypeReference<?> clsRef) {
		super(JApiCompatibilityChangeType.METHOD_ADDED_TO_INTERFACE);
		this.clsRef = clsRef;
	}

	@Override
	public <T> void visitCtClass(CtClass<T> cls) {
		if (!cls.isAbstract()) {
			CtTypeReference<?> typeRef = cls.getReference();
			Set<CtTypeReference<?>> interfaces = new HashSet<>(typeRef.getSuperInterfaces());
			Set<CtTypeReference<?>> superCls = new HashSet<>(Collections.singletonList(typeRef.getSuperclass()));

			if (SpoonTypeHelpers.isSubtype(interfaces, clsRef))
				brokenUse(cls, cls, clsRef, APIUse.IMPLEMENTS);

			if (SpoonTypeHelpers.isSubtype(superCls, clsRef))
				brokenUse(cls, cls, clsRef, APIUse.EXTENDS);
		}
	}
}

