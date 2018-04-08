package prajna.services;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import prajna.models.Account;
import prajna.models.AccountTagId;
import prajna.models.ClientSession;
import prajna.models.MyTag;
import prajna.models.Question;
import prajna.models.QuestionAnswer;
import prajna.models.QuestionTag;
import prajna.models.TagItem;
import prajna.repos.AccountRepo;
import prajna.repos.MyTagRepo;
import prajna.repos.QuestionAnswerRepo;
import prajna.repos.QuestionRepo;
import prajna.repos.QuestionTagRepo;
import prajna.repos.projection.IdQid;
import prajna.repos.projection.IdTag;
import prajna.repos.TagRepo;

@Service
public class DiscussService {
	private static final Logger logger = LogManager.getLogger(DiscussService.class.getName());

	@Autowired TagRepo  			tagRepo;
	@Autowired MyTagRepo 			myTagRepo;
	@Autowired QuestionTagRepo    qusTagRepo;
	@Autowired QuestionRepo		qstnRepo;
	@Autowired QuestionAnswerRepo answerRepo;
	@Autowired AccountRepo        accountRepo;
	@Autowired EmailService             emailService;
	@Autowired AccountService           accountService;
	
	public DiscussService() {
	
	}

	public String getPgMetaJson(Page<Question> page) {
		String pgmeta = "total:" + page.getTotalElements() + ",pageSize:" + page.getSize() + ",";
    	pgmeta += "layout:['sep','first','prev','links','next','last','sep','refresh','info']";
    	return pgmeta;
	}

	public Set<TagItem> getMyTagList(String account, ClientSession client) {
		if (client.myTags.isEmpty()) {
			client.myTags = tagRepo.findAll();
			if (!account.isEmpty()) {
				Set<IdTag> myTags = myTagRepo.findByIdAccount(account);
				Iterator<IdTag> it = myTags.iterator();  
				while (it.hasNext()) {
					IdTag tag = it.next();
					TagItem item = new TagItem(tag.getIdTag(), false);
					client.addTag(item);
				}
			}
		}
		return client.myTags;
	}

	public Page<Question> getQuestionPage(String account, ClientSession client, Pageable page) {
		Set<String>  myTags = new HashSet<String>();
		Set<Integer> myQids = new HashSet<Integer>();

		if (client.myTags.isEmpty()) {
			getMyTagList(account, client);		
		}

		for(TagItem  t:client.myTags) {
			if (t.getSelected()) {
				myTags.add(t.getTag());
			}
        }
	
		Set<IdQid> allQid = qusTagRepo.findByIdTagIn(myTags);
		for(IdQid id:allQid) {
			myQids.add(id.getIdQid());
		}
		
		Page<Question> ret = qstnRepo.findByQidIn(myQids, page);
		if (ret.getContent().isEmpty())
			ret = qstnRepo.findByQidIn(myQids, page.previousOrFirst());
		return ret;
	}
	
	public Question getQuestion(int id) {
		Question q = null;
		String tags = "";
		if (id > 0) {
			q = qstnRepo.findOne(id);
			if (q != null) {
				Set<IdTag> qusnTags = qusTagRepo.findByIdQid(id);
				
				Iterator<IdTag> it = qusnTags.iterator();  
				while (it.hasNext()) {
					tags += it.next().getIdTag();
					if (it.hasNext())
						tags += ",";
				}
				
				q.setTags(tags);
			}
		}
		
		if (q == null) q = new Question();
	
		return q;
	}
	
	public void saveQuestion(Question qstnBean) {
		if (qstnBean.getAuthor().isEmpty()) {
			Account usr = accountRepo.findByAccount(qstnBean.getAccount());
			if (usr != null)
				qstnBean.setAuthor(usr.getName());
		}
		
		int qid = qstnBean.getQid();
		String tags = qstnBean.getTags();
		
		logger.debug("saveQuestion qid:" + qid + " tags:" + tags);
		
		qstnBean = qstnRepo.save(qstnBean);
		if (qid > 0) {
			qusTagRepo.deleteByIdQid(qid);
		}
		
		String[] tagList = tags.split(",");
		for(String tag: tagList) {
			qusTagRepo.save(new QuestionTag(qstnBean.getQid(), tag));
		}
	}
	
	public void deleteQuestion(int id) {
		qstnRepo.delete(id);
		qusTagRepo.deleteByIdQid(id);
	}
	
	public boolean checkInviterPermission(int qid, String login) {
		Question qstnBean = qstnRepo.findOne(qid);

		if (qstnBean != null) {
			if (accountService.isAdmin(login))
				return true;
			if (login.equalsIgnoreCase(qstnBean.getAccount()))
				return true;
		}
		return false;
	}
	
	public String sendInvite(int qid, String login, String invitee, String url) {
    	Question qstnBean = qstnRepo.findOne(qid);
    	if (qstnBean != null) {
    		emailService.invateAnswer(login, invitee, url);
	    	try {
	    		if (qstnRepo.updateInvitees(qid, qstnBean.addInvitee(invitee)) > 0)
	    			return qstnBean.getInvitees();
	    	} catch (Exception e) {
	    		logger.error(e.toString());
    			return "";
    		}
    	}
    
    	return "";
	}
	
	public List<String> getQuestionTags(int qid) {
		List<String> tagList = new ArrayList<String>();
		
		Set<IdTag> qusnTags = qusTagRepo.findByIdQid(qid);
		for(IdTag tag:qusnTags) {
			tagList.add(tag.getIdTag());
		}
		return tagList;
	}
	
	public Page<QuestionAnswer> getAnswers(int qid, Pageable page) {

		Page<QuestionAnswer> ret = answerRepo.findByQid(qid, page);
		if (ret.getContent().isEmpty()) {
			ret = answerRepo.findByQid(qid, page.previousOrFirst());
		}
		return ret;
	}
	
	public QuestionAnswer editAnswer(int id, String text) {
		QuestionAnswer answer = answerRepo.findOne(id);
		answer.setText(text);
	
		return answerRepo.save(answer);
	}
	
	public void deleteAnswer(int id) {
		answerRepo.delete(id); 
	}
	
	public  QuestionAnswer saveAnswer(QuestionAnswer answerBean) {
		if (answerBean.getAuthor().isEmpty()) {
			Account usr = accountRepo.findByAccount(answerBean.getAccount());
			if (usr != null)
				answerBean.setAuthor(usr.getName());
		}
		return answerRepo.save(answerBean); 
	}
	
	public String getCommentPgMetaJson(int qid) {
		Long size = answerRepo.countByQid(qid);
		if (size <= SystemService.COMMT_PG_SIZE)
			return null;
		
		return SystemService.getCommentPgMetaJson(qid, SystemService.COMMT_PG_SIZE, size);
	}
	
	public void setTagSelected(String tag, boolean selected, String account, ClientSession client) {
		TagItem item = new TagItem(tag, selected);
		client.addTag(item);
		if (!account.isEmpty()) {
			if (selected) {
				myTagRepo.delete(new MyTag(new AccountTagId(account, tag)));
			} else {
				myTagRepo.save(new MyTag(new AccountTagId(account, tag)));
			}
		}
	}
	
	public List<String> getTagList() {
		List<String> tagList = new ArrayList<String>();

		Set<TagItem> allTag = tagRepo.findAll();
		for (TagItem t:allTag) {
			tagList.add(t.getTag());
		}
		return tagList;
	}
}
