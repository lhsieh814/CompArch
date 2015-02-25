
import java.io.BufferedWriter;
import java.io.FileOutputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.nio.channels.Channels;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 *
 * @author Gavin
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
