// Copyright 2012- Bill Campbell, Swami Iyer and Bahar Akbal-Delibas

package jminusminus;

import static jminusminus.CLConstants.*;

/**
 * The AST node for a conditional expression.
 */
class JConditionalExpression extends JExpression {
    // Test expression.
    private JExpression condition;

    // Then part.
    private JExpression thenPart;

    // Else part.
    private JExpression elsePart;

    /**
     * Constructs an AST node for a conditional expression.
     *
     * @param line      line in which the conditional expression occurs in the source file.
     * @param condition test expression.
     * @param thenPart  then part.
     * @param elsePart  else part.
     */
    public JConditionalExpression(int line, JExpression condition, JExpression thenPart,
                                  JExpression elsePart) {
        super(line);
        this.condition = condition;
        this.thenPart = thenPart;
        this.elsePart = elsePart;
    }

    /**
     * {@inheritDoc}
     */
    public JExpression analyze(Context context) {
        // TODO
        // Analyzing condition and making sure its boolean.

        condition = (JExpression) condition.analyze(context);
        condition.type().mustMatchExpected(line(), Type.BOOLEAN);
        //analyzing the consequnest and alternate and making sure they have same type.
        thenPart = (JExpression) thenPart.analyze(context);
        elsePart = (JExpression) elsePart.analyze(context);
        // Setting up the type of expression to that the consequnet (or alternaate).
        thenPart.type().mustMatchExpected(line(), elsePart.type());
        type = thenPart.type();
        return this;
    }

    /**
     * {@inheritDoc}
     */
    public void codegen(CLEmitter output) {
        // TODO
        // creaating the label for the then and else part
        String thenLabel = output.createLabel();
        String elseLabel = output.createLabel();

        condition.codegen(output, thenLabel, false);

        thenPart.codegen(output);
        output.addBranchInstruction(GOTO, elseLabel);
        output.addLabel(thenLabel);
        elsePart.codegen(output);
        output.addLabel(elseLabel);
    }

    /**
     * {@inheritDoc}
     */
    public void toJSON(JSONElement json) {
        JSONElement e = new JSONElement();
        json.addChild("JConditionalExpression:" + line, e);
        JSONElement e1 = new JSONElement();
        e.addChild("Condition", e1);
        condition.toJSON(e1);
        JSONElement e2 = new JSONElement();
        e.addChild("ThenPart", e2);
        thenPart.toJSON(e2);
        JSONElement e3 = new JSONElement();
        e.addChild("ElsePart", e3);
        elsePart.toJSON(e3);
    }
}
