package ru.commenthere.comment.model;

import java.io.Serializable;

public class Comment implements Serializable {

	private static final long serialVersionUID = 5L;
	
	private int id;
	private int noteId;
	private int userId;
	private String comment;
	private int isLike;
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
