import org.htmlparser.beans.LinkBean;
import org.htmlparser.beans.StringBean;
import org.htmlparser.util.ParserException;

import java.net.URL;
import java.util.StringTokenizer;
import java.util.Vector;

public class Crawler {
    private String url;
    Crawler(String _url)
    {
        url = _url;
    }
    public Vector<String> extractWords() throws ParserException

    {
        StringBean sb;
        StringTokenizer tok;
        sb = new StringBean ();
        sb.setLinks (false);
        sb.setURL (url);
        tok=new StringTokenizer(sb.getStrings()," ");
        Vector<String> token=new Vector<>();
        while(tok.hasMoreElements()){
            token.add(tok.nextToken());
        }
        // extract words in url and return them
        // use StringTokenizer to tokenize the result from StringBean
        // ADD YOUR CODES HERE
        return token;
    }
    public Vector<String> extractLinks() throws ParserException

    {
        // extract links in url and return them
        // ADD YOUR CODES HERE

        Vector<String> r = new Vector<>();
        LinkBean lb =new LinkBean();
        lb.setURL(url);
        URL[] url_array=lb.getLinks();
        for(int i=0;i<url_array.length;i++){
            r.add(String.valueOf(url_array[i]));
        }
        return r;
    }
}
