import java.util.*;
import java.util.Map.Entry;
import java.io.*;
import java.lang.*;

import static java.util.stream.Collectors.*;

public class Frequenze {

	final static String separatori = " ,.;?!\"\'";
	final static String directory = "./corpus";
	static Map<Character,Integer> c = new TreeMap<Character,Integer>();
	StringBuilder builder = new StringBuilder();
	String saveStats= "stats.fre";
	int paroleTotali = 0;
	int tokensTotali = 0;
	int numHapax = 0;
	public Frequenze () {
		
		Map<Object,Integer> h = new HashMap<Object,Integer>();
		ScanDirectory sd = new ScanDirectory(directory,".txt");
		StringBuilder builder = new StringBuilder();
		StringBuilder builder2 = new StringBuilder();
		String saveFreq = "freq.fre";	
		String saveRendimento = "rendimento.fre";
		
		for(String fileName : sd.list()) {
			read(fileName, h);
			System.out.println("Letto il file: "+fileName);
		}
		Map<Object,Integer> sorted = h
		        .entrySet()
		        .stream()
		        .sorted(Collections.reverseOrder(Map.Entry.comparingByValue()))
		        .collect(
		            toMap(Map.Entry::getKey, Map.Entry::getValue, (e1, e2) -> e2,
		                LinkedHashMap::new));	
		
			// calcolo hapax e frequenze
			for (Map.Entry e : sorted.entrySet()) {
				String line = e.getKey() + " = " + e.getValue() + "\n";
				builder.append(line);
				if(e.getValue().equals(1)) {
					numHapax++;	
				}	
			}		
			String text = builder.toString();
			save(saveFreq, text);		
					
			// calcolo coppie minime e rendimento funzionale
			int numCoppiaMinima=CoppiaMinima(h);
			for (Map.Entry e : c.entrySet()) {
				String line = e.getKey() + " = " + e.getValue() + "\n";
				builder2.append(line);
			}
			text=builder2.toString();
			save(saveRendimento,text);
			System.out.println("Le statistiche dei testi sono salvate nel file "+saveStats+".");
			System.out.println("L'elenco delle frequenze salvato nel file " + saveFreq + ".");			
			System.out.println("L'elenco dei suoni per rendimento funzionale salvato nel file "+ saveRendimento +"."); 
			System.out.println("Le parole totali sono "+paroleTotali);
			System.out.println("I tokens totali sono "+tokensTotali);
			System.out.println("Gli hapax totali sono "+numHapax+".");
			System.out.println("Numero totale di coppie minime: "+numCoppiaMinima+".");		
	}
	
	void add(Map<Object,Integer> m, Object v) {
		Integer o = m.get(v);
		if(o==null) {
			m.put(v,1);
		} else {
			m.put(v,o+1);
		}
	}
	
	void read(String file_input, Map<Object,Integer> h) {
		try {
			BufferedReader in = new BufferedReader(
					new FileReader(directory+"/"+file_input));
			String line = in.readLine();
			int numToken = 0;
			while (line!=null) {
				StringTokenizer st = new StringTokenizer(line, separatori);
				while(st.hasMoreTokens()) {
					add(h,st.nextToken());
					numToken++;
					tokensTotali++;
				}
				line = in.readLine();
			}
			paroleTotali = paroleTotali + h.size();
			String line2 = "- "+file_input+": "+h.size()+" parole e "+numToken+" tokens."+"\n";
			builder.append(line2);
			String text = builder.toString();
			save(saveStats, text);
			in.close();
		}catch (IOException e) {
			System.out.println("ERRORE di I/O");
			System.out.println(e);		
		}
	}
	
	static int CoppiaMinima (Map<Object,Integer> h) {
		String pattern = "[a-zA-Z]*";
		List<Object> list = new ArrayList<Object>(h.keySet());
		List<String> strings = new ArrayList<>(list.size());
		for (Object object : list) {
			if ((Objects.toString(object, null).length()!=1)&&(Objects.toString(object, null).matches(pattern))){	
				strings.add(Objects.toString(object, null).toLowerCase());
			}
		}
		Collections.sort(strings, Comparator.comparing(String::length)); 
		String saveCoppieMinime = "coppieMinime.fre";
		StringBuilder builder = new StringBuilder();
		int cont = 0;	
		int length = 0;
		for(int i = 0; i<strings.size()-1; i++) {
			int ind = i;
			length=strings.get(i).length();
			for (int j = i+1; j<strings.size();j++) {
				if (strings.get(j).length()==length) {
					if(RendimentoFunzionale(strings.get(i),strings.get(j))) {
						cont++;	
						String line = (" \n"+strings.get(i)+" : "+strings.get(j));
						builder.append(line);
					}
				}else {
					break;
				}
			}		
			strings.remove(ind);
			if(cont % 10000 == 0) {
				System.out.println("Il numero di coppie minime: " + cont);
			}
		}
		String text = builder.toString();
		save(saveCoppieMinime,text);
		System.out.println("L'elenco delle coppie minime salvato nel file: "+saveCoppieMinime);	
		return cont;
	}
	
	static boolean RendimentoFunzionale (String x, String y) {			
		Character a = null;
		Character b = null;
		int cont = 0;	
		if (x.equals(y)) {
			return false;
		} else {
			for (int i = 0; i<x.length();i++) {
				char charX = x.charAt(i);				
				char charY = y.charAt(i);		
				if(charX!=charY) {
					cont++;	
					if (cont>1) {
						return false;
					}
					a = charX;
					b = charY;
				}
			}
		}
		if (cont!=1) {
			return false;
		} else {
			if (c.containsKey(a)) {
				int i= c.get(a);
				i++;
				c.put(a,i); 	
			} else {
				c.put(a,1);
			}
			if (c.containsKey(b)) {
				int j = c.get(b);
				j++;
				c.put(b,j);
			} else {
				c.put(b,1);
			}
			return true;
		}
	}
	
	static void save (String save, String text) {
		try {
			ObjectOutputStream file_output = new ObjectOutputStream(
					new BufferedOutputStream(new FileOutputStream(save)));			
			file_output.writeChars(text);
			file_output.close();
	    }catch (IOException e) {
			System.out.println("ERRORE di I/O");
			System.out.println(e);
	    }
	}	
}
