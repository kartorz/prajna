package prajna.accesslog;

import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import prajna.services.DocService;

@Component
public class Statistics  implements ParserListener {
	private static final Logger logger = LogManager.getLogger(Statistics.class.getName() );
    
	public static final String GET = "GET";

    public static final String POST = "POST";

    public static final String HEAD = "HEAD";

    private ILogParser logParser;
    private HashMap<String, HashSet<Integer>> docViewed = null;
	
    @Autowired	Environment env;
    @Autowired  DocService  docService;

    public Statistics() {
		logParser = new TCLogParser();
		logParser.addListener(this);
		docViewed = new HashMap<String, HashSet<Integer>>();
	}

	@Scheduled(cron = "0 0 1 * * ?")
	public void parser() {
		String preDay = new SimpleDateFormat("YYYY-MM-dd").format(
				new Timestamp(System.currentTimeMillis() - 2 * 60 * 60 * 1000)); //2 * 60 * 60 * 1000
		String logPath = env.getProperty("access_log_path");
		String logName =  "localhost_access_log." + preDay + ".txt";
		String source = logPath + "/" + logName;
		
		logger.debug("source:" + source);
	
		logParser.setSourceFile(source);
		logParser.parse(-1);
	}

	public void viewDoc(String IP, int did) {
		logger.debug("viewDoc: IP:"+IP + " did:" + did);
		if (!docViewed.containsKey(IP))
			docViewed.put(IP, new HashSet<Integer>());
		docViewed.get(IP).add(did);
	}

	@Override
	public void onParseUrl(String IP, String method, String url) {
		if (IP != null && method != null) {
			if (method.equalsIgnoreCase(GET)) {
			
				String suburl = url;
				int pos = suburl.indexOf(";"); //;jsessionid=
				if (pos > -1) {
					suburl = suburl.substring(0, pos);
				}
				pos = suburl.indexOf("?");
				if (pos > -1) {
					suburl = suburl.substring(0, pos);
				}
				logger.info(" IP:" + IP + " url:" + suburl);
		
				if (suburl.startsWith("/prajna/doc/")) {
					String[] urlItems = suburl.split("/");
					for (String item: urlItems)
						logger.info(" " + item);
				
					//['', prajna, doc, int]
					if (urlItems.length == 4) {
						if (urlItems[2].equalsIgnoreCase("doc")) {
						try{
							int did = Integer.parseInt(urlItems[3]);
							viewDoc(IP, did);
						} catch (NumberFormatException e) {
						}
						}
					}
				}
			}
		}
	}

	@Override
	public void onParseDone() {
		logger.debug("onParseDone");
		HashMap<Integer, Integer> didMap = new HashMap<Integer, Integer>();
		Iterator<Map.Entry<String, HashSet<Integer>>> it = docViewed.entrySet().iterator();
		while (it.hasNext()) {
		    Map.Entry<String, HashSet<Integer>> pair = it.next();
		    HashSet<Integer> didSet = pair.getValue();
		    for (Integer did : didSet) {
		    	Integer views = 1;
		    	if (didMap.containsKey(did))
		    		views += didMap.get(did);
		    	didMap.put(did, views);
		    }
		}

		Iterator<Map.Entry<Integer, Integer>> dit = didMap.entrySet().iterator();
		while (dit.hasNext()) {
		    Map.Entry<Integer, Integer> pair = dit.next();
		    int did = pair.getKey();
		    int views = pair.getValue();
		    docService.docViewsInc(views, did);
		}
	}
}
