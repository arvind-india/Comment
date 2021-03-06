package ru.commenthere.comment.net;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.HttpEntity;
import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.entity.StringEntity;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpParams;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import ru.commenthere.comment.AppContext;
import ru.commenthere.comment.Application;
import ru.commenthere.comment.model.Comment;
import ru.commenthere.comment.model.Note;
import ru.commenthere.comment.model.User;
import ru.commenthere.comment.utils.AppUtils;

import android.net.Uri;
import android.util.Log;

public class ConnectionProtocol {

	private static final String TAG = "ConnectionProtocol";

	private final static String SEND_CODE_ACTION = "send_code";
	private final static String VERIFY_CODE_ACTION = "verify_code";
	private final static String GET_NOTES_ACTION = "get_notes";
	private final static String CREATE_NOTE_ACTION = "create_note";
	private final static String GET_COMMENTS_ACTION = "get_comments";
	private final static String ADD_COMMENT_ACTION = "add_comment";
	private final static String GET_NEW_COMMENTS_ACTION = "get_new_comments_for_my_notes";
	
	//for batch operations
	private final static String CREATE_NOTES_ACTION = "create_notes";
	private final static String ADD_COMMENTS_ACTION = "add_comments";

	
	private final static int OK_CODE = 0;

	private final static String ERROR_PARAM_NAME = "error";
	private final static String RESULT_PARAM_NAME = "result";
	private final static String ACTION_PARAM_NAME = "act";
	private final static String EMAIL_PARAM_NAME = "email";
	private final static String CODE_PARAM_NAME = "code";
	private final static String TOKEN_PARAM_NAME = "token";
	private final static String ID_PARAM_NAME = "id";
	private final static String LONGITUDE_PARAM_NAME = "longitude";
	private final static String LATITUDE_PARAM_NAME = "latitude";
	private final static String USER_ID_PARAM_NAME = "user_id";
	private final static String DESCRIPTION_PARAM_NAME = "descr";
	private final static String TYPE_PARAM_NAME = "type";
	private final static String FILE_TYPE_PARAM_NAME = "filetype";
	private final static String FILE_NAME_PARAM_NAME = "filename";
	private final static String LIKES_PARAM_NAME = "likes";
	private final static String DISLIKES_PARAM_NAME = "dislikes";
	private final static String CAN_SEND_COMMNET_PARAM_NAME = "is_can_send_comment";
	private final static String FILE_NAME_PREVIEW_PARAM_NAME = "filename_preview";
	private final static String PHOTO_PARAM_NAME = "photo";
	private final static String VIDEO_PARAM_NAME = "video";
	private final static String NOTE_ID_PARAM_NAME = "note_id";
	private final static String COMMENT_PARAM_NAME = "comment";
	private final static String IS_LIKE_PARAM_NAME = "is_like";
	private static final String HAS_NEW_COMMENTS_NAME = "is_has_new_comments";
	private static final String NEW_COMMENTS_ARRAY = "get_new_comments_for_my_notes";

	private static boolean hasNewComments;
	
	private ConnectionClient client;
	private String baseUrl;

	private long startTime;
	private long stopTime;

	public ConnectionProtocol(String baseUrl) {
		this.baseUrl = baseUrl;
		this.client = new ConnectionClient();
	}

	public boolean sendCode(String email) throws ConnectionClientException {

		Uri.Builder ub = Uri.parse(AppContext.API_URL).buildUpon();
		ub.appendQueryParameter(ACTION_PARAM_NAME, SEND_CODE_ACTION);
		ub.appendQueryParameter(EMAIL_PARAM_NAME, email);

		String response = client.sendGetRequest(ub.toString());

		JSONObject result = null;

		try {
			result = new JSONObject(response);
			int errorCode = result.optInt(ERROR_PARAM_NAME);
			if (errorCode == OK_CODE) {
				JSONObject actionResult = result
						.getJSONObject(SEND_CODE_ACTION);
				if (actionResult.optBoolean(RESULT_PARAM_NAME)) {
					return true;
				} else {
					return false;
				}
			} else {
				throw new ConnectionClientException(
						ErrorMessages.errors[errorCode]);
			}

		} catch (JSONException e) {
			throw new ConnectionClientException("JSONException", e);
		}
	}

	public User verifyCode(String email, String code)
			throws ConnectionClientException {
		User user = null;

		Uri.Builder ub = Uri.parse(AppContext.API_URL).buildUpon();
		ub.appendQueryParameter(ACTION_PARAM_NAME, VERIFY_CODE_ACTION);
		ub.appendQueryParameter(EMAIL_PARAM_NAME, email);
		ub.appendQueryParameter(CODE_PARAM_NAME, code);

		String response = client.sendGetRequest(ub.toString());

		JSONObject result = null;

		try {
			result = new JSONObject(response);
			int errorCode = result.optInt(ERROR_PARAM_NAME);
			if (errorCode == OK_CODE) {
				JSONObject actionResult = result
						.getJSONObject(VERIFY_CODE_ACTION);
				String token = actionResult.optString(TOKEN_PARAM_NAME);
				Integer userId = actionResult.optInt(ID_PARAM_NAME);
				user = new User();
				user.setEmail(email);
				user.setId(userId);
				user.setToken(token);
			} else {
				throw new ConnectionClientException(
						ErrorMessages.errors[errorCode]);
			}

		} catch (JSONException e) {
			throw new ConnectionClientException("JSONException", e);
		}

		return user;
	}

	public List<Note> getNotes(double latitude, double longitude,
			String userToken) throws ConnectionClientException {
		List<Note> notes = null;
		Uri.Builder ub = Uri.parse(AppContext.API_URL).buildUpon();
		ub.appendQueryParameter(ACTION_PARAM_NAME, GET_NOTES_ACTION);
		ub.appendQueryParameter(LATITUDE_PARAM_NAME, String.valueOf(latitude));
		ub.appendQueryParameter(LONGITUDE_PARAM_NAME, String.valueOf(longitude));
		ub.appendQueryParameter(TOKEN_PARAM_NAME, userToken);

		String response = client.sendGetRequest(ub.toString());
		JSONObject result = null;
		hasNewComments = false;

		try {
			result = new JSONObject(response);
			int errCode = result.optInt(ERROR_PARAM_NAME);
			if (errCode == OK_CODE) {
				hasNewComments = result.optBoolean(HAS_NEW_COMMENTS_NAME);
				JSONArray respNotes = result.optJSONArray(GET_NOTES_ACTION);
				if (respNotes != null) {
					notes = new ArrayList<Note>(respNotes.length());
					for (int i = 0; i < respNotes.length(); i++) {
						JSONObject note = respNotes.optJSONObject(i);
						if (note != null) {
							Note myNote = new Note();
							myNote.setId(note.optInt(ID_PARAM_NAME));
							myNote.setUserId(note.optInt(USER_ID_PARAM_NAME));
							myNote.setDescription(note
									.optString(DESCRIPTION_PARAM_NAME));
							myNote.setType(note.optInt(TYPE_PARAM_NAME));
							myNote.setFileType(note
									.optInt(FILE_TYPE_PARAM_NAME));
							myNote.setFileName(note
									.optString(FILE_NAME_PARAM_NAME));
							myNote.setLikes(note.optInt(LIKES_PARAM_NAME));
							myNote.setDislikes(note.optInt(DISLIKES_PARAM_NAME));
							myNote.setIsCanSendComment(note
									.optInt(CAN_SEND_COMMNET_PARAM_NAME));
							myNote.setFileNamePreview(note
									.optString(FILE_NAME_PREVIEW_PARAM_NAME));

							notes.add(myNote);
						}
					}
				} else {
					// throw new
					// ConnectionClientException("Error: no items in returned list!!!");
				}

			} else {
				throw new ConnectionClientException(
						ErrorMessages.errors[errCode]);
			}
		} catch (JSONException e) {
			throw new ConnectionClientException("JSONException", e);
		}

		return notes;
	}

	public boolean createNote(Note note, String content)
			throws ConnectionClientException {
		Uri.Builder ub = Uri.parse(AppContext.API_URL).buildUpon();
		ub.appendQueryParameter(ACTION_PARAM_NAME, CREATE_NOTE_ACTION);

		ArrayList<NameValuePair> arguments = new ArrayList<NameValuePair>();
		arguments.add(new BasicNameValuePair(TOKEN_PARAM_NAME, Application
				.getInstance().getAppContext().getUserToken()));
		arguments.add(new BasicNameValuePair(DESCRIPTION_PARAM_NAME, note
				.getDescription()));
		arguments.add(new BasicNameValuePair(TYPE_PARAM_NAME, String
				.valueOf(note.getType())));
		arguments.add(new BasicNameValuePair(FILE_TYPE_PARAM_NAME, String
				.valueOf(note.getFileType())));

		if (note.getType() == 2) {
			arguments.add(new BasicNameValuePair(LONGITUDE_PARAM_NAME, String
					.valueOf(note.getLongitude())));
			arguments.add(new BasicNameValuePair(LATITUDE_PARAM_NAME, String
					.valueOf(note.getLatitude())));
		}

		if (note.getFileType() == 1 /* photo */) {
			arguments.add(new BasicNameValuePair(PHOTO_PARAM_NAME, content));
		} else if (note.getFileType() == 2 /* video */) {
			arguments.add(new BasicNameValuePair(VIDEO_PARAM_NAME, content));
		}

		HttpEntity entity;
		try {
			entity = new UrlEncodedFormEntity(arguments);
			String response = client.sendPostRequest(ub.toString(), entity);

			JSONObject result = null;

			try {
				result = new JSONObject(response);
				int errorCode = result.optInt(ERROR_PARAM_NAME);
				if (errorCode == OK_CODE) {
					JSONObject actionResult = result
							.getJSONObject(CREATE_NOTE_ACTION);
					if (actionResult.optBoolean(RESULT_PARAM_NAME)) {
						return true;
					} else {
						return false;
					}
				} else {
					throw new ConnectionClientException(
							ErrorMessages.errors[errorCode]);
				}

			} catch (JSONException e) {
				throw new ConnectionClientException("JSONException", e);
			}

		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
			throw new ConnectionClientException("UnsupportedEncodingException",
					e);

		} catch (ConnectionClientException e) {
			e.printStackTrace();
			throw new ConnectionClientException("ConnectionClientException", e);
		}
	}

	public List<Comment> getComments(int noteId)
			throws ConnectionClientException {
		List<Comment> comments = null;
		Uri.Builder ub = Uri.parse(AppContext.API_URL).buildUpon();
		ub.appendQueryParameter(ACTION_PARAM_NAME, GET_COMMENTS_ACTION);
		ub.appendQueryParameter(NOTE_ID_PARAM_NAME, String.valueOf(noteId));
		ub.appendQueryParameter(TOKEN_PARAM_NAME, Application.getInstance()
				.getAppContext().getUserToken());

		String response = client.sendGetRequest(ub.toString());
		JSONObject result = null;

		try {
			result = new JSONObject(response);
			int errCode = result.optInt(ERROR_PARAM_NAME);
			if (errCode == OK_CODE) {
				JSONArray respComments = result
						.optJSONArray(GET_COMMENTS_ACTION);
				if (respComments != null) {
					comments = new ArrayList<Comment>(respComments.length());
					for (int i = 0; i < respComments.length(); i++) {
						JSONObject comment = respComments.optJSONObject(i);
						if (comment != null) {
							Comment myComment = new Comment();
							myComment.setId(comment.optInt(ID_PARAM_NAME));
							myComment.setNoteId(comment
									.optInt(NOTE_ID_PARAM_NAME));
							myComment.setUserId(comment
									.optInt(USER_ID_PARAM_NAME));
							myComment.setComment(comment
									.optString(COMMENT_PARAM_NAME));
							myComment.setIsLike(comment
									.optInt(IS_LIKE_PARAM_NAME));
							comments.add(myComment);
						}
					}
				} else {
					// throw new
					// ConnectionClientException("Error: no items in returned list!!!");
				}

			} else {
				throw new ConnectionClientException(
						ErrorMessages.errors[errCode]);
			}
		} catch (JSONException e) {
			throw new ConnectionClientException("JSONException", e);
		}

		return comments;
	}

	public boolean addComment(Comment comment) throws ConnectionClientException {
		Uri.Builder ub = Uri.parse(AppContext.API_URL).buildUpon();
		ub.appendQueryParameter(ACTION_PARAM_NAME, ADD_COMMENT_ACTION);
		ub.appendQueryParameter(COMMENT_PARAM_NAME, comment.getComment());
		ub.appendQueryParameter(NOTE_ID_PARAM_NAME,
				String.valueOf(comment.getNoteId()));
		ub.appendQueryParameter(IS_LIKE_PARAM_NAME,
				String.valueOf(comment.getIsLike()));
		ub.appendQueryParameter(TOKEN_PARAM_NAME, Application.getInstance()
				.getAppContext().getUserToken());

		String response = client.sendGetRequest(ub.toString());

		JSONObject result = null;

		try {
			result = new JSONObject(response);
			int errorCode = result.optInt(ERROR_PARAM_NAME);
			if (errorCode == OK_CODE) {
				JSONObject actionResult = result
						.getJSONObject(ADD_COMMENT_ACTION);
				if (actionResult.optBoolean(RESULT_PARAM_NAME)) {
					return true;
				} else {
					return false;
				}
			} else {
				throw new ConnectionClientException(
						ErrorMessages.errors[errorCode]);
			}

		} catch (JSONException e) {
			throw new ConnectionClientException("JSONException", e);
		}
	}
	
	public List<Comment> getNewComments() throws ConnectionClientException {
		List<Comment> newComments = null;
		Uri.Builder ub = Uri.parse(AppContext.API_URL).buildUpon();
		ub.appendQueryParameter(ACTION_PARAM_NAME, GET_NEW_COMMENTS_ACTION);
		ub.appendQueryParameter(TOKEN_PARAM_NAME, Application.getInstance()
				.getAppContext().getUserToken());
		
		String response = client.sendGetRequest(ub.toString());
		JSONObject result = null;
		
		try {
			result = new JSONObject(response);
			int errorCode = result.optInt(ERROR_PARAM_NAME);
			
			if(errorCode == OK_CODE) {
				JSONArray comments = result.optJSONArray(NEW_COMMENTS_ARRAY);
				newComments = new ArrayList<Comment>(comments.length());
				for(int i = 0; i < comments.length(); i++) {
					Comment comment = new Comment();
					JSONObject rawComment = comments.optJSONObject(i);
					comment.setComment(rawComment.optString(CODE_PARAM_NAME));
					comment.setId(rawComment.optInt(NOTE_ID_PARAM_NAME));
					comment.setIsLike(rawComment.optInt(IS_LIKE_PARAM_NAME));
					comment.setNoteId(rawComment.optInt(NOTE_ID_PARAM_NAME));
					comment.setUserId(rawComment.optInt(USER_ID_PARAM_NAME));
					comment.setFilePreviewUrl(rawComment.optString(FILE_NAME_PREVIEW_PARAM_NAME));
					newComments.add(comment);
				}
			}
		} catch (JSONException e) {
			throw new ConnectionClientException("JSONException", e);
		}
		
		return newComments;
	}

	public static boolean isNewCommentsAvailable() {
		return hasNewComments;
	}
	
	public boolean createNotes(List<Note> notes) throws ConnectionClientException {
		Uri.Builder ub = Uri.parse(AppContext.API_URL).buildUpon();
		ub.appendQueryParameter(ACTION_PARAM_NAME, CREATE_NOTES_ACTION);
		
		HttpParams params = new BasicHttpParams();
		params.setParameter(TOKEN_PARAM_NAME, Application.getInstance().getAppContext().getUserToken());
		
		JSONArray jsonArray = new JSONArray();
		for (Note note : notes){
			JSONObject jsonObject = new JSONObject();
			try {
				jsonObject.put(TOKEN_PARAM_NAME, Application.getInstance().getAppContext().getUserToken());
				jsonObject.put(DESCRIPTION_PARAM_NAME, note.getDescription());
				jsonObject.put(TYPE_PARAM_NAME, String.valueOf(note.getType()));
				if (note.getType() == 2) {
					jsonObject.put(LONGITUDE_PARAM_NAME, String.valueOf(note.getLongitude()));
					jsonObject.put(LATITUDE_PARAM_NAME, String.valueOf(note.getLatitude()));
				}
				String content = AppUtils.encodeFileToBase64(Application.getInstance().getContext(), note.getLocalFilePath());

				if (note.getFileType() == 1 /* photo */) {
					jsonObject.put(PHOTO_PARAM_NAME, content);
				} else if (note.getFileType() == 2 /* video */) {
					jsonObject.put(VIDEO_PARAM_NAME, content);
				}
				jsonArray.put(jsonObject);				
			} catch (JSONException e) {
				e.printStackTrace();
			}
		}

		try {
			String response = client.sendPostRequest(ub.toString(), jsonArray.toString(), params);

			JSONObject result = null;

			try {
				result = new JSONObject(response);
				int errorCode = result.optInt(ERROR_PARAM_NAME);
				if (errorCode == OK_CODE) {
					JSONObject actionResult = result
							.getJSONObject(CREATE_NOTE_ACTION);
					if (actionResult.optBoolean(RESULT_PARAM_NAME)) {
						return true;
					} else {
						return false;
					}
				} else {
					throw new ConnectionClientException(
							ErrorMessages.errors[errorCode]);
				}

			} catch (JSONException e) {
				throw new ConnectionClientException("JSONException", e);
			}

		} catch (ConnectionClientException e) {
			e.printStackTrace();
			throw new ConnectionClientException("ConnectionClientException", e);
		}
	}
	
	public boolean addComments(List<Comment> comments) throws ConnectionClientException {
		Uri.Builder ub = Uri.parse(AppContext.API_URL).buildUpon();
		ub.appendQueryParameter(ACTION_PARAM_NAME, ADD_COMMENTS_ACTION);
		
		HttpParams params = new BasicHttpParams();
		params.setParameter(TOKEN_PARAM_NAME, Application.getInstance().getAppContext().getUserToken());

		JSONArray jsonArray = new JSONArray();
		for (Comment comment : comments){
			JSONObject jsonObject = new JSONObject();
			try {
				jsonObject.put(TOKEN_PARAM_NAME, Application.getInstance().getAppContext().getUserToken());
				jsonObject.put(COMMENT_PARAM_NAME, comment.getComment());
				jsonObject.put(NOTE_ID_PARAM_NAME, String.valueOf(comment.getNoteId()));
				jsonObject.put(IS_LIKE_PARAM_NAME, String.valueOf(comment.getIsLike()));

				jsonArray.put(jsonObject);				
			} catch (JSONException e) {
				e.printStackTrace();
			}
		}

		String response = client.sendPostRequest(ub.toString(), jsonArray.toString(),params);

		JSONObject result = null;

		try {
			result = new JSONObject(response);
			int errorCode = result.optInt(ERROR_PARAM_NAME);
			if (errorCode == OK_CODE) {
				JSONObject actionResult = result
						.getJSONObject(ADD_COMMENT_ACTION);
				if (actionResult.optBoolean(RESULT_PARAM_NAME)) {
					return true;
				} else {
					return false;
				}
			} else {
				throw new ConnectionClientException(
						ErrorMessages.errors[errorCode]);
			}

		} catch (JSONException e) {
			throw new ConnectionClientException("JSONException", e);
		}
	}

	
	
}
