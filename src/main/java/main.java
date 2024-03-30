import jdbm.RecordManager;
import jdbm.RecordManagerFactory;
import org.htmlparser.util.ParserException;

import java.io.IOException;

public class main {
    public static void main(String[] args)
    {
        try
        {
            RecordManager db=RecordManagerFactory.createRecordManager("Spider");
            Spider spider =new Spider("https://www.cse.ust.hk/~kwtleung/COMP4321/testpage.htm");
            search s = new search();
            s.query();

        }
        catch(IOException ex)
        {
            System.err.println(ex.toString());
        } catch (ParserException e) {
            throw new RuntimeException(e);
        }

    }
}

