// Copyright 2012- Bill Campbell, Swami Iyer and Bahar Akbal-Delibas

package jminusminus;

import java.util.ArrayList;
import java.util.TreeMap;
import static jminusminus.CLConstants.*;

/**
 * The AST node for a switch-statement.
 */
public class JSwitchStatement extends JStatement {
    // Test expression.
    private JExpression condition;

    // List of switch-statement groups.
    private ArrayList<SwitchStatementGroup> stmtGroup;
    public boolean hasBreak = false;
    public String breakLabel;

    // Arraylist for the TABLESWITCH
    private ArrayList<String> labels = new ArrayList<String>();
    //TREE MAP for the LOOKUPSWITCH.
    private TreeMap<Integer, String> matchLabelPairs = new TreeMap<Integer, String>();

    public int hi = Integer.MAX_VALUE;
    public int lo = Integer.MIN_VALUE;



    /**
     * Constructs an AST node for a switch-statement.
     *
     * @param line      line in which the switch-statement occurs in the source file.
     * @param condition test expression.
     * @param stmtGroup list of statement groups.
     */
    public JSwitchStatement(int line, JExpression condition,
                            ArrayList<SwitchStatementGroup> stmtGroup) {
        super(line);
        this.condition = condition;
        this.stmtGroup = stmtGroup;
    }

    /**
     * {@inheritDoc}
     */
    public JStatement analyze(Context context) {
        // TODO
        JMember.enclosingStatement.push(this);
       condition = (JExpression) condition.analyze(context);
       condition.type().mustMatchExpected(line(), Type.INT);
       LocalContext localContext = new LocalContext(context);
       for( SwitchStatementGroup s :  stmtGroup)
        {
           s.analyze(localContext);
            // determinng the value of hi and lo
            int check = 0;
            if(s.countNum != null){
                if(check<hi) {
                    if(s.countNum.size() != 0)
                        hi = s.countNum.get(0);
                } }

            if(check > lo){
                lo = s.countNum.get(s.countNum.size()-1); }
        }
        JMember.enclosingStatement.pop();
        return this;
    }

    /**
     * {@inheritDoc}
     */
    public void codegen(CLEmitter output) {
        condition. codegen(output);
        String default_l = output.createLabel();
        String out = output.createLabel();
        breakLabel = output.createLabel();

        for( SwitchStatementGroup s :  stmtGroup)
        {

            // storing the labels into the map and arraylist for the LOOKUPSWITCH and TABLESWITCH.
            for(JExpression label : s.switchLabels)
            {
                if(label != null)
                {
                    String caseLabel = output.createLabel();
                    int key_Table = ((JLiteralInt) label).toInt();
                    labels.add(caseLabel);
                    matchLabelPairs.put(key_Table, caseLabel);
                }
            }


        }
//        System.err.println("lablel list for Table switch" + labels);
//        System.err.println("pairs for lookup switch" + matchLabelPairs);
//
//        System.err.println("hi = " + hi + " lo = " + lo);

        // code gieven by the professor in the project 5 writeup.
        int nlabels = matchLabelPairs.size();
        long tableSpaceCost = 5 + hi - lo;
        long tableTimeCost = 3;
        long lookUpSpaceCost = 3 + 2 * nlabels;
        long lookUpTimeCost = nlabels;
        int opcode = nlabels > 0 && (tableSpaceCost + 3 * tableTimeCost <= lookUpSpaceCost + 3 * lookUpTimeCost)? TABLESWITCH : LOOKUPSWITCH;

        if (opcode == TABLESWITCH)
            output.addTABLESWITCHInstruction(default_l, lo, hi, labels);
        else
            output.addLOOKUPSWITCHInstruction(default_l, nlabels, matchLabelPairs);


        // code bellow will place the lables to the appropriate place and call them.
        int counter =0;

        for (SwitchStatementGroup s : stmtGroup)
        {
            // adding label for the case by looping over the switchlabels arrays.

            for(JExpression label : s.switchLabels)
            {
                if(label !=null)
                {
                    output.addLabel(labels.get(counter));

                    counter++;
                }

            }

                output.addLabel(default_l);
//            }
            // codegen for the block statment for each case
            // checking for the break statement inoreder to apply the break.
            for (JStatement block : s.block)
            {
                if(block instanceof JBreakStatement){
                    output.addLabel(breakLabel);
                    output.addBranchInstruction(GOTO, out);
                }

                else
                    block.codegen(output);
            }
        }


       // output.addLabel(breakLabel);
        output.addLabel(out);

    }

    /**
     * {@inheritDoc}
     */
    public void toJSON(JSONElement json) {
        JSONElement e = new JSONElement();
        json.addChild("JSwitchStatement:" + line, e);
        JSONElement e1 = new JSONElement();
        e.addChild("Condition", e1);
        condition.toJSON(e1);
        for (SwitchStatementGroup group : stmtGroup) {
            group.toJSON(e);
        }
    }
}

/**
 * A switch statement group consists of case labels and a block of statements.
 */
class SwitchStatementGroup {
    // Case labels.
    public ArrayList<JExpression> switchLabels;
    public ArrayList<Integer> countNum;

    // Block of statements.
    public ArrayList<JStatement> block;

    public int hi;
    public int lo;
    /**
     * Constructs a switch-statement group.
     *
     * @param switchLabels case labels.
     * @param block        block of statements.
     */
    public SwitchStatementGroup(ArrayList<JExpression> switchLabels, ArrayList<JStatement> block) {
        this.switchLabels = switchLabels;
        this.block = block;
        this.countNum = new ArrayList<Integer>();
    }
     // james help me in this to generate the arraylist of integer to determine the hi and lo from it in each switch statement.
    public SwitchStatementGroup analyze(Context context) {
        for (int i = 0; i < switchLabels.size(); i++)
        {
            JExpression checkingLabel = switchLabels.get(i);
            if(checkingLabel != null){

                checkingLabel = checkingLabel.analyze(context);
                if (checkingLabel instanceof JLiteralInt)
                {
                    countNum.add(((JLiteralInt) checkingLabel).toInt());
                }
                else
                {
                    // report sematic error.
                    JAST.compilationUnit.reportSemanticError(checkingLabel.line(), "Ileagal switch statement.");
                }
            }
        }
        for (int i = 0; i < block.size(); i ++)
        {
            block.set(i, (JStatement) block.get(i).analyze(context));
        }
//        hi = countNum.get(countNum.size() -1);
//        lo = countNum.get(0);

        return this;
    }


    /**
     * Stores information about this switch statement group in JSON format.
     *
     * @param json the JSON emitter.
     */
    public void toJSON(JSONElement json) {
        JSONElement e = new JSONElement();
        json.addChild("SwitchStatementGroup", e);
        for (JExpression label : switchLabels) {
            JSONElement e1 = new JSONElement();
            if (label != null) {
                e.addChild("Case", e1);
                label.toJSON(e1);
            } else {
                e.addChild("Default", e1);
            }
        }
        if (block != null) {
            for (JStatement stmt : block) {
                stmt.toJSON(e);
            }
        }
    }
}
