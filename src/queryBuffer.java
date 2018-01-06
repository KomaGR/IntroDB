
public class queryBuffer extends sun.misc.Queue<String> {
    private String lastChange;
    public queryBuffer() {
        super();

    }

    public String undo() throws InterruptedException {
        if (lastChange != null) {
           lastChange = this.dequeue();     //TODO: This does not dequeue tha last that was enqueued!
            return lastChange;
        } else return null;
    }


    public String redo() throws InterruptedException {
        this.enqueue(lastChange);
        String temp = lastChange;
        lastChange = null;
        return temp;
    }

}
