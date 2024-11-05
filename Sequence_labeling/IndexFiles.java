package lucene;

/*

 * Licensed to the Apache Software Foundation (ASF) under one or more

 * contributor license agreements.  See the NOTICE file distributed with

 * this work for additional information regarding copyright ownership.

 * The ASF licenses this file to You under the Apache License, Version 2.0

 * (the "License"); you may not use this file except in compliance with

 * the License.  You may obtain a copy of the License at

 *

 *     http://www.apache.org/licenses/LICENSE-2.0

 *

 * Unless required by applicable law or agreed to in writing, software

 * distributed under the License is distributed on an "AS IS" BASIS,

 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.

 * See the License for the specific language governing permissions and

 * limitations under the License.

 */

/*

 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license

 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template

 */

/*

 * Licensed to the Apache Software Foundation (ASF) under one or more

 * contributor license agreements.  See the NOTICE file distributed with

 * this work for additional information regarding copyright ownership.

 * The ASF licenses this file to You under the Apache License, Version 2.0

 * (the "License"); you may not use this file except in compliance with

 * the License.  You may obtain a copy of the License at

 *

 *     http://www.apache.org/licenses/LICENSE-2.0

 *

 * Unless required by applicable law or agreed to in writing, software

 * distributed under the License is distributed on an "AS IS" BASIS,

 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.

 * See the License for the specific language governing permissions and

 * limitations under the License.

 */

import org.apache.lucene.analysis.Analyzer;

import org.apache.lucene.analysis.en.EnglishAnalyzer;

import org.apache.lucene.analysis.standard.StandardAnalyzer;

import org.apache.lucene.document.Document;

import org.apache.lucene.document.Field;

//import org.apache.lucene.document.LongField;

import org.apache.lucene.document.StringField;

import org.apache.lucene.document.TextField;

import org.apache.lucene.index.IndexWriter;

import org.apache.lucene.index.IndexWriterConfig.OpenMode;

import org.apache.lucene.index.IndexWriterConfig;

import org.apache.lucene.index.Term;

import org.apache.lucene.queryparser.flexible.core.util.StringUtils;

import org.apache.lucene.store.Directory;

import org.apache.lucene.store.FSDirectory;

import java.io.BufferedReader;

import java.io.File;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;

import java.io.FileReader;

import java.io.IOException;

import java.io.InputStream;

import java.io.InputStreamReader;

import java.io.PrintWriter;

import java.io.StringReader;

import java.nio.charset.StandardCharsets;

import java.nio.file.FileVisitResult;

import java.nio.file.Files;

import java.nio.file.Path;

import java.nio.file.Paths;

import java.nio.file.SimpleFileVisitor;

import java.nio.file.attribute.BasicFileAttributes;

import java.util.ArrayList;

import java.util.Arrays;

import java.util.Date;
import java.util.HashMap;
import java.util.Scanner;

import org.apache.lucene.analysis.TokenStream;

import org.apache.lucene.analysis.tokenattributes.CharTermAttribute;

/**
 *
 * 
 * 
 * @author subinayadhikary
 * 
 */

public class IndexFiles {

	/**
	 * Index all text files under a directory.
	 * 
	 * @throws IOException
	 */

	public static void main(String[] args) throws IOException {

		String indexPath = "/home/subinay/IR/index6";

		String docsPath = "/home/subinay/Documents/suprem_court/Supreme_court_texts";

		IndexFiles inf = new IndexFiles();

		boolean create = true;

		final Path docDir = Paths.get(docsPath);

		System.out.println("Indexing to directory '" + indexPath + "'...");

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

		IndexWriter writer = new IndexWriter(dir, iwc);
		//inf.getSections("/home/subinay/Legal/Document-Clustering-TFIDF-master/Git Clustering/Clustering_code_Tfidf/ipc.txt","/home/subinay/Legal/datasets_preparation-master/DHC/variations/var_3/preprocess/section_all_sc.txt");

		inf.indexDocs(writer, docDir,
				"/home/subinay/Legal/Document-Clustering-TFIDF-master/Git Clustering/Clustering_code_Tfidf/ipc.txt","/home/subinay/Legal/datasets_preparation-master/DHC/variations/var_3/preprocess/section_all_sc.txt"
						);

		writer.close();

	}

	public void indexDocs(final IndexWriter writer, Path path, String secFile,String secFile1) throws IOException {

		HashMap<String, String> secMap = getSections(secFile,secFile1);
		
		System.out.print(secMap);
		File dir = new File(path.toString());
		File[] directoryListing = dir.listFiles();
		for (File f : directoryListing) {
			System.out.print(f);
			String ipc = secMap.get(f.getName());
			indexDoc(writer, f, Files.getLastModifiedTime(path).toMillis(), ipc);
		}

	}

	public HashMap<String, String> getSections(String filename, String filename1)
			throws FileNotFoundException, IOException {

		HashMap<String, String> secMap = new HashMap<>();

		FileReader fr = new FileReader(new File(filename));

		BufferedReader br = new BufferedReader(fr);

		String line = br.readLine();

		while (line != null) {

			String st[] = line.split("\t");
			//System.out.print(line);
			st[0] = st[0].replaceAll(" ", "");
			try {
				secMap.put(st[0], st[1] + " " + st[2]);
			} catch (Exception e) {
				secMap.put(st[0], "NA");
			}

			line = br.readLine();
			
			

		}
		fr = new FileReader(new File(filename1));

		br = new BufferedReader(fr);

		line = br.readLine();

		while (line != null) {

			String st[] = line.split("\t");
			String ipc="";
			// System.out.print(line);
			st[0] = st[0]+".txt";
			try {
				if (secMap.containsKey(st[0])) {
					ipc = secMap.get(st[0]);
				}
				;
				secMap.put(st[0], st[1]+" "+ipc);

			} catch (Exception e) {
				secMap.put(st[0], "NA");
			}

			line = br.readLine();
			

		}
		//System.out.println(secMap);

		return secMap;

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

	public String getDocument(Path file) throws IOException {

		String s = "";

		String s1 = "IPC";

		try (InputStream stream = Files.newInputStream(file)) {

			// make a new, empty document

			// Document doc = new Document();

			Scanner myReader = new Scanner(stream);

			while (myReader.hasNextLine()) {

				String line = myReader.nextLine();

				if (line.contains("IPC")) {

					// System.out.println(line);

					// String[] strArray = null;

					// converting using String.split() method with whitespace as a delimiter

					String[] strArray1 = line.split("[, .]+");

					int i = 0;

					// printing the converted string array

					try {

						for (i = 0; i < strArray1.length; i++) {

							// System.out.println(strArray1[i]);

							if (s1.equals(strArray1[i])) {

								// System.out.println(strArray1[i]);

								if (isNumeric(strArray1[i + 1])) {

									// System.out.println(strArray1[i+1]);

									s = s + strArray1[i + 1] + ",";

								}

								if (isNumeric(strArray1[i - 1])) {

									// System.out.println(strArray1[i-1]);

									s = s + strArray1[i - 1] + ",";

								}

							}

						}

					} catch (ArrayIndexOutOfBoundsException e) {

						System.out.print(i);

					}

				}

			}

		}

		return (s);

	}

	public boolean isNumeric(String str) {

		// TODO Auto-generated method stub

		// System.out.println(str);

		return str.matches("[-+]?\\d*\\.?\\d+");

	}

	/** Indexes a single document */

	public void indexDoc(IndexWriter writer, File file, long lastModified, String ipc) throws IOException {

		try (InputStream stream = Files.newInputStream(file.toPath())) {

			// make a new, empty document

			Document doc = new Document();

			Scanner myReader = new Scanner(stream);

			String s = "";

			while (myReader.hasNextLine()) {

				String data = myReader.nextLine();

				s = s + data + " ";

			}
//            System.out.println(s);
//            System.out.println(ipc);

			doc.add(new TextField("contents", s.toString(), Field.Store.YES));

			//doc.add(new TextField("Section", ipc, Field.Store.YES));

			//doc.add(new TextField("Analyzed Content", analyze(s), Field.Store.YES));

//			String path1 = "/home/subinay/Legal/Document-Clustering-Doc2vec-master/Clustering/Clean1/" + file.getName();
//
//			File f = new File(path1);
//
//			FileOutputStream fos = new FileOutputStream(f);
//
//			PrintWriter pw = new PrintWriter(fos);
//
//			pw.write(analyze(s));
//
//			pw.close();

			Field pathField = new StringField("Docid", file.getName(), Field.Store.YES);

			doc.add(pathField);

			if (writer.getConfig().getOpenMode() == OpenMode.CREATE) {

				// New index, so we just add the document (no old document can be there):

				System.out.println("adding " + file);

				writer.addDocument(doc);

			} else {

				// Existing index (an old copy of this document may have been indexed) so

				// we use updateDocument instead to replace the old one matching the exact

				// path, if present:

				System.out.println("updating " + file);

				writer.updateDocument(new Term("path", file.toString()), doc);

			}

		}

	}

}