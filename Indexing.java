import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.Map.Entry;
import java.io.*;

import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.MultiFields;
import org.apache.lucene.index.PostingsEnum;
import org.apache.lucene.index.Terms;
import org.apache.lucene.index.TermsEnum;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;
import org.apache.lucene.util.BytesRef;


public class Indexing {
	HashMap<String, LinkedList<Integer>> PostingMap = new HashMap<String, LinkedList<Integer>>();
	String input;
	BufferedWriter bw;
	BufferedReader b;
	
	
	public static void main(String args[]) throws IOException
	{
		String str_p = args[0];
		String file_output = args[1];
		String file_input = args[2];
		Indexing i = new Indexing();
		i.createpostings(str_p,"text_en");
		i.createpostings(str_p,"text_fr");
		i.createpostings(str_p,"text_es");
		i.fileIPOP(file_output,file_input);		
	}
		

public void createpostings(String str_p,String text) throws IOException
{	
	Path path = Paths.get(str_p);
    Directory d = FSDirectory.open(path);
    IndexReader ir = DirectoryReader.open(d);
    Terms terms = MultiFields.getTerms(ir,text);
    TermsEnum termEnum=terms.iterator();
    Set<Entry<String, LinkedList<Integer>>> s = PostingMap.entrySet();
    Iterator<Entry<String, LinkedList<Integer>>> it = s.iterator(); 
    
  
    while (termEnum.next() != null )
    	 {
//    		dictionary.add(term.utf8ToString()); 
    		BytesRef term = termEnum.term();
    		PostingsEnum p = MultiFields.getTermDocsEnum(ir,text,term);
    		LinkedList<Integer> posting = new LinkedList<Integer>();
    		while(p.nextDoc()!=PostingsEnum.NO_MORE_DOCS)
    		{
    				posting.add(p.docID());

    		}
    		
    		PostingMap.put(term.utf8ToString(),posting);
    		//System.out.println(term.utf8ToString());

	}
    
}
	public void fileIPOP(String outputfile, String inputfile) throws IOException
	{
		
		bw = new BufferedWriter(new FileWriter(outputfile));
		FileInputStream f = new FileInputStream(new File(inputfile));
		b = new BufferedReader(new InputStreamReader(f, "UTF-8")); 
		
//		for(Map.Entry<String, LinkedList<Integer>> entry : PostingMap.entrySet())
//		  {
//			  
//			  bw.append("\nGetPostings\n"+entry.getKey()+"\n"+"Postings list: "+entry.getValue()+"\n");
//		  }

		
		while((input = b.readLine())!= null)
		{
			getposting();
			TaatAnd();
			TaatOr();
			DaatAnd();
			//TaatOR();
									
		}
		bw.close();
	}

	public void getposting() throws IOException
	{
//		String words[] = input.split(" ");
//		for(String key:PostingMap.keySet())
//		{
//			
//			for(String w : words)
//			{
//				if(key.equals(w))
//				{
//					LinkedList<Integer> posting = new LinkedList<Integer>();
//					posting = PostingMap.get(key);
//					bw.write("GetPostings \n");
//					bw.write(key+"\n");
//					bw.write("Postings List: \n");
//					for(int docID: posting)
//					{
//						bw.write(docID + " ");
//					}
//					bw.write("\n");
//					
//				}
//			}
//		}
		
		String words[] = input.split(" ");
		for(String w : words)
		{
			for(String key:PostingMap.keySet())
			{
				if(w.equals(key))
				{
					LinkedList<Integer> posting = new LinkedList<Integer>();
					posting = PostingMap.get(key);
					bw.write("GetPostings \n");
					bw.write(key+"\n");
					bw.write("Postings List: ");
					for(int docID: posting)
					{
						bw.write(docID + " ");
					}
					bw.write("\n");
					
				}
			}
		}
		
	}
	
    public void TaatAnd() throws IOException
    {
    	//String words[] = input.split(" ");
    	int count = 0;
    	System.out.println("TaatAND");
    	bw.write("TaatAnd\n");
    	TreeSet<Integer> ts = new TreeSet<Integer>();
    	LinkedList<Integer> list = new LinkedList<Integer>();
    	//LinkedList<Integer> temp = new LinkedList<Integer>();
    	String words[] = input.split(" ");
    	
    	for(String key : PostingMap.keySet())
    	{
    		for(String w : words)
    				{
    					if(key.equals(w))
    					{
    						bw.write(w+" ");
    						if(list.isEmpty())
    						{
    							list.addAll(PostingMap.get(w));
    						}
    						else if(!list.isEmpty())
    						{
    							LinkedList<Integer> t = (LinkedList<Integer>) PostingMap.get(w);
    							ListIterator<Integer> itr = t.listIterator();
    							if(ts.isEmpty())
    							{
    							for(int i: list)
    							{
    								while(itr.hasNext())
    								{
    									int j = itr.next();
    									if(j==i)
    									{
    										ts.add(i);
    										count++;
    										break;
    									}
    									else if(j>i)
    									{
    										itr.previous();
    										count++;
    										break;
    									}
    								}
    							}
    						}
    							else
    							{
    								for(int i: ts)
        							{
        								while(itr.hasNext())
        								{
        									int j = itr.next();
        									if(j==i)
        									{
        										ts.add(j);
        										count++;
        										break;
        									}
        									else if(j>i)
        									{
        										itr.previous();
        										count++;
        										break;
        									}
        								}
        							}
    							}
    						}
    						
    					}    						
    				}
    	}
    
    	
    	if(words.length==1)
    	{
    		bw.write("\nResults:");
    		for(int z: list)
    		{
    			bw.write(" "+z);
    		}
    	}
    	else
    	{
    		bw.write("\nResults:");
    	for(int i: ts)
    	{
    		bw.write(" "+i);
    	}
    	
    	if(ts.isEmpty())
    	{
    		bw.write(" Empty");
    	}
    	}
    	bw.write("\nNumber of documents in results: "+ ts.size());
		bw.write("\nNumber of comparisons: "+ count + "\n");
    }


    public void TaatOr() throws IOException
    {
    	int count = 0;
    	System.out.println("TaatOr");
    	bw.write("TaatOr\n");
    	LinkedList<Integer> list = new LinkedList<Integer>();
    	//LinkedList<Integer> p = new LinkedList<Integer>();
    	TreeSet<Integer> ts = new TreeSet<Integer>();
    	String words[] = input.split(" ");
    	
    	for(String key : PostingMap.keySet())
    	{
    		for(String w : words)
    				{
    					if(key.equals(w))
    					{
    						bw.write(w+" ");
    						if(list.isEmpty())
    						{
    							list.addAll(PostingMap.get(w));
    						}
    						else if(!list.isEmpty())
    						{
    							LinkedList<Integer> t = (LinkedList<Integer>) PostingMap.get(w);
    							ListIterator<Integer> itr = list.listIterator();
    							//HashSet<Integer> hs = new HashSet<Integer>();
    							
    							for(int i: t)
    							{
    								count++;
    								ts.add(i);
    								while(itr.hasNext())
    								{
    									ts.add(i);
    									//count++;
    									int k = itr.next();
    									if(k==i)
    									{    										
    										ts.add(k);
    										//count++;
    										break;
    										
    									}
    									else if(k>i)
    									{
   									//	itr.previous();
//    										hs.add(i);
    										ts.add(k);
    										//count++;
    										break;
    										
    									}
    									else
    									{
    										//count++;
//    										hs.add(i);
    										ts.add(k);
    									}
    								}
    							}
    						}
    						
   

    					}
    				}
    		
    	}
    	if(words.length==1)
    	{
    		bw.write("\nResults:");
    		for(int z: list)
    		{
    			bw.write(" "+z);
    		}
    	}
    	else
    	{
    	bw.write("\nResults:");
    	for(int i :ts)
    	{
    		bw.write(" "+i);
    	}
    	
    	if(ts.isEmpty())
    	{
    		bw.write(" Empty");
    	}
    	}
    	bw.write("\nNumber of documents in results: "+ ts.size());
		bw.write("\nNumber of comparisons: "+ count + "\n");
		//bw.write("\n");
    }
    

 public void DaatAnd() throws IOException
    {
	 	System.out.println("DaatAnd");
    	bw.write("DaatAnd\n");
    	String[] words = input.split(" ");
    	int count=0;
    	int docCount=0;
    	
    	//HashMap<Integer,Integer> h = new TreeMap<Integer,Integer>();
    	TreeMap<Integer,Integer> h = new TreeMap<Integer,Integer>();
    	TreeMap<Integer,Integer> res = new TreeMap<Integer, Integer>();
    	ArrayList<Iterator<Integer>> al = new ArrayList<Iterator<Integer>>();
    	int max = 0;
    	
    	for(String key : PostingMap.keySet())
    	{
    		for(String word: words)
    		{
    			if(word.equals(key))
    			{
    				bw.write(word+" ");
	    			LinkedList<Integer> ll = PostingMap.get(word);
	    			al.add(ll.iterator());
    			if(max<=PostingMap.get(word).size())
    			{
    				max = PostingMap.get(word).size();
    			}
    		}
    	}
    	}
    	for(int i = 0;i<max;i++)
    	{
    		for(int j = 0;j<al.size();j++)
    		{
    			if(al.get(j).hasNext())
    			{
    				int k = al.get(j).next();
     				count++;
    				
    				 boolean flag=false;
    				  for(Map.Entry<Integer, Integer> entry :h.entrySet())
    				  {
    					  int docId=entry.getKey();
    					  if(docId==k)
    					  {
    						  flag= true;
    					  }
    				  }  				 

    				
    						if(flag)
    						{
    							h.put(k,h.get(k)+1);
    						}
    						else
    						{
    							h.put(k, 1);
    						}
    			
    			}
    		}
    	}	
    	
    	
    	
//    	boolean flag = true;
//    	for(int e : h.keySet())
//    	{
//    		if(e==al.size())
//    		{
//    			flag=false;
//    		}
//    	}
    	
    	
    	
    	if(h.isEmpty())
    	{
    		bw.write("Empty");
    	}
    	else
    	{
    		for(Entry<Integer,Integer> e : h.entrySet())
    		{
    			res.put(e.getKey(),e.getValue());
    		  	
    		}
    	}
    	boolean flag = false;
    	if(!res.isEmpty())
    	{
    		for(Map.Entry<Integer, Integer> i2 : res.entrySet())
    		{		
    		    			
    			if(i2.getValue().equals(words.length))
    			{
    				bw.write("\nResults: "+ " "+i2.getKey());
    				docCount++;
    			}
    			else
    			{
    				
    			}
    		}
    	}
    	if(docCount==0)
    	{
    		bw.write("\nResults: Empty");
    	}
    	    	   	 	 		
    	//System.out.println("Number of documents in results: "+(h.size()-res.size()));
    	bw.write("\nNumber of documents in results: "+docCount);
    	bw.write("\nNumber of comparisons: "+ count+"\r");
    	//bw.write("\n");
    	daatOR(h);
 	

}
 public void daatOR(TreeMap<Integer, Integer> h) throws IOException
 {
	System.out.println("DaatOR");
 	bw.write("DaatOr\n");
 	//bw.write("\nResults:");
 	int docCount=0;
 	
 	String words[] = input.split(" ");
	
	for(String key : PostingMap.keySet())
	{
		for(String w : words)
				{
					if(key.equals(w))
					{
						bw.write(w+" ");
					}
				}
	}
 	
	bw.write("\nResults:");
 	if(h.isEmpty())
 	{
 		bw.write("\nResults:Empty");
 	}
 	else
 	{
 		for(Map.Entry<Integer,Integer> e :h.entrySet())
 		{
 			bw.write(" " +e.getKey());
 			docCount++;
 		}
 	}
 	
 	bw.write("\nNumber of documents in results: "+h.size());
 	bw.write("\nNumber of comparisons: "+docCount+"\n");
 	//bw.write("\n");
 }
}

// public void TaatAnd()
// 		{
// 			int count = 0;
// 	    	System.out.println("TaatAND");
// 	    	//bw.write("TaatAND\n");
// 	    	LinkedList<Integer> list = new LinkedList<Integer>();
// 	    	LinkedList<Integer> temp = new LinkedList<Integer>();
// 	    	String words[] = input.split(" ");
// 	    	
// 	    	
// 	    	
// 	    	for(String key : PostingMap.keySet())
// 	    	{
// 	    		for(String w : words)
// 	    				{
// 	    					if(key.equals(w))
// 	    					{
// 	    						//bw.write(w+" ");
// 	    						if(list.isEmpty())
// 	    						{
// 	    							list.addAll(PostingMap.get(w));
// 	    						}
// 	    						else if(!list.isEmpty())
// 	    						{
// 	    							LinkedList<Integer> t = (LinkedList<Integer>) PostingMap.get(w);
// 	    							ListIterator<Integer> itr = list.listIterator();
// 	    							ListIterator<Integer> itr1 = t.listIterator();
// 	    							while(itr.hasNext() && itr1.hasNext())
// 	    							{
// 	    								count=count+1;
// 	    							
// 	    								if(itr.next()<itr1.next())
// 	    									{
// 	    										itr.next();
// 	    									}
// 	    									else if(itr.next()>itr1.next())
// 	    									{
// 	    										itr1.next();
// 	    									}
// 	    									else
// 	    									{
// 	    										temp.add(itr.previous());
// 	    										itr.next();
// 	    										itr1.next();
// 	    									}
// 	    								}
// 	    							}
// 	    					}
// 	    				}
// 	    	}
// 	    	for(int docid : temp)
// 	    	{
// 	    		System.out.println("Results "+ docid + "  ");
// 	    	}
// 	    	
// 		}
//}
// 	    						
//    	
//    	int j=0;
//    	int k=0;
//    	    	
//    	while((j<p[0].size()) && (k<p[1].size()))
//    	{
//    		count = count+1;
//    		if(p[0].get(j)<p[1].get(k))
//    		{
//    			j++;
//    		}
//    		else if(p[0].get(j)>p[1].get(k))
//    		{
//    			k++;
//    		}
//    		else
//    		{
//    			and_taat.add(p[0].get(j));
//    			j++;
//    			k++;
//    		}
//    	}
//    	System.out.println(p[0].size());
//    	System.out.println(p[1].size());
//    	
//    	bw.write("\r\n");
//    	bw.write("\r\n");
//    	System.out.println("Number of comparisons are = " + count);
//    	bw.write("\r\n");
//    	for(int docid : and_taat)
//    	{
//    		bw.write("Results "+ docid + "  ");
//    	}
//    	
//    	
//    }
// 		
	

