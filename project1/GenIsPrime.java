import java.util.ArrayList;

import jminusminus.CLEmitter;

import static jminusminus.CLConstants.*;

/**
 * This class programmatically generates the class file for the following Java application:
 * 
 * <pre>
 * public class IsPrime {
 *     // Entry point.
 *     public static void main(String[] args) {
 *         int n = Integer.parseInt(args[0]);
 *         boolean result = isPrime(n);
 *         if (result) {
 *             System.out.println(n + " is a prime number");
 *         } else {
 *             System.out.println(n + " is not a prime number");
 *         }
 *     }
 *
 *     // Returns true if n is prime, and false otherwise.
 *     private static boolean isPrime(int n) {
 *         if (n < 2) {
 *             return false;
 *         }
 *         for (int i = 2; i <= n / i; i++) {
 *             if (n % i == 0) {
 *                 return false;
 *             }
 *         }
 *         return true;
 *     }
 * }
 * </pre>
 */
public class GenIsPrime {
    public static void main(String[] args) {
        CLEmitter e = new CLEmitter(true);
        ArrayList<String> modifiers = new ArrayList<String>();

        // public class Isprime

        modifiers.add("public");
        e.addClass(modifiers, "IsPrime", "java/lang/Object", null, true);
        //public static void main(Strings [] args) {
        modifiers.clear();
        modifiers.add("public");
        modifiers.add("static");
        e.addMethod(modifiers, "main", "([Ljava/lang/String;)V", null, true);

        // int n = Integer.parseInt(args[0]);

        e.addNoArgInstruction(ALOAD_0 );
        e.addNoArgInstruction(ICONST_0 );
        e.addNoArgInstruction(AALOAD );
        e.addMemberAccessInstruction(INVOKESTATIC , "java/lang/Integer", "parseInt",
                "(Ljava/lang/String;)I");
        e.addNoArgInstruction(ISTORE_1 );

       // boolean result = isPrime(n);
        e.addNoArgInstruction(ILOAD_1);
        e.addMemberAccessInstruction(INVOKESTATIC, "IsPrime", "isPrime", "(I)Z");
        e.addNoArgInstruction(ISTORE_2);

        // if (result) {
        e.addNoArgInstruction(ILOAD_2);
        e.addNoArgInstruction(ICONST_0 );
        e.addBranchInstruction(IF_ICMPEQ, "p1");

        // Get System.out on stack
        //
        e.addMemberAccessInstruction(GETSTATIC, "java/lang/System", "out", "Ljava/io/PrintStream;");

        // Create an intance (say sb) of StringBuffer on stack for string concatenations
        //    sb = new StringBuffer();
        e.addReferenceInstruction(NEW, "java/lang/StringBuffer");
        e.addNoArgInstruction(DUP);
        e.addMemberAccessInstruction(INVOKESPECIAL, "java/lang/StringBuffer", "<init>", "()V");

        // sb.append(n);
        e.addNoArgInstruction(ILOAD_1);
        e.addMemberAccessInstruction(INVOKEVIRTUAL, "java/lang/StringBuffer", "append",
                "(I)Ljava/lang/StringBuffer;");

        // sb.append(" is not a prime number");
        e.addLDCInstruction(" is a prime number");
        e.addMemberAccessInstruction(INVOKEVIRTUAL, "java/lang/StringBuffer", "append",
                "(Ljava/lang/String;)Ljava/lang/StringBuffer;");


        // System.out.println(sb.toString());
        e.addMemberAccessInstruction(INVOKEVIRTUAL, "java/lang/StringBuffer",
                "toString", "()Ljava/lang/String;");
        e.addMemberAccessInstruction(INVOKEVIRTUAL, "java/io/PrintStream", "println",
                "(Ljava/lang/String;)V");

        // return;
        e.addNoArgInstruction(RETURN);

/////////////////////////////////////////////////////^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^6
        //System.out.println(n + " is not prime number");
        e.addLabel("p1");
        // Get System.out on stack
        e.addMemberAccessInstruction(GETSTATIC, "java/lang/System", "out", "Ljava/io/PrintStream;");

        // Create an intance (say sb) of StringBuffer on stack for string concatenations
        //    sb = new StringBuffer();
        e.addReferenceInstruction(NEW, "java/lang/StringBuffer");
        e.addNoArgInstruction(DUP);
        e.addMemberAccessInstruction(INVOKESPECIAL, "java/lang/StringBuffer", "<init>", "()V");

        // sb.append(n);
        e.addNoArgInstruction(ILOAD_1);
        e.addMemberAccessInstruction(INVOKEVIRTUAL, "java/lang/StringBuffer", "append",
                "(I)Ljava/lang/StringBuffer;");

        // sb.append("!=");
        e.addLDCInstruction(" is not a prime number");
        e.addMemberAccessInstruction(INVOKEVIRTUAL, "java/lang/StringBuffer", "append",
                "(Ljava/lang/String;)Ljava/lang/StringBuffer;");


        // System.out.println(sb.toString());
        e.addMemberAccessInstruction(INVOKEVIRTUAL, "java/lang/StringBuffer",
                "toString", "()Ljava/lang/String;");
        e.addMemberAccessInstruction(INVOKEVIRTUAL, "java/io/PrintStream", "println",
                "(Ljava/lang/String;)V");

        // return;
        e.addNoArgInstruction(RETURN);



       // e.write();

        //priate static boolean isPrime(int n) {

        modifiers.clear();
        modifiers.add("private");
        modifiers.add("static");
        e.addMethod(modifiers, "isPrime", "(I)Z", null, true);

        // if (n >=  2){ ryurn false}
        e.addNoArgInstruction(ILOAD_0);
        e.addNoArgInstruction(ICONST_2);
        e.addBranchInstruction(IF_ICMPGE, "A");
        e.addNoArgInstruction(ICONST_0);
        e.addNoArgInstruction(IRETURN);
        // Storing the value of i doing A
        e.addLabel("A");
        e.addNoArgInstruction(ICONST_2);
        e.addNoArgInstruction(ISTORE_3);
        e.addBranchInstruction(GOTO,"D");
        //
        // storing the value of n/i
        e.addLabel("D");
        e.addNoArgInstruction(ILOAD_0);
        e.addNoArgInstruction(ILOAD_3);
        e.addNoArgInstruction(IDIV);
        e.addNoArgInstruction(ISTORE_2);
        //e.addNoArgInstruction(ISTORE_4);

        e.addNoArgInstruction(ILOAD_3);
        e.addNoArgInstruction(ILOAD_2);

       // e.addNoArgInstruction(ILOAD_3);
        //e.addNoArgInstruction(ILOAD_4);
        // go to B
        e.addBranchInstruction(IF_ICMPGT, "B");
        //go to D:
        // if n % i != 0 go to c
        e.addNoArgInstruction(ILOAD_0);
        e.addNoArgInstruction(ILOAD_3);
        e.addNoArgInstruction(IREM);
        //e.addNoArgInstruction(ISTORE_5);
        e.addNoArgInstruction(ISTORE_1);

        e.addNoArgInstruction(ILOAD_1);
        e.addNoArgInstruction(ICONST_0);
        e.addBranchInstruction(IF_ICMPNE, "C");
        e.addNoArgInstruction(ICONST_0);
        e.addNoArgInstruction(IRETURN);

        //label for c
        e.addLabel("C");
        e.addNoArgInstruction(ILOAD_3);
        e.addNoArgInstruction(ICONST_1);
        e.addNoArgInstruction(IADD);
        e.addNoArgInstruction(ISTORE_3);
        e.addBranchInstruction(GOTO, "D");



        // lable for true.
        e.addLabel("B");
        e.addNoArgInstruction(ICONST_1);
        e.addNoArgInstruction(IRETURN);

        e.write();















    }
}
