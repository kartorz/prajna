package prajna;

import java.util.Properties;

import javax.persistence.EntityManagerFactory;
import javax.sql.DataSource;

import org.apache.commons.dbcp.BasicDataSource;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.mail.MailSender;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.transaction.annotation.EnableTransactionManagement;
import org.thymeleaf.spring4.SpringTemplateEngine;
import org.thymeleaf.spring4.templateresolver.SpringResourceTemplateResolver;
import org.thymeleaf.spring4.view.ThymeleafViewResolver;
import org.thymeleaf.templatemode.TemplateMode;

import prajna.controllers.DocController;

@Configuration
@ComponentScan(basePackages= "prajna")
@EnableTransactionManagement
@EnableJpaRepositories 
public class PrajnaAppContext {
    private static final Logger logger = LogManager.getLogger(PrajnaAppContext.class.getName() );
	public static final String DataPath = System.getProperty("user.home") + "/.prajna";
	public static final String EmailFrom =  "xxxx@outlook.com";//"progcada@163.com";
	//public static final String DataPath = "/raid/srv/prajna";
	private final String dbPath = DataPath + "/prajna";
	
	@Autowired
	private ApplicationContext context;
	
	@Bean
	public DataSource dataSource() {
		//String url = "jdbc:h2:file:~/.prajna/prajna;INIT=create schema if not exists usr\\; runscript from 'classpath:schema.sql'";
		String url = "jdbc:h2:file:" + dbPath + ";INIT=create schema if not exists usr\\; runscript from 'classpath:schema.sql'";
		BasicDataSource ds = new BasicDataSource();
		ds.setDriverClassName("org.h2.Driver");
		ds.setUrl(url);
		ds.setUsername("");
		ds.setPassword("");
		return ds;
	}

	@Bean
	LocalContainerEntityManagerFactoryBean entityManagerFactory() {
		LocalContainerEntityManagerFactoryBean em = new LocalContainerEntityManagerFactoryBean();
		em.setDataSource(dataSource());
		em.setPersistenceXmlLocation("META-INF/persistence.xml");
		em.setPersistenceUnitName("springJpaPersistenceUnit");
		//em.setPackagesToScan(new String[] { "prajna.repos" });

		return em;
	}

	@Bean
	JpaTransactionManager transactionManager(EntityManagerFactory emf) {
		JpaTransactionManager tm = new JpaTransactionManager();
		tm.setEntityManagerFactory(emf);
		return tm;
	}
	
	@Bean
	public SpringResourceTemplateResolver templateResolver(){
		SpringResourceTemplateResolver templateResolver = new SpringResourceTemplateResolver();
		templateResolver.setApplicationContext(context);
		templateResolver.setPrefix("/WEB-INF/templates/");
		templateResolver.setSuffix(".html");

		// HTML is the default value, added here for the sake of clarity.
		templateResolver.setTemplateMode(TemplateMode.HTML);
	
		// Template cache is true by default. Set to false if you want
		// templates to be automatically updated when modified.
		templateResolver.setCacheable(true);
		return templateResolver;
	}

	@Bean
	public SpringTemplateEngine templateEngine(){
		SpringTemplateEngine templateEngine = new SpringTemplateEngine();
		templateEngine.setTemplateResolver(templateResolver());

		templateEngine.setEnableSpringELCompiler(true);
		return templateEngine;
	}
	
	@Bean
	public ThymeleafViewResolver viewResolver(){
		ThymeleafViewResolver viewResolver = new ThymeleafViewResolver();
		viewResolver.setTemplateEngine(templateEngine());

		viewResolver.setOrder(1);
		//viewResolver.setViewNames(new String[] {".html", ".xhtml"});
		viewResolver.setCharacterEncoding("utf-8");
		return viewResolver;
	}
	
	@Bean
	public MailSender mailSender() {
	    //Properties props = new Properties();
	    //props.put("mail.smtp.starttls.enable", "true");
	    
		JavaMailSenderImpl mailSender = new JavaMailSenderImpl();
		mailSender.setHost("smtp.office365.com");
		mailSender.setPort(587);
		mailSender.setUsername(EmailFrom);
		mailSender.setPassword("xxxxxxx");
		mailSender.getJavaMailProperties().put("mail.smtp.starttls.enable", "true");
		return mailSender;
	}
}
