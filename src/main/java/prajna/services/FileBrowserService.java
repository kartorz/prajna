package prajna.services;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import javax.annotation.Resource;

//import java.util.function.Consumer;
//import java.util.stream.Stream;

import javax.servlet.ServletContext;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Service;

import prajna.models.FileItem;

@Service
@Scope("application")
public class FileBrowserService {
	private static final Logger logger = LogManager.getLogger(FileBrowserService.class.getName());
	public static String  edocDir = "/raid/file/edoc";
	//public static final String  edocDir = "/mnt/edoc";
	//public static final String  edocDir = "/home/joni/Downloads/edoc";
	
	@Autowired
    ServletContext context;
	
	@Resource public Environment env;
	
	public FileBrowserService() {
		/*String docDirSetting = env.getProperty("edoc_rootdir");
		if (docDirSetting != null && !docDirSetting.isEmpty())
			edocDir = docDirSetting;*/
	}
	
	public List<FileItem> getFileList(String path, boolean recursive) {
		//logger.info("[getFileList], path:" + path);
    	List<FileItem> list = new ArrayList<FileItem>();
		File file = new File(edocDir + path);
        if (file.exists() && file.isDirectory()) {
        	FileItem.resetId();
    		getFileList(file, list, recursive);
        }
        return list;
	}
	
	public List<FileItem> getFileList(File file, boolean recursive) {
		//logger.info("[getFileList], path:" + file.getPath());
    	List<FileItem> list = new ArrayList<FileItem>();
        if (file.exists() && file.isDirectory()) {
        	FileItem.resetId();
    		getFileList(file, list, recursive);
        }
        return list;
	}
	
	private void getFileList(File parentDir, List<FileItem> list, boolean recursive) {
		File[] files = parentDir.listFiles();
		for (File f : files) {
			String path = f.getPath().replaceFirst(edocDir, "");
        	//logger.info("[getFileList2],path:" + path);
			if (f.isFile()) {
            	FileItem item = new FileItem(f);
            	item.setPath(path);
            	list.add(item);
        	} else	if (recursive && f.isDirectory()) {
        		getFileList(f, list, recursive);
        	}
        }
	}
	
	
	public FileItem getDirTree(boolean floderOnly) {
		File file = new File(edocDir);
  
		if (file.exists() && file.isDirectory()) {
    		FileItem.resetId();
    		FileItem root = new FileItem(file);
    		root.setPath("/");
    		root = getDirTree(file, root, floderOnly);
    		if (root.getChildren().isEmpty()) {
    			root.setIconCls(FileItem.LEAF_FOLDER_ICON);
    		}
    		return root;
        }
        return null;
	}

	private FileItem getDirTree(File parentDir, FileItem parentItem, boolean floderOnly) {
		File[] files = parentDir.listFiles();
        for (File f : files) {
        	if (f.isDirectory() || (!floderOnly && f.isFile())) {
            	FileItem item = new FileItem(f);
            	String path = f.getPath().replaceFirst(edocDir, "");
            	//logger.info("[getDirTree], path:" + path);
            	item.setPath(path);
        		item = getDirTree(f, item, floderOnly);
        		if (item.getChildren().isEmpty()) {
        			item.setIconCls(FileItem.LEAF_FOLDER_ICON);
        		}
        		parentItem.getChildren().add(item);
        	}
        }

        return parentItem;
	}
}
