import java.util.HashMap;
import java.util.Vector;
import jdbm.RecordManager;
import jdbm.htree.HTree;
import org.htmlparser.util.ParserException;
import java.io.IOException;

public class web {
    private String url;
    private int id;
    private Vector<String> child_urls;
    private Vector<String> parent_urls;
    private Vector<String> title;
    private Vector<String> body;
    private HashMap<String , String> hashfortitle;
    private HashMap<String , String> hashforbody;

    private String completetitle;

    web(String _url,int _id,Vector<String> child, String Parent) throws ParserException, IOException {
        this.url=_url;
        this.id=_id;
        this.child_urls=child;
        this.parent_urls = new Vector<>();
        this.parent_urls.add(Parent);

        /** creating a cleaned content */
        this.title = doccleaner.titleprocessing(this.url);
        this.body = doccleaner.bodyprocessing(this.url);
        this.completetitle = doccleaner.gettitle(this.url);

        /** Title */
        this.hashfortitle = new HashMap<>();
        /** Body */
        this.hashforbody = new HashMap<>();
        /** indexer */
        this.writefileforbody(this.body);
        this.writefilefortitle(this.title);

    }
    String getUrl(){
        return url;
    }
    public int getid(){ return id;}

    Vector<String> getChild(){
        return child_urls;
    }

    Vector<String> getParent(){
        return parent_urls;
    }
    void updateChild(Vector<String> child){
        child_urls=child;
    }

    public void updateParent(String parent){
        this.parent_urls.add(parent);
    }
    private void writefileforbody(Vector<String> content) throws IOException {

        /** all stems extracted from the page body, together with all statistical information needed to
         support the vector space model (i.e., no need to support Boolean operations), are inserted
         into one inverted file */

        /** title: stem of the title ; body: stem of the body */
        for (String word : content) {
            /** new entry */
            if (hashforbody.get(word) == null) {
                String entry = "1";
                /** adding the entry behind if there is previous entries existing */
                hashforbody.put(word, entry);
            }
            /** add to old entry */
            else {
                int count = Integer.parseInt((String)hashforbody.get(word)) + 1;
                hashforbody.put(word, String.valueOf(count));
            }
        }
    }

    private void writefilefortitle(Vector<String> content) throws IOException {
        for(String word : content){
            /** new entry */
            if (hashfortitle.get(word) == null) {
                String entry = "1";
                /** adding the entry behind if there is previous entries existing */
                hashfortitle.put(word, entry);
            }
            /** add to old entry */
            else{
                int count = Integer.parseInt((String)hashfortitle.get(word)) + 1;
                hashfortitle.put(word , String.valueOf(count));
            }
        }
    }
    public HashMap<String, String> getHashforbody() {
        return hashforbody;
    }
    public HashMap<String, String> getHashfortitle() {
        return hashfortitle;
    }
    public String getCompletetitle() {
        return this.completetitle;
    }
}

