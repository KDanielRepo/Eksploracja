import com.opencsv.CSVParserBuilder;
import com.opencsv.CSVReader;
import com.opencsv.CSVReaderBuilder;

import java.io.File;
import java.io.Reader;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.util.concurrent.ThreadLocalRandom;
import java.util.stream.Collectors;

public class FileUtil {

    public static List<Transaction> getTransactions(String path){
        List<Transaction> transactions = new ArrayList<>();
        try{
            Reader reader = Files.newBufferedReader(Paths.get(path));
            CSVReader csvReader = new CSVReaderBuilder(reader).withCSVParser(
                    new CSVParserBuilder()
                            .withSeparator(';')
                            .build()
            ).build();

            String[] nextRecord = csvReader.readNext();
            while ((nextRecord = csvReader.readNext()) != null) {
                Transaction transaction = new Transaction();
                transaction.setId(Integer.valueOf(nextRecord[0]));
                List<String> products = new ArrayList<>();
                for (int i = 1; i < 9; i++) {
                    products.add(nextRecord[i]);
                }
                transaction.setProducts(products.stream().distinct().collect(Collectors.toList()));
                /*transaction.setProduct_1(Integer.valueOf(nextRecord[1]));
                transaction.setProduct_2(Integer.valueOf(nextRecord[2]));
                transaction.setProduct_3(Integer.valueOf(nextRecord[3]));
                transaction.setProduct_4(Integer.valueOf(nextRecord[4]));
                transaction.setProduct_5(Integer.valueOf(nextRecord[5]));
                transaction.setProduct_6(Integer.valueOf(nextRecord[6]));
                transaction.setProduct_7(Integer.valueOf(nextRecord[7]));
                transaction.setProduct_8(Integer.valueOf(nextRecord[8]));*/
                transactions.add(transaction);
            }
        }catch (Exception e){
            e.printStackTrace();
        }

        return transactions;
    }

    public static List<Point> getPoints(String path, Integer column){
        List<Point> points = new ArrayList<>();
        try {
            Reader reader = Files.newBufferedReader(Paths.get(path));
            CSVReader csvReader = new CSVReaderBuilder(reader).withCSVParser(
                    new CSVParserBuilder()
                            .withSeparator(';')
                            .build()
            ).build();

            String[] nextRecord;
            for (int i = 0; i < 100; i++) {
                if (i < 50) {
                    Point point = new Point();
                    Integer id = ThreadLocalRandom.current().nextInt();
                    while(points.stream().map(p->p.getId()).collect(Collectors.toList()).contains(id)){
                        id = ThreadLocalRandom.current().nextInt();
                    }
                    nextRecord = csvReader.readNext();
                    point.setX(Double.valueOf(nextRecord[column]));
                    point.setId(id);
                    points.add(point);
                } else {
                    nextRecord = csvReader.readNext();
                    points.get(i - 50).setY(Double.valueOf(nextRecord[column]));
                }
            }
        }catch (Exception e){
            e.printStackTrace();
        }
        return points;
    }
}
