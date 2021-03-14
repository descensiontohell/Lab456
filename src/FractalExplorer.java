import java.awt.*;
import javax.swing.*;
import java.awt.geom.Rectangle2D;
import java.awt.event.*;
import javax.swing.filechooser.*;
import java.awt.image.*;

public class FractalExplorer {

    private int displaySize;
    private int rowsRemaining;
    private JImageDisplay display;
    private FractalGenerator fractal;
    private Rectangle2D.Double range;
    private JComboBox chooseFractal;
    private JButton resetButton;
    private JButton saveButton;

    public FractalExplorer(int size) { // сохраняет размер окна и создает объекты
        displaySize = size;
        fractal = new Mandelbrot();
        range = new Rectangle2D.Double();
        fractal.getInitialRange(range);
        display = new JImageDisplay(displaySize, displaySize);
    }

    public void createAndShowGUI() { // задает графический интерфейс и выводит окно
        display.setLayout(new BorderLayout());
        JFrame frame = new JFrame("Fractal Explorer");
        frame.add(display, BorderLayout.CENTER);
        resetButton = new JButton("Сбросить"); // создает и размещает кнопку сброса
        ButtonHandler resetHandler = new ButtonHandler();
        resetButton.addActionListener(resetHandler);
        frame.add(resetButton, BorderLayout.SOUTH);
        MouseHandler click = new MouseHandler();
        display.addMouseListener(click);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE); // закрывает приложение при выходе
        chooseFractal = new JComboBox(); // добавляет окно выбора фрактала
        FractalGenerator mandelbrotFractal = new Mandelbrot();
        chooseFractal.addItem(mandelbrotFractal);
        FractalGenerator tricornFractal = new Tricorn();
        chooseFractal.addItem(tricornFractal);
        FractalGenerator burningShipFractal = new BurningShip();
        chooseFractal.addItem(burningShipFractal);
        ButtonHandler fractalChooser = new ButtonHandler();
        chooseFractal.addActionListener(fractalChooser);

        JPanel panel = new JPanel();
        JLabel label = new JLabel("Фрактал:");
        panel.add(label);
        panel.add(chooseFractal);
        frame.add(panel, BorderLayout.NORTH);

        saveButton = new JButton("Сохранить"); // добавляет кнопку сохранения
        JPanel myBottomPanel = new JPanel();
        myBottomPanel.add(saveButton);
        myBottomPanel.add(resetButton);
        frame.add(myBottomPanel, BorderLayout.SOUTH);
        ButtonHandler saveHandler = new ButtonHandler();
        saveButton.addActionListener(saveHandler);

        frame.pack();
        frame.setVisible(true);
        frame.setResizable(false);
    }


    /// drawFractal для 4 и 5 работы
    /* public void drawFractal(){ // рисует фрактал
        for (int x = 0; x < displaySize; x++){ // цикл проходящий через все пиксели
            for (int y = 0; y < displaySize; y++) {
                double xCoord = fractal.getCoord(range.x,
                        range.x + range.width, displaySize, x);
                double yCoord = fractal.getCoord(range.y,
                        range.y + range.height, displaySize, y);
                int iteration = fractal.numIterations(xCoord, yCoord);
                if (iteration == -1){
                    display.drawPixel(x, y, 0);
                }
                else{ // устанавливает цвет, если итерации не кончились
                    float code = 0.7f + (float) iteration / 200f;
                    int rgbColor = Color.HSBtoRGB(code, 1f, 1f);
                    display.drawPixel(x, y, rgbColor);
                }
            }
        }
        display.repaint();
    } */

    public void drawFractal(){
        enableUI(false); // отключает интерфейс на время выполнения
        rowsRemaining = displaySize; // устанавливает количество строк
        for (int x = 0; x < displaySize; x++){ // для каждой строки вызывает отрисовку
            FractalWorker drawRow = new FractalWorker(x);
            drawRow.execute();
        }
    }

    private void enableUI(boolean val){ // убирает или возвращает возможность взаимодействия с интерфейсом
        chooseFractal.setEnabled(val);
        resetButton.setEnabled(val);
        saveButton.setEnabled(val);
    }

    private class ButtonHandler implements ActionListener{ // обрабатывает действия
        public void actionPerformed(ActionEvent e) {
            String command = e.getActionCommand();
            if (e.getSource() instanceof JComboBox){ // для выбора фрактала
                JComboBox mySource = (JComboBox) e.getSource();
                fractal = (FractalGenerator) mySource.getSelectedItem();
                fractal.getInitialRange(range);
                drawFractal();
            }
            else if (command.equals("Сбросить")){ // для функции сброса
                fractal.getInitialRange(range);
                drawFractal();
            }
            else if (command.equals("Сохранить")){ // для функции сохранения
                JFileChooser chooser = new JFileChooser();
                FileFilter extensionFilter = new FileNameExtensionFilter("PNG Изображение", "png");
                chooser.setFileFilter(extensionFilter);
                chooser.setAcceptAllFileFilterUsed(false);
                int userSelection = chooser.showSaveDialog(display);
                if (userSelection == JFileChooser.APPROVE_OPTION){
                    java.io.File file = chooser.getSelectedFile();
                    String file_name = file.toString();
                    try{
                        BufferedImage displayImage = display.getImage();
                        javax.imageio.ImageIO.write(displayImage, "png", file);
                    }
                    catch (Exception exception){
                        JOptionPane.showMessageDialog(display,
                                exception.getMessage(), "Невозможно сохранить изображение",
                                JOptionPane.ERROR_MESSAGE);
                    }
                }
                else return;
            }
        }
    }

    private class MouseHandler extends MouseAdapter{
        public void mouseClicked(MouseEvent e) { // получает координаты клика и приближает изображение в нажатой точке
            int x = e.getX();
            double xCoord = fractal.getCoord(range.x, range.x + range.width, displaySize, x);
            int y = e.getY();
            double yCoord = fractal.getCoord(range.y, range.y + range.height, displaySize, y);
            fractal.recenterAndZoomRange(range, xCoord, yCoord, 0.5);
            drawFractal();
        }
    }

    private class FractalWorker extends SwingWorker<Object, Object>{
        int yCoordinate;
        int[] computedRGBValues;
        private FractalWorker(int row){
            yCoordinate = row;
        }
        protected Object doInBackground(){
            computedRGBValues = new int[displaySize];
            for (int i = 0; i < computedRGBValues.length; i++){ // цикл проходящий через все пиксели
                double xCoord = fractal.getCoord(range.x,
                        range.x + range.width, displaySize, i);
                double yCoord = fractal.getCoord(range.y,
                        range.y + range.height, displaySize, yCoordinate);
                int iteration = fractal.numIterations(xCoord, yCoord);
                if (iteration == -1){
                    computedRGBValues[i] = 0;
                }
                else { // если остались итерации, соответствующей единице массива задается посчитанный цвет
                    float hue = 0.7f + (float) iteration / 200f;
                    int rgbColor = Color.HSBtoRGB(hue, 1f, 1f);
                    computedRGBValues[i] = rgbColor;
                }
            }
            return null;
        }

        protected void done(){
            for (int i = 0; i < computedRGBValues.length; i++){
                display.drawPixel(i, yCoordinate, computedRGBValues[i]);
            }
            display.repaint(0, 0, yCoordinate, displaySize, 1);
            rowsRemaining--;
            if (rowsRemaining == 0){
                enableUI(true);
            }
        }
    }
}