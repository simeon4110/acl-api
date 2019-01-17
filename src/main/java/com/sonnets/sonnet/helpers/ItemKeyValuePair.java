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
    private K key;
    private V value;

    public ItemKeyValuePair() {
    }

    public ItemKeyValuePair(K key, V value) {
        this.key = key;
        this.value = value;
    }

    public K getKey() {
        return key;
    }

    public V getValue() {
        return value;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ItemKeyValuePair<?, ?> that = (ItemKeyValuePair<?, ?>) o;
        return Objects.equals(key, that.key) &&
                Objects.equals(value, that.value);
    }

    @Override
    public int hashCode() {
        return Objects.hash(key, value);
    }

    @Override
    public String toString() {
        return "ItemKeyValuePair{" +
                "K=" + key +
                ", V=" + value +
                '}';
    }
}
