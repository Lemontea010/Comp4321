import org.htmlparser.util.ParserException;
import java.io.*;
import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;
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

        //vector to string
        StringBuilder sb = new StringBuilder();
        for (String element : content) {
            sb.append(element);
            sb.append(" ");
        }
        String stemword = porter.stripAffixes(String.valueOf(sb));
        //string to vector
        String[] elements = stemword.split(" ");

        for (String element : elements) {
            stem.add(element);
        }
        return stem;
    }


    public static Vector<String> titleprocessing(String url) throws ParserException, IOException {
        Crawler cr = new Crawler(url);
        doccleaner dr = new doccleaner("stopwords.txt");

        String title = cr.extractContent().get(0);
        StringTokenizer tok;
        tok=new StringTokenizer(title," ");
        Vector<String> titletoken=new Vector<>();
        while(tok.hasMoreElements()){
            titletoken.add(tok.nextToken());
        }
        Vector<String> cleantitle = dr.cleanstopwords(titletoken);
        /** stem */
        cleantitle = dr.stemcontent(cleantitle);

        return cleantitle;
    }

    public static Vector<String> bodyprocessing(String url) throws ParserException, IOException {
        Crawler cr = new Crawler(url);
        doccleaner dr = new doccleaner("stopwords.txt");

        String body = cr.extractContent().get(1);
        StringTokenizer bok;
        bok=new StringTokenizer(body," ");
        Vector<String> bodytoken=new Vector<>();
        while(bok.hasMoreElements()){
            bodytoken.add(bok.nextToken());
        }
        Vector<String> cleanbody = dr.cleanstopwords(bodytoken);
        /** stem */
        cleanbody = dr.stemcontent(cleanbody);


        return cleanbody;
    }
}
