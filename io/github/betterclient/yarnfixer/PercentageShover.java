package io.github.betterclient.yarnfixer;

import java.awt.FlowLayout;

import javax.swing.*;

public class PercentageShover extends JFrame {
	private static final long serialVersionUID = 1L;
	public static JProgressBar progressBar;
	public static JLabel lbl;
	public static String lbltextNormal = "File Processing - ";
	
	public PercentageShover() {
        super();
		progressBar = new JProgressBar(0, 100);
        progressBar.setValue(0);
        progressBar.setStringPainted(true);
        
        lbl = new JLabel("Select File");
        
        this.setDefaultCloseOperation(EXIT_ON_CLOSE);
        this.setSize(300,100);
        this.setLayout(new FlowLayout(FlowLayout.CENTER));
        this.getContentPane().add(progressBar);
        this.getContentPane().add(lbl);
        this.setVisible(true);
	}
}
