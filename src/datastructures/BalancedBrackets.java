// Checks whether a bracket sequence is balanced using a stack-based scan.
// This file contains the core logic for this part of the program and keeps the UI
// focused on the algorithm being explained instead of mixing presentation with logic.

// this tells the compiler the package the file belongs to.
package datastructures;

// this is the class that checks whether a bracket sequence is balanced using a stack-based scan.
public class BalancedBrackets {

    //method that checks whether a bracket sequence is balanced using a stack-based scan.
    public OperationReport check(String expression) {

        OperationReport report = new OperationReport();

        char[] stack = new char[expression.length()];
        int top = -1;

        for (int i = 0; i < expression.length(); i++) {

            char current = expression.charAt(i);

            if (current == '(' || current == '{'
                    || current == '[') {

                top++;
                stack[top] = current;

                report.addStep(
                        "Opening bracket '" + current
                        + "' pushed onto stack."
                );

            } else if (current == ')' || current == '}'
                    || current == ']') {

                if (top == -1) {

                    report.addStep(
                            "Closing bracket '" + current
                            + "' has no matching opening bracket."
                    );

                    report.finish();
                    return report;
                }

                char opening = stack[top];

                boolean matches =
                        (opening == '(' && current == ')')
                        || (opening == '{' && current == '}')
                        || (opening == '[' && current == ']');

                if (!matches) {

                    report.addStep(
                            "Mismatched brackets: "
                            + opening + " and " + current
                    );

                    report.finish();
                    return report;
                }

                top--;

                report.addStep(
                        "Matching pair found: "
                        + opening + current
                        + ". Opening bracket removed."
                );
            }
        }

        if (top == -1) {

            report.addStep("SUCCESS: All brackets are balanced.");

        } else {

            report.addStep("FAILED: Unmatched opening brackets remain.");
        }

        report.finish();
        return report;
    }
}
