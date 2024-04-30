import java.io.Serializable;
import java.util.HashMap;

import java.util.Iterator;

import java.util.Vector;

import jdbm.helper.Serializer;
import org.htmlparser.util.ParserException;
import java.io.IOException;

public  class web implements Serializable {
    private String url;
    private int id;

    private int size;
    private long lastmodified_date;
    private long update_date;
    private Vector<String> child_urls;
    private Vector<String> parent_urls;
    private Vector<String> title;
    private Vector<String> body;



    private HashMap<String , Double> score;


    private String completetitle;


    web(String _url,int _id,Vector<String> child) throws ParserException, IOException {

        this.url=_url;
        this.id=_id;
        this.child_urls=child;
        this.parent_urls = new Vector<>();

        this.score=new HashMap<>();

        /** creating a cleaned content */
        this.size = doccleaner.getsize(this.url);
        this.title=doccleaner.titleprocessing(_url);
        this.body=doccleaner.bodyprocessing(_url);
        this.completetitle = doccleaner.gettitle(this.url);
        this.lastmodified_date=doccleaner.get_lastmodified(this.url);
        this.update_date=doccleaner.get_today_date();
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
     * @return true if already in database false otherwise
     * @param parent
     * @func add the parent url if it is not inside the parent Vector
     */
    public boolean updateParent(String parent){

        //if this parent url is not in the parent vector add new vector
        for(int i=0;i< parent_urls.size();i++){
            if(parent_urls.get(i)==parent)
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
}

