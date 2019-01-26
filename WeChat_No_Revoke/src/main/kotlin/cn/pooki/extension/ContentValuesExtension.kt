package cn.pooki.extension

import android.content.ContentValues

operator fun ContentValues.set(s: String, value: Byte) = this.put(s, value)
operator fun ContentValues.set(s: String, value: Short) = this.put(s, value)
operator fun ContentValues.set(s: String, value: Float) = this.put(s, value)
operator fun ContentValues.set(s: String, value: Double) = this.put(s, value)
operator fun ContentValues.set(s: String, value: Long) = this.put(s, value)
operator fun ContentValues.set(s: String, value: Int) = this.put(s, value)
operator fun ContentValues.set(s: String, value: ByteArray?) = this.put(s, value)
operator fun ContentValues.set(s: String, value: String?) = this.put(s, value)
