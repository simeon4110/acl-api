package org.acl.database.helpers;

/**
 * Apparently, Java doesn't implement an generic tuple class. Here one is. For reals, there is no way to
 * create a list of tuples in Java.
 *
 * @param <K> the Key.
 * @param <V> the Value.
 */
public class ItemKeyValuePair<K, V> {
    private K type;
    private V id;

    public ItemKeyValuePair(K type, V id) {
        this.type = type;
        this.id = id;
    }

    public K getType() {
        return type;
    }

    public V getId() {
        return id;
    }

    @Override
    public String toString() {
        return "ItemKeyValuePair{" +
                "K=" + type +
                ", V=" + id +
                '}';
    }
}
