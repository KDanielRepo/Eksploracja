import lombok.Data;

@Data
public class Point {
    private Integer id;
    private Double x;
    private Double y;
    private Point closestCluster;
    private Double distanceToClosestCluster;
    private Double distanceFromStart;

    @Override
    public String toString() {
        return "x="+x+", y="+y;
    }
}
