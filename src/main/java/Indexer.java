import java.util.Hashtable;

public class Indexer {

    private Hashtable<String, Integer> record;
    /** ArrayList 0: DF */

    public Indexer(){
        this.record = new Hashtable<>();
    }

    void replace_count(String word){
        int count;
        if(this.record.containsKey(word)){
            count = this.record.get(word);
            count = count + 1;
            this.record.put(word , count);
        }
        else{
            count = 1;
            this.record.put(word , count);
        }
    }

    double get_df(String word){
        return this.record.get(word);
    }
}
