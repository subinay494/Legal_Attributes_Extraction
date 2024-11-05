package lucene;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.StringReader;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.en.EnglishAnalyzer;
import org.apache.lucene.analysis.tokenattributes.CharTermAttribute;
import org.apache.lucene.document.Document;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.queryparser.classic.ParseException;
import org.apache.lucene.queryparser.classic.QueryParser;
import org.apache.lucene.search.BooleanClause;
import org.apache.lucene.search.BooleanQuery;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.TopDocs;
import org.apache.lucene.search.similarities.BM25Similarity;
import org.apache.lucene.store.FSDirectory;

public class SerachSenTag {

	public ArrayList <String> WriteSearch(String queryString,String tag,String index,String field,String qid) throws IOException, ParseException {
		  
	    ArrayList <String> retrieve= new ArrayList <String> ();
	    int hitsPerPage =200; //top n documents 
	    IndexReader reader = DirectoryReader.open(FSDirectory.open(Paths.get(index)));
	    IndexSearcher searcher = new IndexSearcher(reader);
	    searcher.setSimilarity(new BM25Similarity());
	    Analyzer analyzer = new EnglishAnalyzer();
	    BufferedReader in = null;
	    QueryParser parser = new QueryParser(field, analyzer);
	    BooleanQuery.setMaxClauseCount( Integer.MAX_VALUE );
	    Query query= parser.parse(queryString);
	    QueryParser parser1 = new QueryParser("Tag", analyzer);
	    BooleanQuery.setMaxClauseCount( Integer.MAX_VALUE );
	    Query query1= parser1.parse(tag);
	   // System.out.println(query1);
//	    BooleanQuery bq = new BooleanQuery(hitsPerPage,null);
//	    bq.add(query, BooleanClause.Occur.MUST);
	    //BooleanQuery.Builder qb = new BooleanQuery.Builder();
	    //qb.add(query, BooleanClause.Occur.SHOULD);
	   // qb.add(query1, BooleanClause.Occur.SHOULD);
        
	   
	   //System.out.println("query  "+qb.build());
	    SerachSenTag t= new SerachSenTag();
	    retrieve= t.doSearch(in, searcher, query,query1,hitsPerPage,qid);    
	   
	    reader.close();
	    return  retrieve; 
//	    for (String token : tokens) {
//	    	            TermQuery tq = new TermQuery(new Term(Constants.CONTENT_FIELD, token));
//	    	            qb.add(new BooleanClause(tq, BooleanClause.Occur.SHOULD));
//	    	        }


  }	
	
	public void readInput(String fileName,String index,String content) throws IOException, ParseException {
		FileReader fr = new FileReader(new File(fileName));
	    BufferedReader br = new BufferedReader(fr);
	    FileWriter fw = new FileWriter(new File("/home/subinay/Documents/data/prior_case_retrieval/res_content_0.3.txt"));
		BufferedWriter bw = new BufferedWriter(fw);
		String line = br.readLine();
		while (line != null) {
				line = br.readLine();
				//System.out.println(line);
				try {
				ArrayList <String> retrieve= new ArrayList <String> ();
				String qid=line.split(";")[0];
				String docid=line.split(";")[1];
				
				//String queries=analyze(line.split(";")[2]);
				String queries1=line.split(";")[2];
				String queries2=line.split(";")[3];
				
				
				
				//System.out.println(line.split(";")[3]);
				//String[] words = queries.split(" ");
				//System.out.println(words.length+" "+docid);
				SerachSenTag f= new SerachSenTag();
			    //queries="murder";
			   retrieve=f.WriteSearch(queries1,queries2,index, content,qid);
			   
			    for(String r:retrieve) {
			    	bw.write(r);
			    	bw.newLine();
			    }
				}
				catch (BooleanQuery.TooManyClauses e ){
				     e.printStackTrace();
					
				}
				catch(NullPointerException e) {
					//System.out.println("NullPointerException thrown!");
				}
			//String tag=analyze(line.split(";")[0]);
			//System.out.println(tag);
					
	
			}
			bw.close();
			
  }	
	public static HashMap<String, Float> sortByValue(Map<String, Float> m1)
    {
        // Create a list from elements of HashMap
        List<Map.Entry<String, Float> > list =
               new LinkedList<Map.Entry<String, Float> >(m1.entrySet());
 
        // Sort the list
        Collections.sort(list, new Comparator<Map.Entry<String, Float> >() {
            public int compare(Map.Entry<String, Float> o1,
                               Map.Entry<String, Float> o2)
            {
                return (o2.getValue()).compareTo(o1.getValue());
            }
        });
         
        // put data from sorted list to hashmap
        HashMap<String, Float> temp = new LinkedHashMap<String, Float>();
        for (Map.Entry<String, Float> aa : list) {
            temp.put(aa.getKey(), aa.getValue());
        }
        return temp;
    }
 
	
	public String analyze(String content) throws IOException {

		StringBuffer buff = new StringBuffer();

		TokenStream stream = new EnglishAnalyzer().tokenStream("dummy", new StringReader(content));

		CharTermAttribute termAtt = stream.addAttribute(CharTermAttribute.class);

		stream.reset();

		while (stream.incrementToken()) {

			String term = termAtt.toString();

			term = term.toLowerCase();

			buff.append(term).append(" ");

		}
       
		// System.out.println(buff);

		stream.end();

		stream.close();

		return buff.toString().trim();

	}
	 public float[] sort(int [] array,int hitsPerPage) {  
		  if (hitsPerPage>1) {
			  int maximum=array[1];
			     int minimum=array[array.length-1];
			     float[] arr= new float[hitsPerPage];
			     for (int i=1;i<arr.length;i++) {
			    	 arr[i]=(float)(array[i]-minimum)/(maximum-minimum); // max normalize the similar score
			    	}
			    return arr; 
		  }
			 return new float[0];
	     }  
	  
	 public  ArrayList <String> doSearch(BufferedReader in, IndexSearcher searcher,Query query, Query query1,
             int hitsPerPage,String qid) throws IOException {

// Collect enough docs to show 5 pages
TopDocs results = searcher.search(query, hitsPerPage);
ScoreDoc[] hits = results.scoreDocs;
int numTotalHits =(int) (results.totalHits.value);
//System.out.println(numTotalHits + " total matching documents"+ qid);
TopDocs results1 = searcher.search(query1, hitsPerPage);
ScoreDoc[] hits1 = results1.scoreDocs;
int numTotalHits1 =(int) (results1.totalHits.value);


ArrayList <String> retrieve= new ArrayList <String> ();

int start = 1;
int end = Math.min(numTotalHits, hitsPerPage);
int end_tag=Math.min(numTotalHits1, hitsPerPage);



int[] score1= new int[end];
for (int i= start; i<end; i++) {
score1[i]=(int) hits[i].score;
}

SerachSenTag t= new SerachSenTag();
float[] score2= new float[numTotalHits];
score2=t.sort(score1,numTotalHits);
Map<String, Float> m1 = new HashMap<>();
Map<String, Float> m2 = new HashMap<>();
for (int i= start;i<end;i++) {
	Document doc = searcher.doc(hits[i].doc);
	String path = doc.get("Docid");
	m1.put(path,(float) (0.3*score2[i]));
}
for (int i= start;i<end_tag;i++) {
	Document doc = searcher.doc(hits1[i].doc);
	String path = doc.get("Docid");
	
	m2.put(path,(float) 0.7);
}
m2.forEach((k, v) -> m1.merge(k, v, Float::sum));




Map<String, Float> hm1 = sortByValue(m1);
//System.out.println("============");
//System.out.println(hm1);
int i=0;
for (Entry<String, Float> entry : hm1.entrySet()) {
	i=i+1;
    String key = entry.getKey();
    Float value = entry.getValue();
    retrieve.add(qid+" "+0+" "+key+" "+i+" "+value+" "+"BM25Similarity");
    //System.out.println("Key=" + key + ", Value=" + value);
}
//for (int i = start; i < end; i++) {
//
//Document doc = searcher.doc(hits[i].doc);
//String path = doc.get("Docid");
//
////System.out.println(path);
//double score = 	hits[i].score;
//
//if (path != null) {
//
//
//	retrieve.add(qid+" "+0+" "+path+" "+i+" "+score+" "+"BM25Similarity");
//	 
//
////else {
////	retrieve.add(qid+" "+0+" "+path+" "+i+" "+1+" "+"BM25Similarity");
////	return retrieve;  
////	}
//}

//retrieve.add(qid+" "+0+" "+path+" "+i+" "+score2[i]+" "+"BM25Similarity");


//System.out.println(hits[i]);
//System.out.println(qid+" "+0+" "+path+" "+i+" "+score+" "+"STANDARD");

return retrieve; 
}

	 

public static void main(String[] args) throws Exception, ParseException {
		// TODO Auto-generated method stub
		String index = "/home/subinay/IR/index_200_doc";
	    String field1 = "contents";
	    SerachSenTag t= new SerachSenTag();
	    t.readInput("/home/subinay/Documents/data/prior_case_retrieval/query_tag_new.csv", index,field1);
	    //int repeat = 0;
	    //boolean raw = false;
	}

}
