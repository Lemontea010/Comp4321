package search;

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


public class Spider {
    private HTree urls;                     //<String _url ,search.web search.web>

    private HTree url_to_id;                ///<int id ,search.web search.web>

    private int num_urls;   //total num = num_urls+1
    private HashMap<Integer,Boolean> updated;

    private RecordManager db;

    private Indexer indexer;

    private final int limit=300;


    public Spider(RecordManager _db) throws IOException {
        updated=new HashMap<>();
        db = _db;
        num_urls = 0;
        long recid = _db.getNamedObject("id_to_web");
        if (recid != 0) {
            url_to_id = HTree.load(_db, recid);
        }else{
            url_to_id = HTree.createInstance(db);
        }
        long recid2 = _db.getNamedObject("url_to_web");
        if (recid != 0) {
            urls = HTree.load(_db, recid);
            FastIterator it= urls.keys();
            String temp;
            while((temp=(String)it.next())!=null){
                updated.put(((web)urls.get(temp)).getid(),false);
                num_urls++;
            }
        }else{
            url_to_id= HTree.createInstance(db);
        }

        indexer=new Indexer(db);
        if(url_to_id.get(0)!=null) {
            this.get_url_recursive("https://www.cse.ust.hk/~kwtleung/COMP4321/testpage.htm");
        }else{
            System.out.println("An error has occured");
        }
        db.update(recid,url_to_id);
        db.update(recid2,urls);
        db.update(_db.getNamedObject("Htree_title"),indexer.getHashfortitle());
        db.update(_db.getNamedObject("Htree_body"),indexer.getHashforbody());
        db.update(_db.getNamedObject("Htree_word_to_id"),indexer.getWord_to_id());
        db.commit();
        db.close();
    }

    /**
     *
     * @param _url
     * @throws ParserException
     */
    public Spider(String _url) throws ParserException, IOException {
        updated=new HashMap<>();
        num_urls = 0;
        db = RecordManagerFactory.createRecordManager("search.Spider");
        urls = HTree.createInstance(db);
        db.setNamedObject("url_to_web",urls.getRecid());

        url_to_id= HTree.createInstance(db);
        db.setNamedObject("id_to_web",url_to_id.getRecid());
        indexer=new Indexer(db);

        this.get_url_recursive(_url);

        //System.out.println(num_urls);

        db.commit();
        db.close();
    }

    /**
     * Function add new search.web into search.web if not in the search.web Vector<search.web></>
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
                if(updated.get(cur.getid())){
                    return temp;                    //if already updated return
                }
                if(cur.getUpdate_date()<doccleaner.get_lastmodified(_url)){
                    int id=cur.getid();
                    urls.remove(_url);
                    web a=new web(_url,id,temp);
                    urls.put(_url,a);
                    url_to_id.remove(id);
                    url_to_id.put(id,a);
                    //System.out.println(cur.getid());
                }
                //System.out.println(cur.getid());
                updated.replace(cur.getid(),true);
            }else{
                web a=new web(_url,num_urls,temp)   ;                        //if url not exist
                urls.put(_url,a);//create new search.web class
                url_to_id.put(a.getid(),a);
                Vector<String>title= doccleaner.titleprocessing(_url);                       //get unigram title
                Vector<String>body= doccleaner.bodyprocessing(_url);
                title.addAll(doccleaner.bigramprocessing(crawler.extractContent().get(0))); //get bigram title
                body.addAll(doccleaner.bigramprocessing(crawler.extractContent().get(1)));
                indexer.put(title,body,num_urls);
                updated.put(a.getid(),false);
                num_urls +=1;

                /*System.out.println(_url+"\n");
                int x=((search.web)urls.get(_url)).getsize();
                long y =((search.web)urls.get(_url)).getLastmodified_date();
                Date date = new Date(y);
                System.out.println("Size :"+x+" Byte\n");
                System.out.println("Last Modified Date :"+date+"\n");*/
                                                                        //update search.web class parent
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
            RecordManager temp = RecordManagerFactory.createRecordManager("search.Spider");
            long recid = temp.getNamedObject("id_to_web");
            if (recid != 0) {
                    Spider spider =new Spider(temp);
            }else{
                Spider spider =new Spider("https://www.cse.ust.hk/~kwtleung/COMP4321/testpage.htm");
            }

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


            RecordManager recman = RecordManagerFactory.createRecordManager("search.Spider");
           

            recid = recman.getNamedObject("id_to_web");
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

