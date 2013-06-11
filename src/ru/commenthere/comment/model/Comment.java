package ru.commenthere.comment.model;

import java.io.Serializable;

import com.j256.ormlite.field.DataType;
import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

@DatabaseTable(tableName = "comment")
public class Comment implements Serializable {

	private static final long serialVersionUID = 5L;
	
	@DatabaseField(id = true , columnName = "id")
	private int id;
	
	@DatabaseField(columnName = "note_id")	
	private int noteId;
	
	@DatabaseField(columnName = "user_id")	
	private int userId;
	
	@DatabaseField(columnName = "comment", dataType = DataType.STRING)
	private String comment;
	
	@DatabaseField(columnName = "is_like")	
	private int isLike;
	
	@DatabaseField(columnName = "file_preview_url", dataType = DataType.STRING)
	private String filePreviewUrl;

	public String getFilePreviewUrl() {
		return filePreviewUrl;
	}

	public void setFilePreviewUrl(String filePreviewUrl) {
		this.filePreviewUrl = filePreviewUrl;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public int getNoteId() {
		return noteId;
	}

	public void setNoteId(int noteId) {
		this.noteId = noteId;
	}

	public int getUserId() {
		return userId;
	}

	public void setUserId(int userId) {
		this.userId = userId;
	}

	public String getComment() {
		return comment;
	}

	public void setComment(String comment) {
		this.comment = comment;
	}

	public int getIsLike() {
		return isLike;
	}

	public void setIsLike(int isLike) {
		this.isLike = isLike;
	}
}
