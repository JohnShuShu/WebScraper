package hackforums;

import org.medhelp.User;

import java.util.List;

/**
 * Created by johnshu on 6/22/17.
 */
public class Comment {

    public String comment;
    public String commentDate;
    public String commentTime;
    public User commentor;
    public String commentType;

    public Comment(String comment, String date, String commentTime, User commentor, String commentType) {
        this.comment = comment;
        this.commentDate = commentDate;
        this.commentTime = commentTime;
        this.commentor = commentor;
        this.commentType = commentType;
    }

    public Comment() {

    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public String getCommentTime() {
        return commentTime;
    }

    public void setCommentTime(String commentTime) {
        this.commentTime = commentTime;
    }

    public User getCommentor() {
        return commentor;
    }

    public void setCommentor(User commentor) {
        this.commentor = commentor;
    }

    public String getCommentDate() {
        return commentDate;
    }

    public void setCommentDate(String commentDate) {
        this.commentDate = commentDate;
    }


    public String getCommentType() {
        return commentType;
    }

    public void setCommentType(String commentType) {
        this.commentType = commentType;
    }

    public String printToFileComment() {
        String commentText = "";


        commentText = comment + "," + commentDate + "," + commentTime + "," + commentor.getUserName() + "," ;


        return commentText;

    }
}
