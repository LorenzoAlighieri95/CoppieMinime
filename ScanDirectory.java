import java.io.*;

public class ScanDirectory implements FilenameFilter  {
	String[] list;
	String ends;
	
	public ScanDirectory(String dirName, String ends) {
		this.ends=ends.toLowerCase();
		this.list = (new File(dirName)).list(this);
	}
	
	public String[] list() {
		return list;
	}
	
	public boolean accept (File dir, String s) {
		return s.toLowerCase().endsWith(ends);
	}
}