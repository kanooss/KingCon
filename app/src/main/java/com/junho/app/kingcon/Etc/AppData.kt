package com.junho.app.kingcon.Etc

import com.junho.app.kingcon.Item.TagData
import java.io.*

object AppData {
    var mapData: HashMap<String, Any> = hashMapOf()
    var startTime = Util.getTrueTime()
    /**
     * TAG_DATA -> 앱 태그 데이터
     * TAG_VERSION -> 앱 태그 데이터 버전
     */
    val TAG_DATA = "TAG_DATA"
    val TAG_VERSION = "TAG_VERSION"

    fun loadAppData(){
        val file = File(StringData.appData())
        if(file.exists()) {
            val fis = FileInputStream(file)
            val ois = ObjectInputStream(fis)
            mapData = (ois.readObject() as HashMap<String, Any>)
            ois.close()
        }
    }
    fun updateAppData(){
        val file = File(StringData.appData())
        val fos = FileOutputStream(file)
        val oos = ObjectOutputStream(fos)
        oos.writeObject(mapData)
        oos.flush()
    }
    //태그 버전
    fun getTagVersion() =
        if(mapData[TAG_VERSION] == null) 1
        else mapData[TAG_VERSION] as Int
    fun putTagVersion(version: Int){ mapData[TAG_VERSION] = version }
    //태그 데이터
    fun getTagData() = mapData[TAG_DATA] as TagData
    fun putTagData(tagData: TagData){ mapData[TAG_DATA] = tagData}

    override fun toString(): String = mapData.toString()
}