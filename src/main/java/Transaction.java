import lombok.Data;

import java.util.List;

@Data
public class Transaction {
    private int id;
    private List<String> products;
}
