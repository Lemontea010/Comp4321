import org.htmlparser.util.ParserException;

import java.util.Vector;

public class Spider {
    private Vector<web> urls;

    private int num_urls;   //total num = num_urls+1

    /**
     *
     * @param _url
     * @throws ParserException
     */
    Spider(String _url) throws ParserException {
        Crawler crawler = new Crawler(_url);
        num_urls = 1;
        urls = new Vector<>();
        urls.add(new web(_url,0,crawler.extractLinks()));
        this.get_url_recursive(_url);

    }

    /**
     * Function add new web into web if not in the web Vector<web></>
     * @param _url
     * @return  Child link Vector<String> of current url
     */
    private Vector<String> get_url_recursive(String _url){
        try {
            Crawler crawler = new Crawler(_url);
            Vector<String> temp =crawler.extractLinks();

            for(int i=0;i<temp.size();i++){
                for(int j=0;j<urls.size();j++){
                    if(temp.get(i)==urls.get(j).getUrl()){
                        break;
                    }
                    else if(j+1==urls.size()){
                        num_urls +=1;
                        urls.add(new web(temp.get(i),num_urls,get_url_recursive(temp.get(i))));
                    }
                }
            }
            return temp;
        } catch (ParserException e) {
            e.printStackTrace();
        }
        Vector<String> temp =new Vector<>();
        return temp;
    }
    public int get_num_urls(){
        return num_urls;
    }
    public Vector<web> geturls(){
        return urls;
    }
}
