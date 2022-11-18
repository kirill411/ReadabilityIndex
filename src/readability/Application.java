package readability;

import java.util.Scanner;
import java.util.stream.Collectors;
import java.util.stream.Stream;

class Application {

    TextStatistics txt;

    Application(TextStatistics txt) {
        this.txt = txt;
    }

    void run() {
        txt.printStatistics();
        System.out.printf("Enter the score you want to calculate (%s, all)",
                Stream.of(ReadabilityIndex.values())
                        .map(ReadabilityIndex::name)
                        .collect(Collectors.joining(", ")));

        Scanner sc = new Scanner(System.in);
        String request = sc.next();

        if ("all".equals(request)) {
            double ageTotal = Stream.of(ReadabilityIndex.values())
                    .peek(i -> System.out.print(i.getAsString(txt) + '\n'))
                    .mapToInt(i -> i.getAge(i.getScore(txt))).average().orElse(0);
            System.out.printf("\nThis text should be understood in average by %.2f-year-olds.", ageTotal);
        } else {
            System.out.println(ReadabilityIndex.valueOf(request).getAsString(txt));
        }
    }
}
