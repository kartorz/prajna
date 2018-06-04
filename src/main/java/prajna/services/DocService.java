package prajna.services;

import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.sql.Blob;
import javax.sql.rowset.serial.SerialBlob;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;

import javax.servlet.http.Part;
import org.apache.commons.io.IOUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.hibernate.exception.DataException;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import prajna.SimpleImageInfo;
import prajna.models.Account;
import prajna.models.Doc;
import prajna.models.DocComment;
import prajna.models.Docres;
import prajna.models.Draft;
import prajna.repos.AccountRepo;
import prajna.repos.DocCommentRepo;
import prajna.repos.DocRepo;
import prajna.repos.DocresRepo;
import prajna.repos.DraftRepo;
import prajna.repos.projection.DocOpaque;
import prajna.repos.projection.Id;

@Service
public class DocService {
	private static final Logger logger = LogManager.getLogger(DocService.class.getName());

	public static final int PG_SIZE = 20;
	public static final int COMMT_PG_SIZE = 10;                                                                                                                                               
	
    private boolean  configUseResDir = false;

	@Autowired DocRepo  docRepo;
	@Autowired DraftRepo draftRepo;
	@Autowired AccountRepo  accountRepo;
	@Autowired DocresRepo  docresRepo;
	@Autowired DocCommentRepo docCommentRepo;
	
	@Autowired SystemService  systemService;
	
	public DocService() {
		//logger.info("DocService()");
	}

	public String getPgMetaJson(int cid) {
		Long total;
		if (cid <= Doc.CID_MAINPAGE)
			total = docRepo.count();
		else
			total = docRepo.countByCid(cid);
		if (total > PG_SIZE) {
			//String pgmeta = "total:" + 190 + ",pageSize:" + PG_SIZE + ",cid:1,";
			String pgmeta = "total:" + total + ",pageSize:" + PG_SIZE + ",cid:" + cid + ",";
			pgmeta += "layout:['sep','first','prev','links','next','last','sep','refresh', 'info']";
			return pgmeta;
		}
		return null;
	}

	public List<Doc> getDocList(int cid, Pageable pageable) {
		List<Doc> docList;
		if (cid <= Doc.CID_MAINPAGE) {
			logger.debug("getDocList pageable number:" + pageable.getPageNumber());
			Pageable page = new PageRequest(pageable.getPageNumber(), DocService.PG_SIZE);
			docList = docRepo.findAllByOrderByOnTopDescMdateDesc(page).getContent();
		} else
			docList = docRepo.findByCid(cid, pageable).getContent();
	
		if (docList == null)
			docList = new ArrayList<Doc>();
		return docList;
	}

	// pg: zero-based page index
	public List<Doc> getDocList(int cid, int pg) {
		List<Doc> docList;
		if (cid <= Doc.CID_MAINPAGE) {
			logger.debug("getDocList pg:" + pg);
			Pageable page = new PageRequest(pg, DocService.PG_SIZE);
			docList = docRepo.findAllByOrderByOnTopDescMdateDesc(page).getContent();
		} else
			docList = docRepo.findByCidOrderByMdateDesc(cid, new PageRequest(pg, PG_SIZE)).getContent();
	
		if (docList == null)
			docList = new ArrayList<Doc>();

		/*for (int i=0; i<docList.size(); i++) {
			logger.info("doc: title:" + docList.get(i).getTitle() + " usr:" + docList.get(i).getAccount() + "\n");
		}*/
		return docList;
	}
	
	public Doc getDoc(int id) {
		return docRepo.findById(id);
	}

	public void docViewsInc(int views, int id) {
		views += docRepo.findById(id).getViews();
		docRepo.updateViews(views, id);
	}

	public void updateDocViews(int views, int id) {
		docRepo.updateViews(views, id);
	}

	public void deleteDoc(int id) {
		Doc doc = docRepo.findOne(id);
		if (configUseResDir)
			systemService.deleteUploaderResDir(doc.getResid());
		else 
			try { docresRepo.deleteByDid(doc.getId()); }catch (Exception e) {}

		docRepo.delete(id);
	}
	
	public Doc saveDoc(Draft draft) {
		String account;
		Account usr;
		Doc doc = new Doc(draft);
		//logger.info("doc cid:" + doc.getCid() + " onTop:" + doc.getOnTop());
		String html = doc.getText();
		Document htmlDoc = Jsoup.parseBodyFragment(html);

		int briefLen = htmlDoc.text().length() > 255 ?  255 : htmlDoc.text().length();
		doc.setBrief(htmlDoc.text(). substring(0, briefLen));

		if (draft.getAuthor().trim().isEmpty())
			account = draft.getAccount(); // login's doc.
		else 
			account = draft.getAuthor().trim(); // other's doc.
		
		usr = accountRepo.findByAccount(account);
		if (usr != null) {
			doc.setAccount(usr.getAccount());
			doc.setAuthor(usr.getName());
			doc.setEmail(usr.getEmail());
		} else {
			doc.setAccount("");
			doc.setAuthor(account);
		}

		if (doc.getTitle().trim().isEmpty())
    			doc.setTitle("No Title");

		if (doc.getUrl().trim().isEmpty())
    			doc.setUrl("prajna.top");

		doc.setOpaque(docRepo.findOpaqueById(draft.getDid()));

		doc = docRepo.save(doc);
		attachResToDoc(htmlDoc, doc, draft.getId(), draft.getDid());

		draftRepo.delete(draft);
		//logger.info("saveDoc id:" + doc.getId() + " title:" + doc.getTitle());
		return doc;
	}
	
	public Draft saveDraft(Draft draftBean){
		//logger.info("saveDraft: id:{}", draftBean.getId());
		return draftRepo.save(draftBean);
	}

	public void deleteDraft(Draft draftBean) {
		
		try {docresRepo.deleteByDraftId(draftBean.getId()); } catch (Exception e) {}
	
		try {draftRepo.delete(draftBean);} catch (Exception e) {}
	}
	
	private void attachResToDoc(Document htmlDoc, Doc doc, int draftId, int docId) {
		HashMap<Integer, Boolean> allResid = new HashMap<Integer, Boolean>();
		if (docId != 0) {
			List<Id> residList = docresRepo.findByDid(docId);
			for (int i=0; i< residList.size(); i++) {
				allResid.put(residList.get(i).getId(), false);
			}
		}

		Elements media = htmlDoc.select("[src]");
	    for (Element src : media) {
	    	if (src.tagName().equals("img") && src.hasAttr("src")) {
	    		/* {/}uploader/resid/fid */
	    		String[] srcComps = src.attr("src").trim().split("/");
	    		if (srcComps.length > 2 && srcComps.length < 5)
	    		try {
	    			int resid  = Integer.valueOf(srcComps[srcComps.length-2]);
	    			docresRepo.updateDidById(doc.getId(), resid);
	    			allResid.put(resid, true);
	    			//logger.info("updateDidById, resid:" + resid);
	    		} catch (Exception e) {
	    			logger.error("src:" + src.attr("src") + " e:" + e.toString());
	    		}
	    	}
	    }

	    for (Integer key : allResid.keySet()) {
            if (!allResid.get(key)) {
            	docresRepo.delete(key);
            }
        }
	    docresRepo.deleteByDidAndDraftId(0, draftId); // Remove the deleted image.
		docresRepo.updateDraftId(0, draftId);
	}
		
	public Draft getDraft(String account, int docId) {
		return draftRepo.findByAccountAndDid(account, docId);
	}

    public byte[] getUploaderFile(String resid, String fileName) {
    	try {
    		if (configUseResDir) {
    			//String url = SystemService.uploaderDir + resid + "/" + fileName;
    			String url =  "/uploader/" + resid + "/" + fileName;
    			//logger.info("getUploaderFile:" + url);
        		InputStream imgStream = null;
        		return IOUtils.toByteArray(imgStream);
    		} else {
    			Blob imgData =  docresRepo.findOne(Integer.valueOf(resid)).getData();
    			//byte[] bytes = new byte[(int) imgData.length()];
    			byte[] bytes =  imgData.getBytes(1, (int) imgData.length()); 
    			logger.info("getUploaderFile: imgblob size:" + imgData.length() + "image bystes[" + bytes[1] + "," + bytes[5] + "," + bytes[12] + "]");
    			imgData.free();
    			return bytes;
    		}
    	} catch (Exception e){e.toString();}

    	logger.info("No available stream");
    	return new byte[1];
    }
	
    public String uploadResToDb(String account, int draftId, Part file) {
    	String respError = "{\"uploaded\": 0,\"error\": {\"message\": \"PH0\"}}";
    	String respSuccess =  "{\"uploaded\": 1, \"fileName\": \"PH0\",\"url\": \"PH1\",\"resid\":PH2,\"height\":PH3, \"width\":PH4}";

    	Docres docres =  new Docres(draftId);
		try {
			byte[] buf = new byte[(int) file.getSize()];
			file.getInputStream().read(buf);
			docres.setData(new SerialBlob(buf));
			docresRepo.save(docres);
		
			SimpleImageInfo imageInfo = new SimpleImageInfo(buf);

			respSuccess = respSuccess.replaceFirst("PH0", String.valueOf(docres.getId()))
							.replaceFirst("PH1", "uploader/" + String.valueOf(docres.getId()) +"/0")
							.replaceFirst("PH2", String.valueOf(docres.getId()))
							.replaceFirst("PH3", String.valueOf(imageInfo.getHeight()))
							.replaceFirst("PH4", String.valueOf(imageInfo.getWidth()));
			
			//logger.info("uploadResToDb, draft id:" + draftId + "file size:" + buf.length);
		} catch (Exception e) {
			e.printStackTrace();
			return respError.replaceFirst("PH0", "Can't get image file part");
		}
		return respSuccess;
    }

    public String uploadResToDisk(String account, int draftId, Part file) {
    	String respError = "{\"uploaded\": 0,\"error\": {\"message\": \"PH0\"}}";
    	String respSuccess =  "{\"uploaded\": 1, \"fileName\": \"PH0\",\"url\": \"PH1\", \"resid\":\"PH2\"}";
 
    	if (account.isEmpty()) {
    		return respError.replaceFirst("PH0", "You have logged out");
    	}
    
    	//Draft draftBean = null;
    	Calendar rightNow = Calendar.getInstance();
    	/*if (draftId == 0 ) {
    		draftId = (int) (rightNow.getTimeInMillis()/1000);
    	}*/
	
    	Path  imgPath = systemService.createUploaderResDir(draftId);
		if (imgPath == null) 
			return respError.replaceFirst("PH0", "Can't create image directory");

		try {
			//file.getSubmittedFileName()  == image.png
			String contentType = file.getContentType();
			String suffix = "";
			int i = contentType.lastIndexOf('/');
			if (i > 0) {
				suffix = "." + contentType.substring(i+1);
			}
			Path img = imgPath.resolve(String.valueOf(rightNow.getTimeInMillis()%10000000) + suffix);
			Files.copy(file.getInputStream(), img, StandardCopyOption.REPLACE_EXISTING);
	
			respSuccess = respSuccess.replaceFirst("PH0", img.getFileName().toString())
							.replaceFirst("PH1", "uploader/" + draftId + "/" + img.getFileName().toString())
							.replaceFirst("PH2", String.valueOf(draftId));

		} catch (Exception e) {
			e.printStackTrace();
			return respError.replaceFirst("PH0", "Can't copy image file");
		}

		return respSuccess;
    }
    
    public String uploadRes(String account, int draftId, Part file) {
  		if (configUseResDir)
  			return uploadResToDisk(account, draftId, file);
  		return uploadResToDb(account, draftId, file);
  	}
    
	public List<DocComment> getCommentList(int did, Pageable page) {
		List<DocComment> commtList = docCommentRepo.findByDid(did, page).getContent();
		if (commtList.isEmpty()) {
			commtList = docCommentRepo.findByDid(did, page.previousOrFirst()).getContent();
		}
		return commtList;
	}
	
	public String getCommentPgMetaJson(int did) {
		Long size = docCommentRepo.countByDid(did);
		if (size <= COMMT_PG_SIZE)
			return null;
		return SystemService.getCommentPgMetaJson(did, COMMT_PG_SIZE, size);
	}
	
	public boolean saveDocComment(DocComment docComment){
		try {
			docCommentRepo.save(docComment);
			return true;
		} catch (DataException e) {
			return false;
		}
	}
	
	public int editTextOfDocComment(int id, String account, String text) {
		try {
			return docCommentRepo.updateTextById(id, account, text);
		} catch (Exception e) {
			return -1;
		}
	}
	
	public int deleteDocComment(int id, String account){
		return docCommentRepo.deleteByIdAndAccount(id, account);
	}
}
