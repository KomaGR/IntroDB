import java.util.*;

public class queryBuffer extends LinkedList<Object> {
    private Object lastChange = null;

    public Object undo() {
        if (lastChange == null) {
            lastChange = this.removeLast();
            return lastChange;
        } else return null;
    }

    public Object redo() {
        if (lastChange != null) {
            this.add(lastChange);
            Object temp = lastChange;
            lastChange = null;
            return temp;
        } else return null;
    }
}
