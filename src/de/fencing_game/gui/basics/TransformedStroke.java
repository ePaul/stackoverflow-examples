package de.fencing_game.gui.basics;

import java.awt.*;
import java.awt.geom.*;


/**
 * A implementation of {@link Stroke} which transforms another Stroke
 * with an {@link AffineTransform} before stroking with it.
 *
 * This class is immutable as long as the underlying stroke is
 * immutable.
 *
 * (Mit englischen Kommentaren, weil für eine <a href="http://stackoverflow.com/questions/5046088/affinetransform-without-transforming-stroke">Antwort auf Stackoverflow</a> geschrieben.)
 */
public class TransformedStroke
    implements Stroke
{
    /**
     * To make this serializable without problems.
     */
    private static final long serialVersionUID = 1;

    /**
     * the AffineTransform used to transform the shape before stroking.
     */
    private AffineTransform transform;
    /**
     * The inverse of {@link #transform}, used to transform
     * back after stroking.
     */
    private AffineTransform inverse;
   
    /**
     * Our base stroke.
     */
    private Stroke stroke;


    /**
     * Creates a TransformedStroke based on another Stroke
     * and an AffineTransform.
     */
    public TransformedStroke(Stroke base, AffineTransform at)
        throws NoninvertibleTransformException
    {
        this.transform = new AffineTransform(at);
        this.inverse = transform.createInverse();
        this.stroke = base;
    }


    /**
     * Strokes the given Shape with this stroke, creating an outline.
     *
     * This outline is distorted by our AffineTransform relative to the
     * outline which would be given by the base stroke, but only in terms
     * of scaling (i.e. thickness of the lines), as translation and rotation
     * are undone after the stroking.
     */
    public Shape createStrokedShape(Shape s) {
        Shape sTrans = transform.createTransformedShape(s);
        Shape sTransStroked = stroke.createStrokedShape(sTrans);
        Shape sStroked = inverse.createTransformedShape(sTransStroked);
        return sStroked;
    }

}