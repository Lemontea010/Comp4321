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
    private RecordManager recmantitle;
    private RecordManager recmanbody;
    private HTree hashfortitle;
    private HTree hashforbody;

    web(String _url,int _id,Vector<String> child) throws ParserException, IOException {
        this.url=_url;
        this.id=_id;
        this.child_urls=child;
        this.parent_urls = new Vector<>();


        /** creating a cleaned content */
        this.title = doccleaner.titleprocessing(this.url);
        this.body = doccleaner.bodyprocessing(this.url);

        /** connect to dB */
        this.recmantitle = recmantitle;
        this.recmanbody = recmanbody;

        /** Title */
        if (this.recmantitle.getNamedObject("T" + String.valueOf(this.id)) != 0) {
            this.hashfortitle = HTree.load(this.recmantitle, this.recmantitle.getNamedObject("T" + String.valueOf(this.id)));
        }
        else{
            this.hashfortitle = HTree.createInstance(this.recmantitle);
            this.recmantitle.setNamedObject( "T" + String.valueOf(this.id), hashfortitle.getRecid());
        }
        /** Body */
        if (this.recmanbody.getNamedObject("B" + String.valueOf(this.id)) != 0) {
            this.hashforbody = HTree.load(this.recmanbody, this.recmanbody.getNamedObject("B" + String.valueOf(this.id)));
        }
        else{
            this.hashforbody = HTree.createInstance(this.recmanbody);
            this.recmanbody.setNamedObject( "B" + String.valueOf(this.id), hashforbody.getRecid());
        }
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

    /**
     *
     * @param parent
     * @func add the parent url if it is not inside the parent Vector
     */
    public void updateParent(String parent){

        //if this parent url is not in the parent vector add new vector
        for(int i=0;i< parent_urls.size();i++){
            if(parent_urls.get(i)==parent)
                return;
        }
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
}

