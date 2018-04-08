package prajna;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

import javax.servlet.Filter;
import javax.servlet.MultipartConfigElement;
import javax.servlet.ServletRegistration;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.web.filter.CharacterEncodingFilter;
import org.springframework.web.servlet.support.AbstractAnnotationConfigDispatcherServletInitializer;

public class PrajnaWebApp extends AbstractAnnotationConfigDispatcherServletInitializer {
	private static final Logger logger = LogManager.getLogger(PrajnaWebApp.class.getName() );
	
	@Override
	protected Class<?>[] getRootConfigClasses() {
		return null;
	}

	@Override
	protected Class<?>[] getServletConfigClasses() {
		return new Class[] { PrajnaWebAppConfig.class };
	}

	@Override
	protected String[] getServletMappings() {
		return new String[] { "/" };
	}

	@Override
	protected Filter[] getServletFilters() {
		CharacterEncodingFilter characterEncodingFilter = new CharacterEncodingFilter();
		characterEncodingFilter.setEncoding("UTF-8");

		return new Filter[] { characterEncodingFilter};
    }

	@Override
	protected void customizeRegistration(ServletRegistration.Dynamic registration) {
		Path uploaderPath = Paths.get(UPLOAD_LOCATION);
		if (Files.notExists(uploaderPath))
		try {
			Files.createDirectory(uploaderPath);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		MultipartConfigElement multipartConfigElement = new MultipartConfigElement(UPLOAD_LOCATION, MAX_FILE_SIZE, MAX_REQUEST_SIZE, FILE_SIZE_THRESHOLD);
		registration.setMultipartConfig(multipartConfigElement);
    }

	private static final String UPLOAD_LOCATION = AppContext.DataPath + "/uploader"; // Temporary location where files will be stored
    private static final long MAX_FILE_SIZE = 5242880; // 5MB : Max file size.
                                                        // Beyond that size spring will throw exception.
    private static final long MAX_REQUEST_SIZE = 20971520; // 20MB : Total request size containing Multi part.
     
    private static final int FILE_SIZE_THRESHOLD = 0; // Size threshold after which files will be written to disk
}
