package com.example.calculator;

import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import java.util.Stack;

public class MainActivity extends AppCompatActivity {

    private TextView display;
    private String currentInput = "";
    private boolean lastNumeric;
    private boolean stateError;
    private boolean lastDot;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        display = findViewById(R.id.display);

        setButtonListeners();
    }

    private void setButtonListeners() {
        int[] numberIds = {
                R.id.btn0, R.id.btn1, R.id.btn2, R.id.btn3,
                R.id.btn4, R.id.btn5, R.id.btn6, R.id.btn7,
                R.id.btn8, R.id.btn9
        };

        View.OnClickListener numberClickListener = v -> {
            if (stateError) {
                display.setText("0");
                stateError = false;
            }
            Button button = (Button) v;
            currentInput += button.getText().toString();
            display.setText(currentInput);
            lastNumeric = true;
        };

        for (int id : numberIds) {
            findViewById(id).setOnClickListener(numberClickListener);
        }

        findViewById(R.id.btnDot).setOnClickListener(v -> {
            if (lastNumeric && !stateError && !lastDot) {
                currentInput += ".";
                display.setText(currentInput);
                lastNumeric = false;
                lastDot = true;
            }
        });

        findViewById(R.id.btnAdd).setOnClickListener(v -> onOperatorClick("+"));
        findViewById(R.id.btnSubtract).setOnClickListener(v -> onOperatorClick("-"));
        findViewById(R.id.btnMultiply).setOnClickListener(v -> onOperatorClick("*"));
        findViewById(R.id.btnDivide).setOnClickListener(v -> onOperatorClick("/"));

        findViewById(R.id.btnEquals).setOnClickListener(v -> onEqualClick());

        findViewById(R.id.btnClear).setOnClickListener(v -> {
            display.setText("0");
            currentInput = "";
            lastNumeric = false;
            stateError = false;
            lastDot = false;
        });

        findViewById(R.id.btnDelete).setOnClickListener(v -> {
            if (!stateError && currentInput.length() > 0) {
                currentInput = currentInput.substring(0, currentInput.length() - 1);
                display.setText(currentInput.isEmpty() ? "0" : currentInput);
            }
        });
    }

    private void onOperatorClick(String operator) {
        if (lastNumeric && !stateError) {
            currentInput += operator;
            display.setText(currentInput);
            lastNumeric = false;
            lastDot = false;
        }
    }

    private void onEqualClick() {
        if (lastNumeric && !stateError) {
            try {
                double result = evaluateExpression(currentInput);
                display.setText(String.valueOf(result));
                currentInput = String.valueOf(result);
                lastDot = true;
            } catch (Exception e) {
                display.setText("Error");
                stateError = true;
                currentInput = "";
            }
        }
    }

    private double evaluateExpression(String expression) {
        // Stack for numbers
        Stack<Double> numbers = new Stack<>();
        // Stack for Operators
        Stack<Character> operators = new Stack<>();
        for (int i = 0; i < expression.length(); i++) {
            char c = expression.charAt(i);

            // If the character is a digit, push it to stack for numbers
            if (Character.isDigit(c) || c == '.') {
                StringBuilder sb = new StringBuilder();
                // There may be more than one digits in a number
                while (i < expression.length() && (Character.isDigit(expression.charAt(i)) || expression.charAt(i) == '.')) {
                    sb.append(expression.charAt(i++));
                }
                i--;
                numbers.push(Double.parseDouble(sb.toString()));
            } else if (c == '(') {
                operators.push(c);
            } else if (c == ')') {
                while (operators.peek() != '(') {
                    numbers.push(applyOperation(operators.pop(), numbers.pop(), numbers.pop()));
                }
                operators.pop();
            } else if (c == '+' || c == '-' || c == '*' || c == '/') {
                while (!operators.isEmpty() && hasPrecedence(c, operators.peek())) {
                    numbers.push(applyOperation(operators.pop(), numbers.pop(), numbers.pop()));
                }
                operators.push(c);
            }
        }

        while (!operators.isEmpty()) {
            numbers.push(applyOperation(operators.pop(), numbers.pop(), numbers.pop()));
        }

        return numbers.pop();
    }

    private boolean hasPrecedence(char op1, char op2) {
        if (op2 == '(' || op2 == ')') {
            return false;
        }
        if ((op1 == '*' || op1 == '/') && (op2 == '+' || op2 == '-')) {
            return false;
        }
        return true;
    }

    private double applyOperation(char op, double b, double a) {
        switch (op) {
            case '+':
                return a + b;
            case '-':
                return a - b;
            case '*':
                return a * b;
            case '/':
                if (b == 0) {
                    throw new UnsupportedOperationException("Cannot divide by zero");
                }
                return a / b;
        }
        return 0;
    }
}
