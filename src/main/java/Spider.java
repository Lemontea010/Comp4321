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
        this.get_url_recursive(_url);
        FastIterator iter_web=urls.values();
        Set<String> iter_word;
        web w;

        while((w=(web)iter_web.next())!=null){

            iter_word = w.getHashforbody().keySet();
            Iterator<String> stringIterator=iter_word.iterator();
            if(stringIterator.hasNext()) {
                String word=stringIterator.next();
                indexer.replace_count(word);
            }

        }
        iter_web=urls.values();
        while((w=(web)iter_web.next())!=null){
            iter_word = w.getHashforbody().keySet();
            Iterator<String> stringIterator=iter_word.iterator();
            if(stringIterator.hasNext()) {
                String word=stringIterator.next();
                double score=(w.getHashforbody().get(word))/(w.getmax())*((Math.log(num_urls)/indexer.get_df(word))/Math.log(2));
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
            System.out.println(_url+"\n");
            if(num_urls>=299){
                return temp;
            }
            if(urls.get(_url)!=null){
                return temp;
            }else{
                num_urls +=1;                       //if url not exist
                urls.put(_url,new web(_url,num_urls,temp));//create new web class
                //update web class parent
            }


            for(int i=0;i<temp.size();i++){
                get_url_recursive(temp.get(i));
                ((web)urls.get(temp.get(i))).updateParent(_url);

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
