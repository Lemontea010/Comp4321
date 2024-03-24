import org.htmlparser.beans.StringBean;
import java.util.Vector;
import jdbm.RecordManager;
import jdbm.RecordManagerFactory;
import jdbm.htree.HTree;
import jdbm.helper.FastIterator;
import org.htmlparser.util.ParserException;

import java.io.IOException;
import java.io.Serializable;

public class web {
    private String url;
    private int id;
    private Vector<String> child_urls;

    private Vector<String> title;
    private Vector<String> body;

    private RecordManager recmantitle;

    private RecordManager recmanbody;
    private HTree hashtable;

    web(String _url,int _id,Vector<String> child , RecordManager recmantitle, RecordManager recmanbody, HTree hash) throws ParserException, IOException {
        this.url=_url;
        this.id=_id;
        this.child_urls=child;
        /** creating a cleaned content */
        this.title = doccleaner.titleprocessing(this.url);
        this.body = doccleaner.titleprocessing(this.url);
        /** connect to dB */
        this.recmantitle = recmantitle;
        this.recmanbody = recmanbody;
        this.hashtable = hash;

    }
    String getUrl(){
        return url;
    }
    int getid(){ return id;}

    Vector<String> getChild(){
        return child_urls;
    }

    void updateChild(Vector<String> child){
        child_urls=child;
    }

    private void writefile(Vector<String> content) throws IOException {

        /** Todo : write the cleaned content to the dB */
        /** all stems extracted from the page body, together with all statistical information needed to
         support the vector space model (i.e., no need to support Boolean operations), are inserted
         into one inverted file */
        for(String word : content){
            long recid = recmanbody.getNamedObject(word);

            if (recid != 0)
                hashtable = HTree.load(recmanbody, recid);
            else {
                hashtable = HTree.createInstance(recmanbody);
                recmanbody.setNamedObject( word, hashtable.getRecid() );
            }

            /** new entry */
            if(hashtable.get(word) == null){
                String entry = hashtable.get(word)+ " doc" + this.id;
                /** adding the entry behind if there is previous entries existing */
                hashtable.put(word , entry);
            }
            /** add to old entry */
            else{
                hashtable.put(word , "doc" + this.id);
            }

            /**all stems extracted from the page title are inserted into another inverted file*/
        }

    }
}

