/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package desktoppane;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.beans.PropertyVetoException;
import java.io.File;
import javax.swing.JDesktopPane;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JComboBox;
import java.util.ArrayList;
import java.awt.Desktop;
import java.io.IOException;

/**
 *
 * @author The Will of Fire
 */
import java.util.Vector;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JButton;
import javax.swing.event.InternalFrameEvent;
import javax.swing.event.InternalFrameListener;
import javax.swing.filechooser.FileSystemView;
class App extends JFrame{
    JPanel panel;
    JDesktopPane desktoppane;
    JMenuBar statusbar, menubar;
    JComboBox<String> driveselect;
    private ArrayList<FileFrame> panels;
    FileFrame fileframe;
    private String currentDrive;
    File current;
    
    public App(){
        panels = new ArrayList<FileFrame>();
        panel = new JPanel();
        panel.setLayout(new BorderLayout());
        desktoppane = new JDesktopPane();
        menubar = new JMenuBar();
        
        statusbar = new JMenuBar();
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.setTitle("File Manager");
        this.setSize(1800,1000);
    }
    public void go() {
        //buildToolBar();
        buildMenu();
        buildStatusBar();
        panel.add(menubar, BorderLayout.NORTH);
        panel.add(desktoppane,BorderLayout.CENTER);
        //FileFrame ff = new FileFrame();
        //desktoppane.add(ff);
        add(panel);
        this.setVisible(true);
    }
    
    private void buildMenu(){
        JMenu fileMenu, helpMenu, treeMenu, windowMenu;
        
        
        fileMenu = new JMenu("File");
        treeMenu = new JMenu("Tree");
        windowMenu = new JMenu("Window");
        helpMenu = new JMenu("Help");
        
        JMenuItem rename = new JMenuItem("Rename");
        JMenuItem copy = new JMenuItem("Copy");
        JMenuItem delete = new JMenuItem("Delete");
        JMenuItem run = new JMenuItem("Run");
        JMenuItem exit = new JMenuItem("Exit");
        
        JMenuItem expand = new JMenuItem("Expand Branch");
        JMenuItem collapse = new JMenuItem("Collapse Branch");
        
        
        JMenuItem nw = new JMenuItem("New");
        JMenuItem cascade = new JMenuItem("Cascade");
        
        JMenuItem help = new JMenuItem("Help");
        JMenuItem about = new JMenuItem("About");
        exit.addActionListener(new ExitActionListener());
        nw.addActionListener(new nwActionListener());
        about.addActionListener(new AboutActionListener());
        rename.addActionListener(new renameActionListener());
        copy.addActionListener(new copyActionListener());
        delete.addActionListener(new deleteActionListener());
        run.addActionListener(new runActionListener());
        expand.addActionListener(new expandActionListener());
        collapse.addActionListener(new collapseActionListener());
        cascade.addActionListener(new cascadeActionListener());
        help.addActionListener(new helpActionListener());
        fileMenu.add(rename);
        fileMenu.add(copy);
        fileMenu.add(delete);
        fileMenu.add(run);
        fileMenu.add(exit);
        
        treeMenu.add(expand);
        treeMenu.add(collapse);
        
        windowMenu.add(nw);
        windowMenu.add(cascade);
        
        helpMenu.add(help);
        helpMenu.add(about);
        
        File[] paths;
        paths = File.listRoots();
        Vector<String> list = new Vector<String>();
        for(File path:paths){
            FileSystemView view = FileSystemView.getFileSystemView();
            list.add(path.getAbsolutePath()+"\\");
        }
        JButton detailButton = new JButton("Details");
        JButton simpleButton = new JButton("Simple");
        
        detailButton.addActionListener(new DetailActionListener());
        simpleButton.addActionListener(new SimpleActionListener());
        
        driveselect = new JComboBox<String>(list);
        driveselect.setPreferredSize(new Dimension(5,20));
        menubar.add(fileMenu);
        menubar.add(treeMenu);
        menubar.add(windowMenu);
        menubar.add(helpMenu);
        menubar.add(driveselect);
        menubar.add(detailButton);
        menubar.add(simpleButton);
        panel.add(menubar, BorderLayout.NORTH);
    }
    private class expandActionListener implements ActionListener {

        @Override
        public void actionPerformed(ActionEvent e) {
            Component[] es = desktoppane.getComponentsInLayer(0);
            for(int i = 0; i<es.length;i++){
                if(es[0].equals(panels.get(i))){
                    panels.get(i).expandBranch();
                }
            }
        }
    }
    private class collapseActionListener implements ActionListener {

        @Override
        public void actionPerformed(ActionEvent e) {
            Component[] es = desktoppane.getComponentsInLayer(0);
            for(int i = 0; i<es.length;i++){
                if(es[0].equals(panels.get(i))){
                    panels.get(i).collapseBranch();
                }
            }
        }
    }
    private class cascadeActionListener implements ActionListener {

        @Override
        public void actionPerformed(ActionEvent e) {
           for(int i = 0;i<panels.size();i++){
                //System.out.println(i);
                panels.get(i).setLocation(i*10,i*25);
                try {
                    panels.get(i).setSelected(true);
                    //desktoppane.add(panels.get(i));
                } catch (PropertyVetoException ex) {
                    Logger.getLogger(App.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        }
    }
    public File getSelectedFile(){
        for(int i = 0; i<panels.size();i++){
            if(panels.get(i).getName()==currentDrive){
                //System.out.println(panels.get(i).getSelectedFile());
                return panels.get(i).getSelectedFile();
                
            }
        }
        //return fileframe.getSelectedFile();
        return null;
    }
    private class runActionListener implements ActionListener {

        @Override
        public void actionPerformed(ActionEvent e) {
            File selectedFile = getSelectedFile();
            if(selectedFile.canExecute()&&selectedFile.exists()){
                Desktop desktop = Desktop.getDesktop();
                try {
                    desktop.open(selectedFile);
                } catch (IOException ex) {
                    Logger.getLogger(App.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        }
    }
    private class renameActionListener implements ActionListener {

        @Override
        public void actionPerformed(ActionEvent e) {
            File selectedFile = getSelectedFile();
            if(selectedFile.exists()){
                RenameDlg dlg = new RenameDlg(null, true,selectedFile,fileframe);
                dlg.setVisible(true);
            }
            fileframe.refresh();
            fileframe.revalidate();
        }
    }
    
    
    
    private class copyActionListener implements ActionListener {

        @Override
        public void actionPerformed(ActionEvent e) {
            File selectedFile = getSelectedFile();
            //System.out.println(selectedFile);
            if(selectedFile!=null){
                if(selectedFile.exists()){
                    CopyDlg dlg = new CopyDlg(null, true,selectedFile);
                    dlg.setVisible(true);
                }
            }
            //fileframe.refresh();
            fileframe.revalidate();
        }
    }
    private class deleteActionListener implements ActionListener {

        @Override
        public void actionPerformed(ActionEvent e) {
            File selectedFile = getSelectedFile();
            if(selectedFile.exists()){
                DeleteDlg dlg = new DeleteDlg(null,true,selectedFile,fileframe);
                dlg.setVisible(true);
                fileframe.refresh();
                fileframe.revalidate();
            }
        }
    }
    private class ExitActionListener implements ActionListener {
           
        @Override
        public void actionPerformed(ActionEvent e) {
            System.exit(0);
        }
    }
    private class nwActionListener implements ActionListener {
           
        @Override
        public void actionPerformed(ActionEvent e) {
            Dimension d = new Dimension(0,100);
            panel.add(desktoppane);
            currentDrive = driveselect.getSelectedItem().toString();
            fileframe = new FileFrame(currentDrive);
            fileframe.addInternalFrameListener(new clickListener());
            //fileframe.addFocusListener(new FileFrameFocusListener());
            desktoppane.add(fileframe,d);
            panels.add(fileframe);
            //System.out.println(panels.size());
            add(panel);
            //desktoppane.repaint();
        }
    }
    private class AboutActionListener implements ActionListener{
        
        @Override
        public void actionPerformed(ActionEvent e){
            AboutDlg dlg = new AboutDlg(null, true);
            dlg.setVisible(true);
        }
    }
    private class helpActionListener implements ActionListener{
        
        @Override
        public void actionPerformed(ActionEvent e){
            helpDlg dlg = new helpDlg(null, true);
            dlg.setVisible(true);
        }
    }
    
    
    private void buildStatusBar(){
        //System.out.println(currentDrive);
        if(currentDrive!=null){
            statusbar.removeAll();
            File rootdirectory = new File(currentDrive);
            long totalspace = rootdirectory.getTotalSpace()/1000000000;
            long freespace = rootdirectory.getFreeSpace()/1000000000;
            long usedspace = totalspace-freespace;
            JLabel size = new JLabel("Current Drive:"+currentDrive + "   Free Space:" + freespace + "GB    UsedSpace:" + usedspace + "GB    Total Space:"+totalspace+"GB");
            statusbar.add(size);
            statusbar.revalidate();
        }
        else{
            statusbar.add(new JLabel(" "));
            panel.add(statusbar,BorderLayout.SOUTH);
        }
    }
    
    class clickListener implements InternalFrameListener{
        @Override
        public void internalFrameOpened(InternalFrameEvent e) {
            //throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
        }

        @Override
        public void internalFrameClosing(InternalFrameEvent e) {
            //Component[] es = desktoppane.getComponentsInLayer(0);
            Object source = e.getSource();
            //for(int i = 0; i<es.length-1;i++){
                for(int j = 0;j<panels.size();j++){
                    if(source.equals(panels.get(j))){
                        panels.remove(j);
                    }
                }
                
            //}
        }

        @Override
        public void internalFrameClosed(InternalFrameEvent e) {
            
        }

        @Override
        public void internalFrameIconified(InternalFrameEvent e) {
            //throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
        }

        @Override
        public void internalFrameDeiconified(InternalFrameEvent e) {
            //throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
        }

        @Override
        public void internalFrameActivated(InternalFrameEvent e) {
            currentDrive = e.getInternalFrame().getName();
            Component[] es = desktoppane.getComponentsInLayer(0);
            
//            Component[] es = e.getInternalFrame().getDesktopPane().getComponentsInLayer(0);
//            System.out.println("CURRENT THING WE WANT TO LOOK AT");
//            for(int i = 0; i<es.length;i++){
//                System.out.println(es[i].getName());
//                System.out.println(es[i].is);
//            }
//            System.out.println("================================");
            buildStatusBar();
            //System.out.println(e.getInternalFrame().getName()+"Avtivated");
        }

        @Override
        public void internalFrameDeactivated(InternalFrameEvent e) {
            //System.out.println("Deactivated");       
        }
    }
    private class SimpleActionListener implements ActionListener{
        
        @Override
        public void actionPerformed(ActionEvent e){
            FileFrame transform = null;
            Component[] es = desktoppane.getComponentsInLayer(0);
            //System.out.println(es[0]);
            for(int i = 0; i<es.length;i++){
                if(es[0].equals(panels.get(i))){
                    transform = panels.get(i);
                    //System.out.println(panels.get(i));
                }
            }
            if(transform != null){
                transform.setSimple();
            }
        }
    }
    private class DetailActionListener implements ActionListener{
        @Override
        public void actionPerformed(ActionEvent e){
            FileFrame transform = null;
            Component[] es = desktoppane.getComponentsInLayer(0);
            for(int i = 0; i<es.length;i++){
                if(es[0].equals(panels.get(i))){
                    transform = panels.get(i);
                }
            }
            if(transform != null){
                transform.setDetailed();
            }
            
        }
    }
}
