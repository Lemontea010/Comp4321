import jdbm.RecordManager;
import jdbm.RecordManagerFactory;
import jdbm.helper.FastIterator;
import jdbm.htree.HTree;
import org.htmlparser.util.ParserException;

import java.io.IOException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Set;
import java.util.Vector;

public class Spider {
    private HTree urls;

    private int num_urls;   //total num = num_urls+1

    private RecordManager db;

    private Indexer indexer;



    /**
     *
     * @param _url
     * @throws ParserException
     */
    Spider(String _url) throws ParserException, IOException {
        Crawler crawler = new Crawler(_url);
        num_urls = 1;
        db = RecordManagerFactory.createRecordManager("indexer");
        urls = HTree.createInstance(db);
        indexer=new Indexer();
        urls.put(_url,new web(_url,0,crawler.extractLinks()));//Htree<String url,web>
        this.get_url_recursive(_url);
        FastIterator iter_web=urls.values();
        Set<String> iter_word;
        web w;
        while((w=(web)iter_web.next())!=null){

            iter_word = w.getHashforbody().keySet();
            Iterator<String> stringIterator=iter_word.iterator();
            if(stringIterator.hasNext()) {
                String word=stringIterator.next();
                indexer.replace_count(word,indexer.get_idf(word)+w.getHashforbody().get(word));
            }

        }
        iter_web=urls.values();
        while((w=(web)iter_web.next())!=null){
            iter_word = w.getHashforbody().keySet();
            Iterator<String> stringIterator=iter_word.iterator();
            if(stringIterator.hasNext()) {
                String word=stringIterator.next();
                double score=(w.getHashforbody().get(word))/(w.getmax())*((Math.log(num_urls)/indexer.get_idf(word))/Math.log(2));
                w.update_score(word,score);
            }
        }
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
                    ((web)urls.get(temp.get(i))).updateParent(_url);   // update parent
                }
                else{
                    num_urls +=1;                       //if url not exist
                    urls.put(_url,new web(temp.get(i),num_urls,get_url_recursive(temp.get(i))));//create new web class
                    ((web)urls.get(temp.get(i))).updateParent(_url);//update web class parent
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
    public HTree geturls(){
        return urls;
    }
}
