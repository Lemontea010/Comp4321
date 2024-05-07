package search;

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

    private Vector<String> stopstem (Vector<String> content){
        Vector<String> cleancontent = new Vector<>();
        for(String element : content){
            if(!stopWords.contains(element)){
                if(element != null && porter.stripAffixes(element) != ""){
                    cleancontent.add(porter.stripAffixes(element));
                }
            }
        }
        return cleancontent;
    }

    private Vector<String> stopstemforbigram (ArrayList<String[]> content){
        Vector<String> cleancontent = new Vector<>();
        for(String[] subset : content){
            // if both element in the subset are not stopword
            if (!stopWords.contains(subset[0])&&!stopWords.contains(subset[1])){
                // if both element exist after stemming
                if (subset[0]!= null && porter.stripAffixes(subset[0]) != ""){
                    if (subset[1]!= null && porter.stripAffixes(subset[1]) != ""){
                        // combine the element to stemmed and completed bigram item
                        String bigramitem = porter.stripAffixes(subset[0]) + " " + porter.stripAffixes(subset[1]);
                        cleancontent.add(bigramitem);
                    }
                }
            }
        }
        return cleancontent;
    }


    public static Vector<String> titleprocessing(String url) throws ParserException, IOException {
        Crawler cr = new Crawler(url);
        doccleaner dr = new doccleaner("stopwords.txt");

        String title = cr.extractContent().get(0);
        /** checking */

        String[] tokens = title.split("[ ,?]+");
        Vector<String> vec_tokens = new Vector<>(Arrays.asList(tokens));

        /** remove stopword and stemming */
        return dr.stopstem(vec_tokens);
    }

    public static Vector<String> bodyprocessing(String url) throws ParserException, IOException {
        Crawler cr = new Crawler(url);
        doccleaner dr = new doccleaner("stopwords.txt");

        String body = cr.extractContent().get(1);
        String[] tokens = body.split("[ ,?]+");
        Vector<String> vec_tokens = new Vector<>(Arrays.asList(tokens));


        /** remove stopword and stemming */

        return dr.stopstem(vec_tokens);
    }

    public static Vector<String> queryprocessing(String content) {
        doccleaner dr = new doccleaner("stopwords.txt");

        String query = content;
        String[] tokens = query.split("[ ,?]+");
        Vector<String> vec_tokens = new Vector<>(Arrays.asList(tokens));

        /** remove stopword and stemming */

        return dr.stopstem(vec_tokens);
    }

    public static Vector<String> bigramprocessing(String content){
        doccleaner dr = new doccleaner("stopwords.txt");

        /** bigram construction */
        String[] tokens = content.split("[ ,?]+");
        ArrayList<String[]> bigramset = new ArrayList<>();
        for(int i = 0; i< tokens.length-1 ; i++){
            String[] subset = new String[2];
            subset[0] = tokens[i];
            subset[1] = tokens[i+1];
            bigramset.add(subset);
        }
        /** stemming and stopword removal */
        return dr.stopstemforbigram(bigramset);
    }


    public static String gettitle(String url)throws IOException {
        Crawler cr = new Crawler(url);
        String title = cr.extractContent().get(0);
        return title;
    }

    public static int getsize(String _url)throws IOException {
        URL url = new URL(_url);
        URLConnection connection=url.openConnection();
        return connection.getContentLength();
    }
    public static long get_lastmodified(String _url) throws IOException {
        URL url = new URL(_url);
        URLConnection connection =url.openConnection();
        return connection.getLastModified();
    }
    public static long get_today_date(){
        Date d =new Date();

        return d.getTime();
    }
}
