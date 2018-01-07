import java.util.*;

public class queryBuffer extends LinkedList<String> {
    private String lastChange;

    public String undo() {
        if (lastChange != null) {
            lastChange = this.removeLast();
            return lastChange;
        } else return null;
    }

    public String redo() {
        this.add(lastChange);
        String temp = lastChange;
        lastChange = null;
        return temp;
    }
}
