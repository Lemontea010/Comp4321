import org.htmlparser.beans.LinkBean;
import org.htmlparser.util.ParserException;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Vector;

public class Crawler {
    private String url;
    Crawler(String _url)
    {
        url = _url;
    }

    public Vector<String> extractLinks() throws ParserException

    {
        Vector<String> r = new Vector<>();
        LinkBean lb =new LinkBean();
        lb.setURL(url);
        URL[] url_array=lb.getLinks();
        for(int i=0;i<url_array.length;i++){
            r.add(String.valueOf(url_array[i]));
        }
        return r;
    }

    public ArrayList<String> extractContent() throws IOException {

        ArrayList<String> titleandcontent = new ArrayList<>();
        // Load the web page content
        Document doc = Jsoup.connect(this.url).get();

        // Extract the title
        String title = doc.title();
        titleandcontent.add(title); /** titleandcontent[0] -> title */
        // Extract the body
        Element bodyElement = doc.body();
        String body = bodyElement.text();
        titleandcontent.add(body); /** titleandcontent[1] -> body */

        return titleandcontent;
    }

}
