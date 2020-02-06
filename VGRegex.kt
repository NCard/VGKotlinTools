package vgTools

fun String.isNatureNumberWithZero(): Boolean = this.matches(Regex("^[0-9]+$"))
fun String.isNatureNumber(): Boolean = this.isNatureNumberWithZero() && this != "0"
fun String.isPositiveRealNumber(): Boolean = this.matches(Regex("^[0-9]+.[0-9]+$"))
fun String.isEnglishAndNumber(): Boolean = this.matches(Regex("^[0-9a-zA-Z]+$"))