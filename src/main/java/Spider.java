import jdbm.RecordManager;
import jdbm.RecordManagerFactory;
import jdbm.helper.FastIterator;
import jdbm.htree.HTree;
import org.htmlparser.util.ParserException;

import java.io.IOException;
import java.util.*;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.concurrent.Phaser;


public class Spider {
    private HTree urls;                     //<String _url ,web web>

    private HTree url_to_id;                ///<int id ,web web>

    private int num_urls;   //total num = num_urls+1

    private RecordManager db;

    private Indexer indexer;

    private final int limit=30;



    /**
     *
     * @param _url
     * @throws ParserException
     */
    Spider(String _url) throws ParserException, IOException {
        num_urls = 1;
        db = RecordManagerFactory.createRecordManager("Spider");
        urls = HTree.createInstance(db);
        db.setNamedObject("url_to_web",urls.getRecid());

        url_to_id= HTree.createInstance(db);
        db.setNamedObject("id_to_web",url_to_id.getRecid());
        indexer=new Indexer(db);

        this.get_url_recursive(_url);
        /*FastIterator iter_web=urls.values();
        Set<String> iter_word;
        web w;

        while((w=(web)iter_web.next())!=null){

            iter_word = w.getHashforbody().keySet();
            Iterator<String> stringIterator=iter_word.iterator();
            if(stringIterator.hasNext()) {
                String word=stringIterator.next();

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
        }*/
        db.commit();
        db.close();
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

            if(num_urls>=limit){
                return temp;
            }
            if(urls.get(_url)!=null){
                return temp;
            }else{
                web a=new web(_url,num_urls,temp)   ;                        //if url not exist
                urls.put(_url,a);//create new web class
                url_to_id.put(a.getid(),a);
                indexer.put(doccleaner.titleprocessing(_url),doccleaner.bodyprocessing(_url),num_urls);
                num_urls +=1;
                /*System.out.println(_url+"\n");
                int x=((web)urls.get(_url)).getsize();
                long y =((web)urls.get(_url)).getLastmodified_date();
                Date date = new Date(y);
                System.out.println("Size :"+x+" Byte\n");
                System.out.println("Last Modified Date :"+date+"\n");*/
                                                                        //update web class parent
            }


            for(int i=0;i<temp.size();i++){
                get_url_recursive(temp.get(i));
                if(num_urls>=limit){
                    break;
                }
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
    public static void main(String[] args)
    {
        try
        {
            Spider spider =new Spider("https://www.cse.ust.hk/~kwtleung/COMP4321/testpage.htm");
            RecordManager recman = RecordManagerFactory.createRecordManager("Spider");
            long recid = recman.getNamedObject("id_to_web");
            HTree hashtable = HTree.load(recman, recid);
            FastIterator key=hashtable.keys();
            web x;

            for(int i=1;i<=300;i++){
                x=(web)hashtable.get(i);

                    while(x!=null) {
                        System.out.println("url : " + x.getUrl() + " id : " + x.getid());
                        i++;
                        x=(web)hashtable.get(i);
                    }

            }
            recid = recman.getNamedObject("Htree_word_to_id");
            HTree hashtable1=HTree.load(recman, recid);
            key=hashtable1.keys();
            String word;
            while((word=(String)key.next())!=null){
                System.out.println("Word : "+word+" id : "+hashtable1.get(word));
        }

        }
        catch(IOException ex)
        {
            System.err.println(ex.toString());
        } catch (ParserException e) {
            throw new RuntimeException(e);
        }

    }

}

