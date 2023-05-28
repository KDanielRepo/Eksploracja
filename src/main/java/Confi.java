import com.google.common.collect.Sets;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Function;
import java.util.stream.Collectors;

public class Confi {

    private final String p1 = getClass().getClassLoader().getResource("U-11725.csv").toString().substring(6);
    private final String p2 = getClass().getClassLoader().getResource("74673.csv").toString().substring(6);
    private final String p3 = getClass().getClassLoader().getResource("U-8470.csv").toString().substring(6);
    private final String p4 = getClass().getClassLoader().getResource("U-11379.csv").toString().substring(6);
    private final String p5 = getClass().getClassLoader().getResource("U-11796.csv").toString().substring(6);
    private final String p6 = getClass().getClassLoader().getResource("U-17558.csv").toString().substring(6);
    private final String p7 = getClass().getClassLoader().getResource("U-17559.csv").toString().substring(6);
    private final String p8 = getClass().getClassLoader().getResource("test.csv").toString().substring(6);


    public static void main(String[] args) {
        Confi confi = new Confi();
        System.out.println("plik : U-11725.csv");
        confi.calculate(confi.p1);
        System.out.println("plik : 74673.csv");
        confi.calculate(confi.p2);
        System.out.println("plik : U-8470.csv");
        confi.calculate(confi.p3);
        System.out.println("plik : U-11379.csv");
        confi.calculate(confi.p4);
        System.out.println("plik : U-11796.csv");
        confi.calculate(confi.p5);
        System.out.println("plik : U-17558.csv");
        confi.calculate(confi.p6);
        System.out.println("plik : U-17559.csv");
        confi.calculate(confi.p7);
        System.out.println("plik : test.csv");
        confi.calculate(confi.p8);
    }

    private void calculate(String path) {
        List<Transaction> transactions = FileUtil.getTransactions(path);

        Map<Integer, Double> singleElementMap = transactions
                .stream()
                .flatMap(t -> t.getProducts().stream())
                .map(Integer::valueOf)
                .collect(Collectors.groupingBy(
                        Function.identity(),
                        Collectors.collectingAndThen(
                                Collectors.counting(),
                                k -> BigDecimal.valueOf(k).setScale(2)
                                        .divide(BigDecimal.valueOf(transactions.size()), RoundingMode.HALF_EVEN)
                                        .doubleValue()
                        )));
        Set<Set<Integer>> doubleCombinations = Sets.combinations(singleElementMap.keySet(), 2);

        Map<Set<Integer>, Double> twoElementMap = new HashMap<>();
        doubleCombinations.forEach(combination -> {
            AtomicInteger counter = new AtomicInteger();
            transactions.forEach(t -> {
                Integer i = (Integer) combination.toArray()[0];
                Integer i2 = (Integer) combination.toArray()[1];
                if (t.getProducts().contains(i.toString()) && t.getProducts().contains(i2.toString())) {
                    counter.getAndIncrement();
                }
            });
            Double value = Double.valueOf(counter.get()) / transactions.size();
            twoElementMap.put(combination, Math.floor(value * 100) / 100);
        });

        //System.out.println("ilość zbiorów dwu-elementowych z większym procentem niż 0.1: " + twoElementMap.entrySet().stream().filter(k -> k.getValue() >= 0.1).count());

        Set<Set<Integer>> sets = new HashSet<>();
        twoElementMap.entrySet().stream().filter(k -> k.getValue() >= 0.1).map(k -> k.getKey()).forEach(k -> {
            Integer first = (Integer) k.toArray()[0];
            Integer second = (Integer) k.toArray()[1];
            twoElementMap.entrySet().stream().filter(k2 -> k2.getValue() >= 0.1).map(k2 -> k2.getKey()).forEach(k2 -> {
                Integer first2 = (Integer) k2.toArray()[0];
                Integer second2 = (Integer) k2.toArray()[1];
                if (first.equals(first2) && !second.equals(second2)) {
                    Set<Integer> set = new HashSet<>();
                    set.add(first);
                    set.add(second);
                    set.add(second2);
                    if (!validateSet(sets, set)) {
                        sets.add(set);
                    }
                }
            });
        });

        Map<Set<Integer>, Double> threeElementMap = new HashMap<>();
        sets.forEach(combination -> {
            AtomicInteger counter = new AtomicInteger();
            transactions.forEach(t -> {
                Integer i = (Integer) combination.toArray()[0];
                Integer i2 = (Integer) combination.toArray()[1];
                Integer i3 = (Integer) combination.toArray()[2];
                if (t.getProducts().contains(i.toString()) && t.getProducts().contains(i2.toString()) && t.getProducts().contains(i3.toString())) {
                    counter.getAndIncrement();
                }
            });
            Double value = Double.valueOf(counter.get()) / transactions.size();
            threeElementMap.put(combination, Math.floor(value * 100) / 100);
        });
        System.out.println("ilość zbiorów trzy-elementowych z większym procentem niż 0.1: " + threeElementMap.entrySet().stream().filter(setDoubleEntry -> setDoubleEntry.getValue() >= 0.1).count());
        System.out.println("zbiór trzy-elementowy z najwyższym procentem to: " + threeElementMap.entrySet().stream().max(Comparator.comparing(Map.Entry::getValue)).get());

        calculateConfidence(singleElementMap, twoElementMap, threeElementMap.entrySet().stream().max(Comparator.comparing(Map.Entry::getValue)).get());
    }

    private boolean validateSet(Set<Set<Integer>> sets, Set<Integer> set) {
        for (Set<Integer> integers : sets) {
            if (integers.containsAll(set)) {
                return true;
            }
        }
        return false;
    }

    private void calculateConfidence(Map<Integer, Double> singleElementMap,
                                     Map<Set<Integer>, Double> twoElementMap,
                                     Map.Entry<Set<Integer>, Double> toCheck) {

        toCheck.getKey().forEach(key -> {
            Double value = toCheck.getValue() / singleElementMap.get(key);
            Set<Integer> setWithoutKey = new HashSet<>(toCheck.getKey());
            setWithoutKey.remove(key);
            System.out.println("CONF(" + key + " -> " + setWithoutKey + ") = " + new DecimalFormat("#.##").format(value * 100));
        });
        Set<Set<Integer>> combinations = Sets.combinations(toCheck.getKey(), 2);
        combinations.forEach(c -> {
            Double v1 = twoElementMap.get(c);
            Double v2 = toCheck.getValue();
            Double v3 = v2 / v1;

            Set<Integer> setWithoutKey = new HashSet<>(toCheck.getKey());
            c.forEach(setWithoutKey::remove);
            System.out.println("CONF(" + c + " -> " + setWithoutKey + ") = " + new DecimalFormat("#.##").format(v3 * 100));
        });
    }
}