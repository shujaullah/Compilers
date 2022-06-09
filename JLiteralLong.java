// Copyright 2012- Bill Campbell, Swami Iyer and Bahar Akbal-Delibas

package jminusminus;

import static jminusminus.CLConstants.*;

/**
 * The AST node for a long literal.
 */
class JLiteralLong extends JExpression {
    // String representation of the literal.
    private String text;

    /**
     * Constructs an AST node for a long literal given its line number and string representation.
     *
     * @param line line in which the literal occurs in the source file.
     * @param text string representation of the literal.
     */
    public JLiteralLong(int line, String text) {
        super(line);
        this.text = text;
    }

    /**
     * {@inheritDoc}
     */
    public JExpression analyze(Context context) {

        type = Type.LONG;
        return this;
    }

    /**
     * {@inheritDoc}
     */
    public void codegen(CLEmitter output) {
        // TODO
      //  long l = Long.parseLong(text);

       // String l = text;
       int size = text.length();
       long l = Long.parseLong((text.substring(0, size -1)));
        //System.err.println("value of L :" + l);

       // String l = text;
        if(l == 0L){
                output.addNoArgInstruction(LCONST_0);}
        else if (l == 1L)
        {output.addNoArgInstruction(LCONST_1);
           // System.err.println("its pushing 1");
        }
        else {
            output.addLDCInstruction(l); }

        }


    /**
     * {@inheritDoc}
     */
    public void toJSON(JSONElement json) {
        JSONElement e = new JSONElement();
        json.addChild("JLiteralLong:" + line, e);
        e.addAttribute("type", type == null ? "" : type.toString());
        e.addAttribute("value", text);
    }
}
