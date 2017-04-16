
/**
 * Values that our interpreter works with.  It encapsulates the Java types that
 * are used to implement the interpreter.
 */
public class InterpValue {

    /**
     * Interpret-time values know their type along with their value.
     */
    public InterpValue( InterpType type, Object value) {
        this.type = type;
        this.value = value;
    }

    public InterpType getType() {
        return this.type;
    }

    public Object getValue() {
        return this.value;
    }

    private InterpType type;
    private Object value;
}
