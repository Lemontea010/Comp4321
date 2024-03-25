import java.util.HashMap;

import java.util.Iterator;

import java.util.Vector;
import org.htmlparser.util.ParserException;
import java.io.IOException;

public class web {
    private String url;
    private int id;

    private int size;
    private Vector<String> child_urls;
    private Vector<String> parent_urls;
    private Vector<String> title;
    private Vector<String> body;
    private HashMap<String , Integer> hashfortitle;
    private HashMap<String , Integer> hashforbody;

    private HashMap<String , Double> score;
    private int max_word;


    private String completetitle;


    web(String _url,int _id,Vector<String> child) throws ParserException, IOException {

        this.url=_url;
        this.id=_id;
        this.child_urls=child;
        this.parent_urls = new Vector<>();
        max_word=0;

        /** creating a cleaned content */
        this.size = doccleaner.getsize(this.url);
        this.title = doccleaner.titleprocessing(this.url);
        this.body = doccleaner.bodyprocessing(this.url);
        this.completetitle = doccleaner.gettitle(this.url);

        /** Title */
        this.hashfortitle = new HashMap<>();
        /** Body */
        this.hashforbody = new HashMap<>();

        this.score=new HashMap<>();

        /** indexer */
        this.writefileforbody(this.body);
        this.writefilefortitle(this.title);
        Iterator iter = hashforbody.keySet().iterator();
        String x;
        while((x=((String)iter.next()))!=null){
            if(hashforbody.get(x)>max_word){
                max_word=hashforbody.get(x);
            }
        }

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

    void update_score(String word, double score){
        if(this.score.get(word)!=null) {
            this.score.replace(word, score);
        }else{
            this.score.put(word,score);
        }
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
    private void writefileforbody(Vector<String> content) {

        /** all stems extracted from the page body, together with all statistical information needed to
         support the vector space model (i.e., no need to support Boolean operations), are inserted
         into one inverted file */

        /** title: stem of the title ; body: stem of the body */
        for (String word : content) {
            /** new entry */
            if (hashforbody.get(word) == null) {
                int entry = 1;
                /** adding the entry behind if there is previous entries existing */
                hashforbody.put(word, entry);
            }
            /** add to old entry */
            else {
                int count = hashforbody.get(word) + 1;
                hashforbody.put(word, count);
            }
        }
    }

    private void writefilefortitle(Vector<String> content) throws IOException {
        for(String word : content){
            /** new entry */
            if (hashfortitle.get(word) == null) {
                int entry = 1;
                /** adding the entry behind if there is previous entries existing */
                hashfortitle.put(word, entry);
            }
            /** add to old entry */
            else{
                int count = hashfortitle.get(word) + 1;
                hashfortitle.put(word , count);
            }
        }
    }
    public HashMap<String, Integer> getHashforbody() {
        return hashforbody;
    }
    public HashMap<String, Integer> getHashfortitle() {
        return hashfortitle;
    }
    public String getCompletetitle() {
        return this.completetitle;
    }
    public int getsize(){
        return this.size;
    }

    public int getmax(){
        return max_word;
    }

}

