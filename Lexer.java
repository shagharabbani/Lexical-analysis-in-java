//Seyedeh Shaghayegh Rabbanian
package lexer;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
/**
 *
 * @author srabba2
 */
enum Reservedword{IF,FOR,WHILE,FUNCTION,RETURN,INT,ELSE,DO,BREAK,END,STRING};
public class Lexer
{
    public static void main(String [] args) throws IOException
   {
        String fileName = "testcase1.txt";
        Tokenize(fileName);
   }   

public static void Tokenize(String fileName) throws IOException{

        File f = new File(fileName);     //Creation of File Descriptor for input file
        FileReader fr = new FileReader(f);   //Creation of File Reader object
        BufferedReader br = new BufferedReader(fr);  //Creation of BufferedReader object
        int c = 0;
        String lexeme = "";
        String strWord = "";
        String operator = "";
        char prevChar = ' ';
        boolean operatorFlag = false;
        boolean cmtFlag = false;
        boolean isLineCmt = false;
        boolean isBlockCmt = false;
        boolean strFlag = false;

        HashMap<String, String> map = new HashMap<String, String>();
        map.put("=", "ASSIGN");
        map.put("+", "ADD");
        map.put("-", "SUB");
        map.put("*", "MUL");
        map.put("/", "DIV");
        map.put("%", "MOD");
        map.put(">", "GT");
        map.put("<", "LT");
        map.put(">=", "GE");
        map.put("<=", "LE");
        map.put("++", "INC");
        map.put("(", "LP");
        map.put(")", "RP");
        map.put("{", "LB");
        map.put("}", "RB");
        map.put("|", "OR");
        map.put("&", "AND");
        map.put("==", "EE");
        map.put("!", "NEG");
        map.put(",", "COMMA");
        map.put(";", "SEMI");
       
        while((c = br.read()) != -1)         //Read char by Char
       {
            char character = (char) c;          //converting integer to char
               
            if (!String.valueOf(character).matches(".")){   //new line
                isLineCmt = false;
                cmtFlag = false;
            }

            if (isBlockCmt && character == '/' && prevChar == '*'){     //End of Block comments
                isBlockCmt = false;
                cmtFlag = false;
            }

            if (cmtFlag){
                if (character == '/'){
                    isLineCmt = true;               // InLine comments
                    cmtFlag = false;
                }
                else if (character == '*'){         // Start of Block comments
                    isBlockCmt = true;
                    cmtFlag = false;
                }
                else{                               // Division operator
                    cmtFlag = false;
                    System.out.println(map.get(Character.toString(prevChar)));
                }
            }

            if (!isLineCmt && !isBlockCmt){         //Lines are comment or not
                if (character == '"'){              //Identifying string lit
                    strFlag = !strFlag;
                }

                if (strFlag && character!='"'){     //string lit lexeme maker
                    strWord += character;
                }

                else if (Character.isLetterOrDigit(character)){       //check if the character is letter or digit
                    if (operatorFlag){
                        operator = Character.toString(prevChar);
                        System.out.println(map.get(operator));
                        operatorFlag=false;
                    }

                    lexeme += character;
                }
                else if (character == '/'){        //Possibility for having inline comment,block comment or division operator
                    cmtFlag = true;
                }          
                else{
                    if (operatorFlag && map.get(Character.toString(character))==null){     //Operator with single character
                        operator = Character.toString(prevChar);
                        System.out.println(map.get(operator));
                        operatorFlag=false;
                    }
                    if(EnumUtils.isValidEnum(Reservedword.class, lexeme.toUpperCase())){       //If lexeme is reserved word
                        System.out.println(lexeme.toUpperCase());
                    }
                    else if (isIdentifier(lexeme)){                 //If lexeme is an identifier
                        System.out.println("IDENT:" + lexeme);
                    }
                    else if (isNumber(lexeme)){                 //If lexeme is int-lit
                        System.out.println("INT_LIT:" + lexeme);
                    }
                    else if (strWord!="" && !strFlag){                //str-lit
                        System.out.println("STR_LIT:" + strWord);
                        strWord = "";
                    }
                    else if (!isAcceptable(lexeme)){              //check for invalid identifier name like 9user
                        System.out.println("SYNTAX ERROR: INVALID IDENTITFER NAME");
                        break;
                    }
                   
                    if (!Character.isWhitespace(character)){
                        if (map.get(Character.toString(character))!=null && !operatorFlag){
                            if (character==';'){                                 //semi
                                operatorFlag=false;
                                System.out.println(map.get(Character.toString(character)));
                            }
                            else{
                                operatorFlag=true;
                            }
                        }
                        else if (character==';' && !operatorFlag){
                            operatorFlag=false;
                            System.out.println(map.get(Character.toString(character)));
                        }  
                        else if (map.get(Character.toString(character))!=null && operatorFlag && character!=';'){     //2-char operator
                        operatorFlag=false;
                            operator = Character.toString(prevChar) + Character.toString(character);
                        System.out.println(map.get(operator));
                        prevChar = ' ';
                        }
                        else if (map.get(Character.toString(character))!=null && operatorFlag && character==';'){
                            operatorFlag=false;
                            System.out.println(map.get(")"));
                            System.out.println(map.get(";"));
                            prevChar = ' ';
                        }
                    }
                    lexeme = "";
                }
      }
    prevChar = character;
}
}
   
public static boolean isIdentifier(String lexeme){
    boolean result = false;
    if (EnumUtils.isValidEnum(Reservedword.class, lexeme.toUpperCase()) || lexeme==""){
        result = false;
    }
    else if (!Character.isDigit(lexeme.charAt(0))){
        result = true;
    }
   
    return result;
}

public static boolean isNumber(String lexeme){
    boolean result = false;
    if (lexeme.matches("^[0-9]+$")){
        result = true;
    }
    else {
        result = false;
    }
   
    return result;
}

public static boolean isAcceptable(String lexeme){
    boolean result = false;
    if (isNumber(lexeme) || isIdentifier(lexeme) || EnumUtils.isValidEnum(Reservedword.class, lexeme.toUpperCase()) || lexeme==""){
        result = true;
    }
    else {
        result = false;
    }
    return result;
}

class EnumUtils {
    public static <E extends Enum<E>> boolean isValidEnum(final Class<E> enumClass, final String enumName) {
        if (enumName == null) {
            return false;
        }
        try {
            Enum.valueOf(enumClass, enumName);
            return true;
        } catch (final IllegalArgumentException ex) {
            return false;
        }
    }
}
}