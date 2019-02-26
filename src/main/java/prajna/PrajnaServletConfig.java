package prajna;

import java.nio.charset.Charset;
import java.util.List;
import java.util.Locale;

import javax.servlet.annotation.MultipartConfig;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.StringHttpMessageConverter;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.context.support.ReloadableResourceBundleMessageSource;
import org.springframework.data.web.PageableHandlerMethodArgumentResolver;
import org.springframework.data.web.config.EnableSpringDataWebSupport;
import org.springframework.data.web.config.SpringDataWebConfiguration;
import org.springframework.web.servlet.LocaleResolver;
import org.springframework.web.servlet.config.annotation.DefaultServletHandlerConfigurer;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.i18n.SessionLocaleResolver;

import prajna.controllers.BaseInterceptor;

import org.springframework.web.method.support.HandlerMethodArgumentResolver;

@Configuration
@MultipartConfig 
@ComponentScan(basePackages= "prajna")
@EnableWebMvc
@EnableSpringDataWebSupport
@EnableScheduling
@PropertySource("classpath:/app.properties")
public class PrajnaServletConfig extends SpringDataWebConfiguration{
	private static final Logger logger = LogManager.getLogger(PrajnaServletConfig.class.getName() );

	@Autowired
	private ApplicationContext context;
	
	@Override
	public void addArgumentResolvers(List<HandlerMethodArgumentResolver> argumentResolvers) {
		 PageableHandlerMethodArgumentResolver pgResolver =  new  PageableHandlerMethodArgumentResolver();
		 pgResolver.setPageParameterName("p");
		 pgResolver.setSizeParameterName("s");
		 pgResolver.setOneIndexedParameters(true);
		 argumentResolvers.add(pgResolver);

		 //SortHandlerMethodArgumentResolver sortResolver = new SortHandlerMethodArgumentResolver();
		 //sortResolver.setSortParameter("q");
		 //argumentResolvers.add(sortResolver);
	}

	@Override
	public void addResourceHandlers(ResourceHandlerRegistry registry) {
		//String dataDir = System.getProperty("user.home") + "/.prajna";
		//logger.info("dataDir:" + dataDir + "/uploader/");
		//registry.addResourceHandler("/uploader/**").addResourceLocations(dataDir + "/uploader/");
		registry.addResourceHandler("/css/**").addResourceLocations("/css/");
		registry.addResourceHandler("/js/**").addResourceLocations("/js/").setCachePeriod(31556926);
	}
	
	@Override
	public void configureDefaultServletHandling(DefaultServletHandlerConfigurer configurer) {
		configurer.enable();
	}
	
	@Override
	public void extendMessageConverters(List<HttpMessageConverter<?>> converters) {
		// converts.add(new StringHttpMessageConverter) not working.
		for (HttpMessageConverter<?> obj: converters) {
			if (obj instanceof StringHttpMessageConverter) {
				StringHttpMessageConverter strMsgConv = (StringHttpMessageConverter) obj;
				strMsgConv.setDefaultCharset(Charset.forName("UTF-8"));
				break;
			}
		}
	}

	// replace default, the default converts will not work.
	//@Override
	//public void configureMessageConverters(List<HttpMessageConverter<?>> converters) {
	//}
	
	/*@Bean
	public AdminInterceptor adminInterceptor() {
	    return new AdminInterceptor();
	}*/

	/*@Bean
	public AuthInterceptor authInterceptor() {
	    return new AuthInterceptor();
	}*/	
	
	@Override
	public void addInterceptors(InterceptorRegistry registry) {
	
		registry.addInterceptor(new BaseInterceptor());
		
		/*String[] adminPaths = new String[] {"/doc/**", "/draft/**"};
		registry.addInterceptor(adminInterceptor()).addPathPatterns(adminPaths)
			.excludePathPatterns("/doc/comment/**");*/

		/*String[] unauthPaths = new String[] {"/signup/**", "/signin/**", "/doc/**", "/draft/**"};
		registry.addInterceptor(authInterceptor()).addPathPatterns("/**")
			.excludePathPatterns(unauthPaths);*/
	}
	
	@Bean
	public LocaleResolver localeResolver() {
		SessionLocaleResolver slr = new SessionLocaleResolver();
		slr.setDefaultLocale(Locale.CHINESE);
		return slr;
	}
	    
    @Bean
    public ReloadableResourceBundleMessageSource messageSource(){
    	ReloadableResourceBundleMessageSource messageSource = new ReloadableResourceBundleMessageSource();
        messageSource.setBasename("classpath:messages");
        messageSource.setDefaultEncoding("UTF-8");
        messageSource.setFallbackToSystemLocale(true);
        return messageSource;
    }
}
