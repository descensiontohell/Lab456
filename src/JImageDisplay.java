import javax.swing.*;
import java.awt.image.*;
import java.awt.*;

public class JImageDisplay extends JComponent{

    private BufferedImage displayImage;

    public JImageDisplay(int width, int height){ // инициализирует окно с нужной высотой и длиной
        displayImage = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
        Dimension imageDimension = new Dimension(width, height);
        super.setPreferredSize(imageDimension);
    }

    public void clearImage(){ // устанавливает всем пикселям черный цвет
        int[] blankArray = new int[getWidth() * getHeight()];
        displayImage.setRGB(0, 0, getWidth(), getHeight(), blankArray, 0, 1);
    }

    public void drawPixel(int x, int y, int rgbColor){ // задает цвет определенному пикселю
        displayImage.setRGB(x, y, rgbColor);
    }

    public void paintComponent(Graphics g){
        super.paintComponent(g);
        g.drawImage(displayImage, 0, 0, displayImage.getWidth(), displayImage.getHeight(), null);
    }

    public BufferedImage getImage() { // возвращает изображение для сохранения
        return displayImage;
    }
}