import jdbm.RecordManager;
import jdbm.RecordManagerFactory;
import jdbm.htree.HTree;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.LinkOption;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Vector;

public class Test {
    public static void main(String[] args) throws IOException {

        RecordManager recman = RecordManagerFactory.createRecordManager("Spider");



        long recid = recman.getNamedObject("id_to_web");
        HTree hashtable;
        if(recid!=0){
            hashtable = HTree.load(recman, recid);
            String var10000 = System.getProperty("user.dir");
            String pa = var10000 + File.separator + "spider_result.txt";
            Path path = Paths.get(pa);
            if (Files.exists(path, new LinkOption[0])) {
                (new File(pa)).delete();
            }
            File f = new File(pa);
            FileWriter file = new FileWriter(f);
            BufferedWriter output = new BufferedWriter(file);

            web x;
            for(int i=0;i<30;i++){
                x=((web)hashtable.get(i));
                output.write(x.getCompletetitle()+"\n");
                output.write(x.getUrl()+"\n");
                Date date = new Date(x.getLastmodified_date());
                output.write("Last Modified Date : "+date+" , Size : "+x.getsize()+"\n");
                HashMap<String , Integer> word=new HashMap<>();
                Vector<String> temp=x.getBody();
                for(int j=0;j<temp.size();j++){

                    if(word.containsKey(temp.get(j))){
                        int entry = word.get(temp.get(j));
                        word.put(temp.get(j),entry+1);
                    }else{
                        word.put(temp.get(j),1);
                    }
                }
                temp=x.getTitle();
                for(int j=0;j<temp.size();j++){

                    if(word.containsKey(temp.get(j))){
                        int entry = word.get(temp.get(j));
                        word.put(temp.get(j),entry+1);
                    }else{
                        word.put(temp.get(j),1);
                    }
                }
                Iterator<String> iter =word.keySet().iterator();
                String next;
                int count =0;
                while((iter.hasNext())&&count<10){
                    next=iter.next();
                    output.write(next+" " +word.get(next)+"; ");
                    count++;

                }
                output.write("\n");

                for(int j=0;j<x.getChild().size()&&j<10;j++){
                    output.write(x.getChild().get(j)+"\n");
                }
                output.write("-----------------------------------------------------------------------------------------------------------\n");
            }
            output.close();

        }
    }
}
