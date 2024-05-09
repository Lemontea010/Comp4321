package search;

import jdbm.RecordManager;
import jdbm.RecordManagerFactory;
import jdbm.helper.FastIterator;
import jdbm.htree.HTree;

import java.io.File;
import java.io.IOException;
import java.util.*;


public class Search {

    String query;
    HTree hashfortitle;
    HTree hashforbody;
    HTree word_to_id;
    HTree hashtable;
    int totaldoc;

    public Search(String query) throws IOException {
        this.query = query;
        String var10000 = System.getProperty("user.dir");
        String pa = var10000 + File.separator + "search.Spider";
        RecordManager recman = RecordManagerFactory.createRecordManager(pa);
        long recid = recman.getNamedObject("id_to_web");
        hashtable = HTree.load(recman, recid);
        long recidoftitle = recman.getNamedObject("Htree_title");
        hashfortitle = HTree.load(recman, recidoftitle); //<int word_id,<int doc_id,int freq>>
        long recidofbody = recman.getNamedObject("Htree_body");
        hashforbody = HTree.load(recman, recidofbody); //<int word_id,<int doc_id,int freq>>
        long recidofwid = recman.getNamedObject("Htree_word_to_id");
        word_to_id = HTree.load(recman, recidofwid); //<String word , int id>

        totaldoc = 0;
        FastIterator iter = hashtable.keys();
        while( (iter.next())!=null)
        {
            totaldoc++;
        }
    }

    private Vector<String> stemmingquery(String query) {
        /** Stem and remove stop-words of the query  */
        /** return the stemmed word in the form of string vector */
        return doccleaner.queryprocessing(query);
    }

    public String getstemmedquery()  {
        /** get the complete stemmed query as a sentence */
        StringBuilder builder = new StringBuilder();
        Vector<String> queryitem = stemmingquery(this.query);
        for (String element : queryitem) {
            if (element != null){
                builder.append(element);
                builder.append(" "); // Add delimiter if desired
            }
        }
        if (!builder.isEmpty()) {
            builder.delete(builder.length() - 1, builder.length()); // Remove trailing delimiter
        }
        return builder.toString();
    }

    private double gettermweight(String term, web Web, String mode) throws IOException {
        double weight = 0;
        int wordid = (int)this.word_to_id.get(term);
        if (mode.equals("title")){
            HashMap<Integer,Integer> map = (HashMap<Integer,Integer>)this.hashfortitle.get(wordid);
            double idf = Math.log10((double) totaldoc/map.size())/Math.log(2);
            //compute normalized tf
            int maxtf = Web.getmaxtf("title");
            int tf = Web.gettf(term, "title"); // frequency of the term in doc
            weight = ((double) tf /maxtf)*idf;
        }
        else if (mode.equals("body")) {
            HashMap<Integer,Integer> map = (HashMap<Integer,Integer>)this.hashforbody.get(wordid);
            double idf = Math.log10((double) totaldoc/map.size())/Math.log(2);
            //compute normalized tf
            int maxtf = Web.getmaxtf("body");
            int tf = Web.gettf(term, "body");
            weight = ((double) tf /maxtf)*idf;
        }
        return weight;
    }

    private double gettotalweight(String mode, web Web) throws IOException {
        double totalweight = 0;
        if (mode.equals("title")){
            HashMap<String, Integer> hash = Web.getHashtitle();
            for (String item : hash.keySet()){
                totalweight += Math.pow(gettermweight(item , Web , "title"),2);
            }
        }
        else if(mode.equals("body")){
            HashMap<String, Integer> hash = Web.getHashbody();
            for (String item : hash.keySet()){
                totalweight += Math.pow(gettermweight(item , Web , "body"),2);
            }
        }
        return totalweight;
    }

    public List<web> searchresult() throws  IOException {
        ArrayList<web> result = new ArrayList<>();

        /** combine single item and bigram item */

        //iterate all search.web and compute cosine similarity
        int key = 1;
        while(hashtable.get(key)!=null)
        {
            double productsumoftitle = 0;
            double productsumofbody = 0;
            web Web = (web)hashtable.get(key);
            Vector<String> stemmedquery = stemmingquery(query);
            for (String term : stemmedquery){
                // title
                if (Web.getHashtitle().get(term)!=null){
                    productsumoftitle += 1.5*gettermweight(term, Web, "title");
                }
                // body
                if (Web.getHashbody().get(term)!=null){
                    productsumofbody += gettermweight(term, Web, "body");
                }
            }
            double score = productsumoftitle / ((Math.sqrt(gettotalweight("title", Web)))*Math.sqrt(stemmedquery.size()));
            score += productsumofbody / ((Math.sqrt(gettotalweight("body", Web)))*Math.sqrt(stemmedquery.size()));
            Web.setScore(score);
            result.add(Web);
            key++;
        }
        return result.stream().sorted(Comparator.comparingDouble(web::getScore).reversed()).toList();
    }


    public static void main(String[] args) throws IOException {
        //Get the input query from console
        while (true){
            Scanner scanner = new Scanner(System.in);
            System.out.print("Enter your input: ");
            String input = scanner.nextLine();
            //Create a search instance
            Search result = new Search(input);
            //get the displayed result
            System.out.println("User Entered: " + input);
            if (input.isEmpty()){
                continue;
            }
            //output the results in the console
            /** iterate element in search.web result */
            try {
                List<web> searchresult = result.searchresult();
                if (searchresult.isEmpty()) {
                    System.out.println("No result with stemmed entered : " + result.getstemmedquery());
                }
                else {
                    System.out.println("search.Search result with stemmed enter : "+ result.getstemmedquery());
                }
                int numberofresult = searchresult.size();
                if (numberofresult>50){
                    numberofresult = 50; // upper bound of result provided
                }
                for (int i = 0; i< numberofresult; i++){
                    web resultweb = searchresult.get(i);
                    if (resultweb.getScore() == 0){
                        break; // since the list is sorted , all subsequent search.web score 0
                    }
                    System.out.println(resultweb.getCompletetitle()+" "+resultweb.getScore());
                }
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    }
}
