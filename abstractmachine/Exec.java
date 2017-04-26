import java.util.function.IntConsumer;

public class Exec implements Executor {
    public void doIt(IntConsumer callback) {
        callback.accept(0);
    }
}
