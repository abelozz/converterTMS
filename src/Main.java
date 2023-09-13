import java.io.File;

public class Main {
    public static void main(String[] args) {
        String nameMBTILES, pathFolder;
        pathFolder = "C:\\Users\\user\\Desktop\\Test_CONVERTER_TMS\\n55e037";
      /*  nameMBTILES = "n55e037.mbtiles";
        DataBaseHelper db = new DataBaseHelper(pathFolder,nameMBTILES);
        MetadataTile metadataTile = db.getZoom();
       System.out.println(metadataTile.getMaxZoom());
        int i = Integer.parseInt(metadataTile.getMinZoom());
        int end = Integer.parseInt(metadataTile.getMaxZoom());

         while (i<=end)
        {
            db.convertToWebpFormat(String.valueOf(i));
            i++;
        }*/

        File directory = new File("C:/Users/user/Desktop/Новая папка (6)");
        FileCounter.convertJpgToWebp(directory,FileCounter.countJpgFiles(directory));

    }
}