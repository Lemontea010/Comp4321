package search;

import java.io.Serializable;
import java.util.HashMap;

import java.util.Vector;

import org.htmlparser.util.ParserException;
import search.doccleaner;

import java.io.IOException;

public  class web implements Serializable {
    private String url;
    private int id;

    private int size;
    private long lastmodified_date;
    private Vector<String> child_urls;
    private Vector<String> parent_urls;
    private Vector<String> title;
    private Vector<String> body;
    private long update_date;





    private double score;
    private String completetitle;

    private HashMap<String, Integer> hashtitle; // <word , freq>
    private HashMap<String, Integer> hashbody; // <word , freq>


    public web(String _url,int _id,Vector<String> child) throws ParserException, IOException {

        this.url=_url;
        this.id=_id;
        this.child_urls=child;
        this.parent_urls = new Vector<>();
        this.score=0;

        /** creating a cleaned content */
        this.size = doccleaner.getsize(this.url);
        this.title=doccleaner.titleprocessing(_url);
        this.body=doccleaner.bodyprocessing(_url);
        this.completetitle = doccleaner.gettitle(this.url);
        this.lastmodified_date=doccleaner.get_lastmodified(this.url);
        this.update_date=doccleaner.get_today_date();

        this.hashtitle = new HashMap<>();
        this.hashbody = new HashMap<>();
        wordstore(this.title, "title");
        wordstore(this.body, "body");
        Crawler cr = new Crawler(this.url);
        wordstore(doccleaner.bigramprocessing(cr.extractContent().get(0)),"title");
        wordstore(doccleaner.bigramprocessing(cr.extractContent().get(1)),"body");
        /*for(int i=0;i<body.size();i++){
            System.out.println(body.get(i)+"\n");
        }
        for(int i=0;i<title.size();i++){
            System.out.println(title.get(i)+"\n");
        }*/



        //this.score=new HashMap<>();

        /** indexer */

        /*Iterator iter = hashforbody.keySet().iterator();
        String x;
        while(iter.hasNext()){
            x=(String)iter.next();
            if(hashforbody.get(x)>max_word){
                max_word=hashforbody.get(x);
            }
        }*/

    }
    public String getUrl(){
        return url;
    }

    public int getid(){ return id;}

    public Vector<String> getChild(){
        return child_urls;
    }

    public Vector<String> getParent(){
        return parent_urls;
    }

    void updateChild(Vector<String> child){
        child_urls=child;
    }


    /**
     * @return true if already in database false otherwise
     * @param parent
     * @func add the parent url if it is not inside the parent Vector
     */
    public boolean updateParent(String parent){

        //if this parent url is not in the parent vector add new vector
        for(int i=0;i< parent_urls.size();i++){
            if(parent_urls.get(i).equals(parent))
                return true;
        }
        this.parent_urls.add(parent);
        return false;
    }

    public String getCompletetitle() {
        return this.completetitle;
    }

    public int getsize(){
        return this.size;
    }

    public long getLastmodified_date(){
        return lastmodified_date;
    }
    public Vector<String> getTitle(){
        return title;
    }
    public Vector<String> getBody(){
        return body;
    }
    public long getUpdate_date(){return update_date;}

    public void setScore(double Score){
        this.score = Score;
    }

    public int getmaxtf(String mode){
        int maxtf = 0;
        if (mode.equals("title")){
            for(String key : this.hashtitle.keySet()){
                if (this.hashtitle.get(key)>maxtf){
                    maxtf = this.hashtitle.get(key);
                }
            }
        }
        else if(mode.equals("body")){
            for(String key : this.hashbody.keySet()){
                if (this.hashbody.get(key)>maxtf){
                    maxtf = this.hashbody.get(key);
                }
            }
        }
        return maxtf;
    }

    public int gettf(String term, String mode){
        int tf = 0;
        if (mode.equals("title")){
            tf = this.hashtitle.get(term);
        }
        else if(mode.equals("body")){
            tf = this.hashbody.get(term);
        }
        return tf;
    }

    private void wordstore(Vector<String> stemmedterm, String mode){
        if (mode.equals("title")){
            for (String term : stemmedterm){
                if (this.hashtitle.get(term)==null){
                    hashtitle.put(term, 1);
                }
                else {
                    int freq = hashtitle.get(term);
                    hashtitle.put(term, freq+1);
                }
            }
        }
        else if (mode.equals("body")) {
            for (String term : stemmedterm){
                if (this.hashbody.get(term)==null){
                    hashbody.put(term, 1);
                }
                else {
                    int freq = hashbody.get(term);
                    hashbody.put(term, freq+1);
                }
            }
        }
    }
    public HashMap<String, Integer> getHashbody() {
        return hashbody;
    }
    public HashMap<String, Integer> getHashtitle() {
        return hashtitle;
    }
    public double getScore() {
        return score;
    }
}

