
import java.text.SimpleDateFormat
import java.util.*

object DateConverter{
    fun getLong(dateString: String): Long{
        val formatter = SimpleDateFormat("yyyy-MM-dd HH:mm:ss")
        val date: Date = formatter.parse(dateString) as Date
        return date.time
    }

    fun convertToDays(timeLong: Long): Int {
        val seconds = timeLong / 1000
        val minutes = seconds / 60
        val hours = minutes / 60
        return (hours / 24).toInt()
    }
}

