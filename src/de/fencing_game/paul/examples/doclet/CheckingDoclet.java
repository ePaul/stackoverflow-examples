package de.fencing_game.paul.examples.doclet;

import com.sun.javadoc.*;


/**
 * A simple doclets that checks that each included program element
 * has a non-empty javadoc documentation. The doclet produces no
 * output if no errors are found.
 *
 * <p>
 * Inspired by the question <a href="http://stackoverflow.com/questions/5244048/javadoc-warn-on-no-comment">Javadoc warn on no comment</a> on Stackoverflow.
 *</p>
 */
public class CheckingDoclet extends Doclet {


    /** Constructor only for internal use. */
    private CheckingDoclet() {}
    
    /**
     * did we find an error?
     */
    private boolean errorFound = false;

    /**
     * checks an element on existence of documentation.
     * @param doc the element to check
     * @param err here we output the error.
     */
    private void checkElement(Doc doc,
                              DocErrorReporter err) {
        if(doc.commentText().equals("")) {
            err.printError(doc.position(),
                           typeOf(doc) + " " + doc.name() +
                           " has no documentation!");
            errorFound = true;
        }
    }

    /**
     * checks all elements of an array.
     */
    private void checkAll(Doc[] array,
                          DocErrorReporter err) {
        for(Doc ped : array) {
           checkElement(ped, err);
        }
    }

    /**
     * checks a type declaration and all its elements (constructors, methods,
     * fields, enum constants (if an enum), annotation type elements
     * (if this is an annotation type)).
     * @param clazz the ClassDoc of the type to check.
     * @param here we send the error messages to.
     */
    private void checkClass(ClassDoc clazz, DocErrorReporter err) {
        checkElement(clazz, err);
        checkAll(clazz.constructors(), err);
        checkAll(clazz.fields(), err);
        checkAll(clazz.enumConstants(), err);
        checkAll(clazz.methods(), err);
        if(clazz.isAnnotationType()) {
            checkAll(((AnnotationTypeDoc)clazz).elements(), err);
        }
    }

    /**
     * returns the type of the given {@link Doc}, one of
     * {@code "method"},
     * {@code "enum"},
     * {@code "class"},
     * {@code "field"},
     * {@code "interface"},
     * {@code "enum constant"},
     * {@code "annotation"},
     * {@code "annotation element"},
     * {@code "package"}.
     *
     * Why is something like this not in the Doclet API?
     */
    public static String typeOf(Doc doc) {
        if(doc.isMethod())
            return "method";
        if(doc.isEnum())
            return "enum";
        if(doc.isClass())
            return "class";
        if (doc.isConstructor()) 
            return "constructor";
        if (doc.isField()) 
            return "field";
        if (doc.isInterface())
            return "interface";
        if(doc.isEnumConstant())
            return "enum constant";
        if (doc.isAnnotationType())
            return "annotation";
        if(doc.isAnnotationTypeElement())
            return "annotation element";
        if(doc instanceof PackageDoc) // why is there no method `isPackage()`?
            return "package";
        return "<unknown>";
    }


    /**
     * The entry point of the doclet.
     * @return true if all the included elements have enough documentation,
     *   false if some documentation is missing.
     */
    public static boolean start(RootDoc root) {
        CheckingDoclet checker = new CheckingDoclet();
        checker.checkAll(root.specifiedPackages(), root);
        for(ClassDoc clazz : root.classes()) {
            checker.checkClass(clazz, root);
        }
        return ! checker.errorFound;
    }
}