package prajna.accesslog;

public interface ILogParser {
	void addListener(ParserListener listener);
    void setSourceFile(String source);
    int parse(int parseCount);
}
