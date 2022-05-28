import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Main {
    public static void main(String[] args) {
        Pattern pattern = Pattern.compile("^~~([0-9]|1[0-9]|20)$");
        for (int i = 0; i < 30; i++) {
            Matcher matcher = pattern.matcher("~~" + i);
            boolean matchFound = matcher.find();
            if (matchFound) {
                System.out.println(i + ") Match found");
            } else {
                System.out.println("Match not found");
            }
        }
    }
}