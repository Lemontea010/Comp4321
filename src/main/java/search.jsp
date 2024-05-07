<%@ page import="java.util.*"%>
<%@ page import="java.lang.*"%>

<%@ page import="search.*" %>

<html lang="en">
<head>
    <meta charset="UTF-8">
    <title>COMP4321G41 WEB SEARCH</title>
</head>
<body>
    <%!
        String print_word(web cur_web)
        {

            String str="";
            HashMap<String , Integer> word=new HashMap<>();
            Vector<String> temp=cur_web.getBody();
            for(int j=0;j<temp.size();j++){

                 if(word.containsKey(temp.get(j))){
                      int entry = word.get(temp.get(j));
                      word.put(temp.get(j),entry+1);
                 }else{
                      word.put(temp.get(j),1);
                 }
            }
            temp=cur_web.getTitle();
            for(int j=0;j<temp.size();j++){

                if(word.containsKey(temp.get(j))){
                     int entry = word.get(temp.get(j));
                     word.put(temp.get(j),entry+1);
                }else{
                     word.put(temp.get(j),1);
                }
            }

            for(int j=0;j<5;j++){
                Iterator<String> iter =word.keySet().iterator();
                String next;
                int max=0;
                String stemmed_word=null;
                while((iter.hasNext())){
                    next=iter.next();

                    if(word.get(next)>max){
                        stemmed_word=next;
                        max=word.get(next);

                     }


                }
                if(stemmed_word!=null){
                    str+=stemmed_word+" "+max+"; ";
                    word.remove(stemmed_word);
                }else{
                   str+="null 0; ";

                }
            }
            str+="<br>";
            return str;

        }



    %>

    <%!
        String print_link(Vector<String> links){
            String str="";
            for(int i=0;i<5&&i<links.size();i++){
                str+="    <a href="+links.get(i)+">"+links.get(i)+"</a><br>";
            }
            return str;
        }

    %>
<form method="post" >
    Search Result:<br>
    <input type="text" name="query" size="70" value=<%request.getParameter("query");%>>
    <input type="submit" value="Enter">
<%
if(request.getParameter("query")!="")
{
	String query= request.getParameter("query");
	out.println(query);
}
else
{
	out.println("You input nothing");
}

%>
	<%--
        Search search = new search(query);
        List<web>result=search.searchresult();
	    for(int i=0;i<50&&i<result.size();i++){
	        web cur_web=result.get(i);
            out.println(cur_web.getScore()+"    "+"<a href="+cur_web.getUrl()+">"+cur_web.getCompletetitle()+"</a><br>");
            out.println("    "+"<a href="+cur_web.getUrl()+">"+cur_web.getUrl()+"</a><br>");
            Date date = new Date(cur_web.getLastmodified_date());
            out.println("    "+"Last Modified Date : "+date+" , Size : "+cur_web.getsize()+"<br>");
            print_word(cur_web);
            print_link(cur_web.getParent());
            print_link(cur_web.getChild());
            out.println("<br><br>");
	    }




	--%>
<hr>
<%


%>
</body>
</html>
