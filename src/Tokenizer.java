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

 ???????????? character integer ??????????????????  compileDeclaration()
 ???????????? IDENTIFIER ?????????  ??????????????????  compileAssignment()
                ?????????????????? compileStatement ( ?????????; ??????Assignment)

 ??????IF?????????????????????ch .lt. "z"  ?????????compileStatement()?
 ?????? cS() ????????? ????????????  ????????? ???????????? , ????????? statement????????? ??????

    cS() ?????????????????? ( )???????  (num .div. 3) .ne. 0 .and. (ch .ne. "y")
    ???????????? ?????????cS(),  ?????????recursion


 ??? If the expression contains at least one relationalOp,  .and.  .or.  .not.  then its data type is logical.
 ??? If the expression is not logical and it contains at least one CHARACTER_CONSTANT
 or an IDENTIFIER of data type CHAR then its data type is character.
 ??? If the expression is neither logical nor character then its data type is integer.

 1. let ????????????, ?????? Symbol Table
 2. while loop + if ??? ???????????? logical statement
 3. <= ?????? ?????? ????????? ????????????



 way 1. Assignment ???????????? ?????? ( ????????? <bracket> ( </bracket>  ??????call cs()
 Cs() ?????????   ???????????????element??? (  ???????????????
 cs() ????????????element?????? ?????? ") or ; " ???????????????????????????

 way 2. cs()????????????  ????????????<statement></>
 ??????(  ????????????

*/