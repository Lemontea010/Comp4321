import jdbm.RecordManager;
import jdbm.RecordManagerFactory;
import jdbm.helper.FastIterator;
import jdbm.htree.HTree;
import org.htmlparser.util.ParserException;

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

        RecordManager recman = RecordManagerFactory.createRecordManager("Spider");
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

    public String getstemmedquery() throws ParserException, IOException {
        /** get the complete stemmed query as a sentence */
        StringBuilder builder = new StringBuilder();
        Vector<String> queryitem = stemmingquery(this.query);
        queryitem.addAll(doccleaner.bigramprocessing(this.query));
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

    public Hashtable<web, Double> searchresult() throws ParserException, IOException {
        Hashtable<web, Double> result = new Hashtable<>();
        Vector<String> keyword = stemmingquery(this.query);
        /** combine single item and bigram item */
        keyword.addAll(doccleaner.bigramprocessing(this.query));

        for (String word : keyword){
            if (this.word_to_id.get(word)!=null){
                int wordid = (int)this.word_to_id.get(word);
                /** title */
                if (this.hashfortitle.get(wordid)!=null){
                    HashMap<Integer,Integer> map = (HashMap<Integer,Integer>)this.hashfortitle.get(wordid);
                    double idf = Math.log10((double) totaldoc/map.size())/Math.log(2);
                    for (int docid : map.keySet()){
                        int tf = map.get(docid);
                        web thisweb = (web)hashtable.get(docid);
                        double score = 0;
                        if(result.containsKey((web)thisweb)){
                            score = result.get(thisweb);
                            result.put(thisweb,score);
                        }
                        score = score + 1.5*tf*idf;
                        result.put(thisweb,score);
                    }
                }
                /** body */
                if (this.hashforbody.get(wordid)!=null){
                    HashMap<Integer,Integer> map = (HashMap<Integer,Integer>)this.hashforbody.get(wordid);
                    double idf = Math.log10((double) totaldoc/map.size())/Math.log(2);
                    for (int docid : map.keySet()){
                        int tf = map.get(docid);
                        web thisweb = (web)hashtable.get(docid);
                        double score = 0;
                        if(result.containsKey((web)thisweb)){
                            score = result.get(thisweb);
                            result.put(thisweb,score);
                        }
                        score = score + tf*idf;
                        result.put(thisweb,score);
                    }
                }
            }
        }
        return result;
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
            /** iterate element in web result */
            try {
                Hashtable<web, Double> searchresult = result.searchresult();
                if (searchresult.isEmpty()) {
                    System.out.println("No result with stemmed entered : " + result.getstemmedquery());
                }
                else {
                    System.out.println("Search result with stemmed enter : "+ result.getstemmedquery());
                }
                List<Map.Entry<web, Double>> entryList = new ArrayList<>(searchresult.entrySet());
                Collections.sort(entryList, Map.Entry.comparingByValue());
                Collections.reverse(entryList);
                for (Map.Entry<web, Double> entry : entryList){
                    /** entry.getKey() is web instance */
                    System.out.println(entry.getKey().getCompletetitle()+" score: "+searchresult.get(entry.getKey()));
                }
            } catch (ParserException e) {
                throw new RuntimeException(e);
            }
        }
    }
}
