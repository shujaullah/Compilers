// Copyright 2012- Bill Campbell, Swami Iyer and Bahar Akbal-Delibas

package jminusminus;

import static jminusminus.CLConstants.*;

/**
 * The AST node for a do-statement.
 */
public class JDoStatement extends JStatement {
    // Body.
    private JStatement body;

    // Test expression.
    private JExpression condition;
    public boolean hasBreak;
    public String breakLabel;
    public boolean hasContinue;
    public String continueLabel;


    /**
     * Constructs an AST node for a do-statement.
     *
     * @param line      line in which the do-statement occurs in the source file.
     * @param body      the body.
     * @param condition test expression.
     */
    public JDoStatement(int line, JStatement body, JExpression condition) {
        super(line);
        this.body = body;
        this.condition = condition;
    }

    /**
     * {@inheritDoc}
     */
    public JStatement analyze(Context context) {
        // analyzing the condition and making sure its type is boolean.
        JMember.enclosingStatement.push(this);
        condition = condition.analyze(context);
        condition.type().mustMatchExpected(line(), Type.BOOLEAN);
        //analyzing the body.
         body.analyze(context);
         JMember.enclosingStatement.pop();

        return this;
    }

    /**
     * {@inheritDoc}
     */
    public void codegen(CLEmitter output) {
        // Implementing codegen.
        // creating labels
        // usiing JWHile as an example for it.
        //JMember.enclosingStatement.push(this);
        String main_part = output.createLabel();
        String last_part = output.createLabel();
         breakLabel = output.createLabel();
        continueLabel = output.createLabel();

        // code that will riun for while
        output.addLabel(main_part);
        body.codegen(output);
//        if(hasBreak == true){
//            output.addLabel(breakLabel);
//            output.addBranchInstruction(GOTO, last_part);
//        }
        if(hasContinue == true)
        {
            output.addLabel(continueLabel);
            output.addBranchInstruction(GOTO, main_part);
        }

        // jumping out of the loop body
        //output.addLabel(initial_part);
        // runnih the body while condition is true.
        //if(condition != null) {
            condition.codegen(output, main_part, true);
        //}
        output.addLabel(breakLabel);
        //running label when get outside the loop.
        output.addLabel(last_part);
        //JMember.enclosingStatement.pop();

        //


    }

    /**
     * {@inheritDoc}
     */
    public void toJSON(JSONElement json) {
        JSONElement e = new JSONElement();
        json.addChild("JDoStatement:" + line, e);
        JSONElement e1 = new JSONElement();
        e.addChild("Body", e1);
        body.toJSON(e1);
        JSONElement e2 = new JSONElement();
        e.addChild("Condition", e2);
        condition.toJSON(e2);
    }
}
