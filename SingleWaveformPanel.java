/*
Following along with the tutorial still, this will create  something to paint one channel of audio data
next section will write a waveformpanel container to use multiple of this clkass to handle multi channel audio
--
Drawn by plotting points scaled to the sample data, drawing lines between them.
 */
package waveform;
import javax.swing.*;
import java.awt.*;

/**
 *
 * @author Thomas Back
 */

public class SingleWaveformPanel extends JPanel{
    
    //we will import only one channel, meaning we can use a 1d array for the sample
    private int[] data;
    private WaveForm wf;
    
    //set color attributes for graphics
    private static final Color BACKGROUND_COLOR = Color.black;
    private static final Color REFERENCE_COLOR  = Color.green;
    private static final Color WAVEFORM_COLOR   = Color.blue;
    
    //import the data that contains bit and channel info
    public SingleWaveformPanel(WaveForm wf, int channel)
    {
        super();
        this.wf = wf;
        this.data = wf.getStreamData(channel);
        setBackground(BACKGROUND_COLOR);
    }
    
    protected void paintComponent(Graphics g)
    {
        super.paintComponent(g);
        int lineHeight = getHeight() /2;
        g.setColor(REFERENCE_COLOR);
        g.drawLine(0, lineHeight, (int) getWidth(), lineHeight);
        
        drawWaveform(g, this.data);
        
    }
    
    //scaling since a sample might be too big
    protected double getXScaleFactor(int panelWidth)
    {
        //depending on the width of the JPanel, we want to scale it to the length of our data
        double width = (double) panelWidth;
        return (width / ((double) data.length));
    }
    
    //method to return increment scale
    protected double getIncrement(double xScale)
    {
        //returned value will be used for drawing increments in drawWaveform
        try{
            double increment = (data.length / (data.length * xScale));
            return increment;
        }catch(Exception e)
        {
            System.out.println("Exception when computing increment value from xScale");
        }
        //return -1 if failed
        return -1;
    }
    
    /**
     * 
     * @param g
     *        graphics of the panel 
     * @param data 
     *         the audio bits of a given channel
     * 
     * Draws a waveform with given input into a graphic
     */
    protected void drawWaveform(Graphics g, int[] data)
    {
        int buffer = 30;
        
        //error check for null data
        if(data == null)
        {
            return;
        }
        
        double oldX = 0;
        double xIndex = 0;
        
        //call the increment function which will use the width with a buffer so we have a value
        //to scale the graphic by
        double increment = getIncrement(getXScaleFactor(getWidth() - buffer * 2));
        
        g.setColor(WAVEFORM_COLOR);
        System.out.println("Width: " + this.getWidth());
        
        int drawLength = data.length;
        
        for(double t = 0; t < drawLength;t += increment)
        {
            double scaleFactor = wf.getYScaleFactor(getHeight());
            double scaledSample = data[(int) t] * scaleFactor;
            double y = ((getHeight() / 2) - (scaledSample));
            double yMirror = ((getHeight() / 2) + scaledSample);
            
            g.drawLine((int) (oldX + buffer), (int) yMirror,
                    (int) (xIndex + buffer), (int) y);
            xIndex++;
            oldX = xIndex;
            
        }
    }
}
