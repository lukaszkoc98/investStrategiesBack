package com.prediction;

import com.models.Prediction;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.*;
import java.lang.reflect.Array;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.util.*;

public class Predictor {

    public static ArrayList<Prediction> predict(String metal) throws IOException, ParseException {
        ProcessBuilder builder = new ProcessBuilder(
                "cmd.exe", "/c", "python src\\main\\resources\\predict.py " + metal);
        builder.redirectErrorStream(true);
        Process p = builder.start();
        BufferedReader r = new BufferedReader(new InputStreamReader(p.getInputStream()));
        String line;
        while (true) {
            line = r.readLine();
            if (line == null) { break; }
            System.out.println(line);
        }

        String filePath = new File("").getAbsolutePath();
        filePath = filePath.concat("\\src\\main\\resources\\" + metal + ".xlsx");
        FileInputStream file = new FileInputStream(new File(filePath));
        Workbook workbook = new XSSFWorkbook(file);
        Sheet sheet = workbook.getSheetAt(0);

        Map<Integer, List<String>> data = new HashMap<>();
        ArrayList<Prediction> predictions = new ArrayList<>();
        int i = 0;
        for (Row row : sheet) {
            data.put(i, new ArrayList<String>());
            for (Cell cell : row) {
                switch (cell.getCellTypeEnum()) {
                    case STRING:
                        data.get(new Integer(i)).add(cell.getRichStringCellValue().getString());
                        break;
                    case NUMERIC:
                        if (DateUtil.isCellDateFormatted(cell)) {
                            data.get(i).add(cell.getDateCellValue() + "");
                        } else {
                            data.get(i).add(cell.getNumericCellValue() + "");
                        }
                        break;
                    case BOOLEAN:
                        data.get(i).add(cell.getBooleanCellValue() + "");
                        break;
                    case FORMULA:
                        data.get(i).add(cell.getCellFormula() + "");
                        break;
                    default: data.get(new Integer(i)).add(" ");
                }
            }
            if (i > 0) {
                Prediction prediction = new Prediction(
                        stringToLocalDate(data.get(i).get(1)),
                        Double.parseDouble(data.get(i).get(2)),
                        Double.parseDouble(data.get(i).get(3)),
                        Double.parseDouble(data.get(i).get(4)),
                        stringToLocalDate(data.get(i).get(5))
                );
                predictions.add(prediction);
            }
            i++;
        }
        return predictions;
    }


    private static LocalDate stringToLocalDate(String date) throws ParseException {
        DateFormat dateFormat = new SimpleDateFormat(
                "EEE MMM dd HH:mm:ss zzz yyyy", Locale.US);
        Date dateAsDateType = dateFormat.parse(date);
        return new java.sql.Date(dateAsDateType.getTime()).toLocalDate();
    }
}
