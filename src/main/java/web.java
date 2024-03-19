import java.util.Vector;

public class web {
    private String url;
    private int id;

    private Vector<String> child_urls;

    web(String _url,int _id,Vector<String> child){
        url=_url;
        id=_id;
        child_urls=child;
    }

    String getUrl(){
        return url;
    }
    int getid(){
        return id;
    }
    Vector<String> getChild(){
        return child_urls;
    }
    void updateChild(Vector<String> child){
        child_urls=child;
    }
}
