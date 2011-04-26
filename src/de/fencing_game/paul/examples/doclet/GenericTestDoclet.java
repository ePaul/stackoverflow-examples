package de.fencing_game.paul.examples.doclet;

import com.sun.javadoc.*;
import java.util.ArrayList;

/**
 * A test doclet to see how generic fields work.
 *
 * Inspired by the question <a href="http://stackoverflow.com/q/5731619/600500">Doclet- Get generics of a list</a> on Stackoverflow.
 */
public class GenericTestDoclet<Y> extends Doclet {

    public ArrayList<? extends Y> stringList;

    /**
     * Erstellt ein(e(n)) neu(e(n)) <code>GenericTestDoclet</code>.
     *
     */
    public GenericTestDoclet() {

    }


    public ArrayList<? extends Y> getList() {
        return stringList;
    }

    public void printType(Type fieldType, DocErrorReporter err) {
        err.printNotice("type: " + fieldType);
        if (fieldType.asParameterizedType() != null) {
            ParameterizedType paramType = fieldType.asParameterizedType();
            err.printNotice("paramType:" + paramType);
            String qualiName = paramType.qualifiedTypeName();
            err.printNotice("qualiName: " + qualiName);

            String typeName = fieldType.asParameterizedType().typeName();
            err.printNotice("typeName: " + typeName);

            Type[] parameters = paramType.typeArguments();
            err.printNotice("parameters.length: " + parameters.length);
            for(Type p : parameters) {
                err.printNotice("param: " + p);
            }
        }
        err.printNotice("");
    }


    public void listFields(ClassDoc classDoc, DocErrorReporter err) {
        FieldDoc[] fields = classDoc.fields();
        for (int k = 0; k < fields.length; k++) {
            err.printNotice("field: " + fields[k]);
            Type fieldType = fields[k].type();
            printType(fieldType, err);
        }
    }


    public void listMethods(ClassDoc classDoc, DocErrorReporter err) {
        MethodDoc[] methods = classDoc.methods();
        for (int k = 0; k < methods.length; k++) {
            err.printNotice("method: " + methods[k]);
            Type returnType = methods[k].returnType();
            printType(returnType, err);
        }
    }



    /**
     * The entry point of the doclet.
     * @return true if all the included elements have enough documentation,
     *   false if some documentation is missing.
     */
    public static boolean start(RootDoc root) {
        GenericTestDoclet<?> d = new GenericTestDoclet<Integer>();
        for(ClassDoc clazz : root.classes()) {
            d.listFields(clazz, root);
            d.listMethods(clazz, root);
        }
        return true;
    }



}
