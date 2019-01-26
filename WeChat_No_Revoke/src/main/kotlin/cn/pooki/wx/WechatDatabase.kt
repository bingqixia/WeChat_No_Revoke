package cn.pooki.wx

import android.database.Cursor
import de.robv.android.xposed.XposedHelpers

/**
 * Created by rarnu on 1/11/17.
 */
class WechatDatabase(db: Any?) {

    private var database = db

    // private fun insert(table: String?, selection: String?, values: ContentValues?) = XposedHelpers.callMethod(database, "insert", table, selection, values)

    // private fun rawQuery(query: String?) = rawQuery(query, null)

    private fun rawQuery(query: String?, args: Array<String?>?) = XposedHelpers.callMethod(database, "rawQuery", query, args) as? Cursor

    fun getMessageViaId(id: String?) = rawQuery("select * from message where msgSvrId=?", arrayOf(id))

    // fun insertSystemMessage(talker: String?, talkerId: Int, msg: String?, createTime: Long) = insertMessage(talker, talkerId, msg, 10000, createTime)

    /*
    private fun insertMessage(talker: String?, talkerId: Int, msg: String?, type: Int, createTime: Long) {
        val msgSvrId = createTime + (Random().nextInt())
        val msgId = getNextMsgId()
        val v = ContentValues()
        v["msgid"] = msgId
        v["msgSvrid"] = msgSvrId
        v["type"] = type
        v["status"] = 3
        v["createTime"] = createTime
        v["talker"] = talker
        v["content"] = msg
        if (talkerId != -1) {
            v.put("talkerid", talkerId)
        }
        insert("message", "", v)
    }
    */

    /*
    private fun getNextMsgId(): Long {
        var id = -1L
        val cur = rawQuery("SELECT max(msgId) FROM message")
        if (cur != null) {
            if (cur.moveToFirst()) {
                id = cur.getLong(0) + 1
            }
            cur.close()
        }
        return id
    }
    */
}