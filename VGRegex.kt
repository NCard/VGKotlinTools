package vgTools

fun String.isNatureNumber(): Boolean = this.matches(Regex("^[0-9]+$"))
fun String.isPositiveRealNumber(): Boolean = this.matches(Regex("^[0-9]+.[0-9]+$"))
fun String.isEnglishAndNumber(): Boolean = this.matches(Regex("^[0-9a-zA-Z]+$"))