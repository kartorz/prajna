package prajna.models;

import java.util.HashSet;
import java.util.Set;

public class ClientSession {
	public int docId = 1;
	public Set<TagItem> myTags = new HashSet<TagItem>();

	public void addTag(TagItem item) {
		if (!myTags.add(item)) {
			myTags.remove(item);
			myTags.add(item);
		}
	}
}
