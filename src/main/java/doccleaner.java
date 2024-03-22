
import org.htmlparser.beans.StringBean;

import java.io.*;
import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Vector;

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
    private Vector<String> extractWords(String url)
    {
        // extract words in url and return them
        // use StringTokenizer to tokenize the result from StringBean
        StringBean sb;
        sb = new StringBean();
        sb.setLinks(true);
        sb.setURL(url);
        String text = sb.getStrings();
        String[] tokens = text.split("[ ,?]+");
        Vector<String> vec_tokens = new Vector<>(Arrays.asList(tokens));
        return vec_tokens;
    }
    public static Vector<String> wordprocessing(String url){

        Vector<String> content = this.extractWords(url);
        doccleaner dr = new doccleaner("stopwords.txt");
        Vector<String> cleanedcontent = dr.cleanstopwords(content);
        cleanedcontent = dr.stemcontent(cleanedcontent);
        return cleanedcontent;
    }
}
