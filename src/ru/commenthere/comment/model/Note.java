package ru.commenthere.comment.model;

import java.io.Serializable;

import com.j256.ormlite.field.DataType;
import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

@DatabaseTable(tableName = "note")
public class Note implements Serializable {

	private static final long serialVersionUID = 1L;
	
	@DatabaseField(id = true , columnName = "id")
	private int id;
	
	@DatabaseField(columnName = "file_name", dataType = DataType.STRING)
	private String fileName;
	
	@DatabaseField(dataType = DataType.STRING)
	private String description;
	
	@DatabaseField(columnName = "file_name_preview", dataType = DataType.STRING)	
	private String fileNamePreview;
	
	@DatabaseField(columnName = "user_id")	
	private int userId;

	@DatabaseField(columnName = "type")	
	private int type;
	
	@DatabaseField(columnName = "file_type")		
	private int fileType;
	
	@DatabaseField(columnName = "likes")	
	private int likes;
	
	@DatabaseField(columnName = "dislikes")	
	private int dislikes;
	
	@DatabaseField(columnName = "is_can_send_comment")	
	private int isCanSendComment;
	
	@DatabaseField(columnName = "longitude")	
	private float longitude;
	
	@DatabaseField(columnName = "latitude")	
	private float latitude;

	@DatabaseField(columnName = "local_file_path", dataType = DataType.STRING)		
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
