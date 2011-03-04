package de.fencing_game.paul.examples;

import java.util.Collection;

/**
 * A general interface for a tree node.
 *
 *<p>
 * Inspired by the question <a href="http://stackoverflow.com/questions/4978487/why-java-collection-framework-doesnt-contain-tree-and-graph">Why Java Collection Framework doesn't contain Tree and Graph</a> on Stackoverflow.
 * See my answer there about why this is not a sufficient interface for all
 * usecases to include it in the general API.
 *</p>
 * @param <N> the concrete node type.
 * @see ContentNode
 */
public interface Node<N extends Node<N>> {

   /**
    * equivalent to {@link #children children()}.{@link Collection#isEmpty isEmpty()}.
    * @returns true, if this is a leaf node, else false.
    */
   public boolean isLeaf();

   /**
    * returns a collection of all children of this node.
    * This collection can be changed, if this node is mutable.
    */
   public Collection<N> children();
   /**
    * returns the parent of this node.
    * @return null, if this is the root node, or the node does not know its parent, or has multiple parents.
    */
   public N parent();
}
