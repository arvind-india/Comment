package ru.commenthere.comment.dao;

import java.sql.SQLException;

import ru.commenthere.comment.model.Comment;
import ru.commenthere.comment.model.Note;

import com.j256.ormlite.dao.BaseDaoImpl;
import com.j256.ormlite.support.ConnectionSource;

public class CommentDAO extends BaseDaoImpl<Comment, Integer>{

	   public CommentDAO(ConnectionSource connectionSource,
	           Class<Comment> dataClass) throws SQLException {
	       super(connectionSource, dataClass);
	   }
}