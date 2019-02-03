package sps_p;

/**
 * Child parameter class. Parameter K is initialized in hard-coded.
 */
public class ParameterKij extends Parameter {
    ParameterKij(int num, int type, Swarm swarm) {
        super(num, type, swarm);
    }

    /**
     * Return the hard-coded parameter K which is for 2 types of particles.
     * @return Parameter K for 2 types of particles.
     */
    @Override
    double[][] init2x2() {
        double[][] params = {
                {0.8, 1.7},
                {0.5, 1.2}
        };
        return params;
    }

    /**
     * Return the hard-coded parameter K which is for 3 types of particles.
     * @return Parameter K for 3 types of particles.
     */
    @Override
    double[][] init3x3() {
        double[][] params = {
                {0.0, -0.7, 0.7},
                {0.7, 0.0, -0.7},
                {-0.7, 0.7, 0.0},
        };

        return params;
    }

    /**
     * Return the randomized parameter K which is depending on the number of |pType|.
     * @return Randomized parameter K.
     */
    @Override
    double[][] random() {
        double[][] params = new double[pType][pType];
        double tmp;
        for (int i = 0; i < pType; i++) {
            for (int j = 0; j < pType; j++) {
                tmp = -2.0 + Math.random() * 4.0;
                tmp *= 10;
                tmp = Math.floor(tmp);
                tmp /= 10;
                params[i][j] = tmp;
            }
        }
        return params;
    }
}
