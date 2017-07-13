package lesson170705;

import java.util.Scanner;

/**
 * Created by andrew on 13.07.17.
 */
public class ScannerExample {

    public static void main(String[] args) {

        System.out.println("Введите ваше имя: ");

        Scanner scanner = new Scanner(System.in);

        String name = scanner.nextLine();

        System.out.println("Привет "+ name);

        while (scanner.hasNextLine()){
            String line = scanner.nextLine();
            System.out.println(line);
            if (line.equals("bye")){
                break;
            }
        }
    }

}
