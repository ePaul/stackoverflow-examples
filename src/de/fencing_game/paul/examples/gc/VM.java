package de.fencing_game.paul.examples.gc;

import java.util.*;


public class VM {

    private Allocator alloc;
    private Heap heap;


    private RootList root = new RootList() {
            public Iterator<MemoryCell> iterator() {
                // TODO
                return null;
            }

            public void stopUserThreads() {
                // TODO
            }
            public void resumeUserThreads() {
                // TODO
            }

        };


    public VM(Allocator alloc) {
        // TODO
    }
    

    /**
     * environment of a method frame.
     */
    private class FrameEnvironment
        implements Environment {

        private Deque<FrameEnvironment> stack;

        private Set<MemoryCell> activeCells;
        
        
        public FrameEnvironment(Deque<FrameEnvironment> stack) {
            this.stack = stack;
            this.activeCells = new HashSet<MemoryCell>();
        }


        public int newObject()
        {
            MemoryCell cell = alloc.allocateObject();
            assert ! activeCells.contains(cell);

            activeCells.add(cell);
            return cell.getPosition();
        }

        public int getReference(int object, int index)
        {
            MemoryCell cell = heap.getMemoryCell(object);
            if(!activeCells.contains(cell)) {
                throw new IllegalArgumentException("Object " + object +
                                                   " not referenced!");
            }
            int result = cell.getReference(index);
            if(result != 0) {
                MemoryCell reference = heap.getMemoryCell(result);
                activeCells.add(reference);
            }
            return result;
        }

        public int getData(int object, int index)
        {
            MemoryCell cell = heap.getMemoryCell(object);
            if(!activeCells.contains(cell)) {
                throw new IllegalArgumentException("Object " + object +
                                                   " not referenced!");
            }
            int result = cell.getData(index);
            assert result < 0;
            return result;
        }

        public void release(int object)
        {
            MemoryCell cell = heap.getMemoryCell(object);
            if(!activeCells.contains(cell)) {
                throw new IllegalArgumentException("Object " + object +
                                                   " not referenced!");
            }
            activeCells.remove(cell);
        }

        public void setReference(int object, int index, int value) {
            MemoryCell cell = heap.getMemoryCell(object);
            if(!activeCells.contains(cell)) {
                throw new IllegalArgumentException("Object " + object +
                                                   " not referenced!");
            }
            MemoryCell valueCell = heap.getMemoryCell(value);
            if(!activeCells.contains(valueCell)) {
                throw new IllegalArgumentException("Object " + value +
                                                   " not referenced!");
            }
            cell.setReference(index, value);
        }
        
        public void setData(int object, int index, int value) {
            MemoryCell cell = heap.getMemoryCell(object);
            if(!activeCells.contains(cell)) {
                throw new IllegalArgumentException("Object " + object +
                                                   " not referenced!");
            }
            if(value >= 0) {
                throw new IllegalArgumentException("value = "+value+" >= 0");
            }
            cell.setData(index, value);
        }

        public int callMethod(UserMethod method, int... arguments) {
            FrameEnvironment childEnv = new FrameEnvironment(this.stack);
            this.stack.push(childEnv);
            for(int arg : arguments) {
                if(arg > 0) {
                    MemoryCell cell = heap.getMemoryCell(arg);
                    if(!activeCells.contains(cell)) {
                        throw new IllegalArgumentException("Object " + arg +
                                                           " not referenced!");
                    }
                    childEnv.activeCells.add(cell);
                }
            }
            int result = method.invoke(childEnv, arguments);
            if(result > 0) {
                MemoryCell cell = heap.getMemoryCell(result);
                if(! childEnv.activeCells.contains(cell) ) {
                    throw new IllegalArgumentException("Object " + result +
                                                       "not referenced!");
                }
                this.activeCells.add(cell);
            }
            this.stack.pop();
            return result;
        }


    }



    public static void main(String[] params) {
    }


}