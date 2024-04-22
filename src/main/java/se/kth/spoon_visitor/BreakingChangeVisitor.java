package se.kth.spoon_visitor;

import japicmp.model.JApiCompatibilityChangeType;
import japicmp.model.JApiCompatibilityChangeType;
import lombok.Getter;
import se.kth.breaking_changes.BrokenUse;
import se.kth.core.SpoonHelpers;
import spoon.reflect.cu.position.NoSourcePosition;
import spoon.reflect.declaration.CtElement;
import spoon.reflect.reference.CtReference;
import spoon.reflect.visitor.CtAbstractVisitor;

import java.util.HashSet;
import java.util.Set;

/**
 * Abstract visitor in charge of gathering broken uses in client code.
 */
public abstract class BreakingChangeVisitor extends CtAbstractVisitor {
    /**
     * Kind of breaking change as defined by JApiCmp.
     */
    protected final JApiCompatibilityChangeType change;

    /**
     * Set of detected broken uses.
     * -- GETTER --
     * Returns the set of detected broken uses.
     *
     * @return set of broken uses
     */
    @Getter
    protected final Set<BrokenUse> brokenUses = new HashSet<>();



    /**
     * Creates a BreakingChangeVisitor instance.
     * <p>
     * The constructor first invokes the constructor of the superclass.
     *
     * @param change kind of breaking change as defined by JApiCmp
     * @see <a href="https://javadoc.io/static/fr.inria.gforge.spoon/spoon-core/7.3.0/spoon/reflect/visitor/CtAbstractVisitor.html">
     * spoon.reflect.visitor.CtAbstractVisitor</a>
     */
    protected BreakingChangeVisitor(JApiCompatibilityChangeType change) {
        super();
        this.change = change;
    }

    /**
     * Add a new broken use to the set of detected broken uses.
     *
     * @param element        client code impacted by the breaking change
     * @param usedApiElement API declaration used by the impacted client code
     * @param source         API declaration that introduced the breaking change
     */
    protected void brokenUse(CtElement element, CtElement usedApiElement, CtReference source) {
        // In case we don't get a source code position for the element, we default
        // to the first parent that can be located
        CtElement locatableElement =
                element.getPosition() instanceof NoSourcePosition ?
                        SpoonHelpers.firstLocatableParent(element) :
                        element;

        BrokenUse d = new BrokenUse(
                locatableElement,
                usedApiElement,
                source,

                change
        );

        brokenUses.add(d);
    }
}