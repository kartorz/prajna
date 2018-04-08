package prajna.accesslog;

public interface ParserListener {
	void onParseUrl(String IP, String method, String url);
	void onParseDone();
}
