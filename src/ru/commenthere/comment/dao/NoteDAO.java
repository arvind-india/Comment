package ru.commenthere.comment.dao;

import java.sql.SQLException;
import java.util.List;

import ru.commenthere.comment.model.Note;

import com.j256.ormlite.dao.BaseDaoImpl;
import com.j256.ormlite.support.ConnectionSource;

public class NoteDAO extends BaseDaoImpl<Note, Integer>{

	   public NoteDAO(ConnectionSource connectionSource,
	           Class<Note> dataClass) throws SQLException {
	       super(connectionSource, dataClass);
	   }
	
}