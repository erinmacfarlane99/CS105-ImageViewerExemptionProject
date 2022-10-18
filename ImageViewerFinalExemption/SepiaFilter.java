import java.awt.Color;

/**
 * An image filter to make the image Sepia.
 * 
 * @author Michael Kölling and David J. Barnes.
 * @version 1.0
 */
public class SepiaFilter extends Filter
{
	private int r;
	private int g;
	private int b;
	
	/**
	 * Constructor for objects of class GrayScaleFilter.
	 * @param name The name of the filter.
	 */
	public SepiaFilter(String name)
    {
        super(name);
	}

    /**
     * Apply this filter to an image.
     * 
     * @param  image  The image to be changed by this filter.
     */
    public void apply(OFImage image)
    {    
        int height = image.getHeight();
        int width = image.getWidth();
        for(int y = 0; y < height; y++) {
            for(int x = 0; x < width; x++) {
                Color pix = image.getPixel(x, y);
                double tr = ((0.393 * pix.getRed()) + (0.769 * pix.getGreen()) + (0.189 * pix.getBlue()));
                double tg = ((0.349 * pix.getRed()) + (0.686 * pix.getGreen()) + (0.168 * pix.getBlue())); 
                double tb = ((0.272 * pix.getRed()) + (0.534 * pix.getGreen()) + (0.131 * pix.getBlue()));
                int tri = (int)tr;
                int tgi = (int)tg;
                int tbi = (int)tb;
                
                		if(tri > 255) {
                			r = 255;
                		}
                		else {
                			r = tri;
                		}
                		if(tgi > 255) {
                			g = 255;
                		}
                		else {
                			g = tgi;
                		}
                		if(tbi > 255) {
                			b = 255;
                		}
                		else {
                			b = tbi;
                		}

                image.setPixel(x, y, new Color(r, g, b));
            }
        }
    }
}
