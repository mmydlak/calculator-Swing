package calculator;

import org.mariuszgromada.math.mxparser.Expression;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.text.MessageFormat;


public class CalculatorFrame extends JFrame implements ActionListener {

    private JTextArea historyTextArea;
    private JList<String> functionsList;
    private JTextField formulaInput;
    private JButton evalButton;
    private DefaultListModel<MyPair> listModel;
    private String lastResult = "";
    private String lastFormula = "";

    public CalculatorFrame() {
        super("Calculator");

        setJMenuBar(createMenu(this));

        JPanel mainPanel = new JPanel();
        mainPanel.setLayout(new GridBagLayout());

        GridBagConstraints constrForHistPanel = new GridBagConstraints();
        constrForHistPanel.gridx = 0;                                                       //kolumna 0
        constrForHistPanel.gridy = 0;                                                       //wiersz 0
        constrForHistPanel.weightx = 1;                                                     //moze sie rozszerzac wzgledem x
        constrForHistPanel.weighty = 1;                                                     //moze sie rozszerzac wzgledem y
        constrForHistPanel.fill = GridBagConstraints.BOTH;                                  //wypelniaj (nawet gdy brak tekstu) cale dostepne miejsce e obu kierunkach
        constrForHistPanel.insets = new Insets(10,10,5,5);          //padding (odstęp) dookoła
        mainPanel.add(createHistoryPanel(), constrForHistPanel);

        GridBagConstraints constrForFormulaInput = new GridBagConstraints();
        constrForFormulaInput.gridx = 0;
        constrForFormulaInput.gridy = 1;
        constrForFormulaInput.weightx = 1;
        constrForFormulaInput.weighty = 0;
        constrForFormulaInput.fill = GridBagConstraints.BOTH;
        constrForFormulaInput.insets = new Insets(5,10,10,5);
        mainPanel.add(createFormulaInput(), constrForFormulaInput);

        GridBagConstraints constrForFuncPanel = new GridBagConstraints();
        constrForFuncPanel.gridx = 1;
        constrForFuncPanel.gridy = 0;
        constrForFuncPanel.weightx = 0;
        constrForFuncPanel.weighty = 1;
        constrForFuncPanel.fill = GridBagConstraints.BOTH;
        constrForFuncPanel.insets = new Insets(10,5,5,10);
        mainPanel.add(createFunctionsPanel(), constrForFuncPanel);

        GridBagConstraints constrForEvalButton = new GridBagConstraints();
        constrForEvalButton.gridx = 1;
        constrForEvalButton.gridy = 1;
        constrForEvalButton.weightx = 0;
        constrForEvalButton.weighty = 0;
        constrForEvalButton.fill = GridBagConstraints.NONE;
        constrForEvalButton.insets = new Insets(5, 5, 10, 10);
        constrForEvalButton.ipadx = 80;
        mainPanel.add(createEvalButton(), constrForEvalButton);

        add(mainPanel);

        setPreferredSize(new Dimension(700,500));
        setLocation(50, 50);
        setLookAndFell("Nimbus");
        pack();
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setVisible(true);
    }

    private JMenuBar createMenu(JFrame frame) {
        JMenuBar menuBar = new JMenuBar();

        JMenu menuOptions = new JMenu("Options");
        menuBar.add(menuOptions);

        JMenuItem mReset = new JMenuItem("Reset");
        mReset.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                historyTextArea.setText("");
            }
        });

        JMenuItem mExit = new JMenuItem("Exit");
        mExit.addActionListener(e -> {
            frame.setVisible(false);
            frame.dispose();
        });

        menuOptions.add(mReset);
        menuOptions.add(mExit);

        return menuBar;
    }

    private JPanel createHistoryPanel() {
        JPanel historyPanel = new JPanel();
        historyPanel.setBorder(BorderFactory.createTitledBorder(BorderFactory.createLineBorder(Color.BLACK), "Historia operacji:"));

        historyPanel.setLayout(new GridBagLayout());
        GridBagConstraints constr = new GridBagConstraints();
        constr.weightx = 1;
        constr.weighty = 1;
        constr.fill = GridBagConstraints.BOTH;
        historyPanel.add(createHistoryTextArea(), constr);

        return historyPanel;
    }

    private JScrollPane createHistoryTextArea() {
        historyTextArea = new JTextArea();
        historyTextArea.setEditable(false);
        JScrollPane scrollContainerPane = new JScrollPane(historyTextArea);
        return scrollContainerPane;
    }

    private JTextField createFormulaInput() {
        formulaInput = new JTextField();

        formulaInput.addActionListener(this);
        formulaInput.addKeyListener(new KeyAdapter() {
            @Override
            public void keyReleased(KeyEvent e) {
                if (e.getKeyCode() == 38) {
                    formulaInput.setText(lastFormula);
                }
            }
        });


        return formulaInput;
    }

    private JPanel createFunctionsPanel() {
        JPanel functionsPanel = new JPanel();
        functionsPanel.setBorder(BorderFactory.createTitledBorder(BorderFactory.createLineBorder(Color.BLACK), "Dostepne funkcje:"));

        functionsPanel.setLayout(new GridBagLayout());
        GridBagConstraints constr = new GridBagConstraints();
        constr.weightx = 0;
        constr.weighty = 1;
        constr.fill = GridBagConstraints.BOTH;
        constr.ipadx = 50;                                              //padding (odstep) w srodku na osi x
        functionsPanel.add(createFunctionsList(), constr);

        return functionsPanel;
    }

    private JList<String> createFunctionsList() {
        listModel = new DefaultListModel<>();
        listModel.addElement(new MyPair("Sinus", "sin()"));
        listModel.addElement(new MyPair("Cosinus", "cos()"));
        listModel.addElement(new MyPair("Pierwiastek", "sqrt()"));
        listModel.addElement(new MyPair("Wartosc bezwzgledna", "abs()"));
        listModel.addElement(new MyPair("Logarytm naturalny", "ln()"));
        listModel.addElement(new MyPair("+", "+"));
        listModel.addElement(new MyPair("-", "-"));
        listModel.addElement(new MyPair("*", "*"));
        listModel.addElement(new MyPair("Liczba pi", "pi"));
        listModel.addElement(new MyPair("Liczba e", "e"));
        listModel.addElement(new MyPair("Liczba phi", "[phi]"));
        listModel.addElement(new MyPair("Ostatni wynik", lastResult));

        functionsList = new JList(listModel);

        functionsList.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() == 2) {
                    String stringToInsert = (listModel.getElementAt(functionsList.getSelectedIndex())).getValue();
                    StringBuilder formulaInputText = new StringBuilder(formulaInput.getText());
                    formulaInput.setText((formulaInputText.insert(formulaInput.getCaretPosition(), stringToInsert)).toString());
                    if(formulaInput.getText().contains(")")) {
                        formulaInput.setCaretPosition(formulaInput.getText().indexOf(')'));
                    }
                    formulaInput.requestFocus();
                }
            }
        });

        return functionsList;
    }

    private JButton createEvalButton() {
        evalButton = new JButton("OBLICZ");
        evalButton.addActionListener(this);
        return evalButton;
    }

    private void setLookAndFell(String option) {
        try {
            switch(option) {
                case "Nimbus":
                    UIManager.setLookAndFeel("javax.swing.plaf.nimbus.NimbusLookAndFeel");
                    break;
                case "Windows":
                    UIManager.setLookAndFeel("com.sun.java.swing.plaf.windows.WindowsLookAndFeel");
                    break;
                default:
            }
        } catch (ClassNotFoundException | InstantiationException
                | IllegalAccessException | UnsupportedLookAndFeelException e) {
            e.printStackTrace();
        }
        SwingUtilities.updateComponentTreeUI(this);
    }

    @Override
    public void actionPerformed(ActionEvent e) {

        Object source = e.getSource();

        if (source == formulaInput || source == evalButton) {
            handleFormulaInputEvent();
        }

    }

    private void handleFormulaInputEvent() {
        lastFormula = formulaInput.getText();
        formulaInput.setText("");
        Expression expression = new Expression(lastFormula);
        try {
            if (expression.checkSyntax()) {
                Double result = expression.calculate();
                lastResult = result.toString();
                if (Double.isNaN(result)) {
                    throw new Exception("NaN");
                }
                listModel.set(listModel.size()-1, new MyPair("Ostatni wynik", lastResult));
                String newResult = MessageFormat.format("{0} = {1}\n::::::::::::::::::::::::::::::\n", lastFormula, result);
                historyTextArea.append(newResult);
            } else {
                String errorMessage = expression.getErrorMessage();
                throw new Exception(errorMessage);
            }
        } catch (Exception exc) {
            JOptionPane.showMessageDialog(null, exc.getMessage(), "Niewlasciwa formula!", JOptionPane.ERROR_MESSAGE);
        }
    }

}
