import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.*;
import org.apache.poi.xwpf.usermodel.XWPFDocument;

import java.awt.*;
import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.List;
import java.util.zip.GZIPInputStream;

public class Main {

    static int delivery;
    static int other;
    static int plus;
    static int mounting;
    static int slopes;
    static int interest;
    static double itemPrice;
    static double itemPriceDop;
    static double course;
    static String region;
    static boolean isStand;

    static String nameMeasure;
    //Путь к файлу
    static String ar;

    static LinkedHashMap<String, Measure> tmpHashMap;


    public static void main(String[] args) throws IOException {
        File openFile = null;

        //ar - путь к файлу
        for (String s : args) {
            ar = s;
        }

        //Если путь к файлу найден
        if(ar != null) {
            openFile = new File(ar);
            startProgram(openFile);
        }
    }

    public static void startProgram(File f) throws IOException {

        //Если открыли файл с помощью
        if(f != null) {
            java.lang.reflect.Type type = new TypeToken<LinkedHashMap<String, Measure>>() {}.getType();

            Gson gson = new Gson();
            String json = null;
            FileInputStream fin = null;
            fin = new FileInputStream(f);
            byte[] bytes = new byte[fin.available()];
            fin.read(bytes);

            //Мы получиили файл в виде массива байт, и теперь записываем его в строку что бы расшифровать
            //В троке хранится сжатый hash, опять же в виде byte
            json = new String(bytes);

            //Записываем массив байт в Byte
            byte[] tmpByte = gson.fromJson(json, byte[].class);

            //Расшифровываем сжатый массив байт и записываем в строку в виде расжатого hashmap
            json = new String(uncompress(tmpByte), StandardCharsets.UTF_8);

            //Сохраняем hashMap
            tmpHashMap = gson.fromJson(json, type);

            for(String s : tmpHashMap.keySet()) {
                nameMeasure = s;
            }

            delivery = tmpHashMap.get(nameMeasure).getDelivery();
            other = tmpHashMap.get(nameMeasure).getOther();
            plus = tmpHashMap.get(nameMeasure).getPlus();
            mounting = tmpHashMap.get(nameMeasure).getMounting();
            slopes = tmpHashMap.get(nameMeasure).getSlopes();
            interest = tmpHashMap.get(nameMeasure).getProdInterest();
            itemPrice = Math.ceil(tmpHashMap.get(nameMeasure).getProdItemPrice());
            itemPriceDop = Math.ceil(tmpHashMap.get(nameMeasure).getProdItemPriceDop());
            region = tmpHashMap.get(nameMeasure).getRegion() ? "Регион" : "Минск";
            course = tmpHashMap.get(nameMeasure).getCourse();
            isStand = tmpHashMap.get(nameMeasure).isStand();

            //Сохраняем в excel
            try {
                writeIntoExcel(delivery, itemPrice, itemPriceDop, interest, slopes, mounting, other, plus, nameMeasure,
                        tmpHashMap.get(nameMeasure).getListItem(),tmpHashMap.get(nameMeasure).getItemInfo(),
                        tmpHashMap.get(nameMeasure).getProdItemPriceLst(),
                        tmpHashMap.get(nameMeasure).getProdMounting(), tmpHashMap.get(nameMeasure).getProdSlopes(),
                        region, tmpHashMap.get(nameMeasure).getVersion(), course, isStand);
            }catch (FileNotFoundException ignored) {
            }

            //Открываем файл
            Desktop.getDesktop().open(new File(System.getProperty("user.home") + "\\Desktop\\MScanner\\" + nameMeasure + ".xlsx"));
        }
    }

    // Расшифровывает сжатую строку из файла  и возвращает массив byte со строкой gson(В ней hashMap)
    public static byte[] uncompress(byte[] data) throws IOException {
        if (data == null || data.length == 0) {
            return data;
        }
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        ByteArrayInputStream in = new ByteArrayInputStream(data);
        GZIPInputStream gunzip = new GZIPInputStream(in);
        byte[] buffer = new byte[256];
        int n;
        while ((n = gunzip.read(buffer)) >= 0) {
            out.write(buffer, 0, n);
        }
        gunzip.close();
        in.close();
        return out.toByteArray();
    }

    //СТРОКИ И ЯЧЕЙКИ НУМЕРУЮТСЯ С 0------!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!
    //Сохраняем в excel
    public static void writeIntoExcel(int delivery,double itemPrice,double itemPriceDop,int interest,int slopes,int mounting, int other, int plus,
                                      String nameMeasure, List<String> itemNameLst,
                                      List<String> itemInfoLst,List<Double> itemPriceLst,
                                      List<Integer> prodMountingLst, List<Integer> prodSlopesLst,
                                      String region, String version, double course, boolean isStand) throws IOException {

        double standPrice = 0;

        //Создаем папку на рабочем столе, если она отсутсвует
        File f = new File(System.getProperty("user.home") + "\\Desktop\\MScanner");
        f.mkdir();

        //Указываем путь для сохранения файла
        String outPath = System.getProperty("user.home") + "\\Desktop\\MScanner\\" + nameMeasure + ".xlsx";

        InputStream fis = Main.class.getResourceAsStream("file/MScanner.xlsx");

        XSSFWorkbook book = new XSSFWorkbook (fis);

        XSSFSheet sheet = book.getSheetAt(0);

        Row row = sheet.getRow(2);

        //ПЛЮС
        Cell cell = row.getCell(2);
        cell.setCellValue(plus);

        //Прочее
        cell = row.getCell(3);
        cell.setCellValue(other + itemPriceDop);

        //Монтаж
        cell = row.getCell(4);
        cell.setCellValue(mounting);

        //Отскосы
        cell = row.getCell(5);
        cell.setCellValue(slopes);

        //Интерес
        cell = row.getCell(6);
        cell.setCellValue(interest);

        //Доставка
        cell = row.getCell(8);
        cell.setCellValue(delivery);

        //Курс
        cell = row.getCell(9);
        cell.setCellValue(course);

        //Версия приложения
        cell = row.getCell(10);
        cell.setCellValue(version);

        XSSFDrawing patr = sheet.createDrawingPatriarch();
        XSSFComment comment = null;

        //Заполняем лсит изделий
        for (int i = 0;i < itemNameLst.size();i++){
            row = sheet.getRow(i + 3);

            //Наименование изделий
            cell = row.getCell(16);
            cell.setCellValue((i+1) + ". " + itemNameLst.get(i));

            //Если есть цена изделий
            if(itemPriceLst.get(i) != 0) {
                cell = row.getCell(22);
                cell.setCellValue(itemPriceLst.get(i));
            }
            //Если есть цена монтажа
            else if (prodMountingLst.get(i) != 0){
                cell = row.getCell(23);
                cell.setCellValue(prodMountingLst.get(i));
            }
            //Если есть цена откосов
            else {
                cell = row.getCell(23);
                cell.setCellValue(prodSlopesLst.get(i));
            }

            //Если есть информация об изделии, то присваиваем комментарий
            //i - позиция в списке, (+3 - на две строки опускаем вниз)
            if(!itemInfoLst.get(i).equals("")) {

                //Берем первую строку описания и вычиняем из нее цену подставочника
                String[] str = itemInfoLst.get(i).split("\n");
                standPrice += getStandPrice(str[0]);

                comment = patr.createCellComment(new XSSFClientAnchor(0, 0, 0, 0, 16, (i + 3), 20, (i+3+15)));
                comment.setString(new XSSFRichTextString(itemInfoLst.get(i)));
            }

        }

        row = sheet.getRow(2);
        //Стоимость изделий
        cell = row.getCell(7);
        cell.setCellValue(itemPrice - standPrice);

        //Если что то было отдельно в прочее, нужно добавить
        row = sheet.getRow(itemNameLst.size() + 3);

        //Наименование изделий (ПРОЧЕЕ, что-бы видеть, что конкретно ввел замерщик и различать стоимость нащельников пвх и бруса)
        cell = row.getCell(16);
        cell.setCellValue("Прочее:");

        //Цена
        cell = row.getCell(22);
        cell.setCellValue(other);

//================================================================
        //Заполняем стоимость подставочного профиля
        row = sheet.getRow(itemNameLst.size() + 4);

        //Наименование изделий
        cell = row.getCell(16);
        cell.setCellValue("Подставочный профиль:");

        //Цена
        cell = row.getCell(22);
        cell.setCellValue(isStand ? standPrice : 0);
//================================================================


        //Обновляем все
        XSSFFormulaEvaluator.evaluateAllFormulaCells(book);

        // Записываем всё в файл
        book.write(new FileOutputStream(outPath));
        book.close();
    }


    //Вычленяет стоимость подставочника
    public static double getStandPrice(String e) {
        String result  = "";

        if(e.contains("УНЗ_№_") && !e.contains("G")) {
            result = e.replace("УНЗ_№_", "");
            result = result.replace('V', '.');
        }
        else {
            return 0;
        }

        return Double.parseDouble(result);
    }

}