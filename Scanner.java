// Copyright 2012- Bill Campbell, Swami Iyer and Bahar Akbal-Delibas

package jminusminus;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.LineNumberReader;
import java.util.Hashtable;

import static jminusminus.TokenKind.*;

/**
 * A lexical analyzer for j--, that has no backtracking mechanism.
 */
class Scanner {
    // End of file character.
    public final static char EOFCH = CharReader.EOFCH;

    // Keywords in j--.
    private Hashtable<String, TokenKind> reserved;

    // Source characters.
    private CharReader input;

    // Next unscanned character.
    private char ch;

    // Whether a scanner error has been found.
    private boolean isInError;

    // Source file name.
    private String fileName;

    // Line number of current token.
    private int line;

    /**
     * Constructs a Scanner from a file name.
     *
     * @param fileName name of the source file.
     * @throws FileNotFoundException when the named file cannot be found.
     */
    public Scanner(String fileName) throws FileNotFoundException {
        this.input = new CharReader(fileName);
        this.fileName = fileName;
        isInError = false;

        // Keywords in j--
        reserved = new Hashtable<String, TokenKind>();
        reserved.put(ABSTRACT.image(), ABSTRACT);
        reserved.put(BOOLEAN.image(), BOOLEAN);
        reserved.put(CHAR.image(), CHAR);
        reserved.put(CLASS.image(), CLASS);
        reserved.put(ELSE.image(), ELSE);
        reserved.put(EXTENDS.image(), EXTENDS);
        reserved.put(FALSE.image(), FALSE);
        reserved.put(IF.image(), IF);
        reserved.put(IMPORT.image(), IMPORT);
        reserved.put(INSTANCEOF.image(), INSTANCEOF);
        reserved.put(INT.image(), INT);
        reserved.put(NEW.image(), NEW);
        reserved.put(NULL.image(), NULL);
        reserved.put(PACKAGE.image(), PACKAGE);
        reserved.put(PRIVATE.image(), PRIVATE);
        reserved.put(PROTECTED.image(), PROTECTED);
        reserved.put(PUBLIC.image(), PUBLIC);
        reserved.put(RETURN.image(), RETURN);
        reserved.put(STATIC.image(), STATIC);
        reserved.put(SUPER.image(), SUPER);
        reserved.put(THIS.image(), THIS);
        reserved.put(TRUE.image(), TRUE);
        reserved.put(VOID.image(), VOID);
        reserved.put(WHILE.image(), WHILE);
        // Adding here the code for the problem#3 in assignment 2
        // just using the code given for reserved words before and
        // apply it for the additional reserved words given in the assignment 2.
        reserved.put(BREAK.image(),BREAK);
        reserved.put(CASE.image(), CASE);
        reserved.put(CATCH.image(), CATCH);
        reserved.put(CONTINUE.image(),CONTINUE);
        reserved.put(DEFAULT.image(), DEFAULT);
        reserved.put(DO.image(), DO);
        reserved.put(DOUBLE.image(), DOUBLE);
        reserved.put(FINALLY.image(), FINALLY);
        reserved.put(FOR.image(),FOR);
        reserved.put(IMPLEMENTS.image(), IMPLEMENTS);
        reserved.put(INTERFACE.image(), INTERFACE);
        reserved.put(LONG.image(), LONG);
        reserved.put(SWITCH.image(), SWITCH);
        reserved.put(THROW.image(), THROW);
        reserved.put(THROWS.image(), THROWS);
        reserved.put(TRY.image(), TRY);

        // Prime the pump.
        nextCh();
    }

    //LONG_LITERAL    ::=  INT_LITERAL ( "l" | "L" )111   EXPONENT
    // ::= ( "e" | "E" ) [ ( "+" | "-" ) ] DIGITS112
    // SUFFIX           ::= "d" | "D"113
    // DOUBLE_LITERAL  ::=  DIGITS  "." [ DIGITS ] [ EXPONENT ] [ SUFFIX ]|
    // "."  DIGITS [ EXPONENT ] [ SUFFIX ]
    // | DIGITS  EXPONENT [ SUFFIX ]
    // helper method for double literals
    // checking for the exponential
    public boolean isExponential(char ch)
    {
        boolean a = (ch == 'e' || ch == 'E');
        return a;
    }
    // checking for char D and d
    public boolean isSuffix(char ch)
    {
        boolean a =  (ch == 'd' || ch == 'D');
        return a;
    }
    //checking for the '.' character.
    public boolean dot(char ch)
    {
        boolean a = (ch == '.');
        return a;
    }
    // This method will get all the digits
    public void isDigitLoop (StringBuffer s)//  char ch)
    {
        while (isDigit(ch) || ch =='#')
        {
            s.append(ch);
            //System.out.println(ch);
            nextCh();

        }

    }
    // This method will return if the given have exponential
    // if yes it will return true
    public boolean isExponential1(StringBuffer s)
    {
        if(isExponential(ch)){
            s.append(ch);
            nextCh();
            // checking for the - and + as the grammar file excpect us to check inside exponential.
            if(ch == '-' || ch =='+')
            {
                //appending the string buffer accrodingly.
                s.append(ch);
                nextCh();
            }
            // after sccaning the remaining digits after scannig the E and + or -.
            isDigitLoop(s);
            return true;
        }

        return false;

    }
    // Method will return the true if the given digits has a dot '.'
    public boolean isDot1(StringBuffer s) {
        // calling helper method to check for the '.' character
        if (dot(ch))
        {
            // appending the string buffer accordingly.
            s.append(ch);
            nextCh();
            //looping the digits after appending the '.' character
            isDigitLoop(s);
            return  true;

        }
        return false;

    }
      // this method will rertun true or false it the 
      // digits d or D and append it to string buffer.
    public boolean isSuffix1(StringBuffer s)
    {
        if (isSuffix(ch))
        {
            s.append(ch);
            nextCh();
            return true;
        }
        return false;
    }
// This method is basically a case if the digit start with '.' character.


    public boolean decimalStartWithDot(StringBuffer s)
    {
        boolean check = false;

        if (isDigit(ch))
        {

            // this appending the '.' character 
            // in the string buffer. 
            // the whole thing after this is similar by 
            //checking for the exponetial and isSuffix. 
            s.append('.');

            isDigitLoop(s);
            if(isExponential(ch)) {
                s.append(ch);
                nextCh();
                if (ch =='-' || ch =='+') {
                    s.append(ch);
                    nextCh();
                }

                isDigitLoop(s);


            }

            if(isSuffix(ch)){
                s.append(ch);
                nextCh();

            }

         //if that if conditions runs we will return true for double literal and will handle in DOT token case. 
            return  true;//new TokenInfo(DOUBLE_LITERAL, buffer.toString(),line);
        }
        // else false. 
        return false;

    }
    /**
     * Scans and returns the next token from input.
     *
     * @return the next scanned token.
     */
    public TokenInfo getNextToken() {
        StringBuffer buffer;
        boolean moreWhiteSpace = true;
        while (moreWhiteSpace) {
            while (isWhitespace(ch)) {
                nextCh();
            }
            // Adding here the code for the multiline comments.

            if (ch == '/') {
                nextCh();
                if (ch == '/') {
                    // CharReader maps all new lines to '\n'.
                    while (ch != '\n' && ch != EOFCH) {
                        nextCh();
                    }
                }
                //adding support in the scanner file for the '/=' operator.
                else if (ch == '=')
                {
                    nextCh();
                    return new TokenInfo(DIV_ASSIGN, line);
                }
                // This else if statement will deal with multilne comments for the scanner file.
                // simply checking for '*' after checking for '/'
                // then read the file till EOFCH  by using the while loop. In the mean time if find one more '*'
                // and right after this
                // if found '/' break the loop and  will be done with the multiline comments.
                else if (ch == '*') {

                    while (ch != EOFCH) {
                        nextCh();
                        if (ch == '*') {
                            nextCh();
                            if (ch == '/')
                            { nextCh();
                                break; }

                        }
                    }
                }
                else {
                    //reportScannerError("Operator / is not supported in j--");
                    return new TokenInfo(DIV, line);
                }
            }
            else if (ch =='%') {
                nextCh();
                // adding code here for the %= operator.
                if(ch == '=')
                {
                    nextCh();
                    return new TokenInfo(REM_ASSIGN, line);
                }
                else {
                    //nextCh();
                    return new TokenInfo(REM, line);
                }
            }

            else if(ch==':') {
                nextCh();
                return new TokenInfo(COLON, line);
            }

            else if (ch == '|') {
                nextCh();
                // code that checks for the |=
                if (ch =='=') {
                    nextCh();
                    return new TokenInfo(IOR_ASSIGN, line);
                }
                // code that checks for the '||' operator
                else if (ch == '|') {
                    nextCh();
                    return new TokenInfo(COR, line);
                }
                else {
                    return new TokenInfo(IOR, line);
                }

            }
            else if (ch=='&') {
                nextCh();
                // code for the && operator.
                if (ch == '&') {
                    nextCh();
                    return new TokenInfo(LAND, line);
                }
                // code for the &= operator.
                else if(ch =='=') {
                    nextCh();
                    return new TokenInfo(BAND_ASSIGN, line);
                }
                else {
                    //reportScannerError("Operator & is not supported in j--");
                    //return getNextToken();
                    //nextCh();
                    return new TokenInfo(BAND, line);
                }

            }
            else if (ch =='^') {
                nextCh();
                // code for that will scan the ^= operator.
                if (ch == '=')
                {
                    nextCh();
                    return new TokenInfo(XOR_ASSIGN, line);
                }
                else {
                    //nextCh();
                    return new TokenInfo(XOR, line);
                }

            }
            else if (ch =='~') {
                nextCh();
                return new TokenInfo(BCOMP, line);
            }

            else if (ch =='>') {
                nextCh();
                // code that will scan for the >=
                if (ch == '=')
                {
                    nextCh();
                    return new TokenInfo(GT_ASSIGN, line);
                }
                // code that will scan for the >>= and >>>= operator.
                else if (ch == '>') {
                    nextCh();
                    if (ch == '>') {
                        nextCh();
                        if (ch == '=') {
                            nextCh();
                            return new TokenInfo(LRS_ASSIGN, line);
                        } else {
                            return new TokenInfo(LRS, line);
                        }
                    }
                    //>>=
                    else if (ch == '=')
                    {
                        nextCh();
                        return new TokenInfo(ARS_ASSIGN, line);
                    }
                    // code that will run >>.
                    else {
                        return new TokenInfo(ARS,line);
                    }
                }
                // greater then > operator.
                else {
                    return new TokenInfo(GT, line);
                }


            }
            else {
                moreWhiteSpace = false;
            }
        }
        line = input.line();
        switch (ch) {
            case ',':
                nextCh();
                return new TokenInfo(COMMA, line);
            case '.':
                nextCh();
               // cretaing the new string buffer here.
                buffer = new StringBuffer();
                // assigning the decimalDot as false
                boolean decimalDot = false;
               // now checking for the dot case 
               // example of this is .67898E+282

                if(decimalStartWithDot(buffer))
                {
                    decimalDot= true;
                }
                // if its true we will return double literal token else DOT token.
                if(decimalDot)
                {
                    return new TokenInfo(DOUBLE_LITERAL, buffer.toString(),line);
                }
                    return new TokenInfo(DOT, line); //}

            case '[':
                nextCh();
                return new TokenInfo(LBRACK, line);
            case '{':
                nextCh();
                return new TokenInfo(LCURLY, line);
            case '(':
                nextCh();
                return new TokenInfo(LPAREN, line);
            case ']':
                nextCh();
                return new TokenInfo(RBRACK, line);
                // code that will scan for the '?' operator.
            case'?':
                nextCh();
                return new TokenInfo(QUESTION, line);
            case '}':
                nextCh();
                return new TokenInfo(RCURLY, line);
            case ')':
                nextCh();
                return new TokenInfo(RPAREN, line);
            case ';':
                nextCh();
                return new TokenInfo(SEMI, line);
            case '*':
                nextCh();
                // code that will scan for the '*='
                if (ch == '=')
                {
                    nextCh();
                    return new TokenInfo(STAR_ASSIGN, line);
                }
                else {
                    nextCh();
                    return new TokenInfo(STAR, line);
                }
            case '+':
                nextCh();
                if (ch == '=') {
                    nextCh();
                    return new TokenInfo(PLUS_ASSIGN, line);
                } else if (ch == '+') {
                    nextCh();
                    return new TokenInfo(INC, line);
                } else {
                    return new TokenInfo(PLUS, line);
                }
            case '-':
                nextCh();
                if (ch == '-') {
                    nextCh();
                    return new TokenInfo(DEC, line);
                }
                else if (ch == '=')
                {
                    nextCh();
                    return new TokenInfo(MINUS_ASSIGN, line);
                }
                else {
                    return new TokenInfo(MINUS, line);
                }

            case '=':
                nextCh();
                if (ch == '=') {
                    nextCh();
                    return new TokenInfo(EQUAL, line);
                } else {
                    return new TokenInfo(ASSIGN, line);
                }
            // this code will handle now for the less then operator.

            case '<':
                nextCh();
                // code that will scan for the <=
                if (ch == '=') {
                    nextCh();
                    return new TokenInfo(LE, line);
                }
                //code that will scan for <<= and <<
                else if  (ch == '<') {
                    nextCh();
                    if (ch == '='){
                        nextCh();
                        return new TokenInfo(ALS_ASSIGN, line);
                    }
                    else {
                        //nextCh();
                        return new TokenInfo(ALS, line);
                    }
                }
                else {
                    //reportScannerError("Operator < is not supported in j--");
                    return new TokenInfo(LT, line);
                }
            case '!':
                nextCh();
                if (ch =='=')
                {
                    nextCh();
                    return new TokenInfo(NOT_EQUAL, line);
                }
                else {
                    nextCh();
                    return new TokenInfo(LNOT, line);
                }

            case '\'':
                buffer = new StringBuffer();
                buffer.append('\'');
                nextCh();
                if (ch == '\\') {
                    nextCh();
                    buffer.append(escape());
                } else {
                    buffer.append(ch);
                    nextCh();
                }
                if (ch == '\'') {
                    buffer.append('\'');
                    nextCh();
                    return new TokenInfo(CHAR_LITERAL, buffer.toString(), line);
                } else {
                    // Expected a ' ; report error and try to recover.
                    reportScannerError(ch + " found by scanner where closing ' was expected");
                    while (ch != '\'' && ch != ';' && ch != '\n') {
                        nextCh();
                    }
                    return new TokenInfo(CHAR_LITERAL, buffer.toString(), line);
                }
            case '"':
                buffer = new StringBuffer();
                buffer.append("\"");
                nextCh();
                while (ch != '"' && ch != '\n' && ch != EOFCH) {
                    if (ch == '\\') {
                        nextCh();
                        buffer.append(escape());
                    } else {
                        buffer.append(ch);
                        nextCh();
                    }
                }
                if (ch == '\n') {
                    reportScannerError("Unexpected end of line found in string");
                } else if (ch == EOFCH) {
                    reportScannerError("Unexpected end of file found in string");
                } else {
                    // Scan the closing "
                    nextCh();
                    buffer.append("\"");
                }
                return new TokenInfo(STRING_LITERAL, buffer.toString(), line);
            case EOFCH:
                return new TokenInfo(EOF, line);

            case '0':
            case '1':
            case '2':
            case '3':
            case '4':
            case '5':
            case '6':
            case '7':
            case '8':
            case '9':
            // the boolean assigned to false but if any case for the 
            // double will come true it will assigned to true. 
                boolean decimalNum1 = false;

                buffer = new StringBuffer();

                // method that will read the digits.
                 isDigitLoop(buffer);



                // cheking for the LONG LITERALS
                // if yes will return tehtoken for the long.
                if (ch == 'l' || ch =='L')
                {
                    buffer.append(ch);
                    nextCh();
                    return new TokenInfo(LONG_LITERL, buffer.toString(), line);
                }
                /// The code will check the cases for the double literal.//
                // I bascially follow this
                // if statemnt checking for the given digit has '.' character in it or not
                // and it will assign the decimalNum true if it comes true. 
                if(isDot1(buffer))
                {
                    decimalNum1=true;
                }
                  // Similary we checking here for exponential
                if(isExponential1(buffer))
                {
                    decimalNum1= true;
                }
                // checking for the isSuffix.
                if (isSuffix1(buffer))
                {

                    decimalNum1= true;
                }

                //System.out.println(decimalNum1);
                // if any case will come true or all the cases come true 
                // then we pass that digit as the token of DOUBLE LITERAL. 
                if(decimalNum1)
                {
                    return new TokenInfo(DOUBLE_LITERAL, buffer.toString(), line);
                }
                // else regular int literal. 

                    return new TokenInfo(INT_LITERAL, buffer.toString(), line);

            default:
                if (isIdentifierStart(ch)) {
                    buffer = new StringBuffer();
                    while (isIdentifierPart(ch)) {
                        buffer.append(ch);
                        nextCh();
                    }
                    String identifier = buffer.toString();
                    if (reserved.containsKey(identifier)) {
                        return new TokenInfo(reserved.get(identifier), line);
                    } else {
                        return new TokenInfo(IDENTIFIER, identifier, line);
                    }
                } else {
                    reportScannerError("Unidentified input token: '%c'", ch);
                    nextCh();
                    return getNextToken();
                }
        }
    }







    /**
     * Returns true if an error has occurred, and false otherwise.
     *
     * @return true if an error has occurred, and false otherwise.
     */
    public boolean errorHasOccurred() {
        return isInError;
    }

    /**
     * Returns the name of the source file.
     *
     * @return the name of the source file.
     */
    public String fileName() {
        return fileName;
    }

    // Scans and returns an escaped character.
    private String escape() {
        switch (ch) {
            case 'b':
                nextCh();
                return "\\b";
            case 't':
                nextCh();
                return "\\t";
            case 'n':
                nextCh();
                return "\\n";
            case 'f':
                nextCh();
                return "\\f";
            case 'r':
                nextCh();
                return "\\r";
            case '"':
                nextCh();
                return "\\\"";
            case '\'':
                nextCh();
                return "\\'";
            case '\\':
                nextCh();
                return "\\\\";
            default:
                reportScannerError("Badly formed escape: \\%c", ch);
                nextCh();
                return "";
        }
    }

    // Advances ch to the next character from input, and updates the line number.
    private void nextCh() {
        line = input.line();
        try {
            ch = input.nextChar();
        } catch (Exception e) {
            reportScannerError("Unable to read characters from input");
        }
    }

    // Reports a lexical error and records the fact that an error has occurred. This fact can be
    // ascertained from the Scanner by sending it an errorHasOccurred message.
    private void reportScannerError(String message, Object... args) {
        isInError = true;
        System.err.printf("%s:%d: error: ", fileName, line);
        System.err.printf(message, args);
        System.err.println();
    }

    // Returns true if the specified character is a digit (0-9), and false otherwise.
    private boolean isDigit(char c) {
        return (c >= '0' && c <= '9');
    }

    // Returns true if the specified character is a whitespace, and false otherwise.
    private boolean isWhitespace(char c) {
        return (c == ' ' || c == '\t' || c == '\n' || c == '\f');
    }

    // Returns true if the specified character can start an identifier name, and false otherwise.
    private boolean isIdentifierStart(char c) {
        return (c >= 'a' && c <= 'z' || c >= 'A' && c <= 'Z' || c == '_' || c == '$');
    }

    // Returns true if the specified character can be part of an identifier name, and false
    // otherwise.
    private boolean isIdentifierPart(char c) {
        return (isIdentifierStart(c) || isDigit(c));
    }
}

/**
 * A buffered character reader, which abstracts out differences between platforms, mapping all new
 * lines to '\n', and also keeps track of line numbers.
 */
class CharReader {
    // Representation of the end of file as a character.
    public final static char EOFCH = (char) -1;

    // The underlying reader records line numbers.
    private LineNumberReader lineNumberReader;

    // Name of the file that is being read.
    private String fileName;

    /**
     * Constructs a CharReader from a file name.
     *
     * @param fileName the name of the input file.
     * @throws FileNotFoundException if the file is not found.
     */
    public CharReader(String fileName) throws FileNotFoundException {
        lineNumberReader = new LineNumberReader(new FileReader(fileName));
        this.fileName = fileName;
    }

    /**
     * Scans and returns the next character.
     *
     * @return the character scanned.
     * @throws IOException if an I/O error occurs.
     */
    public char nextChar() throws IOException {
        return (char) lineNumberReader.read();
    }

    /**
     * Returns the current line number in the source file.
     *
     * @return the current line number in the source file.
     */
    public int line() {
        return lineNumberReader.getLineNumber() + 1; // LineNumberReader counts lines from 0
    }

    /**
     * Returns the file name.
     *
     * @return the file name.
     */
    public String fileName() {
        return fileName;
    }

    /**
     * Closes the file.
     *
     * @throws IOException if an I/O error occurs.
     */
    public void close() throws IOException {
        lineNumberReader.close();
    }
}
