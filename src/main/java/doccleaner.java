
import org.htmlparser.beans.StringBean;
import org.htmlparser.util.ParserException;

import java.io.*;
import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.*;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

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

    private static ArrayList<String> extractContent(String url) throws IOException {

        ArrayList<String> titleandcontent = new ArrayList<>();
        // Load the web page content
        Document doc = Jsoup.connect(url).get();

        // Extract the title
        String title = doc.title();
        titleandcontent.add(title);
        // Extract the body
        Element bodyElement = doc.body();
        String body = bodyElement.text();
        titleandcontent.add(body);

        return titleandcontent;
    }

    public static Vector<String> titleprocessing(String url) throws ParserException, IOException {
        /**
        Crawler cr = new Crawler(url);
        Vector<String> content = cr.extractWords();
        */

        doccleaner dr = new doccleaner("stopwords.txt");

        String title = extractContent(url).get(0);
        StringTokenizer tok;
        tok=new StringTokenizer(title," ");
        Vector<String> titletoken=new Vector<>();
        while(tok.hasMoreElements()){
            titletoken.add(tok.nextToken());
        }
        Vector<String> cleantitle = dr.cleanstopwords(titletoken);
        cleantitle = dr.stemcontent(cleantitle);

        String body = extractContent(url).get(1);

        return cleantitle;
    }

    public static Vector<String> bodyprocessing(String url) throws ParserException, IOException {

        doccleaner dr = new doccleaner("stopwords.txt");

        String body = extractContent(url).get(1);
        StringTokenizer bok;
        bok=new StringTokenizer(body," ");
        Vector<String> bodytoken=new Vector<>();
        while(bok.hasMoreElements()){
            bodytoken.add(bok.nextToken());
        }
        Vector<String> cleanbody = dr.cleanstopwords(bodytoken);
        cleanbody = dr.stemcontent(cleanbody);


        return cleanbody;
    }
}
