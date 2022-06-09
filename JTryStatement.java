// Copyright 2012- Bill Campbell, Swami Iyer and Bahar Akbal-Delibas

package jminusminus;

import java.util.ArrayList;

import static jminusminus.CLConstants.*;

/**
 * The AST node for a try-catch-finally statement.
 */
class JTryStatement extends JStatement {
    // The try block.
    private JBlock tryBlock;

    // The catch parameters.
    private ArrayList<JFormalParameter> parameters;

    // The catch blocks.
    private ArrayList<JBlock> catchBlocks;

    // The finally block.
    private JBlock finallyBlock;
    public boolean hasFinal;

    private  ArrayList<JVariable> keppVarr = new ArrayList<>();
    private JBlock catchBlock;
    LocalContext localContext1;

    /**
     * Constructs an AST node for a try-statement.
     *
     * @param line         line in which the while-statement occurs in the source file.
     * @param tryBlock     the try block.
     * @param parameters   the catch parameters.
     * @param catchBlocks  the catch blocks.
     * @param finallyBlock the finally block.
     */
    public JTryStatement(int line, JBlock tryBlock, ArrayList<JFormalParameter> parameters,
                         ArrayList<JBlock> catchBlocks, JBlock finallyBlock) {
        super(line);
        this.tryBlock = tryBlock;
        this.parameters = parameters;
        this.catchBlocks = catchBlocks;
        this.finallyBlock = finallyBlock;
    }

    /**
     * {@inheritDoc}
     */
    public JTryStatement analyze(Context context) {

        tryBlock = tryBlock.analyze(context);
        JFormalParameter p;
        // James help in the analyze here because some of the instructions were missing so he droopeed
        // hints and I  made it work.
       for(int i = 0; i < parameters.size(); i++)
       {
           // making a new local context.
           LocalContext localContext = new LocalContext(context);
           p = parameters.get(i);
           JVariable v1 = new JVariable(p.line(), p.name());
           p.setType(p.type().resolve(localContext));
           LocalVariableDefn defn = new LocalVariableDefn(p.type(), localContext.nextOffset());
           defn.initialize();
           localContext.addEntry(p.line(), p.name(),defn);
           keppVarr.add((JVariable) v1.analyze(localContext));
           catchBlocks.set(i, catchBlocks.get(i).analyze(localContext));

       }
       // checking for the finally block and analyzing it with a new local parent context.
//       if (finallyBlock != null)
//       {
           localContext1 = new LocalContext(context);
           finallyBlock = finallyBlock.analyze(localContext1);

//      // }



        return this;
    }

    /**
     * {@inheritDoc}
     */
    public void codegen(CLEmitter output) {
        // creating all the labels Neeed for the codegen.
        String startFinally = output.createLabel();
        String endFinally = output.createLabel();
        String startTry = output.createLabel();
        String endTry = output.createLabel();
        String startCatch= "";
        String startFinallyPlusOne = output.createLabel();
        String endCatch = output.createLabel();

        output.addLabel(startTry);
        tryBlock.codegen(output);

            finallyBlock.codegen(output);


        output.addBranchInstruction(GOTO, endFinally);
        output.addLabel(endTry);

        for (int i = 0; i < catchBlocks.size(); i++) {
             startCatch = output.createLabel();
            output.addLabel(startCatch);

            JVariable catchVar = keppVarr.get(i);
            JBlock catchBlock = catchBlocks.get(i);
            catchVar.codegenStore(output);
            catchBlock.codegen(output);
            output.addLabel(endCatch);
            String catrchStr= catchVar.type().jvmName();


            output.addExceptionHandler(startTry,endCatch, startCatch, catrchStr);


                finallyBlock.codegen(output);

            output.addBranchInstruction(GOTO, endFinally);
            output.addLabel(endFinally);
        }

            output.addLabel(startFinally);
           int offset =  localContext1.nextOffset();
            output.addOneArgInstruction(ASTORE, offset);

            output.addLabel(startFinallyPlusOne);

            finallyBlock.codegen(output);

            output.addOneArgInstruction(ALOAD, offset);
            output.addNoArgInstruction(ATHROW);
            output.addLabel(endFinally);

            output.addExceptionHandler(startTry, endTry, startFinally, null);

            for(JBlock i : catchBlocks)
            {
                output.addExceptionHandler(startCatch,endCatch, startFinally, null);
            }
            output.addExceptionHandler(startFinally, startFinallyPlusOne, startFinally, null);
        }


    /**
     * {@inheritDoc}
     */
    public void toJSON(JSONElement json) {
        JSONElement e = new JSONElement();
        json.addChild("JTryStatement:" + line, e);
        JSONElement e1 = new JSONElement();
        e.addChild("TryBlock", e1);
        tryBlock.toJSON(e1);
        if (catchBlocks != null) {
            for (int i = 0; i < catchBlocks.size(); i++) {
                JFormalParameter param = parameters.get(i);
                JBlock catchBlock = catchBlocks.get(i);
                JSONElement e2 = new JSONElement();
                e.addChild("CatchBlock", e2);
                String s = String.format("[\"%s\", \"%s\"]", param.name(), param.type() == null ?
                        "" : param.type().toString());
                e2.addAttribute("parameter", s);
                catchBlock.toJSON(e2);
            }
        }
        if (finallyBlock != null) {
            JSONElement e2 = new JSONElement();
            e.addChild("FinallyBlock", e2);
            finallyBlock.toJSON(e2);
        }
    }
}
