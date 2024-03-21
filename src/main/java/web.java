import org.htmlparser.beans.StringBean;

import java.util.Arrays;
import java.util.Vector;

public class web {
    private String url;
    private int id;
    private Vector<String> child_urls;

    private String parent_urls;
    web(String _url,int _id,Vector<String> child , String parent){
        this.url=_url;
        this.id=_id;
        this.child_urls=child;
        this.parent_urls = parent;
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

    String getParent_urls(){return this.parent_urls;}

    private Vector<String> extractWords()
    {
        // extract words in url and return them
        // use StringTokenizer to tokenize the result from StringBean
        StringBean sb;
        sb = new StringBean();
        sb.setLinks(true);
        sb.setURL(this.url);
        String text = sb.getStrings();
        String[] tokens = text.split("[ ,?]+");
        Vector<String> vec_tokens = new Vector<>(Arrays.asList(tokens));
        return vec_tokens;
    }

    private Vector<String> wordprocessing(){

        doccleaner dr = new doccleaner("stopwords.txt");
        Vector<String> cleanedcontent = dr.cleanstopwords(this.extractWords());
        cleanedcontent = dr.stemcontent(cleanedcontent);
        return cleanedcontent;
    }

    void writefile(Vector<String> content){

    }
}

