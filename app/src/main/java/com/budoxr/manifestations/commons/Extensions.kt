package com.budoxr.manifestations.commons

import android.annotation.SuppressLint
import android.util.Log
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.transformLatest
import java.text.DecimalFormat
import java.text.SimpleDateFormat
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.Date
import java.util.Locale
import kotlin.time.Duration

/**
 * The extension allows to perform a periodical broadcasting through a flow of data.
 * return matchDataUseCase.getUpcomingMatches()
 * Samples of use:
fun getDataFlow() : Flow<List<Data>> {
val lstItem: MutableList<MatchItemData> = mutableListOf()
return dataUseCase.getAllDataFlow()
.emitLastestPeriodically(30.seconds)     // <--  AQUI
.map { entities ->
entities.filter { !it.played }.forEach { entity ->
var model = entity.toModel()
var teams: List<TeamEntity> = listOf(
matchDataUseCase.getTeamById(entity.homeTeamId),
matchDataUseCase.getTeamById(entity.awayTeamId),
)
model.homeTeam = teams[0].toModel()
model.awayTeam = teams[1].toModel()
var item = getItemSelectedMatch(model)
lstItem.add(item)
}
return@map lstItem
}
}
 */
@OptIn(kotlinx.coroutines.ExperimentalCoroutinesApi::class)
fun <T> Flow<T>.emitLastestPeriodically(interval: Duration): Flow<T> = transformLatest {
    while(true) {
        Log.d(TAG, "emitLastestPeriodically() -> emit: $it")
        emit (it)
        delay(interval)
    }
}

fun String.toLocalDate(): LocalDate {
    val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd kk:mm:ss")
    val localDateTime = LocalDateTime.parse(this, formatter)
    return localDateTime.toLocalDate()
}

@SuppressLint("LogNotTimber")
fun String.fromFechaTimeDb(): Date {
    val patternIn: String  = "yyyy-MM-dd kk:mm:ss"
    val sDate = this.substring(0,19).replace('T',' ')
    Log.d(TAG, "fromFechaTimeDb() -> \'$this\', to: \'$sDate\'")
    val sdf = SimpleDateFormat(patternIn, Locale("en", "US"))
    val date = sdf.parse(sDate)
    return date!!

}


fun Date.toFechaTimeDb(): String {
    val patternOut: String  = "yyyy-MM-dd kk:mm:ss"
    val sdf = SimpleDateFormat(patternOut, Locale.getDefault())
    val fecha = sdf.format(this)
    return fecha
}


// el formato: 20240315172407208
fun Date.toFechaTimeSms(): String {
    val patternOut: String  = "yyyyMMddkkmmssSSS"
    val sdf = SimpleDateFormat(patternOut, Locale("en", "US"))
    val fecha = sdf.format(this)
    return fecha
}

fun Date.toFechaUi(): String {
    //12 noviembre 2023
    val patternOut: String  = "dd MMMM yyyy"
    val sdf = SimpleDateFormat(patternOut, Locale("en", "US"))
    val fecha = sdf.format(this)
    return fecha
}

fun Date.toFechaShortUi(): String {
    //     31/12/2023
    val patternOut: String  = "dd/MM/yyyy"
    val sdf = SimpleDateFormat(patternOut, Locale("en", "US"))
    val fecha = sdf.format(this)
    return fecha
}

fun Date.toTimeUi(): String {
    val patternOut: String  = "hh:mm:ss"
    val sdf = SimpleDateFormat(patternOut, Locale("en", "US"))
    val fecha = sdf.format(this)
    return fecha
}

fun Date.toShortTimeUi(): String {
    val patternOut: String  = "hh:mm a"
    val sdf = SimpleDateFormat(patternOut, Locale("en", "US"))
    val fecha = sdf.format(this)
    return fecha
}

fun Date.toFechaTimeUi(): String {
    val patternOut: String  = "dd-MM-yyyy : hh:mm:ss a"
    val sdf = SimpleDateFormat(patternOut, Locale("en", "US"))
    val fecha = sdf.format(this).replace("PM", "p.m.").replace("AM", "a.m.")
    return fecha
}

/**
 * Convert:
 * 04141112233 to (0414)111-2233
 * 4141112233 to (0414)111-2233
 */
fun String.toPhoneUI(): String {
    // 04141112233 a (0414)-111-2233
    val tlf = this
    // (0414) 111-2233
    var fmtPhone = ""
    if (tlf.first() == '0') {
        fmtPhone = "(${tlf.substring(0,4)})"
        fmtPhone += " ${tlf.substring(4, 7)}"
        fmtPhone += "-${tlf.substring(7, tlf.length)}"
    } else {
        fmtPhone = "(${tlf.substring(0,3)})"
        fmtPhone += " ${tlf.substring(3, 6)}"
        fmtPhone += "-${tlf.substring(6, tlf.length)}"
    }
    return fmtPhone
}

/**
 *  convert: V11222333 to V-11.222.333
 */
fun String.toCedulaUI(): String {
    val id = this
    val num = id.substring(1).toDouble()
    val df: DecimalFormat = DecimalFormat("###,###,###")
    val strNumber = df.format(num).replace(',', '.')

    return "${id.substring(0, 1)}-${strNumber}"

}

/**
 *  convert: 1234567.35 to 1.234.567,35
 */
fun String.toAmountUI(): String {
    val num = this.toDouble()
    val df: DecimalFormat = DecimalFormat("#,###,###,##0.00")
    val strNumber = df.format(num).replace('.', '@').replace(',','.')

    return "${strNumber.replace('@',',')}"
}

fun String.toAmountSms(): String {
    val num = this.toDouble()
    val df: DecimalFormat = DecimalFormat("##0.00")
    val strNumber = df.format(num).replace('.', ',')

    return strNumber
}


fun String.toDecimalUI(): String {
    val num = this.toDouble()
    val df: DecimalFormat = DecimalFormat("#0.00")
    val strNumber = df.format(num).replace('.', ',')

    return "$strNumber"
}





private const val TAG = "che.Extensions"

