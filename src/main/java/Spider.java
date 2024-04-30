import jdbm.RecordManager;
import jdbm.RecordManagerFactory;
import jdbm.helper.FastIterator;
import jdbm.htree.HTree;
import org.htmlparser.util.ParserException;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.LinkOption;
import java.nio.file.Path;
import java.nio.file.Paths;
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



    Spider(RecordManager _db) throws IOException {
        db = _db;
        num_urls = 0;
        long recid = db.getNamedObject("id_to_web");
        if (recid != 0) {
            url_to_id = HTree.load(db, recid);
        }else{
            urls = HTree.createInstance(db);

        }
        db.setNamedObject("url_to_web",urls.getRecid());
        recid = db.getNamedObject("url_to_web");
        if (recid != 0) {
            urls = HTree.load(db, recid);
            FastIterator it= urls.keys();

            while(it.next()!=null){
                num_urls++;
            }
        }else{
            url_to_id= HTree.createInstance(db);

        }
        db.setNamedObject("id_to_web",url_to_id.getRecid());
        indexer=new Indexer(db);
        if(url_to_id.get(0)!=null){
            this.get_url_recursive(((web)url_to_id.get(0)).getUrl());
        }
        db.commit();
        db.close();
    }

    /**
     *
     * @param _url
     * @throws ParserException
     */
    Spider(String _url) throws ParserException, IOException {
        num_urls = 0;
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
                web cur=(web)(urls.get(_url));
                if(cur.getUpdate_date()>doccleaner.get_lastmodified(_url)){
                    int id=cur.getid();
                    urls.remove(_url);
                    web a=new web(_url,id,temp);
                    urls.put(_url,a);
                    url_to_id.remove(id);
                    url_to_id.put(id,a);
                }
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

            String var10000 = System.getProperty("user.dir");
            String pa = var10000 + File.separator + "Url_to_id.txt";
            Path path = Paths.get(pa);
            if (Files.exists(path, new LinkOption[0])) {
                (new File(pa)).delete();
            }
            File f = new File(pa);
            FileWriter file = new FileWriter(f);
            BufferedWriter output = new BufferedWriter(file);
            output.write("Id\t\t<==>\t\tUrl\n");

            RecordManager recman = RecordManagerFactory.createRecordManager("Spider");

           

            long recid = recman.getNamedObject("id_to_web");
            HTree hashtable = HTree.load(recman, recid);
            FastIterator key=hashtable.keys();
            web x;

            for(int i=0;i<30;i++){
                x=(web)hashtable.get(i);

                    while(x!=null) {
                        output.write("id : "+x.getid()+"\t\t\turl : "+x.getUrl()+"\n");
                        i++;
                        x=(web)hashtable.get(i);
                    }

            }
            output.close();

            pa = var10000 + File.separator + "word_to_id.txt";
            path = Paths.get(pa);
            file = new FileWriter(new File(pa));
            output = new BufferedWriter(file);
            if (Files.exists(path, new LinkOption[0])) {
                (new File(pa)).delete();
            }
            output.write("id\t\t\t<==>\t\t\tword\n");
            recid = recman.getNamedObject("Htree_word_to_id");
            HTree hashtable1=HTree.load(recman, recid);
            key=hashtable1.keys();
            String word;
            while((word=(String)key.next())!=null){

                output.write("id : "+hashtable1.get(word)+"\t\tword : "+word+"\n");
        }
            output.close();



        }
        catch(IOException ex)
        {
            System.err.println(ex.toString());
        } catch (ParserException e) {
            throw new RuntimeException(e);
        }

    }

}

