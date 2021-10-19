/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package desktoppane;


import java.awt.BorderLayout;
import java.awt.Desktop;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.datatransfer.DataFlavor;
import java.awt.dnd.DnDConstants;
import java.awt.dnd.DropTarget;
import java.awt.dnd.DropTargetDropEvent;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.util.List;
import javax.swing.DefaultListModel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import java.util.ArrayList;
import java.util.Date;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;
import javax.swing.SwingUtilities;


/**
 *
 * @author Dr. Hoffman
 */

public class FilePanel extends JPanel {
    ArrayList<File> fileList;
    JList list = new JList();
    DefaultListModel model = new DefaultListModel();
    private File selectedFile, directory, copy;
    //DefaultListCellRenderer renderer;
    private final JPopupMenu popupMenu = new JPopupMenu();
    private FileFrame fileframe;
    
    public FilePanel(String s, FileFrame f){
        if(f != null){
            fileframe = f;
        }
        directory = new File(s);
        fileList = new ArrayList<File>();
        this.setLayout(new BorderLayout());
        list.setPreferredSize(new Dimension(500,500));
        this.setDropTarget(new MyDropTarget());
        list.setDragEnabled(true);
        //renderer = (DefaultListCellRenderer) list.getCellRenderer();
        list.setModel(model);
        list.setFont(new Font("monospaced", Font.BOLD, 12));
        generatePopup();
        //list.setCellRenderer(renderer);
        list.addListSelectionListener(new MyListSelectionListener());
        list.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent e) {
                JList list = (JList)e.getSource();
                if (SwingUtilities.isRightMouseButton(e))
                {
                    //JList list = (JList) e.getSource();

                    int preferredHeight = list.getPreferredSize().height;
                    int mouseHeight = e.getPoint().y;
                    int mouseX = e.getPoint().x;

                    if (mouseHeight > preferredHeight)
                        return;

                    int row = list.locationToIndex(e.getPoint());
                    list.setSelectedIndex(row);
                    //show JPopupMenu
                    showPopup(mouseX,mouseHeight);
                }
                if (e.getClickCount() == 2) 
                {
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
        });
        this.fillList(directory);
        JScrollPane filepane = new JScrollPane(list,JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED,JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
        add(filepane,BorderLayout.CENTER);
    }
    private void showPopup(int mouseX,int mouseY){
        popupMenu.show(this, mouseX, mouseY);
    }
    
    
    private void generatePopup(){
        JMenuItem Rename, Copy, Paste, Delete;
        Rename = new JMenuItem("Rename");
        Copy = new JMenuItem("Copy");
        Paste = new JMenuItem("Paste");
        Delete = new JMenuItem("Delete");
        
        
        Rename.addActionListener(new ActionListener(){
            @Override
            public void actionPerformed(ActionEvent e){
                popupMenu.setVisible(false);
                if(selectedFile.exists()){
                    RenameDlg dlg = new RenameDlg(null, true,selectedFile,fileframe);
                    dlg.setVisible(true);
                }
                if(fileframe!=null){
                  fileframe.revalidate();
                }
            }
        });
        Copy.addActionListener(new ActionListener(){
            @Override
            public void actionPerformed(ActionEvent e){
                popupMenu.setVisible(false);
                if(selectedFile.exists()){
                    fileframe.copy = selectedFile;
                }
            }
        });
        Paste.addActionListener(new ActionListener(){
            @Override
            public void actionPerformed(ActionEvent e){
                popupMenu.setVisible(false);
                System.out.println(fileframe.copy);
                if(selectedFile.exists() && fileframe.copy.exists()){
                    File directory = selectedFile.getParentFile();
                    String newDirectory = directory.toString()+"\\"+copy.getName();
                    directory = new File(newDirectory);
                    if(fileframe.copy!=null && fileframe.copy.exists()){
                      try {
                        //System.out.println("WHAT YOU NEED TO LOOK AT   " + directory.toString());
                        Files.copy(directory.toPath(),fileframe.copy.toPath(),StandardCopyOption.REPLACE_EXISTING);

                    } catch (IOException ex) {
                        Logger.getLogger(CopyDlg.class.getName()).log(Level.SEVERE, null, ex);
                    }  
                    }
                    
                }
            }
        });
        Delete.addActionListener(new ActionListener(){
            @Override
            public void actionPerformed(ActionEvent e){
                popupMenu.setVisible(false);
                if(selectedFile.exists()){
                    DeleteDlg dlg = new DeleteDlg(null,true,selectedFile,fileframe);
                    dlg.setVisible(true);
                }
                if(fileframe!=null){
                  fileframe.revalidate();
                }
            }
        });
        popupMenu.add(Rename);
        popupMenu.add(Copy);
        popupMenu.add(Paste);
        popupMenu.add(Delete);
        
    }
    
    public void fillList(File dir) {
        directory = dir;
        File[] files;
        files = dir.listFiles();
        model.clear();
        if(files!=null){
            list.removeAll();
            fileList.clear();
            for(int i =0; i<files.length;i++){
                if(!files[i].isHidden()){
                    model.addElement(files[i].getName());
                    fileList.add(files[i]);
                }
            }
            list.setModel(model);
        }
    }
    
    public void fillDetailedList(File dir){
        directory = dir;
        File[] files;
        files = dir.listFiles();
        model.clear();
        if(files!=null){
            list.removeAll();
            fileList.clear();
            for(int i =0; i<files.length;i++){
                if(!files[i].isHidden()){
                    String file = files[i].getName();
                    String fileName = String.format("%-40s", file);
                    
                    Date d = new Date(files[i].lastModified());
                    String modified = d.toString();
                    String date = String.format("%-40s",modified);
                    
                    String fileSize = files[i].length()/1000000 + "Mb";
                    
                    model.addElement(fileName  + date  + fileSize);
                    fileList.add(files[i]);
                }
            }
            list.setModel(model);
        }
    }
    
    public File getSelectedFile(){
        if(selectedFile!=null){
            if(selectedFile.exists()){
                return selectedFile;
            }
        }
        return null;
    }
    public void refresh(){
        fillList(directory);
        selectedFile = null;
    }
    
    void setSimple(){
        fillList(directory);
    }
    
    void setDetailed() {
        fillDetailedList(directory);
    }
    
    
    class MyListSelectionListener implements ListSelectionListener{
        @Override
        public void valueChanged(ListSelectionEvent e) {
            
            int fileIndex = list.getSelectedIndex();
            if(fileIndex != -1){
                File file = fileList.get(fileIndex);
                //System.out.println("SELECTED FILE: " + file);
                selectedFile = file;
                //System.out.println(fileIndex);
            }
        }
    }
    /*************************************************************************
     * class MyDropTarget handles the dropping of files onto its owner
     * (whatever JList it is assigned to). As written, it can process two
     * types: strings and files (String, File). The String type is necessary
     * to process internal source drops from another FilePanel object. The
     * File type is necessary to process drops from external sources such 
     * as Windows Explorer or IOS.
     * 
     * Note: no code is provided that actually copies files to the target
     * directory. Also, you may need to adjust this code if your list model
     * is not the default model. JList assumes a toString operation is
     * defined for whatever class is used.
     */
    class MyDropTarget extends DropTarget {
    /**************************************************************************
     * 
     * @param evt the event that caused this drop operation to be invoked
     */    
        @Override
        public void drop(DropTargetDropEvent evt){
            try {
                //types of events accepted
                evt.acceptDrop(DnDConstants.ACTION_COPY);
                //storage to hold the drop data for processing
                List result = new ArrayList();
                //what is being dropped? First, Strings are processed
                if(evt.getTransferable().isDataFlavorSupported(DataFlavor.stringFlavor)){    
                    //System.out.println(evt.getSource().);
                    String temp = (String)evt.getTransferable().getTransferData(DataFlavor.stringFlavor);
                    //String events are concatenated if more than one list item 
                    //selected in the source. The strings are separated by
                    //newline characters. Use split to break the string into
                    //individual file names and store in String[]
                    String[] next = temp.split("\\n");
                    File tempFile;
                    File oldFile = new File(temp);
                    String[] newFilePath = temp.split("\\\\");
                    File newFile = new File(directory+newFilePath[newFilePath.length-1]);
                    //add the strings to the listmodel
                    for(int i=0; i<next.length;i++){
                        //System.out.println(next[i]);
                        fileList.add(new File(next[i]));
                        //System.out.println("Temp = " + oldFile.toString() + "     Next=" + newFile.toString());
                        //Files.copy(oldFile.toPath(),newFile.toPath(),StandardCopyOption.REPLACE_EXISTING);
                        model.addElement(temp); 
                    }
                }
                else{ //then if not String, Files are assumed
                    result =(List)evt.getTransferable().getTransferData(DataFlavor.javaFileListFlavor);
                    //process the input
                    for(Object o : result){
                        //System.out.println(o.toString());
                        File oldFile = new File(o.toString());
                        String[] newFilePath = o.toString().split("\\\\");
                        //System.out.println(directory+newFilePath[newFilePath.length-1]);
                        File newFile = new File(directory+newFilePath[newFilePath.length-1]);
                        fileList.add(newFile);
                        Files.copy(oldFile.toPath(),newFile.toPath(),StandardCopyOption.REPLACE_EXISTING);
                        model.addElement(newFilePath[newFilePath.length-1]);
                    }
                }
            }
            catch(Exception ex){
                ex.printStackTrace();
            }
        }
        
    }

}