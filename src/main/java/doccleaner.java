import org.htmlparser.util.ParserException;
import java.io.*;
import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.URL;
import java.net.URLConnection;
import java.util.*;


public class doccleaner {

    private Porter porter;
    private HashSet<String> stopWords;

    public doccleaner(String str){
        super();
        porter = new Porter();
        stopWords = new HashSet<>();

        BufferedReader br = null;
        try{
            FileReader fr = new FileReader(str);
            br = new BufferedReader(fr);
        }catch (FileNotFoundException e) {
            e.printStackTrace();
        }

        String line = null;
        try {
            while ((line = br.readLine()) != null) {
                stopWords.add(line);
            }
        }catch (IOException e){
            e.printStackTrace();
        }
    }

    private Vector<String> cleanstopwords (Vector<String> content){
        Vector<String> cleancontent = new Vector<>();
        for(String element : content.toArray(new String[0])){
            if(!stopWords.contains(element)){
                cleancontent.add(element);
            }
        }
        return cleancontent;
    }
    private Vector<String> stemcontent(Vector<String> content){
        Vector<String> stem = new Vector<>();

        for(String str : content){
            stem.add(porter.stripAffixes(str));
        }
        return stem;
    }

    public static Vector<String> titleprocessing(String url) throws ParserException, IOException {
        Crawler cr = new Crawler(url);
        doccleaner dr = new doccleaner("stopwords.txt");

        String title = cr.extractContent().get(0);
        /** checking */
        System.out.println("title: "+title);

        String[] tokens = title.split("[ ,?]+");
        Vector<String> vec_tokens = new Vector<>();
        for(int i=0; i< tokens.length; i++){
            vec_tokens.add(tokens[i]);
        }
        Vector<String> cleantitle = dr.cleanstopwords(vec_tokens);

        /** stem */
        cleantitle = dr.stemcontent(cleantitle);
        return cleantitle;
    }

    public static Vector<String> bodyprocessing(String url) throws ParserException, IOException {
        Crawler cr = new Crawler(url);
        doccleaner dr = new doccleaner("stopwords.txt");

        String body = cr.extractContent().get(1);
        String[] tokens = body.split("[ ,?]+");
        Vector<String> vec_tokens = new Vector<>();
        for(int i=0; i< tokens.length; i++){
            vec_tokens.add(tokens[i]);
        }
        Vector<String> cleanbody = dr.cleanstopwords(vec_tokens);
        /** stem */
        cleanbody = dr.stemcontent(cleanbody);
        return cleanbody;
    }

    public static String gettitle(String url)throws ParserException, IOException {
        Crawler cr = new Crawler(url);
        String title = cr.extractContent().get(0);
        return title;
    }

    public static int getsize(String _url)throws ParserException, IOException {
        URL url = new URL(_url);
        URLConnection connection=url.openConnection();
        return connection.getContentLength();
    }
    public static long get_lastmodified(String _url) throws IOException {
        URL url = new URL(_url);
        URLConnection connection =url.openConnection();
        return connection.getLastModified();
    }

}
