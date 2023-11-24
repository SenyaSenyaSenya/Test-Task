package com.example.testtask.model


class PexelsImageWrapper {
    var id = 0
    var originalUrl: String? = null
    var photographer: String? = null
    var mediumUrl: String? = null

    constructor() {}
    constructor(id: Int, photographer: String?, originalUrl: String?, mediumUrl: String?) {
        this.id = id
        this.photographer = photographer
        this.originalUrl = originalUrl
        this.mediumUrl = mediumUrl
    }

    constructor(originalUrl: String?, photographer: String?, mediumUrl: String?) {
        this.originalUrl = originalUrl
        this.photographer = photographer
        this.mediumUrl = mediumUrl
    }
}
