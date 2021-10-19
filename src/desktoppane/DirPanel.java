/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package desktoppane;

import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTree;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import java.awt.BorderLayout;
import java.io.File;
import javax.swing.event.TreeExpansionEvent;
import javax.swing.event.TreeExpansionListener;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.text.Position;
import javax.swing.tree.DefaultTreeCellRenderer;
import javax.swing.tree.TreePath;

/**
 *
 * @author The Will of Fire
 */
public class DirPanel extends JPanel{
    private JScrollPane scrollpane;
    private File selectedDirectory;
    private JTree tree = new JTree();
    private FilePanel filepanel;
    DefaultTreeModel treemodel;
    DefaultMutableTreeNode root;
    private TreePath current;
    String og;
    FileFrame fileframe;
    
    public DirPanel(String s,FileFrame f){
        fileframe = f;
        og = s;
        //tree = new JTree();
        tree.addTreeSelectionListener(new MyTreeSelectionListener());
        tree.addTreeExpansionListener(new MyTreeExpansionListener());
        //setLayout(new BorderLayout());
        //tree.setPreferredSize(new Dimension(100,500));
        this.setLayout(new BorderLayout());
        buildTree(s);
        DefaultTreeCellRenderer renderer = (DefaultTreeCellRenderer) tree.getCellRenderer();
        renderer.setLeafIcon(renderer.getClosedIcon());
        JScrollPane treepane = new JScrollPane(tree,JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED,JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
        //treepane.setSize(50,50);
        //treepane.setLayout(null);
        this.add(treepane,BorderLayout.CENTER); 
    }
    public void setFilePanel(FilePanel fp){
        filepanel = fp;
    }
    
    private void buildTree(String s){
        root = new DefaultMutableTreeNode(s);
        treemodel = new DefaultTreeModel(root);
        readFiles(s);
        tree.setModel(treemodel);
    }
    
    public String getStringDirectory(){
        if(selectedDirectory!=null){
            return selectedDirectory.toString();
        }
        return og;
    }
    
    public File getSelectedDirectory(){
        return selectedDirectory;
    }

    void collapseBranch() {
        if(current!=null){
            tree.collapsePath(current);
        }
    }

    void expandBranch() {
        if(current!=null){
            tree.expandPath(current);
        }
    }
    class MyTreeExpansionListener implements TreeExpansionListener{

        @Override
        public void treeExpanded(TreeExpansionEvent event) {
            TreePath path = event.getPath();
            String stringPath = "";
            String search = "";
            for(int i = 0; i<path.getPathCount();i++){
                stringPath+=path.getPathComponent(i);
                if(i+1 == path.getPathCount()){
                    search += path.getPathComponent(i);
                }
                else{
                    stringPath+="\\";
                }
            }
            //System.out.println(stringPath);
            File expand = new File(stringPath); // THE FILE BEING EXPANDED ex. Program Files        ALREADY LOADED
            File[] expandedChildren = expand.listFiles();//List of expanded Files ex. Adobe         ALREADY LOADED
            if(expandedChildren!=null){
                for(int x = 0; x<expandedChildren.length;x++){
                    if(expandedChildren[x].isDirectory()){
                        TreePath searchedNode = tree.getNextMatch(search, 0, Position.Bias.Forward);
                        DefaultMutableTreeNode node = (DefaultMutableTreeNode)searchedNode.getLastPathComponent();  //Node of Adobe
                        if(node.getChildCount()>0){
                            if(expandedChildren[x].listFiles() != null && expandedChildren[x].listFiles().length>0){
                                File[] subExpandedChildren = expandedChildren[x].listFiles();//List of files inside expanded files  NOT LOADED
//                                int iSize = subExpandedChildren.length;
//                                for(int i = 0; i<iSize; i++){
                                        //System.out.println(iSize);
                                        if(expandedChildren[x].listFiles().length>0){
                                            
                                            TreePath subSearchedNode = tree.getNextMatch(expandedChildren[x].getName(), 0, Position.Bias.Forward);
                                            DefaultMutableTreeNode subNode = (DefaultMutableTreeNode)subSearchedNode.getLastPathComponent();  //Node of Adobe
                                            for(int a = 0; a<subExpandedChildren.length;a++){
                                                if(subExpandedChildren[a].isDirectory()){
                                                    DefaultMutableTreeNode subExpandedNode = new DefaultMutableTreeNode(subExpandedChildren[a].getName());
                                                    //System.out.println(subExpandedNode);
                                                    //System.out.println(subExpandedChildren[i]);
                                                    subNode.add(subExpandedNode);
//                                                    if(subExpandedChildren[a].isDirectory()&&subExpandedNode.getChildCount()==0){
//                                                        File[] omegaExpansion = subExpandedChildren[a].listFiles();
//                                                        for(int k = 0; k<omegaExpansion.length; k++){
//                                                            if(omegaExpansion[k].isDirectory()){
//                                                                DefaultMutableTreeNode omegaExpandedNode = new DefaultMutableTreeNode(omegaExpansion[k].getName());
//                                                                subExpandedNode.add(omegaExpandedNode);
//                                                            }
//                                                        }
//                                                    }
                                                }
                                            }
                                        //}
                                }
                            }
                        }

                    }
                }
            
                
            }
        }

        @Override
        public void treeCollapsed(TreeExpansionEvent event) {
            //treemodel.reload();
        }
        
    }
    class MyTreeSelectionListener implements TreeSelectionListener{

        @Override
        public void valueChanged(TreeSelectionEvent e) {
            current = e.getPath();
            Object[] path = e.getPath().getPath();
            DefaultMutableTreeNode node = (DefaultMutableTreeNode)
                    tree.getLastSelectedPathComponent();
            String directory = "";
            for(int i = 0; i<path.length;i++){
                if(i == 0 || i == path.length-1){
                    directory+=path[i].toString();
                }
                else{
                    directory+=path[i].toString();
                    directory+="\\";
                }
            }
            //System.out.println(directory);
            File file = new File(directory);
            selectedDirectory=file;
            //System.out.println(file.isDirectory());
            if(file.isDirectory()&&file!=null){
                filepanel.fillList(file);
            }
            fileframe.changeName(directory);
        }
    }
    private void readFiles(String s){
        File file = new File(s);
        File[] files;
        files = file.listFiles();
        //System.out.println(files[0]);
        if(files.length>1){
            for(int i =0; i<files.length;i++){
                if(files[i].isDirectory()&&!(files[i].isHidden())&&(files[i].listFiles()!= null)){
                    if(files[i].listFiles().length>0){
                        DefaultMutableTreeNode folder = new DefaultMutableTreeNode(files[i].getName());
                        root.add(folder);
                        //folder.add(new DefaultMutableTreeNode(""));
                        File subFolder = new File(files[i].getAbsolutePath());
                        File[] subfiles = subFolder.listFiles();
                        if(subfiles!=null ){
                            for(int j = 0; j<subfiles.length;j++){
                                if(subfiles[j].isDirectory()){
                                    DefaultMutableTreeNode contents = new DefaultMutableTreeNode(subfiles[j].getName());
                                    folder.add(contents);
                                }
                                
                            }
                        }
                    }
                    

                }
//                else{
//                    DefaultMutableTreeNode Subfile = new DefaultMutableTreeNode(files[i].getName());
//                    root.add(Subfile);
//                }

            }
        }
        
    }
}
