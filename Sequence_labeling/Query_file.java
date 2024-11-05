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
import org.apache.lucene.search.BooleanQuery;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.TopDocs;
import org.apache.lucene.search.similarities.BM25Similarity;
import org.apache.lucene.store.FSDirectory;

public class Query_file {
	public void WriteSearch(String queryString, String index, String field, ArrayList <String> q,int qid)
			throws IOException, ParseException {
		
		int hitsPerPage = 1000;
		IndexReader reader = DirectoryReader.open(FSDirectory.open(Paths.get(index)));
		IndexSearcher searcher = new IndexSearcher(reader);
		searcher.setSimilarity(new BM25Similarity());
		Analyzer analyzer = new EnglishAnalyzer();

		BufferedReader in = null;
		QueryParser parser = new QueryParser(field, analyzer);
		BooleanQuery.setMaxClauseCount( Integer.MAX_VALUE );
		// BM25Similarity searcher = new BM25Similarity(2,1);
		Query query = parser.parse(queryString);
		
		doSearch(in, searcher, query, hitsPerPage,q,qid);
		reader.close();

	}
	public ArrayList <String> readInput(String fileName) throws IOException, ParseException {
		FileReader fr = new FileReader(new File(fileName));
	    BufferedReader br = new BufferedReader(fr);
	    


		String line = br.readLine();
		ArrayList <String> quries= new ArrayList <String> ();
		
		
			
			while (line != null) {
				line = br.readLine();
				//System.out.println(line);
				try {
				
				quries.add(analyze(line));
				
				
				}
				catch (Exception e){
					
				}
			//String tag=analyze(line.split(";")[0]);
			//System.out.println(tag);
					
	
			}
			return quries;
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

	public void doSearch(BufferedReader in, IndexSearcher searcher, Query query, int hitsPerPage,ArrayList <String> q,int qid) throws IOException {


		TopDocs results = searcher.search(query, hitsPerPage);
		ScoreDoc[] hits = results.scoreDocs;
		int numTotalHits = (int) (results.totalHits.value);
//System.out.println(numTotalHits + " total matching documents");
		

		int start = 1;
		int end = Math.min(numTotalHits,50);

		for (int i = start; i < end; i++) {
			Document doc = searcher.doc(hits[i].doc);
			String path = doc.get("contents");
			 double score = 	hits[i].score;
			//System.out.println(path);
			
			 path=path.replaceAll(";", "");
			 path=path.replaceAll("#", "");
			 path=path.replaceAll(",", "");
			 //path=path.replaceAll(")", "");
			 //path=path.replaceAll("(", "");
			 
			q.add(qid+"##"+path+"##"+score);
			
		}
	}
	public static HashMap<String, Float> sortByValue(HashMap<String, Float> hm)
    {
        // Create a list from elements of HashMap
        List<Map.Entry<String, Float> > list =
               new LinkedList<Map.Entry<String, Float> >(hm.entrySet());
 
        // Sort the list
        Collections.sort(list, new Comparator<Map.Entry<String,Float> >() {
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
	

	public static void main(String[] args) throws IOException, ParseException {
		// TODO Auto-generated method stub
		Query_file r= new Query_file();
		String index="/home/subinay/IR/sentence_doc_all_murder_docs";
		ArrayList <String> q= new ArrayList <String> ();
		ArrayList <String> quries =r.readInput("/home/subinay/Documents/data/sequence_labeling/8_tags/assault_query_append.txt");
        int qid=0;
		for (String query: quries) {
			
			System.out.println(query);
    	   r.WriteSearch(query,index,"contents",q,qid);
    	   qid=qid+1;
       }
//		for(String x:q) {
//			System.out.println(x.split("#$")[2]);
//			System.out.println(x);
//		}
		ArrayList <String> q_final= new ArrayList <String> ();
		HashMap<String, Float> hm = new HashMap<String, Float>();
		for(int i=0;i<q.size();i++) {
			float sum=0;
			int count=0;
			for (int j=i+1;j<q.size();j++) {
				if(q.get(i).split("##")[1].equals(q.get(j).split("##")[1])) {
					count=count+1;
					try {
					sum=sum+ Float.parseFloat(q.get(j).split("##")[2]);  
				}
					
					catch (NumberFormatException nfe) {
			            System.out.println("NumberFormat Exception: invalid input string");
			        }
					  q.remove(q.get(j));  
			}
		}
			if(sum==0) {
				//q_final.add("homicidenot"+"##"+q.get(i).split("##")[1]+"##"+q.get(i).split("##")[2]);
				hm.put(q.get(i).split("##")[1],Float.parseFloat(q.get(i).split("##")[2]));
			}
			
			else {
				
				sum=sum/count;
				//q_final.add("homicidenot"+"##"+q.get(i).split("##")[1]+"##"+sum);
				hm.put(q.get(i).split("##")[1],sum);
			}
			
		}
		Map<String, Float> hm1 = sortByValue(hm);
		 
        // print the sorted hashmap
        for (Map.Entry<String, Float> en : hm1.entrySet()) {
        	q_final.add(en.getKey());
            //System.out.println("Key = " + en.getKey() +", Value = " + en.getValue());
        }
    
		
		 
        FileWriter fw = new FileWriter(new File("/home/subinay/Documents/data/sequence_labeling/8_tags/assault_query_append_feedback.txt"));
	    BufferedWriter bw = new BufferedWriter(fw);
//	    for (int i=0; i<q_final.size();i++) {
//	    	if(Float.parseFloat(q_final.get(i).split("##")[2])> 0) {
//	    		bw.write(q_final.get(i));
//	    		bw.newLine();
//	    	}
//	    }
//	    bw.close();
//	   
	    for (String query_2:q_final) {
	    	 bw.write(query_2);
	    	 bw.write(";");
	    	 //System.out.println(query_2);
	    	 String token[] =query_2.split(" ");
	    	 for(String s:token) {
	    		 bw.write("assault"+" ");
	    	 }
 	    bw.newLine();
	    }
	    bw.close();
	   
	}

}
