package calculator;

import javafx.util.Pair;

import javax.swing.*;

public class MyPair extends Pair<String, String> {

    public MyPair(String key, String value) {
        super(key, value);
    }

    @Override
    public String toString() {
        return getKey();
    }
}

