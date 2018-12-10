import java.math.BigDecimal;


public class KaKmKp extends Parameter {
    private final BigDecimal a = new BigDecimal(0.7); // This value is defined in Kano's thesis(2017).
    private final BigDecimal p = new BigDecimal(0.9); // This value is defined in Kano's thesis(2017).
    private BigDecimal m;

    KaKmKp(int dimension) {
        super(dimension);
    }

    @Override
    BigDecimal[][] initTwoDim() {
        return new BigDecimal[0][];
    }

    @Override
    BigDecimal[][] initThreeDim() {
        return new BigDecimal[0][];
    }

    @Override
    BigDecimal[][] random() {
        return new BigDecimal[0][];
    }
}
