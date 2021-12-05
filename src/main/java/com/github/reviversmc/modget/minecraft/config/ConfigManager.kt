//package com.github.pozitp.config
//
//import java.io.File
//import java.io.FileOutputStream
//import java.io.FileReader
//import java.io.IOException
//import java.util.*
//
//
//class ConfigManager {
//    companion object {
//        val INSTANCE: ConfigManager = ConfigManager()
//    }
//    private var loaded = false
//    private val prop: Properties = Properties()
//
//
//    fun getBooleanProperty(key: String?): Boolean {
//        if (!loaded) load()
//        return java.lang.Boolean.parseBoolean(prop.getProperty(key))
//    }
//
///*    fun getStringProperty(key: String?): String? {
//        if (!loaded) load()
//        return prop.getProperty(key)
//    }
//
//    fun getNumberProperty(key: String?): Int {
//        if (!loaded) load()
//        return prop.getProperty(key).toInt()
//    }
//
//    fun getDoubleProperty(key: String?): Double {
//        if (!loaded) load()
//        return prop.getProperty(key).toDouble()
//    }*/
//
//    private val file: File = File("./config/fabrilousupdater/config.properties")
//    fun setValue(key: String, value: String) {
//        prop.setProperty(key, value)
//        val writer = FileOutputStream(file)
//        file.createNewFile()
//        prop.store(writer, "FabrilousUpdater config")
//        writer.close()
//    }
//    private fun load() {
//        loaded = true
//        try {
//            File("./config/fabrilousupdater").mkdir()
//            if (file.exists()) {
//                val reader = FileReader(file)
//                prop.load(reader)
//                reader.close()
//            } else {
//                val writer = FileOutputStream(file)
//                file.createNewFile()
//                prop.setProperty("autoCheck", "true")
//                prop.store(writer, "FabrilousUpdater config")
//                writer.close()
//            }
//        } catch (e: IOException) {
//            e.printStackTrace()
//        }
//    }
//}
