package io.github.betterclient.yarnfixer;

import java.awt.Desktop;
import java.awt.FileDialog;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.InputStream;
import java.util.Arrays;
import java.util.Enumeration;
import java.util.List;
import java.util.Scanner;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;
import java.util.zip.ZipOutputStream;

import javax.swing.JFrame;

public class FixYarn {
	
	public static void main(String[] args) throws Exception {
		List<String> remove = Arrays.asList(
				"@Environment(EnvType.CLIENT)",
				"import net.fabricmc.api.Environment;",
				"import net.fabricmc.api.EnvType;",
				"/*      */ "
		);
		String change = ".java";
		File f = new File(selectFile());
		ZipFile zip = new ZipFile(f);
		Enumeration<? extends ZipEntry> ze = zip.entries();
		
		String nahhh = f.getParent();
		File file = new File(nahhh);
		File fileOutput = new File(file, "fixed-" + f.getName().substring(0, f.getName().indexOf(".") + 5) + ".zip");
		
		fileOutput.createNewFile();
		ZipOutputStream zout = new ZipOutputStream(new FileOutputStream(fileOutput));
		int indexjavaFile = 0;
		int indexdir = 0;
		int indexnonjava = 0;
		while (ze.hasMoreElements()) {
			ZipEntry entry = (ZipEntry) ze.nextElement();
			
			if(entry.getName().endsWith(change)) {
				indexjavaFile++;
				Scanner scanner = new Scanner(zip.getInputStream(entry));
				String entryy = "";
				
				Scanner decomp = new Scanner(zip.getInputStream(entry));
				int linenumber = 0;
				while (decomp.hasNextLine()) { linenumber++; decomp.nextLine(); }
				decomp.close();
				
				while (scanner.hasNextLine()) {
					String zen = (String) scanner.nextLine();
					
					for(String s : remove) {
						if(zen.contains(s.subSequence(0, s.length()))) {
							zen = zen.replace(s, "");
						}
					}
					
					int index = 1;
					for(@SuppressWarnings("unused") String s : new String[linenumber]) {
						String decompilerFix = "/*  " + index + " */ ";
						String decompilerFix2 = "/* " + index + " */ ";
						
						if(zen.contains(decompilerFix) || zen.contains(decompilerFix2)) {
							zen = zen.replace(decompilerFix, "");
							zen = zen.replace(decompilerFix2, "");
						}
						
						index++;
					}
					
					entryy = entryy + "\n" + zen;
				}
				
				File temp = File.createTempFile(entry.getName(), ".java");
				FileWriter write = new FileWriter(temp);
				write.write(entryy);
				write.close();
				FileInputStream fis = new FileInputStream(temp);
				
				if(indexjavaFile % 25 == 0) {
					System.err.println("Created " + indexjavaFile + " .java files. " + indexjavaFile + "th file was " + entry.getName());
				}
				
				zout.putNextEntry(entry);
				putEntryData(zout, fis);
				zout.closeEntry();
			}else {
				if(entry.isDirectory()) {
					indexdir++;
					if(indexdir % 50 == 0) {
						System.err.println("Created " + indexdir + " directories. " + indexdir + "th directory was " + entry.getName());
					}
					zout.putNextEntry(entry);
					zout.closeEntry();
				}else {
					indexnonjava++;
					if(indexnonjava % 100 == 0) {
						System.out.println("Created " + indexnonjava + " non .java files. " + indexnonjava + "th file was " + entry.getName());
					}
					zout.putNextEntry(entry);
					putEntryData(zout, zip.getInputStream(entry));
					zout.closeEntry();
				}
			}
		}
		Desktop desk = Desktop.getDesktop();
		desk.open(fileOutput);

		zout.close();
		zip.close();
		System.out.println("Success!");
		System.exit(0);
	}
	
	public static String selectFile() {
		JFrame jframe = new JFrame("bgruh");
		FileDialog fd = new FileDialog(jframe, "Choose a file", FileDialog.LOAD);
		fd.setDirectory("C:\\");
		fd.setFile("*.zip");
		fd.setVisible(true);
		fd.setAlwaysOnTop(true);
		fd.setFilenameFilter((dir, name) -> name.endsWith(".zip"));
		String filename = fd.getFile();
		if (filename == null)
		  System.exit(0);
		else
		  return fd.getDirectory() + fd.getFile();
		
		return null;
	}

	public static void putEntryData(ZipOutputStream zipout, InputStream data) throws Exception {
		byte[] datra = data.readAllBytes();
		zipout.write(datra, 0, datra.length);
		zipout.closeEntry();
	}
}

