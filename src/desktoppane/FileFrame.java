/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package desktoppane;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.io.File;
import javax.swing.JInternalFrame;
import javax.swing.JSplitPane;

/**
 *
 * @author The Will of Fire
 */
public class FileFrame extends JInternalFrame{
    FilePanel filepanel;
    boolean activated = false;
    JSplitPane splitpane;
    DirPanel dirpanel;
    public File copy;
    public FileFrame(String s){
        this.setName(s);
        this.setLayout(new BorderLayout());
        dirpanel = new DirPanel(s,this);
        filepanel = new FilePanel(s,this);
        dirpanel.setFilePanel(filepanel);
        splitpane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, dirpanel, filepanel);
        splitpane.setDividerLocation(250);
        splitpane.setDividerSize(2);
        dirpanel.setMinimumSize(new Dimension(250,100));
        this.setTitle(s);
        this.getContentPane().add(splitpane);
        this.setMaximizable(true);
        this.setIconifiable(true);
        this.setClosable(true);
        this.setSize(900,400);
        this.setVisible(true);
        this.setResizable(true);
    }
    public File getSelectedFile(){
        return filepanel.getSelectedFile();
    }
    public void refresh(){
        filepanel.refresh();
    }
    public FileFrame getFileFrame(){
        return this;
    }

    public void setDetailed() {
        filepanel.setDetailed();
    }
    
    public void setSimple(){
        filepanel.setSimple();
    }
    public void changeName(String newName){
        this.setTitle(newName);
        this.revalidate();
    }

    public void collapseBranch() {
        dirpanel.collapseBranch();
    }

    public void expandBranch() {
        dirpanel.expandBranch();
    }
}
