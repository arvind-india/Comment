package ru.commenthere.comment.db;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import ru.commenthere.comment.dao.CommentDAO;
import ru.commenthere.comment.dao.NoteDAO;
import ru.commenthere.comment.model.Comment;
import ru.commenthere.comment.model.Note;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.j256.ormlite.android.apptools.OrmLiteSqliteOpenHelper;
import com.j256.ormlite.support.ConnectionSource;
import com.j256.ormlite.table.TableUtils;

public class ORMDatabaseHelper extends OrmLiteSqliteOpenHelper {

	private static final String TAG = ORMDatabaseHelper.class.getSimpleName();

	private static final String DATABASE_NAME = "comment.sqlite";

	private static final int DATABASE_VERSION = 1;

	private NoteDAO noteDao = null;
	private CommentDAO commentDAO = null;
	
	public ORMDatabaseHelper(Context context) {
		super(context, DATABASE_NAME, null, DATABASE_VERSION);
	}

	@Override
	public void onCreate(SQLiteDatabase db, ConnectionSource connectionSource) {
		try {
			TableUtils.createTable(connectionSource, Note.class);
			TableUtils.createTable(connectionSource, Comment.class);
	
		} catch (SQLException e) {
			Log.e(TAG, "error creating DB " + DATABASE_NAME);
			throw new RuntimeException(e);
		}
	}


	@Override
	public void onUpgrade(SQLiteDatabase db, ConnectionSource connectionSource,
			int oldVer, int newVer) {
		try {
			TableUtils.dropTable(connectionSource, Note.class, true);
			TableUtils.dropTable(connectionSource, Comment.class, true);
			onCreate(db, connectionSource);
		} catch (SQLException e) {
			Log.e(TAG, "error upgrading db " + DATABASE_NAME + "from ver "
					+ oldVer);
			throw new RuntimeException(e);
		}
	}

	public NoteDAO getNoteDAO() throws SQLException {
		if (noteDao == null) {
			noteDao = new NoteDAO(getConnectionSource(), Note.class);
		}
		return noteDao;
	}

	public CommentDAO getCommentDAO() throws SQLException {
		if (commentDAO == null) {
			commentDAO = new CommentDAO(getConnectionSource(),Comment.class);
		}
		return commentDAO;
	}



	@Override
	public void close() {
		super.close();
		noteDao = null;
		commentDAO = null;
	}
}