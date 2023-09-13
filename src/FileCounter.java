import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;

public class FileCounter {
    private static Integer actualNumber=0;
    public static int countJpgFiles(File directory) {
        int count = 0;

        // Получаем список файлов и подкаталогов в заданном каталоге
        File[] files = directory.listFiles();

        if (files != null) {
            for (File file : files) {
                if (file.isDirectory()) {
                    // Рекурсивно вызываем метод для каждого подкаталога
                    count += countJpgFiles(file);
                } else {
                    // Если файл имеет расширение .jpg, увеличиваем счетчик
                    if (file.getName().toLowerCase().endsWith(".jpg")) {
                        count++;
                    }
                }
            }
        }

        return count;
    }
    public static void convertJpgToWebp(File directory, Integer countFileJPG) {

        // Получаем список файлов и подкаталогов в заданном каталоге
        File[] files = directory.listFiles();

        if (files != null) {
            for (File file : files) {
                if (file.isDirectory()) {
                    // Рекурсивно вызываем метод для каждого подкаталога
                    convertJpgToWebp(file,countFileJPG);
                } else if (file.isFile() && file.getName().toLowerCase().endsWith(".jpg")) {
                    try {
                        // Чтение изображения JPG
                        BufferedImage image = ImageIO.read(file);

                        // Создаем новый файл с расширением WebP
                        File webpFile = new File(file.getAbsolutePath().replace(".jpg",".webp"));

                        // Конвертация и сохранение изображения в формате WebP
                        try (OutputStream os = new FileOutputStream(webpFile)) {
                            ImageIO.write(image, "webp", os);
                        }
                        actualNumber = actualNumber+1;
                        System.out.println("Конвертация "+actualNumber+" из "+countFileJPG);
                        file.delete();
                    } catch (IOException e) {
                        System.out.println("Ошибка при конвертации файла " + file.getName() + ": " + e.getMessage());
                    }
                }
            }
        }
    }
}
