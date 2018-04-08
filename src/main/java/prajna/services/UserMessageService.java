package prajna.services;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import prajna.models.UserMessage;
import prajna.repos.UserMessageRepo;

@Service
public class UserMessageService {
	private static final Logger logger = LogManager.getLogger(UserMessageService.class.getName());
	
	@Autowired UserMessageRepo  	usrMessageRepo;
	@Autowired AccountService       accountService;
	
	public UserMessageService() {
	}

	public String getCommentPgMetaJson() {
		Long size = usrMessageRepo.count();
		if (size <= SystemService.COMMT_PG_SIZE)
			return null;
		
		return SystemService.getCommentPgMetaJson(0, SystemService.COMMT_PG_SIZE, size);
	}
	
	public Page<UserMessage> retrieveMessages(Pageable page) {
		Page<UserMessage> ret = usrMessageRepo.findAll(page);
		if (ret.getContent().isEmpty()) {
			ret = usrMessageRepo.findAll(page.previousOrFirst());
		}
		return ret;
	}
	
	public int updateMessage(int id, String account, String text) {
		try {
			return usrMessageRepo.updateTextById(id, account, text);
		} catch (Exception e) {
			return -1;
		}
	}
	
	public int deleteMessage(int id, String account){
		return usrMessageRepo.deleteByIdAndAccount(id, account);
	}
	
	public UserMessage saveMessage(UserMessage msgBean) {
		if (msgBean.getAuthor().isEmpty()) {
			msgBean.setAuthor(
					accountService.getAccount(msgBean.getAccount()).getName());
		}
		return usrMessageRepo.save(msgBean); 
	}
}
