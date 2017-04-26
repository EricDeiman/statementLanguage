import java.util.function.IntConsumer;

public class NoOp implements Executor {
    public void doIt(IntConsumer callback) {
        /* do nothing */
        return;
    }
}
