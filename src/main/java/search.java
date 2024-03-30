import java.io.BufferedWriter;
import java.io.FileWriter;
import java.util.Vector;
import java.io.IOException;
import java.util.*;
import jdbm.RecordManager;
import jdbm.RecordManagerFactory;
import jdbm.htree.HTree;
import jdbm.helper.FastIterator;

public class search {
//    keywordInput -> store user's input
//    foundWordId -> store all the word id of user's input
//    outputTable -> store all the hashmap needed after filter
//    foundWeb -> store all web_id and web object that need to print

//    outputTable -> <word_id, <doc_id, freq>>
//    foundWeb -> <doc_id, web object>
//    foundWordId -> <word_id, String>

    private ArrayList<String> keywordInput;
    private HashMap<Integer, String> foundWordId;
    private HashMap<Integer, HashMap<Integer,Integer> > outputTable;
    private LinkedHashMap<Integer, web> foundWeb;
    private RecordManager recman;
    private String recordmanager = "Spider";

    //total 5 table to be opened
    private String id_to_web = "id_to_web";               //<int doc_id ,web web>
    private String url_to_web = "url_to_web";
    private String word_to_id = "Htree_word_to_id";
    private String webtitle = "Htree_title";
    private String webbody = "Htree_body";
    private HTree id_to_web_table;
    private HTree url_to_web_table;
    private HTree word_to_id_table;
    private HTree webtitle_table;
    private HTree webbody_table;
    private boolean tableExist = true;

    public search(){
        System.out.println("construct search object");
    }

    public void query() throws IOException {
        System.out.println("try to get data");
        recman = RecordManagerFactory.createRecordManager(recordmanager);
        id_to_web_table = getDB(recordmanager, id_to_web);
        url_to_web_table = getDB(recordmanager, url_to_web);
        word_to_id_table = getDB(recordmanager, word_to_id);
        webtitle_table = getDB(recordmanager, webtitle);
        webbody_table = getDB(recordmanager, webbody);

        System.out.println("get data finished");

        recman.commit();

        FastIterator iter = word_to_id_table.keys();
        String word = (String) iter.next();
        while ( word != null ) {
            int id = (Integer) word_to_id_table.get(word);
            System.out.println(id);
            System.out.println(" are ");
            System.out.println(word );
            word = (String) iter.next();
        }
        if(tableExist){
            readUserInput();
            filterWordId();
            filterWeb();
            //systemPrint();
            writeFile();
        }
        recman.close();
    }


    //get htree for all five database, stop all process if the table not exist
    public HTree getDB(String recordmanager, String tablename) throws IOException {
        long tableid = recman.getNamedObject(tablename);
        System.out.println(tableid);


        if (tableid != 0) {
            return HTree.load(recman, tableid);
        } else {
            System.out.println("Required" + tablename + "not exist");
            tableExist = false;
            return null;
        }
    }

    //read user input and store in keywordInput
    //need modify by adding UI
    public void readUserInput() {
        Scanner scanner = new Scanner(System.in);
        System.out.print("Enter a list of keywords (separated by spaces): ");
        String userInput = scanner.nextLine();
        String[] keywordsArray = userInput.split("\\s+");

        keywordInput = new ArrayList<>();
        for (String keyword : keywordsArray) {
            keywordInput.add(keyword);
            System.out.println(keyword);
        }
        scanner.close();
    }

    //loop through the user input, get id from word_to_id database, save all the id into foundWordId
    public void filterWordId() throws IOException {
        foundWordId = new HashMap<Integer, String>();
        int temp;

        for (int i=0;i<keywordInput.size();i++){
            if (word_to_id_table.get(keywordInput.get(i))!=null){
                temp = (int) word_to_id_table.get(keywordInput.get(i));
                foundWordId.put(temp, keywordInput.get(i));
            } else {
                System.out.println("keyword" + keywordInput.get(i) + "does not exist in the table");
            }
        }
    }


    //save all the doc_id that contains the word_id into outputTable, and save the web data into foundweb
    //outputTable -> <word_id, <doc_id, freq>>
    //foundWeb -> <doc_id, web object>
    //foundWordId -> <word_id, string>
    public void filterWeb() throws IOException {
        outputTable = new HashMap<Integer, HashMap<Integer, Integer>>();
        foundWeb = new LinkedHashMap<Integer, web>();

        for (Map.Entry<Integer, String> set :foundWordId.entrySet()) {
            if(webbody_table.get(set.getKey())!=null){
                outputTable.put(set.getKey(), (HashMap<Integer, Integer>) webbody_table.get(set.getKey()));
            }
        }

        //add web to the output list if it cannot be found in the list
        outputTable.forEach((key, value) -> {
            value.forEach((doc, freq) -> {
                if(!foundWeb.containsKey(doc)) {
                    try {
                        foundWeb.put(doc, (web) id_to_web_table.get(doc));
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                }
            });
        });
    }


    //    page title
    //    URL
    //    date, page size
    //    keyword1 freq; keyword2 freq;
    //    child link1
    //    child link2
    //    -------------------------------------
    public void writeFile() throws IOException {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter("spider_result.txt"))) {
            for (Map.Entry<Integer, web> entry : foundWeb.entrySet()) {
                if(entry!=null&&entry.getValue()!=null){
                    writer.write(entry.getValue().getCompletetitle());
                    writer.newLine();
                    writer.write(entry.getValue().getUrl());
                    writer.newLine();
                    writer.write(String.valueOf(entry.getValue().getLastmodified_date()));
                    writer.write(", ");
                    writer.write(String.valueOf(entry.getValue().getsize()));
                    writer.newLine();


                    //print the keyword and freq, according to user input order

                    //iterate required word id
                    foundWordId.forEach((word_id, wordString) -> {
                        //iterate web list
                        outputTable.forEach((key,value) -> {
                            //get freq for each word
                            value.forEach((doc_id,freq) -> {
                                //print the required freq if the doc_id match current web entry id
                                if(entry.getKey() == doc_id){
                                    try {
                                        writer.write(wordString);
                                    } catch (IOException e) {
                                        throw new RuntimeException(e);
                                    }
                                    try {
                                        writer.write(" freq" + String.valueOf(freq) + " ");
                                    } catch (IOException e) {
                                        throw new RuntimeException(e);
                                    }
                                }
                            });
                        });
                    });
                    writer.newLine();

                    //print child link, null pointer check
                    Vector<String> child_urls = entry.getValue().getChild();
                    if (child_urls != null) {
                        for (Integer i = 0; i < child_urls.size(); i++){
                            writer.write(child_urls.get(i));
                            writer.newLine();
                        }
                    } else {
                        System.out.println("null pointer in child link.");
                    }
                    writer.write("-----------------------------------");
                }
            }
            System.out.println("Data written to spider_result.txt successfully.");
        } catch (IOException e) {
            System.err.println("An error occurred while writing to the file.");
            throw e;
        }
    }
}
