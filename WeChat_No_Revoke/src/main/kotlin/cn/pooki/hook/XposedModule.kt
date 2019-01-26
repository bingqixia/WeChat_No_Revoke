package cn.pooki.hook

import android.util.Log
import cn.pooki.wx.WechatDatabase
import cn.pooki.wx.WechatVersion
import de.robv.android.xposed.IXposedHookLoadPackage
import de.robv.android.xposed.XC_MethodHook
import de.robv.android.xposed.XposedHelpers
import de.robv.android.xposed.callbacks.XC_LoadPackage

class XposedModule : IXposedHookLoadPackage {

    companion object {
        const val TAG = "pooki_xposed"
    }

    private var database: WechatDatabase? = null
    private var hookMap = mutableMapOf<String, String>()

    @Throws(Throwable::class)
    override fun handleLoadPackage(loadParam: XC_LoadPackage.LoadPackageParam) {
        if (loadParam.packageName == "com.tencent.mm") {
            WechatVersion.getWxVersionName {
                WechatVersion.loadHookMap(hookMap, it)
                if (hookMap.isNotEmpty()) {
                    hookRevoke(loadParam)
                }
            }
        }
    }

    private fun hookRevoke(loadParam: XC_LoadPackage.LoadPackageParam) {

        val aclz = XposedHelpers.findClass("com.tencent.mm.cf.h\$a", loadParam.classLoader)
        XposedHelpers.findAndHookConstructor("com.tencent.mm.cf.h", loadParam.classLoader, aclz, object : XC_MethodHook() {
            override fun afterHookedMethod(param: MethodHookParam) {
                Log.e(TAG, "hookDatabase 1")
                database = WechatDatabase(param.thisObject)
                Log.e("Pooki", "hookDatabase 1 db $database")
            }
        })

        XposedHelpers.findAndHookMethod(hookMap["mapClass"], loadParam.classLoader, hookMap["mapMethod"], String::class.java, String::class.java, object : XC_MethodHook() {
            override fun afterHookedMethod(param: MethodHookParam) {
                Log.e(TAG, "pooki in xposed method ")

                @Suppress("UNCHECKED_CAST")
                val m = param.result as MutableMap<String, String?>?
                if (m == null) {
                    Log.e(TAG, "map is null")
                    return
                }
                for ((k, v) in m) {
                    Log.e(TAG, "k => $k, v => $v")
                }

                val type = m[".sysmsg.\$type"]
                if (type == "revokemsg") {
                    Log.e(TAG, "type => $type")
                    var replaceMsg = m[".sysmsg.revokemsg.replacemsg"]!!
                    Log.e(TAG, "replace => $replaceMsg")
                    if (replaceMsg.startsWith("你") || replaceMsg.toLowerCase().startsWith("you")) {
                        return
                    }
                    try {
                        val msgId = m[".sysmsg.revokemsg.newmsgid"]
                        Log.e(TAG, "msgid => $msgId")
                        val cur = database?.getMessageViaId(msgId)
                        Log.e(TAG, "cursor => $cur")
                        if (cur != null) {
                            if (cur.moveToFirst()) {

                                val t = cur.getInt(cur.getColumnIndex("type"))
                                if (t == 1) {
                                    val content = cur.getString(cur.getColumnIndex("content")).trim()
                                    Log.e(TAG, "revoked content => $content")
                                    val rep = replaceMsg.split("\"")
                                    replaceMsg = "\"${rep[1]}\" 试图撤回一条消息:\n$content"
                                    Log.e(TAG, "replaced content => $replaceMsg")
                                    m[".sysmsg.revokemsg.replacemsg"] = replaceMsg
                                } else {
                                    m[".sysmsg.\$type"] = null
                                }
                            }
                            cur.close()
                        }
                        param.result = m
                    } catch (t: Throwable) {

                    }
                }
            }
        })
    }
}
