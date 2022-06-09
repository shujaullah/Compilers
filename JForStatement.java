// Copyright 2012- Bill Campbell, Swami Iyer and Bahar Akbal-Delibas

package jminusminus;

import java.util.ArrayList;

import static jminusminus.CLConstants.*;

/**
 * The AST node for a for-statement.
 */
class JForStatement extends JStatement {
    // Initialization.
    private ArrayList<JStatement> init;

    // Test expression
    private JExpression condition;

    // Update.
    private ArrayList<JStatement> update;

    // The body.
    private JStatement body;
    // declaring two instance variable for the break statement;
    public boolean hasBreak = false;
    public String breakLabel;

    // Declaring instance variable for continue.
    public boolean hasContinue ;
    public String continueLabel;


    /**
     * Constructs an AST node for a for-statement.
     *
     * @param line      line in which the for-statement occurs in the source file.
     * @param init      the initialization.
     * @param condition the test expression.
     * @param update    the update.
     * @param body      the body.
     */
    public JForStatement(int line, ArrayList<JStatement> init, JExpression condition,
                         ArrayList<JStatement> update, JStatement body) {
        super(line);
        this.init = init;
        this.condition = condition;
        this.update = update;
        this.body = body;
    }

    /**
     * {@inheritDoc}
     */
    public JForStatement analyze(Context context) {
        // TODO
        //creating a new localContext with context as parent
        JMember.enclosingStatement.push(this);

        LocalContext localContext = new LocalContext(context);

        // Analyzing the init in the new context
        if (init != null) {
            for (JStatement i : init) {
                i.analyze(localContext);

            }
        }
        if (condition != null) {
        condition.analyze(localContext);
        condition.type().mustMatchExpected(line(), Type.BOOLEAN); }
      if(update != null) {
          for (JStatement i : update) {

              i.analyze(localContext);

          }
      }
      if (body != null) {
          body.analyze(localContext);
      }
        JMember.enclosingStatement.pop();

        return this;
    }

    /**
     * {@inheritDoc}
     */
    public void codegen(CLEmitter output) {
        // using the codegen code from the while loop.
        String test = output.createLabel();
        String out = output.createLabel();
         breakLabel = output.createLabel();
         continueLabel = output.createLabel();
        if (init != null) {
            for (int i = 0; i < init.toArray().length; i++) {
                init.get(i).codegen(output);
            }
        }


        output.addLabel(test);
        if(condition != null)
            condition.codegen(output, out, false);
        output.addLabel(continueLabel);

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

        //output.addLabel(output.createLabel());
        if(update != null){
            for (int i = 0; i < update.toArray().length; i++) {
                update.get(i).codegen(output);
            }
        }
        output.addBranchInstruction(GOTO, test);
        output.addLabel(breakLabel);

        output.addLabel(out);


    }

    /**
     * {@inheritDoc}
     */
    public void toJSON(JSONElement json) {
        JSONElement e = new JSONElement();
        json.addChild("JForStatement:" + line, e);
        if (init != null) {
            JSONElement e1 = new JSONElement();
            e.addChild("Init", e1);
            for (JStatement stmt : init) {
                stmt.toJSON(e1);
            }
        }
        if (condition != null) {
            JSONElement e1 = new JSONElement();
            e.addChild("Condition", e1);
            condition.toJSON(e1);
        }
        if (update != null) {
            JSONElement e1 = new JSONElement();
            e.addChild("Update", e1);
            for (JStatement stmt : update) {
                stmt.toJSON(e1);
            }
        }
        if (body != null) {
            JSONElement e1 = new JSONElement();
            e.addChild("Body", e1);
            body.toJSON(e1);
        }
    }
}
