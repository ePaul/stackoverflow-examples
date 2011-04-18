package de.fencing_game.gui.basics;

import javax.swing.JComponent;
import javax.swing.GroupLayout;
import javax.swing.GroupLayout.Group;
import javax.swing.GroupLayout.Alignment;
import java.awt.Container;

/**
 * Eine Hilfsklasse zum bequemeren Umgang mit {@link GroupLayout}.
 *
 * <p>Diese Klasse erweitert {@link GroupLayout}, damit man auch bequem die
 * Methoden von GroupLayout selbst benutzen kann.
 * Dem im Konstruktor übergebenen Container wird dieses LayoutHelper-Objekt
 * (welches ja auch ein GroupLayout ist) als LayoutManager verpasst.
 *</p>
 *
 *<p>Hier eine Beispiel-Verwendung:</p>
 * <pre>
 *     LayoutHelper h = new LayoutHelper(panel, false);
 *
 *     h.setHorizontalGroup
 *         (   h.parallel
 *             (  h.sequential
 *                (   h.parallel(nameLabel, pwdLabel),
 *                    h.parallel(name, pwd)),
 *                h.sequential(loginB)));
 *     h.setVerticalGroup
 *         (   h.sequential
 *             (   h.parallel(nameLabel, name),
 *                 h.parallel(pwdLabel, pwd),
 *                 h.parallel(loginB)));
 * </pre>
 * <p>(Dabei gehen wir davon aus, dass nameLabel, pwdLabel, name, pwd und
 *   loginB jeweils Komponenten sind.)</p>
 *
 * Die parallel-Methode kann neben Gruppen/Komponenten als erste Parameter
 * auch eine Einstellung für Größenänderbarkeit (boolean) und für die
 * gewünschte Ausrichtung ({@link Alignment}) bekommen - falls nicht gegeben,
 * werden die in dieser Klasse (mittels Konstruktor) festgelegten
 * Default-Werte verwendet.
 */
public class LayoutHelper
    extends GroupLayout
{
    private boolean resizableDef;
    private Alignment alignmentDef;


    /**
     * Erstellt einen LayoutHelper für einen Container.
     * Dies entspricht {@code LayoutHelper(cont, true, Alignment.LEADING)}.
     */
    public LayoutHelper(Container cont) {
        this(cont, true, Alignment.LEADING);
    }
    /**
     * Erstellt einen LayoutHelper für einen Container.
     * Dies entspricht {@code LayoutHelper(cont, r, Alignment.LEADING)}.
     */
    public LayoutHelper(Container cont, boolean r) {
        this(cont, r, Alignment.LEADING);
    }
    /**
     * Erstellt einen LayoutHelper für einen Container.
     * Dies entspricht {@code LayoutHelper(cont, true, a)}.
     */
    public LayoutHelper(Container cont, Alignment a) {
        this(cont, true, a);
    }

    /**
     * Erstellt einen neuen LayoutHelper.
     * @param cont der Container, der von unserem GroupLayout zu managen ist.
     * @param r  sind parallele Gruppen standardmäßig resizable?
     * @param a  wie sollen die Elemente in parallelen Gruppen angeordnet
     *           werden?
     */
    public LayoutHelper(Container cont, boolean r, Alignment a) {
        super(cont);
        this.resizableDef = r;
        this.alignmentDef = a;
        cont.setLayout(this);
        setAutoCreateGaps(true);
        setAutoCreateContainerGaps(true);
    }

    /**
     * Fügt alle Gruppen als Untergruppen zur ersten hinzu.
     * @param g eine Gruppe.
     * @param subgroups einige weitere Gruppen.
     * @return die Gruppe g.
     */
    public Group addAll(Group g, Group... subgroups) {
        for(Group c : subgroups) {
            g.addGroup(c);
        }
        return g;
    }

    /**
     * Fügt alle Komponenten zur Gruppe hinzu.
     * @param g eine Gruppe.
     * @param liste einige Komponenten.
     * @return die Gruppe g.
     */
    public Group addAll(Group g, JComponent... liste) {
        for(JComponent c : liste) {
            g.addComponent(c);
        }
        return g;
    }

    /**
     * Erstellt eine parallele Gruppe und fügt die Komponenten hinzu.
     * @param resizable soll die Gruppe in der Größe änderbar sein?
     * @param alignment wie sollen die Komponenten ausgerichtet werden?
     * @param comps die hinzuzufügenden Komponenten.
     */
    public Group parallel(boolean resizable,
                          Alignment alignment,
                          JComponent... comps)
    {
        return addAll(parallel(resizable, alignment), comps);
    }

    /**
     * Erstellt eine parallele Gruppe und fügt die Komponenten hinzu.
     * @param comps die hinzuzufügenden Komponenten.
     */
    public Group parallel(JComponent... comps) {
        return addAll(parallel(), comps);
    }
    /**
     * Erstellt eine parallele Gruppe und fügt die Komponenten hinzu.
     * @param resizable soll die Gruppe in der Größe änderbar sein?
     * @param comps die hinzuzufügenden Komponenten.
     */
    public Group parallel(boolean resizable, JComponent... comps) {
        return addAll(parallel(resizable), comps);
    }
    /**
     * Erstellt eine parallele Gruppe und fügt die Komponenten hinzu.
     * @param al wie sollen die Komponenten ausgerichtet werden?
     * @param comps die hinzuzufügenden Komponenten.
     */
    public Group parallel(Alignment al, JComponent... comps) {
        return addAll(parallel(al), comps);
    }

    /**
     * Erstellt eine leere parallele Gruppe.
     * Die Ausrichtung und zoombarkeit werden aus den Default-Werten
     * des LayoutHelpers genommen.
     */
    public Group parallel() {
        return parallel(this.resizableDef, this.alignmentDef);
    }

    /**
     * Erstellt eine leere parallele Gruppe.
     * Die Ausrichtung wird aus den Default-Werten
     * des LayoutHelpers genommen.
     * @param resizable soll die Gruppe in der Größe änderbar sein?
     */
    public Group parallel(boolean resizable) {
        return parallel(resizable, this.alignmentDef);
    }

    /**
     * Erstellt eine leere parallele Gruppe.
     * @param resizable soll die Gruppe in der Größe änderbar sein?
     * @param al wie sollen die Komponenten ausgerichtet werden?
     */
    public Group parallel(boolean resizable, Alignment al) {
        return createParallelGroup(al, resizable);
    }

    /**
     * Erstellt eine leere parallele Gruppe.
     * Die Zoombarkeit wird aus den Default-Werten
     * des LayoutHelpers genommen.
     * @param alignment wie sollen die Komponenten ausgerichtet werden?
     */
    public Group parallel(Alignment alignment) {
        return parallel(this.resizableDef, alignment);
    }

    /**
     * Erstellt eine parallele Gruppe und fügt die Untergruppen hinzu.
     */
    public Group parallel(Group... subgroups) {
        return addAll(parallel(),subgroups);
    }

    /**
     * Erstellt eine parallele Gruppe und fügt die Untergruppen hinzu.
     * @param a wie sollen die Komponenten ausgerichtet werden?
     */
    public Group parallel(Alignment a, Group... subgroups) {
        return addAll(parallel(a), subgroups);
    }

    /**
     * Erstellt eine parallele Gruppe und fügt die Untergruppen hinzu.
     * @param resizable soll die Gruppe in der Größe änderbar sein?
     */
    public Group parallel(boolean resizable, Group... subgroups) {
        return addAll(parallel(resizable), subgroups);
    }

    /**
     * Erstellt eine parallele Gruppe und fügt die Untergruppen hinzu.
     * @param resizable soll die Gruppe in der Größe änderbar sein?
     * @param a wie sollen die Komponenten ausgerichtet werden?
     */
    public Group parallel(boolean resizable, Alignment a,
                          Group... subgroups) {
        return addAll(parallel(resizable, a), subgroups);
    }

    /**
     * Erstellt eine leere sequentielle Gruppe.
     */
    public Group sequential() {
        return createSequentialGroup();
    }

    /**
     * Erstellt eine sequentielle Gruppe und fügt
     * einige Komponenten hinzu.
     */
    public Group sequential(JComponent... comps) {
        return addAll(sequential(), comps);
    }

    /**
     * Erstellt eine sequentielle Gruppe und fügt
     * einige Gruppen als Untergruppen hinzu.
     */
    public Group sequential(Group... subgroups) {
        return addAll(sequential(), subgroups);
    }
    
}