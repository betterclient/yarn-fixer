package io.github.betterclient.yarnfixer;

import java.awt.Desktop;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.InputStream;
import java.util.Arrays;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.List;
import java.util.Scanner;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;
import java.util.zip.ZipOutputStream;

public class FixYarnSource {
	
	public static void doit(String[] args) throws Exception {
		System.out.println("Fixing Yarn - Source Code");
		
		List<String> remove = Arrays.asList(
				"@Environment(EnvType.CLIENT)",
				"import net.fabricmc.api.Environment;",
				"import net.fabricmc.api.EnvType;",
				"/*      */ "
		);
		String change = ".java";
		File f = new File(args[0]);
		ZipFile zip = new ZipFile(f);
		Enumeration<? extends ZipEntry> ze = zip.entries();
		int allclasses = 0;
		for (Iterator<? extends ZipEntry> iterator = zip.entries().asIterator(); iterator.hasNext();) { if(!iterator.next().isDirectory()) allclasses++; }
		
		String nahhh = f.getParent();
		File file = new File(nahhh);
		File fileOutput = new File(file, "fixed-" + f.getName().substring(0, f.getName().indexOf(".") + 5) + ".zip");
		
		fileOutput.createNewFile();
		ZipOutputStream zout = new ZipOutputStream(new FileOutputStream(fileOutput));
		int indexjavaFile = 0;
		int indexdir = 0;
		int indexnonjava = 0;
		int indexTotal = 0;
		int percentage = 0;
		while (ze.hasMoreElements()) {
			indexTotal++;
			percentage = ((100 * indexTotal) / allclasses) - 1;
			PercentageShover.progressBar.setValue(percentage);
			ZipEntry entry = (ZipEntry) ze.nextElement();
			
			if(entry.getName().endsWith(change)) {
				indexjavaFile++;
				if(indexjavaFile % 10 == 0) {
					String fileLastName = entry.getName().substring(entry.getName().lastIndexOf("/") + 1, entry.getName().length());
					PercentageShover.lbl.setText(PercentageShover.lbltextNormal + fileLastName);
				}
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
					if(indexnonjava % 50 == 0) {
						String fileLastName = entry.getName().substring(entry.getName().lastIndexOf("/") + 1, entry.getName().length());
						PercentageShover.lbl.setText(PercentageShover.lbltextNormal + fileLastName);
					}
					if(indexnonjava % 100 == 0) {
						System.out.println("Created " + indexnonjava + " non .java files. " + indexnonjava + "th file was " + entry.getName());
					}
					zout.putNextEntry(entry);
					putEntryData(zout, zip.getInputStream(entry));
					zout.closeEntry();
				}
			}
		}
		YarnFixer.p.setVisible(false);
		
		Desktop desk = Desktop.getDesktop();
		desk.open(fileOutput);

		zout.close();
		zip.close();
		System.out.println("Success!");
		System.exit(0);
	}

	public static void putEntryData(ZipOutputStream zipout, InputStream data) throws Exception {
		byte[] datra = data.readAllBytes();
		zipout.write(datra, 0, datra.length);
		zipout.closeEntry();
	}
}
