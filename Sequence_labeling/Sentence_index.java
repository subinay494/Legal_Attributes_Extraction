package lucene;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Date;
import java.util.HashMap;
import java.util.Scanner;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.en.EnglishAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.StringField;
import org.apache.lucene.document.TextField;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.index.Term;
import org.apache.lucene.index.IndexWriterConfig.OpenMode;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;

public class Sentence_index {
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		 String indexPath = "/home/subinay/IR/sentence_doc_all_murder_docs";
		   	
		    String docsPath = "/home/subinay/Documents/data/sequence_labeling/sentence_doc_all_murder_docs";
		    	
		    		
		    		
	    boolean create = true;

	    final Path docDir = Paths.get(docsPath);
	    if (!Files.isReadable(docDir)) {
	      System.out.println("Document directory '" +docDir.toAbsolutePath()+ "' does not exist or is not readable, please check the path");
	      System.exit(1);
	    }
	    
	    Date start = new Date();
	    try {
	     // System.out.println("Indexing to directory '" + indexPath + "'...");

	      Directory dir = FSDirectory.open(Paths.get(indexPath));
	      Analyzer analyzer = new EnglishAnalyzer();
	      IndexWriterConfig iwc = new IndexWriterConfig(analyzer);

	      if (create) {
	        // Create a new index in the directory, removing any
	        // previously indexed documents:
	        iwc.setOpenMode(OpenMode.CREATE);
	     } else {
	        // Add new documents to an existing index:
	        iwc.setOpenMode(OpenMode.CREATE_OR_APPEND);
	      }

	      // Optional: for better indexing performance, if you
	      // are indexing many documents, increase the RAM
	     // buffer.  But if you do this, increase the max heap
	      // size to the JVM (eg add -Xmx512m or -Xmx1g):
	      //
	      // iwc.setRAMBufferSizeMB(256.0);

	      IndexWriter writer = new IndexWriter(dir, iwc);
	      Sentence_index r=new Sentence_index();
	      r.indexDocs(writer, docDir);
	      

	      // NOTE: if you want to maximize search performance,
	      // you can optionally call forceMerge here.  This can be
	      // a terribly costly operation, so generally it's only
	      // worth it when your index is relatively static (ie
	      // you're done adding documents to it):
	      //
	      // writer.forceMerge(1);

	      writer.close();

	      //Date end = new Date();
	     // System.out.println(end.getTime() - start.getTime() + " total milliseconds");

	    } catch (IOException e) {
	      System.out.println(" caught a " + e.getClass() +
	       "\n with message: " + e.getMessage());
	    }

	}
	public void indexDocs(final IndexWriter writer, Path path) throws IOException {

		
		File dir = new File(path.toString());
		File[] directoryListing = dir.listFiles();
		for (File f : directoryListing) {
			System.out.println(f);
			indexDoc(writer, f);
			
		}
		writer.close();
	}
	public void indexDoc(IndexWriter writer, File file) throws IOException {
	    try (InputStream stream = Files.newInputStream(file.toPath())) {
	      // make a new, empty document
	      //Document doc = new Document();
	      Scanner myReader = new Scanner(stream);
	      //String s = "";
	      while (myReader.hasNextLine()) {
	        String data = myReader.nextLine();
	        
	        Document doc = index(data,file);
	        writer.addDocument(doc);
	        
	        }
	      }
	    }
	  
	public Document index(String data,File file) {
		Document doc = new Document();
		Field pathField = new StringField("Docid",file.getName().toString(), Field.Store.YES);
	     doc.add(pathField);
         doc.add(new TextField("contents",data.toString(), Field.Store.YES));
         return doc;
     
		
	}

}
