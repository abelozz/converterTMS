import com.luciad.imageio.webp.WebPWriteParam;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.*;
import java.sql.*;
import java.time.LocalTime;

public class DataBaseHelper {
    private static String pathFolder, nameMBTILES;
    private static int cout =0;
    private static int all=0;
    public DataBaseHelper(String path, String name) {
        pathFolder = path;
        nameMBTILES = name;
    }

    public MetadataTile getZoom() {
        MetadataTile metadataTile = new MetadataTile();
        try (Connection conn = DriverManager.getConnection("jdbc:sqlite:" + pathFolder + "\\" + nameMBTILES)) {
            // Создание запроса
            Statement statement = conn.createStatement();
            String query = "SELECT * FROM metadata";
            ResultSet resultSet = statement.executeQuery(query);

            // Чтение данных из результата
            while (resultSet.next()) {
                // Получение значений полей
                String names = resultSet.getString("name"); // Замените "id" на имя поля с ID
                String value = resultSet.getString("value"); // Замените "name" на имя поля с именем
                if (names.equals("minzoom")) {
                    metadataTile.setMinZoom(resultSet.getString("value"));
                } else if (names.equals("maxzoom")) {
                    metadataTile.setMaxZoom(resultSet.getString("value"));
                }
            }

            // Закрытие результата и запроса
            resultSet.close();
            statement.close();
        } catch (SQLException e) {
            System.out.println("Ошибка при работе с базой данных: " + e.getMessage());
        }
        getCout();
        return metadataTile;
    }
    private void getCout()
    {
        try (Connection conn = DriverManager.getConnection("jdbc:sqlite:" + pathFolder + "\\" + nameMBTILES)) {
            Statement statement = conn.createStatement();
            String query = "SELECT COUNT(DISTINCT tile_column) FROM tiles";
            ResultSet resultSet = statement.executeQuery(query);

            if (resultSet.next()) {
                cout = resultSet.getInt(1);
            }

            resultSet.close();
            statement.close();
        } catch (SQLException e) {
            System.out.println("Ошибка при работе с базой данных: " + e.getMessage());
        }
    }
    public void saveTile(String zoom) {
        try (Connection conn = DriverManager.getConnection("jdbc:sqlite:" + pathFolder + "\\" + nameMBTILES)) {
            Statement statement = conn.createStatement();
            String query = "SELECT * FROM tiles where zoom_level=" + zoom;
            ResultSet resultSet = statement.executeQuery(query);

            while (resultSet.next()) {
                String row = resultSet.getString("tile_row"); // Замените "id" на имя поля с ID
                String column = resultSet.getString("tile_column"); // Замените "name" на имя поля с именем
                createPath(zoom,column);

                InputStream inputStream = resultSet.getBinaryStream("tile_data");
                BufferedImage image = ImageIO.read(inputStream);
                File outputFile = new File(pathFolder+"\\"+zoom+"\\"+column+"\\"+row+".jpg");
                ImageIO.write(image, "jpg", outputFile);
                System.out.println("Zoom: "+zoom+" Тайл №: "+all+" из "+cout + " "+ LocalTime.now());
                all++;
            }
            resultSet.close();
            statement.close();
        } catch (SQLException e) {
            System.out.println("Ошибка при работе с базой данных: " + e.getMessage());
            all=0;
        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
    public void convertToWebpFormat(String zoom) {
        try (Connection conn = DriverManager.getConnection("jdbc:sqlite:" + pathFolder + "\\" + nameMBTILES)) {
            Statement statement = conn.createStatement();
            String query = "SELECT * FROM tiles WHERE zoom_level=" + zoom;
            ResultSet resultSet = statement.executeQuery(query);

            while (resultSet.next()) {
                String row = resultSet.getString("tile_row");
                String column = resultSet.getString("tile_column");

                InputStream inputStream = resultSet.getBinaryStream("tile_data");
                BufferedImage image = ImageIO.read(inputStream);

                // Создаем временный файл для сохранения webp изображения
                File tmpFile = File.createTempFile("temp", ".webp");

                // Сохраняем изображение в формате webp
                try (OutputStream os = new FileOutputStream(tmpFile)) {
                    ImageIO.write(image, "webp", os);
                }

                // Обновляем базу данных с новым webp изображением
                String updateQuery = "UPDATE tiles SET tile_data = ? WHERE zoom_level = ? AND tile_column = ? AND tile_row = ?";
                try (PreparedStatement updateStmt = conn.prepareStatement(updateQuery)) {
                    byte[] webpBytes;
                    try (InputStream webpInputStream = new FileInputStream(tmpFile)) {
                        webpBytes = webpInputStream.readAllBytes();
                    }
                    updateStmt.setBytes(1, webpBytes);
                    updateStmt.setString(2, zoom);
                    updateStmt.setString(3, column);
                    updateStmt.setString(4, row);
                    updateStmt.executeUpdate();
                }


                // Удаляем временный файл
                tmpFile.delete();

                System.out.println("Zoom: " + zoom + " Тайл №: " + all + " из " + cout + " " + LocalTime.now());
                all++;
            }
            resultSet.close();
            statement.close();
        } catch (SQLException e) {
            System.out.println("Ошибка при работе с базой данных: " + e.getMessage());
            all = 0;
        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }



    private void createPath(String zoom, String column)
    {
        File path1 = new File(pathFolder+"\\"+zoom);
        File path2 = new File(pathFolder+"\\"+zoom+"\\"+column);
        if (!path1.exists()) path1.mkdir();
        if (!path2.exists()) path2.mkdir();

    }
}
