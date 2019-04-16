package prajna.services;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.context.annotation.Scope;
import org.springframework.mail.MailException;
import org.springframework.mail.MailSender;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.stereotype.Service;

import prajna.PrajnaAppContext;

@Service
@Scope("application")
public class EmailService {
	private static final Logger logger = LogManager.getLogger(EmailService.class.getName());
	
	private MailSender mailSender;
	public EmailService(MailSender mailSender) {
		this.mailSender = mailSender;
	}

	public void resetPassword(String addr, String passwd) {
		SimpleMailMessage msg = new SimpleMailMessage();
		String content = "Dear: "  + "\n\n";
		content +=  "您的密码已重置为：(" + passwd + ")\n\n";
		content += "\n\n\n===================================\n";
		content += "  prajna.top 系统自动发送邮件，请勿回复\n";
		msg.setFrom(PrajnaAppContext.EmailFrom);
		msg.setTo(addr);
		msg.setSubject("[Prajna] 密码重置");
		msg.setText(content);

		sendMessage(msg);
		logger.info("resetPassword: send email to " + addr + " passwd:" + passwd);
	}

	public void invateAnswer(String inviter, String  invitee, String url) {
		//logger.info("from:" + inviter + " to:" + invitee + " url:" + url);
		SimpleMailMessage msg = new SimpleMailMessage();
	
		String content = "Dear: "  + "\n\n";
		content += inviter  + "  邀请你回答一个问题：\n\n";
		content += url;
		content += "\n\n\n===================================\n";
		content += "  prajna.top 系统自动发送邮件，请勿回复\n";
		msg.setFrom(PrajnaAppContext.EmailFrom);
		msg.setTo(invitee);
		msg.setSubject("[Prajna] "  + inviter + " 邀请您回答问题");
		msg.setText(content);

		sendMessage(msg);
	}
	
	private void sendMessage(SimpleMailMessage msg) {
		try{
			this.mailSender.send(msg);
		}
		catch (MailException ex) {
			// simply log it and go on...
			System.err.println(ex.getMessage());
		}
	}
}
