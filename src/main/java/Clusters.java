import java.util.*;
import java.util.concurrent.ThreadLocalRandom;
import java.util.stream.Collectors;

public class Clusters {

    private final String path = getClass().getClassLoader().getResource("ED_cw3.csv").toString().substring(6);
    private boolean shifted;

    public static void main(String[] args) {
        Clusters clusters = new Clusters();

        clusters.initialize(clusters.path, 9);
    }

    public void initialize(String path, Integer column) {
        List<Point> points = FileUtil.getPoints(path, column);
        List<Point> centersOfGravity = new ArrayList<>();

        Double maximumX = points.stream().max(Comparator.comparing(Point::getX)).get().getX();
        Double maximumY = points.stream().max(Comparator.comparing(Point::getX)).get().getY();
        for (int i = 0; i < 4; i++) {
            Point point = new Point();
            Integer id = ThreadLocalRandom.current().nextInt();
            Integer x = ThreadLocalRandom.current().nextInt(0, maximumX.intValue());
            Integer y = ThreadLocalRandom.current().nextInt(0, maximumY.intValue());
            while (existsPointForGivenCoords(centersOfGravity, x, y) || existsById(centersOfGravity, id)) {
                x = ThreadLocalRandom.current().nextInt(0, maximumX.intValue());
                y = ThreadLocalRandom.current().nextInt(0, maximumY.intValue());
            }
            point.setX(Double.valueOf(x));
            point.setY(Double.valueOf(y));
            point.setId(id);
            centersOfGravity.add(point);
        }

        System.out.println("Losowo wybrane środki ciężkości to: ");
        System.out.println(centersOfGravity);
        System.out.println();

        do {
            Map<Point, List<Point>> pointListMap = calculateCenterOfGravityToPointMap(points, centersOfGravity);
            calculateNewCenterOfGravityPosition(pointListMap);
            Map<Point, List<Point>> temp = calculateCenterOfGravityToPointMap(points, centersOfGravity);
            shifted = checkIfAnyPointShifted(pointListMap, temp);
            if (shifted) {
                System.out.println("Po przeliczeniu nowych środków ciężkości zmieniono pozycję jednego lub więcej elementów\n");
            } else {
                System.out.println("Po przeliczeniu nowych środków ciężkości nie zmieniono pozycji elementów \n");
            }
        } while (shifted);
    }

    private boolean checkIfAnyPointShifted(Map<Point, List<Point>> original, Map<Point, List<Point>> next) {
        boolean numberOfKeys = original.keySet().size() != next.keySet().size();
        Set<Integer> originalSizes = new HashSet<>();
        Set<Integer> nextSizes = new HashSet<>();
        original.forEach((k, v) -> {
            originalSizes.add(v.size());
        });
        next.forEach((k, v) -> {
            nextSizes.add(v.size());
        });

        boolean numberOfSizes = !originalSizes.equals(nextSizes);

        //System.out.println("number of keys: "+numberOfKeys);
        //System.out.println("original: "+original.keySet().size()+" next: "+ next.keySet().size());
        //System.out.println("number of sizes: "+numberOfSizes);
        //System.out.println("original: "+originalSizes + " next: "+nextSizes);
        return numberOfKeys || numberOfSizes;
    }

    private boolean existsPointForGivenCoords(List<Point> points, Integer x, Integer y) {
        return points.stream().anyMatch(p -> p.getX().equals(x) && p.getY().equals(y));
    }

    private boolean existsById(List<Point> points, Integer id) {
        return points.stream().map(p -> p.getId()).collect(Collectors.toList()).contains(id);
    }

    private void calculateNewCenterOfGravityPosition(Map<Point, List<Point>> centerOfGravityToPointMap) {
        centerOfGravityToPointMap.forEach((k, v) -> {
            Double newX = v.stream().map(Point::getX).mapToDouble(d -> d).sum() / v.stream().map(Point::getX).count();
            Double newY = v.stream().map(Point::getY).mapToDouble(d -> d).sum() / v.stream().map(Point::getY).count();
            k.setX(newX);
            k.setY(newY);
        });
    }

    private Map<Point, List<Point>> calculateCenterOfGravityToPointMap(List<Point> points, List<Point> centersOfGravity) {
        for (Point point : points) {
            Map<Point, Double> distanceMap = new HashMap<>();
            for (Point centers : centersOfGravity) {
                Double distance = Math.abs(point.getX() - centers.getX()) + Math.abs(point.getY() - centers.getY());
                distanceMap.put(centers, distance);
            }
            point.setClosestCluster(distanceMap.entrySet().stream().min(Comparator.comparing(Map.Entry::getValue)).get().getKey());
            point.setDistanceToClosestCluster(distanceMap.entrySet().stream().min(Comparator.comparing(Map.Entry::getValue)).get().getValue());
        }

        Map<Point, List<Point>> centerOfGravityToPointMap = points.stream().collect(Collectors.groupingBy(
                Point::getClosestCluster
        ));

        centerOfGravityToPointMap.forEach((k, v) -> {
            System.out.println("środek ciężkości : " + k);
            System.out.println(v);
        });

        return centerOfGravityToPointMap;
    }
}
