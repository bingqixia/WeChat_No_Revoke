package cn.pooki.wx

import com.rarnu.kt.android.runCommand

object WechatVersion {

    private val maps = mapOf(
            "7.0.0" to mapOf(
                    "mapClass" to "com.tencent.mm.sdk.platformtools.br",     // map class
                    "mapMethod" to "y",                                      // method in map class
                    "databaseParam" to "com.tencent.mm.cf.h\$a",             // param type in get-database method
                    "databaseClass" to "com.tencent.mm.cf.h"                 // database class
            )
    )


    fun getWxVersionName(callback: (ver: String) -> Unit) {
        runCommand {
            runAsRoot = true
            commands.add("pm dump com.tencent.mm")
            result { output, _ ->
                if (output != "") {
                    var vstr = output.substring(output.indexOf("versionName="))
                    vstr = vstr.substring(0, vstr.indexOf("\n"))
                    vstr = vstr.replace("versionName=", "").trim()
                    callback(vstr)
                }
            }
        }
    }

    fun loadHookMap(map: MutableMap<String, String>, version: String) {
        val m = maps[version]
        if (m != null) {
            map.putAll(m)
        }
    }

}