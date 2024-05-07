import org.htmlparser.util.ParserException;
import java.io.*;
import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.URL;
import java.net.URLConnection;
import java.util.*;


public class doccleaner {

    private static Porter porter;
    private static HashSet<String> stopWords;

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

    /** todo : "Hong Kong University" of Science and Technology "*/
    public static Vector<String> queryprocessing(String content) {
        doccleaner dr = new doccleaner("stopwords.txt");

        /** split out phrase and not phrase here */
        String[] tokens = content.split("[ ,?]+");
        Vector<String> vec_tokens = new Vector<>(Arrays.asList(tokens));

        /** remove stopword and stemming */

        return dr.stopstem(vec_tokens);
    }

    /** mode : 0-> not filtering for query ; 1-> filtering for indexing*/
    /** need to provide complete content */
    /** Todo : add trigram too */
    public static Vector<String> bigramprocessing(String content) {


        String[] tokens = content.split("[ ,?]+");
        /** bigram construction */
        ArrayList<String[]> bigramset = new ArrayList<>();
        for (int i = 0; i < tokens.length - 1; i++) {
            String[] subset = new String[2];
            subset[0] = tokens[i];
            subset[1] = tokens[i + 1];
            bigramset.add(subset);
        }
        /** trigram construction */
        ArrayList<String[]> trigramset = new ArrayList<>();
        for (int i = 0; i < tokens.length - 2; i++) {
            String[] subset = new String[3];
            subset[0] = tokens[i];
            subset[1] = tokens[i + 1];
            subset[2] = tokens[i + 2];
            trigramset.add(subset);
        }

        /** stemming and stopword removal */
        Vector<String> bicontent = new Vector<>();
        for (String[] subset : bigramset) {
            // if both element in the subset are not stopword
            if (!stopWords.contains(subset[0]) && !stopWords.contains(subset[1])) {
                // if both element exist after stemming
                if (subset[0] != null && porter.stripAffixes(subset[0]) != "") {
                    if (subset[1] != null && porter.stripAffixes(subset[1]) != "") {
                        // combine the element to stemmed and completed bigram item
                        String bigramitem = porter.stripAffixes(subset[0]) + "_" + porter.stripAffixes(subset[1]);
                        bicontent.add(bigramitem);
                    }
                }
            }
        }
        Vector<String> tricontent = new Vector<>();
        for (String[] subset : trigramset) {
            // if both element in the subset are not stopword
            if (!stopWords.contains(subset[0]) && !stopWords.contains(subset[1])&&!stopWords.contains(subset[2])) {
                // if both element exist after stemming
                if (subset[0] != null && porter.stripAffixes(subset[0]) != "") {
                    if (subset[1] != null && porter.stripAffixes(subset[1]) != "") {
                        if (subset[2] != null && porter.stripAffixes(subset[2]) != "") {
                            // combine the element to stemmed and completed bigram item
                            String trigramitem = porter.stripAffixes(subset[0]) + "_" + porter.stripAffixes(subset[1]) + "_" + porter.stripAffixes(subset[2]);
                            tricontent.add(trigramitem);
                        }
                    }
                }
            }
        }
        Vector<String> pmifilter = new Vector<>();
        /** perform PMI filtering */
        /** store individual item to hashmap */
        HashMap<String, Integer> freq = new HashMap<>();
        Vector<String> stem = new Vector<>();
        for(String element : tokens){
            if(!stopWords.contains(element)){
                if(element != null && porter.stripAffixes(element) != ""){
                    stem.add(porter.stripAffixes(element));
                }
            }
        }
        for (String item : stem) {
            if (freq.get(item) == null) {
                freq.put(item, 1);
            } else {
                int f = freq.get(item);
                freq.put(item, f + 1);
            }
        }
        /** store bigram and trigram item to hashmap */
        for (String biitem : bicontent) {
            if (freq.get(biitem) == null) {
                freq.put(biitem, 1);
            } else {
                int f = freq.get(biitem);
                freq.put(biitem, f + 1);
            }
        }
        for (String triitem : tricontent) {
            if (freq.get(triitem) == null) {
                freq.put(triitem, 1);
            } else {
                int f = freq.get(triitem);
                freq.put(triitem, f + 1);
            }
        }
        /** calculate the PMI and add informative phrase to return vector */
        for (String biitem : bicontent){
            String[] subpart = biitem.split("_");
            String item1 = subpart[0];
            String item2 = subpart[1];
            double pmi = Math.log10((double) (freq.get(biitem) * tokens.length) /(freq.get(item1)*freq.get(item2)))/Math.log(2);
            if (Math.abs(pmi)>3){
                /** return meaning phrase */
                pmifilter.add(biitem);
            }
        }
        for (String triitem : tricontent){
            String[] subpart = triitem.split("_");
            String item1 = subpart[0];
            String item2 = subpart[1];
            String item3 = subpart[2];
            double pmi = Math.log10((double) (freq.get(triitem) * tokens.length) /(freq.get(item1)*freq.get(item2)*freq.get(item3)))/Math.log(2);
            if (Math.abs(pmi)>3){
                /** return meaning phrase */
                pmifilter.add(triitem);
            }
        }
        return pmifilter;
    }


    public static String gettitle(String url)throws IOException {
        Crawler cr = new Crawler(url);
        return cr.extractContent().get(0);
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

}
