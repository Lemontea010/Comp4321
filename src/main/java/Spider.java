import jdbm.RecordManager;
import jdbm.htree.HTree;
import org.htmlparser.util.ParserException;

import java.io.IOException;
import java.util.Vector;

public class Spider {
    private Vector<web> urls;

    private int num_urls;   //total num = num_urls+1

    private RecordManager recmantitle;

    private RecordManager recmanbody;
    private HTree hashtable;
    /**
     *
     * @param _url
     * @throws ParserException
     */
    Spider(String _url, RecordManager recmantitle, RecordManager recmanbody,  HTree hashtable) throws ParserException, IOException {
        Crawler crawler = new Crawler(_url);
        num_urls = 1;
        urls = new Vector<>();
        this.hashtable = hashtable;
        this.recmantitle = recmantitle;
        this.recmanbody = recmanbody;
        urls.add(new web(_url,0,crawler.extractLinks());
        this.get_url_recursive(_url);
    }

    /**
     * Function add new web into web if not in the web Vector<web></>
     * @param _url
     * @return  Child link Vector<String> of current url
     */
    private Vector<String> get_url_recursive(String _url){
        try {
            Crawler crawler = new Crawler(_url);
            Vector<String> temp =crawler.extractLinks();

            for(int i=0;i<temp.size();i++){
                for(int j=0;j<urls.size();j++){
                    if(temp.get(i)==urls.get(j).getUrl()){
                        break;
                    }
                    else if(j+1==urls.size()){
                        num_urls +=1;
                        urls.add(new web(temp.get(i),num_urls,get_url_recursive(temp.get(i))));
                    }
                }
            }
            return temp;
        } catch (ParserException | IOException e) {
            e.printStackTrace();
        }
        Vector<String> temp =new Vector<>();
        return temp;
    }
    public int get_num_urls(){
        return num_urls;
    }
    public Vector<web> geturls(){
        return urls;
    }
}
