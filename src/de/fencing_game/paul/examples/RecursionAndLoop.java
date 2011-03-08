package de.fencing_game.paul.examples;

import java.util.*;


/**
 * From http://stackoverflow.com/questions/5237461/how-to-get-recursion-and-for-loop-work-together-in-java.
 */
public class RecursionAndLoop {


    private static class Move {
        private String id;
        public Move(String type) {
            this.id = type;
        }
        public String toString(){
            return "Move(" + id + ")";
        }

    }


    private static class GameState {
        private static int nextID;

        private int id;

        GameState() {
            this.id = nextID++;
        }

        public GameState getNewInstance(Move move) {
            return new GameState();
        }

        public int getPossibleMoveCount(int index) {
            return 5;
        }

        public Vector<Move> getPossibleMoves(int index) {
            Vector<Move> v = new Vector<Move>();
            for(int i = 0; i < 5; i++) {
                v.add(new Move(index + "×" + i));
            }
            return v;
        }

        public int getMarkCount(int index) {
            return 20 + index;
        }

        public String toString() {
            return "GameState[" + id + "]";
        }
    }

    private static class Node {
        private GameState state;
        private Move move;

        private List<Node> children;
        private Node parent;
        private int score;

        public Node(GameState s, Move m) {
            this.children = new ArrayList<Node>();
            this.state = s;
            this.move = m;
        }

        public void addChild(Node child) {
            children.add(child);
        }

        public void setParent(Node node) {
            parent = node;
        }

        public void setScore(int neu) {
            this.score = neu;
        }

        public int getDepth() {
            if(parent == null) {
                return 0;
            }
            return 1 + parent.getDepth();
        }

        /**
         * prints a simple tree view of this ZipNode and its descendants
         * on {@link System.out}.
         * @param prefix a prefix string to add before all lines.
         * @param self a prefix string to add before the line of this node
         *    itself (after the general prefix).
         * @param sub a prefix string to add before the line of all subnodes
         *    of this node (after the general prefix).
         */
        private void printTree(String prefix,
                               String self,
                               String sub) {
            System.out.println(prefix + self + state + " - " + move +
                               " - " + score);
            String subPrefix = prefix + sub;
            // the prefix strings for the next level.
            String nextSelf = " ├─ ";
            String nextSub =  " │ ";
            Iterator<Node> iterator =
                this.children.iterator();
            while(iterator.hasNext()) {
                Node child = iterator.next();
                if(!iterator.hasNext() ) {
                    // last item, without the "|"
                    nextSelf = " └─ ";
                    nextSub =  "   ";
                }
                child.printTree(subPrefix, nextSelf, nextSub);
            }
        }



    }


    int switchIndex(int index) {
        return index + 1;
    }

    


    private void makeTree(GameState prevState, Vector<Move> moves, Node parentNode, int index, int depthLimit) {

        if(prevState.getPossibleMoveCount(index) != 0){

            for(int i = 0; i < moves.size(); i++){

                Move thisMove = moves.get(i);
                GameState newState = prevState.getNewInstance(thisMove);
                Node child = new Node(newState, thisMove);
                parentNode.addChild(child);
                child.setParent(parentNode);

                if((child.getDepth() + 1) < depthLimit){

                    int newIndex = switchIndex(index);
                    Vector<Move> newMoves = newState.getPossibleMoves(newIndex);
                    makeTree(newState, newMoves, child, newIndex, depthLimit);

                }else{

                    child.setScore(newState.getMarkCount(index));
                }
            }
        }
    }


    public static void main(String[] params) {
        GameState start = new GameState();
        Vector<Move> m = new Vector<Move>();
        m.add(new Move("start"));
        Node root = new Node(start, null);
        int index = 7;
        int depthLimit = 6;
        new RecursionAndLoop().makeTree(start, m, root, index, depthLimit);
        root.printTree("", " ", "");
    }




}
