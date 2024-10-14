package test2;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.math.BigInteger;
import java.util.Scanner;

public class Test {


    @org.junit.jupiter.api.Test
    public void test(){
// 模拟输入
        String input = "42\n"; // 你希望输入的数字
        InputStream in = new ByteArrayInputStream(input.getBytes());
        System.setIn(in);


        Scanner scan = new Scanner(System.in);
        int a = scan.nextInt();
        System.out.println(a);

    }
    @org.junit.jupiter.api.Test
    public void test2(){
        String[] words = "He is a very very good boy, isn't he?".split("[\\s,'?]+");

        System.out.println(words.length);
        for (String word : words) {
            if (!word.isEmpty()) {
                System.out.println(word);
            }
        }

    }
    @org.junit.jupiter.api.Test
    public void test3(){
        Scanner scanner = new Scanner(System.in);

        // Read the two huge numbers as strings
        String num1 = scanner.nextLine();
        String num2 = scanner.nextLine();

        // Create BigInteger instances from the input strings
        BigInteger bigInt1 = new BigInteger(num1);
        BigInteger bigInt2 = new BigInteger(num2);

        // Perform addition and multiplication
        BigInteger sum = bigInt1.add(bigInt2);
        BigInteger product = bigInt1.multiply(bigInt2);

        // Output the results
        System.out.println(sum);
        System.out.println(product);

        scanner.close();

    }


}
