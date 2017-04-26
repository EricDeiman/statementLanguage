import java.util.function.IntConsumer;

public interface Executor {
    public void doIt(IntConsumer callback);
}
