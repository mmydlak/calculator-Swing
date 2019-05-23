package calculator;

import java.awt.*;

public class Main {

    static public void main(String[] args) {
        EventQueue.invokeLater(new Runnable() {
            @Override
            public void run() {
                new CalculatorFrame();
            }
        });
    }
}
