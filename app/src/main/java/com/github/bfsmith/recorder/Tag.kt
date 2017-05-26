package com.github.bfsmith.recorder

data class Tag( val id: Int = 0, val tag: String) {
    constructor(tag: String) : this(0, tag)
}
