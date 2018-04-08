package prajna.services;

import java.util.HashMap;
import java.util.Map;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;

import prajna.models.ClientSession;

@Service
@Scope("application")
public class SessionService {
	private Map<String, ClientSession> clients;
	
	SessionService(){
		clients = new HashMap<String, ClientSession>();
	}
	
	public ClientSession getClientSeesion(String ssid) {
		if (ssid == null || ssid.isEmpty())
			return new ClientSession();

		if (clients.get(ssid) == null)
			clients.put(ssid, new ClientSession());
		
		return clients.get(ssid);
	}

}
