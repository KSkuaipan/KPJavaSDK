package com.kuaipan.demo;

import com.kuaipan.client.KuaipanAPI;
import com.kuaipan.client.exception.KuaipanAuthExpiredException;
import com.kuaipan.client.exception.KuaipanIOException;
import com.kuaipan.client.exception.KuaipanServerException;
import com.kuaipan.client.model.KuaipanFile;
import com.kuaipan.client.model.KuaipanHTTPResponse;
import com.kuaipan.client.session.OauthSession;
import com.kuaipan.test.KPTestUtility;

import java.io.*;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Iterator;

import static org.junit.Assert.assertTrue;

public class SimpleCommandConsole {
	private KuaipanAPI api = null;
	private String path = "/";
	private BufferedWriter stdout;
	private BufferedReader stdin;
	private String prompt = ">";
	
	public SimpleCommandConsole(OutputStream stdout, InputStream stdin) {
		OauthSession session = new OauthSession(KPTestUtility.CONSUMER_KEY, 
				KPTestUtility.CONSUMER_SECRET, OauthSession.Root.APP_FOLDER);
		api = new KuaipanAPI(session);
		try {
			this.stdout = new BufferedWriter(new OutputStreamWriter(stdout, System.getProperty("file.encoding")));
			this.stdin = new BufferedReader(new InputStreamReader(stdin, System.getProperty("file.encoding")));
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		
	}
	
	/**
	 * return false indicates to exit.
	 * @return 
	 */
	public boolean execute() {
		empty();
		String[] args = readOneLine();
		if (args.length == 0)
			;
		else {
			String cmd = args[0];
			try {
				if (cmd.equals("ls"))
					do_ls();
				else if (cmd.equals("login"))				
					do_login();
				else if (cmd.equals("cd") && args.length > 1)				
					do_cd(args[1]);
				else if (cmd.equals("mkdir") && args.length > 1)				
					do_mkdir(args[1]);
				else if (cmd.equals("rm") && args.length > 1)				
					do_rm(args[1]);
				else if (cmd.equals("cat") && args.length > 1)				
					do_cat(args[1]);
				else if (cmd.equals("upload") && args.length > 2)				
					do_upload(args[1], args[2]);
				else if (cmd.equals("exit"))
					return false;
				else if (!cmd.isEmpty())
					do_help();
			} catch (KuaipanIOException e) {
				e.printStackTrace();
			} catch (KuaipanServerException e) {
				e.printStackTrace();
			} catch (KuaipanAuthExpiredException e) {
				println("You ought to login first.");
			}
		}
		
		return true;
	}
	
	private void do_help() {
		println("CMD");
		println("\tlogin");
		println("\t\t- authorize this application to visit your files.");
		println("\tls");
		println("\t\t- list files in current path.");		
		println("\tupload DEST SRC");
		println("\t\t- upload file from your local device to Kuaipan server.");
		println("\tcat TARGET");
		println("\t\t- show remote file content to the console.");
		println("\tmkdir DIR");
		println("\t\t- create folder.");
		println("\trm TARGET");
		println("\t\t- delete file.");
		println("\tcd TARGET");
		println("\t\t- change directory.");
		println("\texit");
		println("\t\t- exit.");
	}
	
	private void do_ls() throws KuaipanIOException, KuaipanServerException, KuaipanAuthExpiredException {
		KuaipanFile file = api.metadata(path, true);		
		for (Iterator<KuaipanFile> it=file.files.iterator(); it.hasNext();) {
			KuaipanFile temp = it.next();
			println(temp.name);
		}
	}
	
	private void do_cd(String dir) {
		this.path = joinPath(dir);
	}
	
	private void do_mkdir(String dir) throws KuaipanIOException, KuaipanServerException, KuaipanAuthExpiredException {
		api.createFolder(joinPath(dir));
	}
	
	private void do_rm(String dir) throws KuaipanIOException, KuaipanServerException, KuaipanAuthExpiredException {
		api.delete(joinPath(dir));
	}
	
	private void do_upload(String dir, String file_location) 
			throws KuaipanIOException, KuaipanServerException, KuaipanAuthExpiredException {
		File file = new File(file_location);
		long size = file.length();

		InputStream is = null;
		try {
			is = new FileInputStream(file);
		} catch (FileNotFoundException e) {}
		KuaipanFile kpfile = api.uploadFile(joinPath(dir), is, size, true, null);
		println(kpfile.toString());
	}
	
	private void do_cat(String dir) throws KuaipanIOException, KuaipanServerException, KuaipanAuthExpiredException {
		ByteArrayOutputStream os = new ByteArrayOutputStream();
		KuaipanHTTPResponse resp = api.downloadFile(joinPath(dir), os , null);
		assertTrue(resp.code == 200);
		String download_content = KPTestUtility.outputStream2String(os);
		try {
			os.close();
		} catch (IOException e) {}
		println(download_content);
	}
	
	private void do_login() throws KuaipanIOException, KuaipanServerException, KuaipanAuthExpiredException {
		String auth_url = api.requestToken();
		openBrowser(auth_url);
		println(auth_url);
		print("Visit this url and authorize the client, then press ENTER to continue.");
		waitForInput();
		api.accessToken();
	}
	
	private String joinPath(String dir) {
		if (dir.startsWith("/")) 
			return dir;
		else if (dir.equals("..")) {
			String[] path_slices = this.path.split("/");
			if (path_slices.length > 1)
				return stringJoin(path_slices, '/', 1, path_slices.length-1);
			return "/";
		}
		else {
			if (!path.endsWith("/"))
				return path + "/" + dir;
			return this.path + dir;
		}
	}
	
	private void empty() {
		print(path + prompt);
	}
	
	private void println(String str) {
		print(str+"\n");
	}
	
	private void print(String str) {
		try {
			stdout.write(str);
			stdout.flush();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	private void openBrowser(String url) {
		URI uri = null;
		try {
			uri = new URI(url);
		} catch (URISyntaxException e) {
		}
		try {
			java.awt.Desktop.getDesktop().browse(uri);
		} catch (IOException e) {
		}
	}
	
	private void waitForInput() {
		readOneLine();
	}
	
	private String[] readOneLine() {
		try {
			return stdin.readLine().split(" ");
		} catch (IOException e) {}
		return null;
	}
	
	private String stringJoin(String[] seq, char c, int start, int end) {
		if (start >= end) return "/";
		StringBuffer buf = new StringBuffer();
		for (int i=start; i<end; i++) {
			buf.append(c);
			buf.append(seq[i]);			
		}
		return buf.toString();
	}
}
