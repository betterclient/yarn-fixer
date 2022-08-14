package io.github.betterclient.yarnFixer;

import java.awt.FileDialog;

import javax.swing.JFrame;

public class YarnFixer {

	public static void main(String[] args) throws Exception {
		String s = selectFile();
		if(s.endsWith(".jar")) {
			FixYarnCompiled.doit(new String[] {s});
		}else if (s.endsWith(".zip")) {
			FixYarnSource.doit(new String[] {s});
		}
	}

	public static String selectFile() {
		JFrame jframe = new JFrame("bgruh");
		FileDialog fd = new FileDialog(jframe, "Choose a file", FileDialog.LOAD);
		fd.setDirectory("C:\\");
		fd.setVisible(true);
		fd.setAlwaysOnTop(true);
		fd.setFilenameFilter((dir, name) -> name.endsWith(".jar") || name.endsWith(".zip"));
		String filename = fd.getFile();
		if (filename == null)
		  System.exit(0);
		else
		  return fd.getDirectory() + fd.getFile();
		
		return null;
	}
}
