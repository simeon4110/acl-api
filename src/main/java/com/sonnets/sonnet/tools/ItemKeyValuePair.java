package com.sonnets.sonnet.tools;

import java.util.Objects;

public class ItemKeyValuePair<key, value> {
    private key key;
    private value value;

    public ItemKeyValuePair() {
    }

    public ItemKeyValuePair(key key, value value) {
        this.key = key;
        this.value = value;
    }

    public key getKey() {
        return key;
    }

    public value getValue() {
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
                "key=" + key +
                ", value=" + value +
                '}';
    }
}
