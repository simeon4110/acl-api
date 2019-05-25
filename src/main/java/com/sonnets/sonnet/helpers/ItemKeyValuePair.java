package com.sonnets.sonnet.helpers;

import java.util.Objects;

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
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ItemKeyValuePair<?, ?> that = (ItemKeyValuePair<?, ?>) o;
        return Objects.equals(type, that.type) &&
                Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(type, id);
    }

    @Override
    public String toString() {
        return "ItemKeyValuePair{" +
                "K=" + type +
                ", V=" + id +
                '}';
    }
}
