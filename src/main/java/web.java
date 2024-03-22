import org.htmlparser.beans.StringBean;
import java.util.Vector;
import jdbm.RecordManager;
import jdbm.RecordManagerFactory;
import jdbm.htree.HTree;
import jdbm.helper.FastIterator;
import java.io.IOException;
import java.io.Serializable;

public class web {
    private String url;
    private int id;
    private Vector<String> child_urls;
    private Vector<String> content;
    private String parent_urls;
    web(String _url,int _id,Vector<String> child , String parent){
        this.url=_url;
        this.id=_id;
        this.child_urls=child;
        this.parent_urls = parent;
        /** creating a cleaned content */
        this.content = doccleaner.wordprocessing(this.url);
        /** connect to dB */

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

    String getParent_urls(){return this.parent_urls;}

    private void writefile(Vector<String> content){

        /** Todo : write the cleaned content to the dB */
        for(String word : content){

            }
            /** add to old entry */

    }
}

