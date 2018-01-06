import java.util.*;

public class queryBuffer extends LinkedList<String> {
    private String lastChange;
    public queryBuffer() {
        super();

    }

    public String undo() throws InterruptedException {
        if (lastChange != null) {
           lastChange = this.pop();     //TODO: This does not dequeue tha last that was enqueued!
            return lastChange;
        } else return null;
    }


    public String redo() throws InterruptedException {
        this.add(lastChange);
        String temp = lastChange;
        lastChange = null;
        return temp;
    }

}
