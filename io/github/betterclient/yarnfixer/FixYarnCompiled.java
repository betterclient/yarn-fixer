package io.github.betterclient.yarnfixer;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import java.util.jar.JarOutputStream;
import java.util.zip.ZipOutputStream;

import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.tree.AnnotationNode;
import org.objectweb.asm.tree.ClassNode;

public class FixYarnCompiled {

	@SuppressWarnings("all")
	public static void doit(String[] args) throws Exception {
		System.out.println("Fixing Yarn - Compiled");
		
		File f = new File(args[0]);
		JarFile zip = new JarFile(f);
		Enumeration<? extends JarEntry> ze = zip.entries();
		int allclasses = 0;
		for (Iterator<? extends JarEntry> iterator = zip.entries().asIterator(); iterator.hasNext();) { if(!iterator.next().isDirectory()) allclasses++; }
		
		String nahhh = f.getParent();
		File file = new File(nahhh);
		File fileOutput = new File(file, "fixed-" + f.getName().substring(0, f.getName().indexOf(".") + 5) + ".jar");
		
		fileOutput.createNewFile();
		JarOutputStream zout = new JarOutputStream(new FileOutputStream(fileOutput));
		
		int indexjavaFile = 0;
		int indexdir = 0;
		int indexnonjava = 0;
		int indexTotal = 0;
		int percentage = 0;
		while (ze.hasMoreElements()) {
			indexTotal++;
			percentage = ((100 * indexTotal) / allclasses) - 1;
			PercentageShover.progressBar.setValue(percentage);
			JarEntry jarEntry = (JarEntry) ze.nextElement();
			
			if(jarEntry.getName().endsWith(".class")) {
				indexjavaFile++;
				
				if(indexjavaFile % 10 == 0) {
					String fileLastName = jarEntry.getName().substring(jarEntry.getName().lastIndexOf("/") + 1, jarEntry.getName().length());
					PercentageShover.lbl.setText(PercentageShover.lbltextNormal + fileLastName);
				}
				
				if(indexjavaFile % 25 == 0) {
					System.err.println("Created " + indexjavaFile + " .class files. " + indexjavaFile + "th file was " + jarEntry.getName());
				}
				
				ClassReader reader = new ClassReader(zip.getInputStream(jarEntry));
				ClassNode node = new ClassNode();
				reader.accept(node, 0);
				
				ArrayList<AnnotationNode> remove = new ArrayList<>();
				if(node.visibleAnnotations != null)
				for (Object obj : node.visibleAnnotations) {
					AnnotationNode type = (AnnotationNode) obj;
					
					if(type.desc.equals("L" + "net.fabricmc.api.Environment".replace('.', '/') + ";")) {
						remove.add(type);
					}
				}
				
				if(node.visibleAnnotations != null)
				node.visibleAnnotations.removeAll(remove);
				
				ClassWriter writer = new ClassWriter(0);
				node.accept(writer);
				zout.putNextEntry(jarEntry);
				putEntryData(zout, writer.toByteArray());
				zout.closeEntry();
			}else {
				if(jarEntry.isDirectory()) {
					indexdir++;
					if(indexdir % 50 == 0) {
						System.err.println("Created " + indexdir + " directories. " + indexdir + "th directory was " + jarEntry.getName());
					}
					zout.putNextEntry(jarEntry);
					zout.closeEntry();
				}else {
					indexnonjava++;
					if(indexnonjava % 50 == 0) {
						String fileLastName = jarEntry.getName().substring(jarEntry.getName().lastIndexOf("/") + 1, jarEntry.getName().length());
						PercentageShover.lbl.setText(PercentageShover.lbltextNormal + fileLastName);
					}
					if(indexnonjava % 100 == 0) {
						System.out.println("Created " + indexnonjava + " non .java files. " + indexnonjava + "th file was " + jarEntry.getName());
					}
					zout.putNextEntry(jarEntry);
					putEntryData(zout, zip.getInputStream(jarEntry));
					zout.closeEntry();
				}
			}
		}
		
		YarnFixer.p.setVisible(false);
		
		String EXPLORER_EXE = "explorer.exe";

	    String command = EXPLORER_EXE + " /SELECT,\"" + fileOutput.getAbsolutePath() + "\"";
	    Runtime.getRuntime().exec(command);
		
		zip.close();
		zout.close();
		System.out.println("Success!");
		System.exit(0);
	}

	public static void putEntryData(ZipOutputStream zipout, InputStream data) throws Exception {
		byte[] datra = data.readAllBytes();
		zipout.write(datra, 0, datra.length);
		zipout.closeEntry();
	}
	
	public static void putEntryData(ZipOutputStream zipout, byte[] data) throws Exception {
		byte[] datra = data;
		zipout.write(datra, 0, datra.length);
		zipout.closeEntry();
	}
}
