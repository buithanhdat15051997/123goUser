package sg.go.user;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import sg.go.user.Models.ChatObject;

import java.util.LinkedList;


public class DatabaseHandler extends SQLiteOpenHelper {

	// Database Version
	private static final int DATABASE_VERSION = 1;

	// Database Name
	private static final String DATABASE_NAME = "Smartcar_Chat";

	// Table Chat
	private static final String TABLE_CHAT = "table_chat";
	private static final String _ID = "_id";
	private static final String CLIENT_ID = "client_id";
	private static final String DRIVER_ID = "driver_id";
	private static final String MESSAGE = "message";
	private static final String TYPE = "type";
	private static final String DATA_TYPE = "data_type";
	private static final String REQUEST_ID = "request_id";

	public DatabaseHandler(Context context) {
		super(context, DATABASE_NAME, null, DATABASE_VERSION);

	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		// TODO Auto-generated method stub

		String create_Chat_table = "CREATE TABLE IF NOT EXISTS " + TABLE_CHAT
				+ "(" + _ID + " TEXT PRIMARY KEY, " + CLIENT_ID + " TEXT,"
				+ DRIVER_ID + " TEXT," + TYPE + " TEXT,"+ DATA_TYPE + " TEXT,"+ REQUEST_ID + " TEXT," + MESSAGE
				+ " TEXT )";

		db.execSQL(create_Chat_table);
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		// TODO Auto-generated method stub
		db.execSQL("DROP TABLE IF EXISTS " + TABLE_CHAT);
	}

	public boolean insertChat(ChatObject chat) {

		SQLiteDatabase db = this.getWritableDatabase();
		ContentValues values = new ContentValues();

		// byte[] data= getBitmapAsByteArray(image);
		//Log.d("pavan", "Got a new message");
		values.put(REQUEST_ID, chat.getRequest_id());
		values.put(MESSAGE, chat.getMessage());
		values.put(DRIVER_ID, chat.getDriver_id());
		values.put(DATA_TYPE, chat.getData_type());
		values.put(TYPE, chat.getType());
		values.put(CLIENT_ID, chat.getClient_id());

		long done = db.insert(TABLE_CHAT, null, values);

		db.close();

		if (done > 0) {
			return true;

		} else {

			return false;
		}
	}

	public LinkedList<ChatObject> get_Chat(String request_id) {

		SQLiteDatabase db = this.getReadableDatabase();



		Cursor cursor = db.rawQuery("select * from " + TABLE_CHAT + " where "+REQUEST_ID+"="+request_id, null);

		LinkedList<ChatObject> list = new LinkedList<ChatObject>();

		if (cursor != null && cursor.getCount() > 0) {

			cursor.moveToFirst();
			ChatObject data = null;

			do {

				data = new ChatObject(cursor.getString(cursor
						.getColumnIndex(MESSAGE)), cursor.getString(cursor
						.getColumnIndex(TYPE)), cursor.getString(cursor
						.getColumnIndex(DATA_TYPE)), cursor.getString(cursor
						.getColumnIndex(REQUEST_ID)), cursor.getString(cursor
						.getColumnIndex(DRIVER_ID)), cursor.getString(cursor
						.getColumnIndex(CLIENT_ID)));


				list.add(data);

			} while (cursor.moveToNext());
			db.close();
			//Log.d("pavan", "list.size() " + list.size());

			return list;
		}
		// //db.close();
		return null;

	}


	public boolean DeleteChat(String request_id) {


		SQLiteDatabase db = this.getReadableDatabase();

		if(db.delete(TABLE_CHAT, REQUEST_ID+"="+request_id, null)>0){
			return true;

		}


		return false;
	}
}
