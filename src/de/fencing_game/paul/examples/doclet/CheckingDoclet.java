package de.fencing_game.paul.examples.doclet;

import com.sun.javadoc.*;

public class CheckingDoclet extends Doclet {

    private static void checkElement(ProgramElementDoc ped,
                                     DocErrorReporter err) {
        if(ped.commentText().equals("")) {
            err.printError(ped.position(), ped + " has no documentation!");
        }
    }

    private static void checkAll(ProgramElementDoc[] array,
                                 DocErrorReporter err) {
        for(ProgramElementDoc ped : array) {
           checkElement(ped, err);
        }
    }

    public static boolean start(RootDoc root) {
        for(ClassDoc clazz : root.classes()) {
           checkElement(clazz, root);
           checkAll(clazz.constructors(), root);
           checkAll(clazz.fields(), root);
           checkAll(clazz.enumConstants(), root);
           checkAll(clazz.methods(), root);
        }
        return true;
    }
}