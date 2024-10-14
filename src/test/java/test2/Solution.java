import java.io.*;
import java.math.*;
import java.security.*;
import java.text.*;
import java.util.*;
import java.util.concurrent.*;
import java.util.function.*;
import java.util.regex.*;
import java.util.stream.*;
import static java.util.stream.Collectors.joining;
import static java.util.stream.Collectors.toList;


class Result {

    /*
     * Complete the 'matchingBraces' function below.
     *
     * The function is expected to return a STRING_ARRAY.
     * The function accepts STRING_ARRAY braces as parameter.
     */

    public static List<String> matchingBraces(List<String> braces) {


        return braces.stream().map(element->{
            Stack<String> b1 = new Stack();  // for {}
            Stack<String> b2 = new Stack(); // for []
            Stack<String> b3 = new Stack(); // for ()

            final int len =element.length();

            for(int i=0; i<len-1; i++){
                if(element.substring(i,i+1).equals("[") || element.substring(i,i+1).equals("(") || element.substring(i,i+1).equals("{")){
                    b1.push(element.substring(i,i+1));
                    continue;
                }
                if(!b3.isEmpty() && b3.peek().equals("(")){
                    if(!element.substring(i,i+1).equals(")")){
                        return "NO";
                    }
                }
                if(!b2.isEmpty() && b2.peek().equals("[")){
                    if(!element.substring(i,i+1).equals("]")){
                        return "NO";
                    }
                }


                if(!b1.isEmpty()&& element.substring(i,i+1).equals("}") ){
                    b1.pop();
                    continue;
                }
                if(!b2.isEmpty()&& element.substring(i,i+1).equals("]") ){
                    b2.pop();
                    continue;
                }
                if(!b3.isEmpty()&& element.substring(i,i+1).equals(")")){
                    b3.pop();
                    continue;
                }



            }
            return b1.isEmpty() && b2.isEmpty() && b3.isEmpty()?"YES":"NO";
        }).collect(Collectors.toList());


    }

}
public class Solution {
    public static void main(String[] args) throws IOException {
        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(System.in));
        BufferedWriter bufferedWriter = new BufferedWriter(new FileWriter(System.getenv("OUTPUT_PATH")));

        int bracesCount = Integer.parseInt(bufferedReader.readLine().trim());

        List<String> braces = IntStream.range(0, bracesCount).mapToObj(i -> {
                try {
                    return bufferedReader.readLine();
                } catch (IOException ex) {
                    throw new RuntimeException(ex);
                }
            })
            .collect(toList());

        List<String> result = Result.matchingBraces(braces);

        bufferedWriter.write(
            result.stream()
                .collect(joining("\n"))
                + "\n"
        );

        bufferedReader.close();
        bufferedWriter.close();
    }
}
