import org.htmlparser.util.ParserException;

import java.io.IOException;

public class main {
    public static void main(String[] args)
    {
        try
        {

        Spider spider =new Spider("https://www.cse.ust.hk/~kwtleung/COMP4321/testpage.htm");
        }
        catch(IOException | ParserException ex)
        {
            System.err.println(ex.toString());
        }

    }
}

