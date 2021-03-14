import java.awt.geom.Rectangle2D;

public class BurningShip extends FractalGenerator{

    public static final int MAX_ITERATIONS = 2000;

    public void getInitialRange(Rectangle2D.Double range){ // задает начальные координаты
        range.x = -2;
        range.y = -2.5;
        range.width = 4;
        range.height = 4;
    }

    public int numIterations(double x, double y){ // реализует итеративную функцию
        int iteration = 0;
        double z = 0;
        double zcomp = 0;
        while (iteration < MAX_ITERATIONS && z * z + zcomp * zcomp < 4){
            double zNew = z * z - zcomp * zcomp + x;
            double zcompNew = 2 * Math.abs(z) * Math.abs(zcomp) + y;
            z = zNew;
            zcomp = zcompNew;
            iteration += 1;
        }
        if (iteration == MAX_ITERATIONS){
            return -1;
        }
        return iteration;
    }
    public String toString(){
        return "Burning Ship";
    }
}