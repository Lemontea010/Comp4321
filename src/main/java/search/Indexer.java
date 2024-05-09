package search;

import jdbm.RecordManager;
import jdbm.helper.FastIterator;
import jdbm.htree.HTree;

import java.io.IOException;
import java.util.HashMap;

import java.util.Vector;

public class Indexer {


    private HTree hashforbody;             //<int word_id,<int doc_id,int freq>>
    private HTree hashfortitle;             //<int word_id,<int doc_id,int freq>>
    private HTree word_to_id;               //<String word , int id>
    private int num_of_word;
    /** ArrayList 0: DF */

    public Indexer(RecordManager db) throws IOException {

        hashfortitle=HTree.createInstance(db);

        num_of_word=0;
        long recid = db.getNamedObject("Htree_title");
        if (recid != 0) {
            hashfortitle = HTree.load(db, recid);
        }else {
            hashfortitle = HTree.createInstance(db);
            db.setNamedObject("Htree_title", hashfortitle.getRecid());
        }


        recid = db.getNamedObject("Htree_body");
        if (recid != 0) {
            hashforbody = HTree.load(db, recid);
        }else {
            hashforbody = HTree.createInstance(db);
            db.setNamedObject("Htree_body", hashforbody.getRecid());
        }



        recid = db.getNamedObject("Htree_word_to_id");
        if (recid != 0) {
            word_to_id = HTree.load(db, recid);
            FastIterator it= word_to_id.keys();

            while(it.next()!=null){
                num_of_word++;
            }
        }else {
            word_to_id = HTree.createInstance(db);
            db.setNamedObject("Htree_word_to_id", word_to_id.getRecid());
        }



    }

    public void put(Vector<String> title , Vector<String> body ,int id) throws IOException {
        writefilefortitle(title,id);
        writefileforbody(body,id);

    }

    private void writefileforbody(Vector<String> content,int id) throws IOException {

        /** all stems extracted from the page body, together with all statistical information needed to
         support the vector space model (i.e., no need to support Boolean operations), are inserted
         into one inverted file */
        HashMap<Integer,Integer> temp;
        /** title: stem of the title ; body: stem of the body */
        for (String word : content) {
            /** new entry */
            if(word_to_id.get(word)==null){                 // if word not exist in the file
                word_to_id.put(word,num_of_word);
                num_of_word+=1;
            }
            int x = (int)word_to_id.get(word);
            if (hashforbody.get(x) == null) {               //if the word does not exist in the body
                int entry = 1;

                /** adding the entry behind if there is previous entries existing */
                temp=new HashMap<>();                       //create new word in body
                temp.put(id,entry);
                hashforbody.put(word_to_id.get(word), temp);
            }
            /** add to old entry */
            else {                                  //if the word exist in the body
                temp = (HashMap<Integer,Integer>)hashforbody.get(x);
                int entry;
                if(temp.get(id)==null){             // if the search.web does not exist in the file
                    entry=1;                        //entry =1
                }
                else{
                    entry=temp.get(id)+1;           //entry = original count +1
                }
                temp.put(x,entry);
                hashforbody.put(id, temp);
            }
        }
    }

    private void writefilefortitle(Vector<String> content,int id) throws IOException {
        HashMap<Integer, Integer> temp;
        /** title: stem of the title ; body: stem of the body */
        for (String word : content) {
            /** new entry */
            if (word_to_id.get(word) == null) {                 // if word not exist in the file
                word_to_id.put(word, num_of_word);
                num_of_word += 1;
            }
            int x = (int)word_to_id.get(word);
            if (hashfortitle.get(x) == null) {               //if the word does not exist in the body
                int entry = 1;
                /** adding the entry behind if there is previous entries existing */
                temp = new HashMap<>();                       //create new word in body
                temp.put(id, entry);
                hashfortitle.put(word_to_id.get(word), temp);
            }
            /** add to old entry */
            else {                                  //if the word exist in the body
                temp = (HashMap<Integer,Integer>)hashfortitle.get(x);
                int entry;
                if (temp.get(id) == null) {             // if the search.web does not exist in the file
                    entry = 1;                        //entry =1
                } else {
                    entry = temp.get(id) + 1;           //entry = original count +1
                }
                temp.put(x, entry);
                hashfortitle.put(id, temp);
            }
        }
    }

    public int getNum_of_word() {
        return num_of_word;
    }

    public HTree getHashforbody() {
        return hashforbody;
    }

    public HTree getHashfortitle() {
        return hashfortitle;
    }

    public HTree getWord_to_id() {
        return word_to_id;
    }
}
