package stonering.utils.global;

import java.io.Serializable;

/**
 * Taken from javafx.util
 *
 * @author Alexander on 19.07.2018.
 */
public class Pair<K, V> implements Serializable {

    /**
     * <p>A convenience class to represent title-value pairs.</p>
     * @since JavaFX 2.0
     */

    /**
     * Key of this <code>Pair</code>.
     */
    private K key;

    /**
     * Gets the key for this pair.
     *
     * @return key for this pair
     */
    public K getKey() {
        return key;
    }

    /**
     * Value of this this <code>Pair</code>.
     */
    private V value;

    /**
     * Gets the value for this pair.
     *
     * @return value for this pair
     */
    public V getValue() {
        return value;
    }

    /**
     * Creates a new pair
     *
     * @param key   The key for this pair
     * @param value The value to use for this pair
     */
    public Pair(K key, V value) {
        this.key = key;
        this.value = value;
    }

    /**
     * <p><code>String</code> representation of this
     * <code>Pair</code>.</p>
     * <p>
     * <p>The default title/value delimiter '=' is always used.</p>
     *
     * @return <code>String</code> representation of this <code>Pair</code>
     */
    @Override
    public String toString() {
        return key + "=" + value;
    }

    /**
     * <p>Generate a hash code for this <code>Pair</code>.</p>
     * <p>
     * <p>The hash code is calculated using both the title and
     * the value of the <code>Pair</code>.</p>
     *
     * @return hash code for this <code>Pair</code>
     */
    @Override
    public int hashCode() {
        // title's hashCode is multiplied by an arbitrary prime number (13)
        // in order to make sure there is a difference in the hashCode between
        // these two parameters:
        //  title: a  value: aa
        //  title: aa value: a
        return key.hashCode() * 13 + (value == null ? 0 : value.hashCode());
    }

    /**
     * <p>Test this <code>Pair</code> for equality with another
     * <code>Object</code>.</p>
     * <p>
     * <p>If the <code>Object</code> to be tested is not a
     * <code>Pair</code> or is <code>null</code>, then this method
     * returns <code>false</code>.</p>
     * <p>
     * <p>Two <code>Pair</code>s are considered equal if and only if
     * both the names and values are equal.</p>
     *
     * @param o the <code>Object</code> to test for
     *          equality with this <code>Pair</code>
     * @return <code>true</code> if the given <code>Object</code> is
     * equal to this <code>Pair</code> else <code>false</code>
     */
    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (o instanceof Pair) {
            Pair pair = (Pair) o;
            if (key != null ? !key.equals(pair.key) : pair.key != null)
                return false;
            if (value != null ? !value.equals(pair.value)
                    : pair.value != null)
                return false;
            return true;
        }
        return false;
    }
}
