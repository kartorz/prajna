package prajna.services;

import java.io.IOException;
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
	public static final int PG_SIZE = 20;
	public static final int COMMT_PG_SIZE = 10;
	public Path uploaderPath = null;
    
	@Autowired
    ServletContext context;
	
	@Autowired
    MessageSource messageSource;
	
	public SystemService() {
		logger.info("SystemService()");
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

    public static String getMd5String(String str) {
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
}
