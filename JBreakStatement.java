// Copyright 2012- Bill Campbell, Swami Iyer and Bahar Akbal-Delibas

package jminusminus;

import static jminusminus.CLConstants.*;

/**
 * An AST node for a break-statement.
 */
public class JBreakStatement extends JStatement {
    private JStatement enclosingStatement;
    //private String breakLabel;

    /**
     * Constructs an AST node for a break-statement.
     *
     * @param line line in which the break-statement occurs in the source file.
     */
    public JBreakStatement(int line) {
        super(line);
    }

    /**
     * {@inheritDoc}
     */
    public JStatement analyze(Context context) {
        enclosingStatement = JMember.enclosingStatement.peek();
        if(enclosingStatement instanceof JForStatement) {
            ((JForStatement) enclosingStatement).hasBreak =  true;
        }
        else if (enclosingStatement instanceof JDoStatement) {
            ((JDoStatement) enclosingStatement).hasBreak =  true;
        }
        else if (enclosingStatement instanceof JWhileStatement) {
            ((JWhileStatement) enclosingStatement).hasBreak =  true;
        }
        return this;
    }


    /**
     * {@inheritDoc}
     */
    public void codegen(CLEmitter output) {
        // TODO
        String breakLabel;
        if(enclosingStatement instanceof JForStatement) {
            breakLabel = ((JForStatement) enclosingStatement).breakLabel;
            output.addBranchInstruction(GOTO, breakLabel);
        }
        else if (enclosingStatement instanceof JDoStatement) {
            breakLabel = ((JDoStatement) enclosingStatement).breakLabel;
            output.addBranchInstruction(GOTO, breakLabel);

        }
        else if (enclosingStatement instanceof JWhileStatement) {
            breakLabel = ((JWhileStatement) enclosingStatement).breakLabel;
            output.addBranchInstruction(GOTO, breakLabel);
        }




    }

    /**
     * {@inheritDoc}
     */
    public void toJSON(JSONElement json) {
        JSONElement e = new JSONElement();
        json.addChild("JBreakStatement:" + line, e);
    }
}
