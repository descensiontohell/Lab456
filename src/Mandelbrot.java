import java.awt.geom.Rectangle2D;

public class Mandelbrot extends FractalGenerator {

    public void getInitialRange(Rectangle2D.Double range) {
        range.x = -2;
        range.y = -1.5;
        range.width = 3;
        range.height = 3;
    }

    public static final int MAX_ITERATIONS = 2000;

    public int numIterations(double x, double y){ // реализует итеративную функцию
        int iteration = 0;
        double z = 0;
        double zcomp = 0;
        while (iteration < MAX_ITERATIONS && z * z + zcomp * zcomp < 4){ // если максимальное количество итераций не достигнуто
            double zNew = z * z - zcomp * zcomp + x;
            double zcompNew = 2 * z * zcomp + y;
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
        return "Mandelbrot";
    }
}
