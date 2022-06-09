// Copyright 2012- Bill Campbell, Swami Iyer and Bahar Akbal-Delibas

package jminusminus;

import static jminusminus.CLConstants.*;

/**
 * The AST node for a while-statement.
 */
class JWhileStatement extends JStatement {
    // Test expression.
    private JExpression condition;

    // Body.
    private JStatement body;
    public boolean hasBreak = false;
    public String breakLabel;

    // Declaring instance variable for continue.
    public boolean hasContinue;
    public String continueLabel;


    /**
     * Constructs an AST node for a while-statement.
     *
     * @param line      line in which the while-statement occurs in the source file.
     * @param condition test expression.
     * @param body      the body.
     */
    public JWhileStatement(int line, JExpression condition, JStatement body) {
        super(line);
        this.condition = condition;
        this.body = body;
    }

    /**
     * {@inheritDoc}
     */
    public JWhileStatement analyze(Context context) {
        JMember.enclosingStatement.push(this);
        condition = condition.analyze(context);
        condition.type().mustMatchExpected(line(), Type.BOOLEAN);
        body = (JStatement) body.analyze(context);
        JMember.enclosingStatement.pop();
        return this;
    }

    /**
     * {@inheritDoc}
     */
    public void codegen(CLEmitter output) {

        String test = output.createLabel();
        String out = output.createLabel();
         breakLabel = output.createLabel();
         continueLabel = output.createLabel();

        output.addLabel(test);
        //if(condition != null) {
            condition.codegen(output, out, false);
        output.addLabel(continueLabel);
       // }
        body.codegen(output);
        output.addLabel(continueLabel);
//        if(hasBreak == true){
//            output.addLabel(breakLabel);
//            output.addBranchInstruction(GOTO, out);
//        }
//        if(hasContinue == true)
//        {
//            output.addLabel(continueLabel);
//            output.addBranchInstruction(GOTO, test);
//        }
        output.addBranchInstruction(GOTO, test);
        output.addLabel(breakLabel);
        output.addLabel(out);

    }

    /**
     * {@inheritDoc}
     */
    public void toJSON(JSONElement json) {
        JSONElement e = new JSONElement();
        json.addChild("JWhileStatement:" + line, e);
        JSONElement e1 = new JSONElement();
        e.addChild("Condition", e1);
        condition.toJSON(e1);
        JSONElement e2 = new JSONElement();
        e.addChild("Body", e2);
        body.toJSON(e2);
    }
}
