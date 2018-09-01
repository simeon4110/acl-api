package com.sonnets.sonnet.tools;

import java.util.Objects;

/**
 * Apparently, Java doesn't implement an generic tuple class. Here one is. For reals, there is no way to
 * create a list of tuples in Java.
 *
 * @param <K>   the K.
 * @param <V> the V.
 */
public class ItemKeyValuePair<K, V> {
    private K k;
    private V v;

    public ItemKeyValuePair() {
    }

    public ItemKeyValuePair(K k, V v) {
        this.k = k;
        this.v = v;
    }

    public K getKey() {
        return k;
    }

    public V getValue() {
        return v;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ItemKeyValuePair<?, ?> that = (ItemKeyValuePair<?, ?>) o;
        return Objects.equals(k, that.k) &&
                Objects.equals(v, that.v);
    }

    @Override
    public int hashCode() {
        return Objects.hash(k, v);
    }

    @Override
    public String toString() {
        return "ItemKeyValuePair{" +
                "K=" + k +
                ", V=" + v +
                '}';
    }
}
