import java.io.File;

public class Main {
    public static void main(String[] args) {


        File directory = new File("C:/Users/user/Desktop/Новая папка (6)");
        FileCounter.convertJpgToWebp(directory,FileCounter.countJpgFiles(directory));

    }
}