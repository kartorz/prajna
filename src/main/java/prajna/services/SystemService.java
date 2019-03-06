package prajna.services;

import java.io.FileInputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
//import java.util.function.Consumer;
//import java.util.stream.Stream;
import java.util.Locale;

import javax.servlet.ServletContext;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.context.annotation.Scope;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.stereotype.Service;

@Service
@Scope("application")
public class SystemService {
	private static final Logger logger = LogManager.getLogger(SystemService.class.getName());
    public static final String  UPLOADERDIR_NAME =  "uploader";
	public static final String  dataDir = System.getProperty("user.home") + "/.prajna";
	public static final String  uploaderDir = dataDir + "/" + UPLOADERDIR_NAME + "/" ;
	public static final String  edocDir = dataDir + "/edoc";

	public static final int PG_SIZE = 20;
	public static final int COMMT_PG_SIZE = 10;
	public static Path uploaderPath = null;
    public static Path edocPath = null;

    @Autowired
    ServletContext context;
	
	@Autowired
    MessageSource messageSource;
	
	public SystemService() {
		edocPath = createDir(edocDir);
		logger.info("SystemService, edoc path:" + edocDir);
	}
    
	public String getMessage(String id) {
		Locale locale = LocaleContextHolder.getLocale();
	    return messageSource.getMessage(id,null,locale);
	}

    public static String getCommentPgMetaJson(int did, int pgSize, Long size) {
		
		String pgmeta = "total:" + size + ",pageSize:" + pgSize + ",did:" + did + ",";
    	pgmeta += "layout:['sep','first','prev','links','next','last','sep','refresh', 'info']";
    	return pgmeta;
	}
    
   
    public void deleteUploaderResDir(long resid) {
		if (uploaderPath == null || resid == 0) 
			return;
		/*Path resPath = uploaderPath.resolve(String.valueOf(resid));
		if (Files.exists(resPath)) {
		try {
			Stream<Path> fileList = Files.list(resPath);
			fileList.forEach(new Consumer<Path>() {
				public void accept(Path t) {
					try {
						logger.info("delete Path:" + t);
						Files.delete(t);
					} catch (IOException e) {
						e.printStackTrace();
					}
				} 
			});
			fileList.close();
			Files.delete(resPath);
		} catch (IOException e){
			e.printStackTrace();
		}
		}*/
	}

	public Path createUploaderResDir(long resid) {
		if (uploaderPath == null) {
			//Path rootPath = Paths.get(context.getRealPath("/"));
			uploaderPath = Paths.get(dataDir).resolve(UPLOADERDIR_NAME);
			logger.debug("uploaderPath: " + uploaderPath.toString());
		
			try {
				if (Files.notExists(uploaderPath))
					Files.createDirectory(uploaderPath);
			} catch (IOException e) {
				e.printStackTrace();
				uploaderPath = null;
				return null;
			}
		}

		Path resPath = uploaderPath.resolve(String.valueOf(resid));
		try {
			if (Files.notExists(resPath))
				Files.createDirectory(resPath);
		} catch (IOException e) {
			e.printStackTrace();
			resPath = null;
			
		}
		return resPath;
	}
	
	
    private Path createDir(String dir) {
    	Path dirPath = Paths.get(dir);
    	logger.debug("createDataDir: " + dirPath.toString());
		
    	try {
			if (Files.notExists(dirPath))
				Files.createDirectory(dirPath);
		} catch (IOException e) {
			e.printStackTrace();
			return null;
		}
    	
    	return dirPath;
	}
	
    public static String trim(String s, int width) {
        if (s.length() > width)
            return s.substring(0, width-1) + ".";
        else
            return s;
    }
    
    public static String toHtml(String src) {
    	String det =  src.replaceAll("\\\n","<br/>");
    	det = det.replaceAll("\\ ","&nbsp;");
    	return det;

    }

    public static String getStrMd5(String str) {
		try {
	    	MessageDigest md = MessageDigest.getInstance("MD5");
			md.update(str.getBytes());
			byte bytes[] = md.digest();
	        StringBuffer sb = new StringBuffer();
	        for (int i = 0; i < bytes.length; i++) {
	        	sb.append(Integer.toString((bytes[i] & 0xff) + 0x100, 16).substring(1));
	        }
	        return sb.toString();
		} catch (NoSuchAlgorithmException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return "";
		}
    }
    
	public static String getFileMd5(String file) {
        MessageDigest md;
		
        try {
			md = MessageDigest.getInstance("MD5");
	        FileInputStream input = new FileInputStream(file);
	        FileChannel channel = input.getChannel();
	        byte[] buffer = new byte[256 * 1024];
	        ByteBuffer byteBuffer = ByteBuffer.wrap(buffer);
	        int bytes;
	        while ((bytes = channel.read(byteBuffer)) > 0) {
	        	md.update(byteBuffer.array(), 0, bytes);
	            byteBuffer.clear();
	        }
	    
	        channel.close();
	        input.close();
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
			return "";
		}  catch (IOException e) {
			e.printStackTrace();
			return "";
		}

		byte byteData[] = md.digest();
        StringBuffer sb = new StringBuffer();
        for (int i = 0; i < byteData.length; i++) {
            sb.append(Integer.toString((byteData[i] & 0xff) + 0x100, 16).substring(1));
        } 
        return sb.toString();
	}
	


	public static String getFileExtension(String name) {
		int lastIndexOf = name.lastIndexOf(".");
		if (lastIndexOf == -1) {
			return ""; // empty extension
		}
		return name.substring(lastIndexOf);
	}

}
