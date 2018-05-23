package com.ugao.ugaomanagement.model

class Article {

    lateinit var title: String
    lateinit var author: String

    constructor(title: String, author: String) {
        this.title = title
        this.author = author
    }

    constructor()
}