package prajna.repos.projection;

import java.sql.Timestamp;

public interface DocOpaque {
	int getViews();
	int getScore();
	int getPositive();
	int getNegative();
	Timestamp getCdate();
}
