

/**
 * A simple class to return unique labels. Never returns the same one twice.
 */
public class Labeller {

    public Labeller() {
        counter = 0;
        label = "label";
    }

    public String make() {
        return make(this.label);
    }

    public String make(String prefix) {
        return String.format("%s%d", prefix, counter++);
    }

    private long counter;
    private String label;
}
