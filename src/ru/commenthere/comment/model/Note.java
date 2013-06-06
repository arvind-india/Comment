package ru.commenthere.comment.model;

import java.io.Serializable;

public class Note implements Serializable {

	private static final long serialVersionUID = 1L;

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
	private float longitude;
	private float latitude;
	
	private String localFilePath;

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

	public float getLongitude() {
		return longitude;
	}

	public void setLongitude(float longitude) {
		this.longitude = longitude;
	}

	public float getLatitude() {
		return latitude;
	}

	public void setLatitude(float latitude) {
		this.latitude = latitude;
	}
	

	public String getLocalFilePath() {
		return localFilePath;
	}

	public void setLocalFilePath(String localFilePath) {
		this.localFilePath = localFilePath;
	}

	@Override
	public String toString() {
		return "Note [fileName=" + fileName + ", description=" + description
				+ ", fileNamePreview=" + fileNamePreview + ", id=" + id
				+ ", userId=" + userId + ", type=" + type + ", fileType="
				+ fileType + ", likes=" + likes + ", dislikes=" + dislikes
				+ ", isCanSendComment=" + isCanSendComment + "]";
	}
}
