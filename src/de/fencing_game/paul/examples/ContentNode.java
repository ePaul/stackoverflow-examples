package de.fencing_game.paul.examples;

/**
 * a tree node with content objects.
 *<p>
 * Inspired by the question <a href="http://stackoverflow.com/questions/4978487/why-java-collection-framework-doesnt-contain-tree-and-graph">Why Java Collection Framework doesn't contain Tree and Graph</a> on Stackoverflow.
 *</p>
 *
 * @param <N> the concrete node type
 * @param <C> the type of the content of this node.
 */
public interface ContentNode<C,N extends ContentNode<C,N>>
          extends Node<N>
{
   /**
    * returns the content of this node, if any.
    */
   public C getContent();

}