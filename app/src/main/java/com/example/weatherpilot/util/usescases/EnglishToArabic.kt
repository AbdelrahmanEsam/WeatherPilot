package com.example.weatherpilot.util.usescases

fun String.convertNumberEnglishToArabic(convert : Boolean): String {

    if (convert){

    val sb = StringBuilder()
    for (i in this.indices) {
        when (this[i]) {
            '0' -> sb.append('\u0660')
            '1' -> sb.append('\u0661')
            '2' -> sb.append('\u0662')
            '3' -> sb.append('\u0663')
            '4' -> sb.append('\u0664')
            '5' -> sb.append('\u0665')
            '6' -> sb.append('\u0666')
            '7' -> sb.append('\u0667')
            '8' -> sb.append('\u0668')
            '9' -> sb.append('\u0669')
            else -> sb.append(this[i])
        }
    }
    return sb.toString()
    }else{
        return this
    }
}