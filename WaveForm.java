/*
Attempt to create an audio streram that will then display a
visual representation of the sound waves. More of a test
Using the Build an Audio Waveform Display  tutorial from codeidol
 */

package waveform;
import java.io.*;
import java.lang.Object.*;
import javax.sound.sampled.*;


/**
 *
 * @author Thomas Back
 */
public class WaveForm {

    //define some attributes for the object
    AudioInputStream audioInputStream;
    int frameLength, frameSize, result, numChannels, biggestSample;
    byte[] bytes;
    
    //create two dimensional int referencing channel and samples per channel(stereo has 2 channels)
    int[][] data;
    
    
    //will open the audio stream with specified file
    public void loadRawData(String f)
    {
        //open file for reading, audio file
        File file = new File(f);
        
        //open Audio input stream
        try
        {
            audioInputStream = AudioSystem.getAudioInputStream(file);
            
        //catch exceptions
        }catch(UnsupportedAudioFileException e)
        {
            System.out.println("Unsupported Audio Exception: Use correct audio file.");
        }catch(IOException e)
        {
            System.out.println("IOException: Could not read from audio stream.");
        }
   
    }
    
    public void createData()
    {
        // we need to be able to read in the entire audio file meaning
        //we should take bytes per frame * total number of frames to get total numb of bytes
        frameLength = (int) audioInputStream.getFrameLength();
        frameSize   = (int) audioInputStream.getFormat().getFrameSize();
        
        //set byte array to the size of the audio file
        bytes = new byte[frameLength * frameSize];
        
        //read in the audio now
        result = 0;
        try{
            result = audioInputStream.read(bytes);
        }catch(Exception e)
        {
            System.out.println("Could not read in bytes from audio stream.");
        }
        
        // the whole thing isnt very useful so we need to break it down into smaller bits
        //initialize the int[][] we created earlier
        numChannels = audioInputStream.getFormat().getChannels();
        frameLength = (int) audioInputStream.getFrameLength();
        data = new int[numChannels][frameLength];
        storeDataInArray(bytes);
 
    }
    
    public void storeDataInArray(byte[] eightBitByteArray)
    {
        int sampleIndex = 0;
        int sampleMin   = 0;
        int sampleMax   = 0;
        
        //loop through the byte[]
        for (int t = 0;t < eightBitByteArray.length;)
        {
            //for each iteration, loop through the channels
            for (int channel = 0; channel < numChannels; channel++)
            {
                //this piece of code grabs the min and max of a 16 bit sample,
                //stores whatsin between it in a sixteen bit sample
                int low = (int) eightBitByteArray[t];
                t++;
                int high = (int) eightBitByteArray[t];
                t++;
                
                //call our helper function that performs bitwise operations to convert to 16 bit sample
                int sample = getSixteenBitSample(high, low);
                
                //this is some checking to store the largest sample size for Y Scaling later
                if(sample > sampleMax)
                {
                    biggestSample = sample;
                    sampleMax = sample;
                }
                data[channel][sampleIndex] = sample;
            }
            
            sampleIndex++;
        }
    }
    
    //function to retriew the sixteen bit sample from byte array using a min and max
    private int getSixteenBitSample(int high, int low)
    {
        return (high << 8) + (low & 0x00ff);
    }
    
    //get the stream from this object specifying a channel
    public int[] getStreamData(int channel)
    {
        try{
             return data[channel];
        }catch(Exception e)
        {
            System.out.println("You must enter an index for the proper amount of channels");
        }
        return null;
       
    }
    
    public double getYScaleFactor(int panelHeight)
    {
        return (panelHeight / (biggestSample * 2 * 1.5));
    }
    
    public static void main(String[] args)
    {

        WaveForm wf = new WaveForm();
        wf.loadRawData("New Order -Bizarre Love Triangle(TBack Flip).wav");
        wf.createData();
        SingleWaveformPanel p = new SingleWaveformPanel(wf, 1);
        p.paintComponents();
        
    }
    
}
