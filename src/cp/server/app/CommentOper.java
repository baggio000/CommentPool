package cp.server.app;

import java.util.List;

import cp.server.common.Comment;
import cp.server.common.Page;
import cp.server.common.SourceType;


public interface CommentOper {
	
    public List<Comment> fetchCommentsFromPage(Page page) throws ParserException;
    public void init();
    public void setSource(SourceType source);
}
