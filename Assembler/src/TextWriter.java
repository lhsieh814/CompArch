import java.io.BufferedWriter;
import java.io.FileOutputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.nio.channels.Channels;

/**
 * ECSE 415 Project, Assembler Implementation
 * Author: Yang Zhou
 * Group 6
 * Members: Yukun Su, Yang Zhou, Wei Sing Ta, Lena Hsieh
 */
public class TextWriter {
    public static Writer writer = null;
    
    public static void createTextFile(StringBuffer stringBuffer){
        try {
            System.out.println("Creating output txt file");
            writer = new BufferedWriter(new OutputStreamWriter(
                    new FileOutputStream("bin_result.txt"),"ASCII"));
            
            writer.write(stringBuffer.toString());
        }
        catch (Exception e) {
            System.out.println(e.toString());
        }
        finally{
            try {
                writer.close();
            }
            catch (Exception e) {
                System.out.println(e.toString());
            }
        }
    }
}
