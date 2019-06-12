package vgTools

import java.text.SimpleDateFormat
import java.util.*
import java.util.logging.FileHandler
import java.util.logging.Level
import java.util.logging.Logger
import java.util.logging.SimpleFormatter

class VGLogger(
    private val name: String = "no title"
) {
    var logger: Logger? = null
        get() {
            if (field == null) logger = buildLogger()
            return field
        }
    private val sdf = SimpleDateFormat("yyyy-MM-dd")
    private val fileName = "$name.${sdf.format(Date())}.log"
    private val fileHandler = FileHandler(fileName, true)

    private fun buildLogger(): Logger {
        val logger = Logger.getLogger(name)
        settingFileHandler()
        addHandler()
        return logger
    }

    private fun settingFileHandler() {
        fileHandler.level = Level.ALL
        fileHandler.formatter = SimpleFormatter()
    }

    private fun addHandler() = logger?.addHandler(fileHandler)
}