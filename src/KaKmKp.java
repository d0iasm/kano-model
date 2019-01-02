import java.math.BigDecimal;

/**
 * Parameters in this class are defined in Kano's thesis(Mathematical Analysis for Non-reciprocal-interaction-based Model of Collective Behavior, 2017).
 */
public class KaKmKp extends Parameter {
    private final BigDecimal a = new BigDecimal(0.7);
    private final BigDecimal p = new BigDecimal(0.9);
    private BigDecimal m;

    /**
     *  X = (N^(-1) * Σ(N, i=1)|ri - rg|)^(-1)
     *  rg = N^(-1) * Σ(N, i=1)ri
     *  rg denotes the position of the center of gravity.
     *  X converges to zero when at least one of the particles moves an infinite distance from the center of gravity.
     */
    private BigDecimal x;
    /**
     * V = N^(-1) * Σ(N, i=1)|ri(dot)-rg(dot)|
     * V converges to zero when the relative velocities of all particles with respect to the center of gravity converge to zero.
     */
    private BigDecimal v;
    /**
     * The position of the center of gravity.
     * rg = N^(-1) * Σ(N, i=1)ri
     */
    private BigDecimal gravity;

    KaKmKp(int dimension) {
        super(dimension);
    }

    @Override
    double[][] initTwoDim() {
        // TODO: Fix this parameter because it is workaround.
        return new double[][] {
                {0.8, 1.7},
                {0.5, 1.2}
        };
    }

    @Override
    double[][] initThreeDim() {
        return new double[0][];
    }

    @Override
    double[][] random() {
        return new double[0][];
    }
}
