package readability;

import java.io.FileInputStream;
import java.io.IOException;

public class Main {

    public static void main(String[] args) {

        try (FileInputStream inputStream = new FileInputStream(args[0])) {
            new Application(new TextStatistics(inputStream)).run();
        } catch (IOException e) {
            System.out.println("File not found");
        }
    }
}
