import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.LinkedList;
import java.util.List;


public class Tokenizer {
    FileInputStream fi;
    public Tokenizer(String file) throws FileNotFoundException {
        // Load file
        fi = new FileInputStream((file));
    }

    public String[] Tokenize() throws IOException {
        String bb = new String(fi.readAllBytes(), StandardCharsets.UTF_8);


        LinkedList<String> temp = new LinkedList<>();
        String[] temp1 = bb.split("\n");
        for (int i = 0; i < temp1.length; i++) {
            String[] temp2 = temp1[i].split("\s+");
            temp.addAll(List.of(temp2));
        }
        String[] ss = temp.toArray(String[]::new);


        String[] newArray = new String[ss.length*2];
        int newArrayIndex = 0;
        int trueLength = 0;
        for (int i=0; i<ss.length; i++){
            String one = ss[i];
            if (!one.isEmpty()) {
                one = one.trim();
                if (one.length()==1){
                    newArray[newArrayIndex] = one;
                    newArrayIndex++;
                }else if (one.startsWith("(")) {
                    newArray[newArrayIndex] = one.substring(0,1);
                    newArrayIndex++;
                    newArray[newArrayIndex] = one.substring(1,one.length());
                    newArrayIndex++;
                }else {
                    if (one.endsWith(")")) {
                        newArray[newArrayIndex] = one.substring(0, one.length()-1);
                        newArrayIndex++;
                        newArray[newArrayIndex] = one.substring(one.length() - 1, one.length());
                        newArrayIndex++;
                    }else if (one.endsWith(",")) {
                        newArray[newArrayIndex] = one.substring(0, one.length()-1);
                        newArrayIndex++;
                        newArray[newArrayIndex] = one.substring(one.length() - 1, one.length());
                        newArrayIndex++;
                    }else if (one.endsWith(";")) {
                        newArray[newArrayIndex] = one.substring(0, one.length()-1);
                        newArrayIndex++;
                        newArray[newArrayIndex] = one.substring(one.length() - 1, one.length());
                        newArrayIndex++;
                    }else{
                        if (one.equals("finish")){
                            newArray[newArrayIndex] = one;
                            newArrayIndex++;
                            trueLength = newArrayIndex;
                            break;
                        }else {
                            newArray[newArrayIndex] = one;
                            newArrayIndex++;
                        }
                    }
                }
            }
        }
        if (trueLength==0) return null;
        String[] str = new String[trueLength];
        System.arraycopy(newArray,0,str,0,trueLength);

//        for (String aa: str){
//            System.out.println(aa);
//        }
        return str;
    }
}


/** types:    keyword: logical?

 Keywords:
 start  finish
 integer  character
 if then else endif;          If and Loop involve recursions ( nested loop and if)
 loopif  do  endloop;
 print


 Symbol:
 ,  ;  <-  (  )

Operator:
 '.plus.' | '.minus.' | '.mul.' | '.div.'
 .and. | .or.
 '.eq.' | '.ne.' | '.lt.' | '.gt.' | '.le.' | '.ge.'
                 less than      less or equal     greater or equal

 Identifier:
 1. one or more alphabetic, digit or underscore characters, of
 which the first must be alphabetic
 2. can not be Keyword

 INTEGER_CONSTANT:     one or more digits (0-9)

 CHARACTER_CONSTANT:    double quotes with only One character  "x"

 ---------------------------------------------
 If string is one of the keywords?

 else If Regex: "  ^[a-zA-Z][a-zA-Z0-9_]+  "   Identifier

 else If start and end with .
                If is one of above .and.        Operator

 else If is all digits:  Regex" [0-9]+ "        INTEGER_CONSTANT

 else If start and end with "  and StringLength is 3      CHARACTER_CONSTANT

 else
        Illegal input, quit parser
 -----------------------------------------------------

 only If and Loop involve recursions ( nested loop and if),  So two methods:
    compileIf() { if seeing "if"  call self,  seeing Loopif call compileLoopif()  }
    compileLoopif()

 如果碰到 character integer 就是声明语句  compileDeclaration()
 如果碰到 IDENTIFIER 变量名  就是赋值语句  compileAssignment()
                然后内部调用 compileStatement ( 最后的; 属于Assignment)

 如果IF后面的判断语句ch .lt. "z"  也调用compileStatement()?
 那么 cS() 既要做 逻辑判断  也要做 算数判断 , 并设置 statement标签的 属性

    cS() 里面也设计到 ( )括号?  (num .div. 3) .ne. 0 .and. (ch .ne. "y")
    括号里是 另一个cS(),  必须要recursion


 • If the expression contains at least one relationalOp,  .and.  .or.  .not.  then its data type is logical.
 • If the expression is not logical and it contains at least one CHARACTER_CONSTANT
 or an IDENTIFIER of data type CHAR then its data type is character.
 • If the expression is neither logical nor character then its data type is integer.

 1. let 关键词时, 创建 Symbol Table
 2. while loop + if 等 必须跟着 logical statement
 3. <= 赋值 两边 必须要 类型一样



 way 1. Assignment 调用如果 碰到 ( 就写入 <bracket> ( </bracket>  然后call cs()
 Cs() 第一句   如果第一个element是 (  就调用自己
 cs() 从第二个element开始 检测 ") or ; " 作为自己的结束标志

 way 2. cs()涵盖一切  先写一个<statement></>
 碰到(  再写一个

*/