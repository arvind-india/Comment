package ru.commenthere.comment.model;

public class Note {

	private String fileName;
	private String description;
	private String fileNamePreview;
	private int id;
	private int userId;
	private int type;
	private int fileType;
	private int likes;
	private int dislikes;
	private int isCanSendComment;
	
	
	public String getFileNamePreview() {
		return fileNamePreview;
	}
	public void setFileNamePreview(String fileNamePreview) {
		this.fileNamePreview = fileNamePreview;
	}
	
	public String getFileName() {
		return fileName;
	}
	public void setFileName(String fileName) {
		this.fileName = fileName;
	}
	public String getDescription() {
		return description;
	}
	public void setDescription(String description) {
		this.description = description;
	}
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public int getUserId() {
		return userId;
	}
	public void setUserId(int userId) {
		this.userId = userId;
	}
	public int getType() {
		return type;
	}
	public void setType(int type) {
		this.type = type;
	}
	public int getFileType() {
		return fileType;
	}
	public void setFileType(int fileType) {
		this.fileType = fileType;
	}
	public int getLikes() {
		return likes;
	}
	public void setLikes(int likes) {
		this.likes = likes;
	}
	public int getDislikes() {
		return dislikes;
	}
	public void setDislikes(int dislikes) {
		this.dislikes = dislikes;
	}
	public int getIsCanSendComment() {
		return isCanSendComment;
	}
	public void setIsCanSendComment(int isCanSendComment) {
		this.isCanSendComment = isCanSendComment;
	}
}
