package com.github.bfsmith.recorder.util

import java.text.SimpleDateFormat
import java.util.*

fun Date.toString(format: String): String = SimpleDateFormat(format).format(this)
