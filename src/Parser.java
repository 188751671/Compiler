import org.w3c.dom.Document;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;

public class Parser {  
    private final Tokenizer tokenizer;
    private final SymbolTable table;
    String[] strings;
    int index=0;
    HashSet<String> keywords = new HashSet<>(Arrays.asList(
            "start",
            "finish",
            "if",
            "then",
            "else",
            "endif",
            "loopif",
            "do",
            "endloop",
            "integer",
            "character",
            "print"
    ));
    HashSet<Character> symbols = new HashSet<>(Arrays.asList(
            '(',
            ')',
            ',',
            ';'
    ));
    HashSet<String> arithmeticOp = new HashSet<>(Arrays.asList(
            ".plus.",
            ".minus.",
            ".mul.",
            ".div."
    ));
    HashSet<String> logicOp = new HashSet<>(Arrays.asList(
            ".eq.",
            ".ne.",
            ".lt.",
            ".gt.",
            ".le.",
            ".ge.",

            ".and.",
            ".or."
    ));

    public Parser(String filename) throws IOException {
        tokenizer = new Tokenizer(filename);
        table = new SymbolTable();
    }

    public void parse() throws IOException, ParserConfigurationException {
        strings = tokenizer.Tokenize();

        // compile every word
        if (compile() == false){
            System.out.println("Error");
        }else {
            System.out.println("OK");
        }
    }

    public boolean compile(){
        int ifCount = 0, loopIfCount = 0;
        while (index<strings.length){  // index 由 子程序 来修改递增
            if (strings[index]!=null) {
                if (keywords.contains(strings[index])){
                    if (strings[index].equals("character") || strings[index].equals("integer")) {
                        if (!compileDeclaration(index)) return false;
                        // finished declaration
                    }
                    else if (strings[index].equals("start")) index++;
                    else if (strings[index].equals("finish")) {
                        if (ifCount!=0 || loopIfCount!=0)   return false;
                        return true;
                    }
                    else if (strings[index].equals("print")) {
                        int endIndex = findNext(";");
                        index = endIndex+1;
                    } else if (strings[index].equals("if")) {
                        ifCount++;
                        compileIf(index);
                    } else if (strings[index].equals("else")) {
                        index++;
                    } else if (strings[index].equals("endif")) {
                        ifCount--;
                        index++;    // reaching the ";" following endif
                        if (!strings[index].equals(";")) return false;
                        index++;
                    } else if (strings[index].equals("loopif")) {
                        loopIfCount++;
                        compileLoop(index);
                    } else if (strings[index].equals("endloop")){
                        loopIfCount--;
                        index++;    // reaching the ";" following endloop
                        if (!strings[index].equals(";")) return false;
                        index++;
                    } else{     // unrecognized keyword
                        return false;
                    }

                } else if (table.isInTable(strings[index])) {  // an identifier in table
                    if (compileAssignment(index)==false)   return false;
                }else {     // unrecognized
                    return false;
                }
            }
            System.out.println(strings[index]);
        }
        return true;
    }
    public boolean compileDeclaration(int i){
        if (strings[i].equals("integer")){
            i++;
            while(!strings[i].equals(";")){
                if (keywords.contains(strings[i]))  return false;   // no ; found, but reached next sentence
                if (strings[i].equals(",")) i++;
                else{
                    if (!table.set(strings[i],1)) return false;   // int 1== integer  2==character
                    i++;
                }
            }
        }else {
            i++;
            while(!strings[i].equals(";")){
                if (keywords.contains(strings[i]))  return false;   // no ; found, but reached next sentence
                if (strings[i].equals(",")) i++;
                else{
                    if (!table.set(strings[i],2)) return false;
                    i++;
                }
            }
        }
        index = i+1;
        return true;
    }
    public boolean compileAssignment(int i){
        if (!strings[i+1].equals("<-"))   return false;

        int endIndex = findNext(";");
        if (endIndex==0)    return false;

        int type = table.getType(strings[i]);
        if (type != 1 && type != 2) return false;

        if (!compileStatement(i+2,endIndex-1, type))
            return false;

        index = endIndex+1;
        return true;
    }
    public boolean compileIf(int i){
        int thenIndex = findNext("then");
        if (thenIndex==0)    return false;

        if(!compileStatement(i+1,thenIndex-1,3))
            return false;

        index = thenIndex+1;
        return true;
    }
    public boolean compileLoop(int i){
        int doIndex = findNext("do");
        if (doIndex==0)    return false;

        if(!compileStatement(i+1,doIndex-1,3))
            return false;

        index = doIndex+1;
        return true;
    }
    /** CompileStatement
     *  requiredType: 0= default  1== integer  2==character  3 = logical
     */
    public boolean compileStatement(int startIndex, int endIndex, int requiredType){
        // ch .lt. "z"
        // "a"
        // 1
        // sum .minus. num
        // (num .div. 3) .ne. 0 .and. (ch .ne. "y")
        int type = findType(startIndex,endIndex);

        if (type!=requiredType)     return false;
        return true;
    }
    private int findType(int start, int end){
        boolean charSpotted = false;
        for (int i= start;i<=end;i++){
            if (strings[i].startsWith(".") && logicOp.contains(strings[i])){
                return 3;
            }else if(strings[i].startsWith("\"") && strings[i].length()==3) {
                charSpotted = true;
            }else{
                if (table.isInTable(strings[i])){
                    int type = table.getType(strings[i]);
                    if (type==2) charSpotted =true;
                }
            }
        }
        if (charSpotted)    return 2;
        return 1;
    }
    public int findNext(String symbol){
        int i = index;
        for (;i<strings.length;i++){
            if (strings[i].equals(symbol))
                return i;
        }
        return 0;
    }
}
