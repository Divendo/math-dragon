package org.teaminfty.math_dragon.view;

import org.teaminfty.math_dragon.view.math.Expression;


/**
 * Container of different states when e.g. a {@link Expression} is being dragged
 * or hovered by the cursor pointer.
 * 
 * @author Folkert van Verseveld
 */
public enum HoverState
{
    /** Mouse/cursor pointer does not intersect with current instance. */
    NONE,
    /** Mouse/cursor pointer intersects with current instance. */
    HOVER,
    /** The current instance is being dragged by the cursor pointer. */
    DRAG
}
