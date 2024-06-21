import java.math.*;
import java.util.*;
import java.lang.*;
import java.io.*;

public class Calculator {

    public static double calculate(String input) {
        if (!isValidInput(input)) {
            System.out.println("Ошибка: Некорректный ввод");
            return Double.NaN;
        }

        try {
            return evaluateExpression(input);
        } catch (ArithmeticException e) {
            System.out.println("Ошибка: " + e.getMessage());
            return Double.NaN;
        }
    }

    private static boolean isValidInput(String input) {
        return input.matches("[0-9+\\-*/(). ]+");
    }

    private static double evaluateExpression(String input) {
        Stack<Double> numbers = new Stack<>();
        Stack<Character> operators = new Stack<>();

        for (int i = 0; i < input.length(); i++) {
            char ch = input.charAt(i);

            if (ch == '(') {
                operators.push(ch);
            } else if ((ch == '+' || ch == '-') && (i == 0 || input.charAt(i - 1) == '(')) {
                // Обработка случая, когда оператор стоит перед скобкой или сразу после неё
                StringBuilder num = new StringBuilder(String.valueOf(ch));
                i++;
                while (i < input.length() && (Character.isDigit(input.charAt(i)) || input.charAt(i) == '.')) {
                    num.append(input.charAt(i));
                    i++;
                }
                i--;
                numbers.push(Double.parseDouble(num.toString()));
            } else if (Character.isDigit(ch)) {
                StringBuilder num = new StringBuilder();
                while (i < input.length() && (Character.isDigit(input.charAt(i)) || input.charAt(i) == '.')) {
                    num.append(input.charAt(i));
                    i++;
                }
                i--;
                numbers.push(Double.parseDouble(num.toString()));
            } else if (ch == ')') {
                while (!operators.isEmpty() && operators.peek()!= '(') {
                    numbers.push(applyOperator(operators.pop(), numbers.pop(), numbers.pop()));
                }
                operators.pop();
            } else if (ch == '+' || ch == '*' || ch == '/') {
                while (!operators.isEmpty() && precedence(ch) <= precedence(operators.peek())) {
                    numbers.push(applyOperator(operators.pop(), numbers.pop(), numbers.pop()));
                }
                operators.push(ch);
            } else if (ch == '-' && (i == 0 || input.charAt(i - 1) == '(')) {
                // Обработка унарного минуса
                if (i + 1 < input.length() && Character.isDigit(input.charAt(i + 1))) {
                    // Минус является частью отрицательного числа
                    StringBuilder num = new StringBuilder("-");
                    i++;
                    while (i < input.length() && (Character.isDigit(input.charAt(i)) || input.charAt(i) == '.')) {
                        num.append(input.charAt(i));
                        i++;
                    }
                    i--;
                    numbers.push(Double.parseDouble(num.toString()));
                } else {
                    // Минус не является частью отрицательного числа, обрабатываем как ошибку
                    System.out.println("Ошибка: Некорректный ввод");
                    return Double.NaN;
                }
            } else {
                operators.push(ch);
            }
        }

        while (!operators.isEmpty()) {
            numbers.push(applyOperator(operators.pop(), numbers.pop(), numbers.pop()));
        }

        return numbers.pop();
    }

    private static double applyOperator(char operator, double b, double a) {
        switch (operator) {
            case '+':
                return a + b;
            case '-':
                return a - b;
            case '*':
                return a * b;
            case '/':
                if (b == 0) {
                    throw new ArithmeticException("Деление на 0");
                }
                return a / b;
        }
        return 0;
    }

    private static int precedence(char operator) {
        if (operator == '+' || operator == '-') {
            return 1;
        } else if (operator == '*' || operator == '/') {
            return 2;
        } else {
            return 0;
        }
    }

    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);
        double result;
        System.out.println("Введите арифметическое выражение или x для выхода:");
        String expression = sc.nextLine().trim();

        while ((!expression.equals("x")) && (!expression.equals("X"))) {
            expression = expression.replaceAll("\\s", "").replaceAll("=*$", "").replaceAll(",", ".").replaceAll("^(?!.*[+-]{2})[+-]?0*(\\.0*)?([+*/-])", "$2");
            System.out.println(expression);
            try {
                System.out.println(calculate(expression));
            } catch (Exception e) {
                System.err.println(e.getMessage());
            }
            expression = sc.nextLine().trim();
        }

        sc.close();
    }
}