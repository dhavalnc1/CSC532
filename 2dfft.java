/*************************************************************************
 *  Compilation:  java Picture.java
 *  Execution:    java Picture imagename
 *
 *  Data type for manipulating individual pixels of an image. The original
 *  image can be read from a file in jpg, gif, or png format, or the
 *  user can create a blank image of a given size. Includes methods for
 *  displaying the image in a window on the screen or saving to a file.
 *
 *  % java Picture mandrill.jpg
 *
 *  Remarks
 *  -------
 *   - pixel (x, y) is column x and row y, where (0, 0) is upper left
 *
 *   - see also GrayPicture.java for a grayscale version
 *
 *************************************************************************/

import javax.imageio.ImageIO;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JLabel;
import javax.swing.JMenuItem;
import javax.swing.ImageIcon;
import javax.swing.KeyStroke;
import java.awt.FileDialog;
import java.awt.Toolkit;
import java.awt.Color;
import java.awt.image.BufferedImage;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.io.File;
import java.io.IOException;
import java.net.URL;


/**
 *  This class provides methods for manipulating individual pixels of
 *  an image. The original image can be read from a file in JPEG, GIF,
 *  or PNG format, or the user can create a blank image of a given size.
 *  This class includes methods for displaying the image in a window on
 *  the screen or saving to a file.
 *  <p>
 *  Pixel (x, y) is column x, row y, where (0, 0) is upper left.
 *  <p>
 *  For additional documentation, see
 *  <a href="http://www.cs.princeton.edu/introcs/31datatype">Section 3.1</a> of
 *  <i>Introduction to Programming in Java: An Interdisciplinary Approach</i>
 *  by Robert Sedgewick and Kevin Wayne.
 */
public final class fastCorr implements ActionListener {
    private BufferedImage image;    // the rasterized image
    private JFrame frame;           // on-screen view
    private String filename;        // name of file

   /**
     * Create an empty w-by-h picture.
     */
    public fastCorr(int w, int h) {
        image = new BufferedImage(w, h, BufferedImage.TYPE_INT_RGB);
        // set to TYPE_INT_ARGB to support transparency
        filename = w + "-by-" + h;
    }

   /**
     * Create a picture by reading in a .png, .gif, or .jpg from
     * the given filename or URL name.
     */
    public fastCorr(String filename) {
        this.filename = filename;
        try {
            // try to read from file in working directory
            File file = new File(filename);
            if (file.isFile()) {
                image = ImageIO.read(file);
            }

            // now try to read from file in same directory as this .class file
            else {
                URL url = getClass().getResource(filename);
                if (url == null) { url = new URL(filename); }
                image = ImageIO.read(url);
            }
        }
        catch (IOException e) {
            // e.printStackTrace();
            throw new RuntimeException("Could not open file: " + filename);
        }

        // check that image was read in
        if (image == null) {
            throw new RuntimeException("Invalid image file: " + filename);
        }
    }

   /**
     * Create a picture by reading in a .png, .gif, or .jpg from a File.
     */
    public fastCorr(File file) {
        try { image = ImageIO.read(file); }
        catch (IOException e) {
            e.printStackTrace();
            throw new RuntimeException("Could not open file: " + file);
        }
        if (image == null) {
            throw new RuntimeException("Invalid image file: " + file);
        }
    }

   /**
     * Return a JLabel containing this Picture, for embedding in a JPanel,
     * JFrame or other GUI widget.
     */
    public JLabel getJLabel() {
        if (image == null) { return null; }         // no image available
        ImageIcon icon = new ImageIcon(image);
        return new JLabel(icon);
    }

   /**
     * Display the picture in a window on the screen.
     */
    public void show() {

        // create the GUI for viewing the image if needed
        if (frame == null) {
            frame = new JFrame();

            JMenuBar menuBar = new JMenuBar();
            JMenu menu = new JMenu("File");
            menuBar.add(menu);
            JMenuItem menuItem1 = new JMenuItem(" Save...   ");
            menuItem1.addActionListener(this);
            menuItem1.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_S,
                                     Toolkit.getDefaultToolkit().getMenuShortcutKeyMask()));
            menu.add(menuItem1);
            frame.setJMenuBar(menuBar);



            frame.setContentPane(getJLabel());
            // f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
            frame.setTitle(filename);
            frame.setResizable(false);
            frame.pack();
            frame.setVisible(true);
        }

        // draw
        frame.repaint();
    }

   /**
     * Return the height of the picture (in pixels).
     */
    public int height() {
        return image.getHeight(null);
    }

   /**
     * Return the width of the picture (in pixels).
     */
    public int width() {
        return image.getWidth(null);
    }

   /**
     * Return the Color of pixel (i, j).
     */
    public Color get(int i, int j) {
        return new Color(image.getRGB(i, j));
    }
    
    /**
     * Return the Color of pixel (i, j).
     */
    public Color[][] getColorArray() {
    	Color[][] c = new Color[height()][width()];
    	for(int i = 0; i < c[0].length; i++)
    		for(int j = 0; j < c.length; j++)
    			c[j][i] = new Color(image.getRGB(i, j));
        return c;
    }

   /**
     * Set the Color of pixel (i, j) to c.
     */
    public void set(int i, int j, Color c) {
        if (c == null) { throw new RuntimeException("can't set Color to null"); }
        image.setRGB(i, j, c.getRGB());
    }

   /**
     * Save the picture to a file in a standard image format.
     * The filetype must be .png or .jpg.
     */
    public void save(String name) {
        save(new File(name));
    }

   /**
     * Save the picture to a file in a standard image format.
     */
    public void save(File file) {
        this.filename = file.getName();
        if (frame != null) { frame.setTitle(filename); }
        String suffix = filename.substring(filename.lastIndexOf('.') + 1);
        suffix = suffix.toLowerCase();
        if (suffix.equals("jpg") || suffix.equals("png")) {
            try { ImageIO.write(image, suffix, file); }
            catch (IOException e) { e.printStackTrace(); }
        }
        else {
            System.out.println("Error: filename must end in .jpg or .png");
        }
    }

   /**
     * Opens a save dialog box when the user selects "Save As" from the menu.
     */
    public void actionPerformed(ActionEvent e) {
        FileDialog chooser = new FileDialog(frame,
                             "Use a .png or .jpg extension", FileDialog.SAVE);
        chooser.setVisible(true);
        if (chooser.getFile() != null) {
            save(chooser.getDirectory() + File.separator + chooser.getFile());
        }
    }

 // compute the FFT of x[], assuming its length is a power of 2
    public static Complex[] fft(Complex[] x) {
        int n = x.length;

        // base case
        if (n == 1) return new Complex[] { x[0] };

        // radix 2 Cooley-Tukey FFT
        if (n % 2 != 0) {
            throw new IllegalArgumentException("n is not a power of 2");
        }

        // fft of even terms
        Complex[] even = new Complex[n/2];
        for (int k = 0; k < n/2; k++) {
            even[k] = x[2*k];
        }
        Complex[] q = fft(even);

        // fft of odd terms
        Complex[] odd  = even;  // reuse the array
        for (int k = 0; k < n/2; k++) {
            odd[k] = x[2*k + 1];
        }
        Complex[] r = fft(odd);

        // combine
        Complex[] y = new Complex[n];
        for (int k = 0; k < n/2; k++) {
            double kth = -2 * k * Math.PI / n;
            Complex wk = new Complex(Math.cos(kth), Math.sin(kth));
            y[k]       = q[k].plus(wk.times(r[k]));
            y[k + n/2] = q[k].minus(wk.times(r[k]));
        }
        return y;
    }


    // compute the inverse FFT of x[], assuming its length is a power of 2
    public static Complex[] ifft(Complex[] x) {
        int n = x.length;
        Complex[] y = new Complex[n];

        // take conjugate
        for (int i = 0; i < n; i++) {
            y[i] = x[i].conjugate();
        }

        // compute forward FFT
        y = fft(y);

        // take conjugate again
        for (int i = 0; i < n; i++) {
            y[i] = y[i].conjugate();
        }

        // divide by n
        for (int i = 0; i < n; i++) {
            y[i] = y[i].scale(1.0 / n);
        }

        return y;

    }

    // compute the circular convolution of x and y
    public static Complex[] cconvolve(Complex[] x, Complex[] y) {

        // should probably pad x and y with 0s so that they have same length
        // and are powers of 2
        if (x.length != y.length) {
            throw new IllegalArgumentException("Dimensions don't agree");
        }

        int n = x.length;

        // compute FFT of each sequence
        Complex[] a = fft(x);
        Complex[] b = fft(y);

        // point-wise multiply
        Complex[] c = new Complex[n];
        for (int i = 0; i < n; i++) {
            c[i] = a[i].times(b[i]);
        }

        // compute inverse FFT
        return ifft(c);
    }


    // compute the linear convolution of x and y
    public static Complex[] convolve(Complex[] x, Complex[] y) {
        Complex ZERO = new Complex(0, 0);

        Complex[] a = new Complex[2*x.length];
        for (int i = 0;        i <   x.length; i++) a[i] = x[i];
        for (int i = x.length; i < 2*x.length; i++) a[i] = ZERO;

        Complex[] b = new Complex[2*y.length];
        for (int i = 0;        i <   y.length; i++) b[i] = y[i];
        for (int i = y.length; i < 2*y.length; i++) b[i] = ZERO;

        return cconvolve(a, b);
    }

    // display an array of Complex numbers to standard output
    public static void show(Complex[] x, String title) {
        StdOut.println(title);
        StdOut.println("-------------------");
        for (int i = 0; i < x.length; i++) {
            StdOut.println(x[i]);
        }
        StdOut.println();
    }
    

   /**
     * Test client. Reads a picture specified by the command-line argument,
     * and shows it in a window on the screen.
     */
    public static void main(String[] args) {
        Picture pic = new Picture("return.png" );
        Picture pic2 = new Picture("pulse.png" );
        System.out.printf("%d-by-%d\n", pic.width(), pic.height());
        
        Color testImage;
        
        int imageWidth, imageHeight;
        imageWidth = pic.width();
        imageHeight = pic.height();
        
        Complex[][] Return = new Complex[512][512];
        Complex[][] Pulse = new Complex[512][512];
        
        /* Color to greyscale conversion */
        
        for(int i=0;i<imageWidth;i++)
        {
        	for(int j=0;j<imageHeight;j++)
        	{
        		testImage = pic.get(i,j);
        		
        		int red = (int) (testImage.getRed());
				
				Complex c = new Complex(red, 0); 
				Return[j][i] = c;
        	}
        	
        }
        
        for(int i=0;i<imageWidth;i++)
        {
        	for(int j=0;j<imageHeight;j++)
        	{
        		testImage = pic2.get(i,j);
        		
        		int red = (int) (testImage.getRed());
			
				Complex c = new Complex(red, 0);
				Pulse[j][i] = c;
        	}
        	
        }
        
        
//        for(int i=0;i<512;i++)
//        {
//        	System.out.println(Pulse[0][i] + "  " + i);
//        }
        
        Picture pic3 = new Picture(imageWidth, imageHeight);
        Picture pic4 = new Picture(imageWidth, imageHeight);
        
        // calculate 2D fft of both images and store it into separate 2D array
        
        Complex[][] fftReturn = new Complex[512][512];
        Complex[][] fftPulse = new Complex[512][512];
        
        Complex[] x = new Complex[512];
        
        // first calculate 1D fft
        for(int j=0;j<512;j++)
        {
        	for(int i=0;i<512;i++)
        	{
        		x[i] = Return[j][i];
        	}
        	
        	Complex[] y = fft(x);
        	
        	for(int i=0;i<512;i++)
        	{
        		fftReturn[j][i] = y[i];
        	}
        }
        
        for(int j=0;j<512;j++)
        {
        	for(int i=0;i<512;i++)
        	{
        		x[i] = Pulse[j][i];
        	}
        	
        	Complex[] y = fft(x);
        	
        	for(int i=0;i<512;i++)
        	{
        		fftPulse[j][i] = y[i];
        	}
        }
        
        // calculate 2d fft
        
        Complex[][] twodfftReturn = new Complex[512][512];
        Complex[][] twodfftPulse = new Complex[512][512];
        
        for(int i=0;i<512;i++)
        {
        	for(int j=0;j<512;j++)
        	{
        		x[j] = fftReturn[j][i];
        	}
        	
        	Complex[] y = fft(x);
        	
        	for(int j=0;j<512;j++)
        	{
        		twodfftReturn[j][i] = y[j];
        	}
        }
        
        for(int i=0;i<512;i++)
        {
        	for(int j=0;j<512;j++)
        	{
        		x[j] = fftPulse[j][i];
        	}
        	
        	Complex[] y = fft(x);
        	
        	for(int j=0;j<512;j++)
        	{
        		twodfftPulse[j][i] = y[j];
        	}
        }
        
        // calculate conjugate of 2dfft of pulse
        
//        System.out.println(twodfftPulse[0][0].re() + " " + twodfftPulse[0][0].im());
        
        for(int i=0;i<512;i++)
        {
        	for(int j=0;j<512;j++)
        	{
        		twodfftPulse[i][j] = twodfftPulse[i][j].conjugate();
        	}
        }
        
//        System.out.println(twodfftPulse[0][0].re() + " " + twodfftPulse[0][0].im());
//        System.out.println(twodfftPulse[100][50].re() + " " + twodfftPulse[100][50].im());
//        System.out.println(twodfftPulse[250][75].re() + " " + twodfftPulse[250][75].im());
//        System.out.println(twodfftPulse[487][0].re() + " " + twodfftPulse[487][0].im());
//        System.out.println(twodfftPulse[511][511].re() + " " + twodfftPulse[511][511].im());
        
        // multiply fft of return with com-conj of pulse
        
        Complex[][] starfft = new Complex[512][512];
        for(int i=0;i<512;i++)
        {
        	for(int j=0;j<512;j++)
        	{
        		starfft[i][j] = twodfftReturn[i][j].times(twodfftPulse[i][j]);
        	}
        }
        
        // calculate inverse fft of starfft
        Complex[][] inversefft = new Complex[512][512];
        Complex[][] twodinversefft = new Complex[512][512];
        
        for(int j=0;j<512;j++)
        {
        	for(int i=0;i<512;i++)
        	{
        		x[i] = starfft[j][i];
        	}
        	
        	Complex[] y = fft(x);
        	
        	for(int i=0;i<512;i++)
        	{
        		inversefft[j][i] = y[i];
        	}
        }
        
        // 2d inverse
        
        for(int i=0;i<512;i++)
        {
        	for(int j=0;j<512;j++)
        	{
        		x[j] = inversefft[j][i];
        	}
        	
        	Complex[] y = fft(x);
        	
        	for(int j=0;j<512;j++)
        	{
        		twodinversefft[j][i] = y[j];
        	}
        }
        
        // store only real value
        
        float[][] newImage = new float[512][512];

        for(int i=0;i<512;i++)
        {
        	for(int j=0;j<512;j++)
        	{
        		newImage[i][j] = (float) twodinversefft[i][j].re();
        	}
        }
        
        // find peak value
        float max = newImage[0][0];
        
        for(int i=0;i<512;i++)
        {
        	for(int j=0;j<512;j++)
        	{
        		if(max < newImage[i][j])
        		{
        			max = newImage[i][j];
        		}
        	}
        }
        
        System.out.println("max = " + max);
        
        // find 10% correlation
        
        for(int i=0;i<512;i++)
        {
        	for(int j=0;j<512;j++)
        	{
        		if(newImage[i][j] >= (0.9*max))
        		{
        			Color newColor = new Color(255,0,0);
        			pic3.set(j, i, newColor);
        		}
        		else if(newImage[i][j] > 0)
        		{
        			float temp = newImage[i][j]/max;
        			Color newColor = new Color((int)(temp*255),(int)(temp*255),(int)(temp*255));
        			pic3.set(j, i, newColor);
        		}
        		else
        		{
        			Color newColor = new Color(0,0,0);
        			pic3.set(j, i, newColor);
        		}
        	}
        }
        
        
        pic.show();
        pic2.show();
        pic3.show();
        
        ///////////////////////////////////

        
    }

}
