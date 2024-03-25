import jdbm.RecordManager;
import jdbm.htree.HTree;
import org.htmlparser.util.ParserException;

import java.io.IOException;
import java.util.HashMap;
import java.util.Vector;

public class Spider {
    private HashMap<String,web> urls;

    private int num_urls;   //total num = num_urls+1

    private RecordManager db;



    /**
     *
     * @param _url
     * @throws ParserException
     */
    Spider(String _url) throws ParserException, IOException {
        Crawler crawler = new Crawler(_url);
        num_urls = 1;
        urls = new HashMap<>();
        urls.put(_url,new web(_url,0,crawler.extractLinks()));
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
                if(urls.get(temp.get(i))!=null){        //if url exist
                    urls.get(temp.get(i)).updateParent(_url);   // update parent
                }
                else{
                    num_urls +=1;                       //if url not exist
                    urls.put(_url,new web(temp.get(i),num_urls,get_url_recursive(temp.get(i))));//create new web class
                    urls.get(temp.get(i)).updateParent(_url);//update web class parent
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
    public HashMap<String,web> geturls(){
        return urls;
    }
}
