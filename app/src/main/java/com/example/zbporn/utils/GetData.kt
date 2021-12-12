package com.example.zbporn.utils

import android.util.Log
import com.example.zbporn.models.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.jsoup.Jsoup
import java.io.IOException

object GetData {

    suspend fun getZBVideoSource(href: String): String {
        var source = ""
        withContext(Dispatchers.IO) {
            try {
                val document = Jsoup.connect(href).get()
                val element = document.getElementsByClass("player slot-1")
                val link = element.toString()
                    .substringAfterLast("video_url: '")
                    .substringBefore("',")
                source = link
                Log.e("source", "${link}")
            } catch (e: IOException) {
                e.printStackTrace()
            }
        }
        return source
    }

    suspend fun getModelInfo(href: String): MutableList<String> {
        var result = mutableListOf<String>()
        withContext(Dispatchers.IO) {
            try {
                val document = Jsoup.connect(href).get()
                val elements = document.getElementsByClass("model-info")

                val likes = elements.select("a[class=item-action rate-like]")
                    .select("span[class=number]").text()

                val dislikes = elements.select("a[class=item-action rate-dislike]")
                    .select("span[class=number]").text()

                var about = "Aliases: Unknow\nBirthday: Unknow\nAge: Unknow\nBirthplace: Unknow"
                val elmAbout = elements.select("div[class=model-about]")
                    .select("div[class=row-item]")
                if (!elmAbout.isNullOrEmpty()) {
                    var strBuilder = StringBuilder()
                    val size = elmAbout.size
                    for (i in 0 until size) {
                        val first = elmAbout[i].getElementsByClass("label").text()
                        val last = elmAbout[i].getElementsByClass("text").text()
                        strBuilder.append("${first} ${last}\n")
                    }
                    about = strBuilder.toString().trim()
                }

                Log.e("about", "${about}")

                result.add(0, likes)
                result.add(1, dislikes)
                result.add(2, about)
            } catch (e: IOException) {
                e.printStackTrace()
            }
        }
        return result
    }

    suspend fun getChannelInfos(href: String): MutableList<String> {
        var result = mutableListOf<String>()
        withContext(Dispatchers.IO) {
            try {
                val document = Jsoup.connect(href).get()
                val elements = document.getElementsByClass("content-block")

                val backdrop = elements.select("div[class=img-holder desk]")
                    .select("img").attr("src")
                val thumb = elements.select("div[class=row-info]")
                    .select("img").attr("data-src")
                val title = elements.select("div[class=img-holder desk]")
                    .select("img").attr("alt")
                val views = elements.select("div[class=cs-info]")
                    .select("div[class=item-detail]").select("span[class=text]").text()
                val likes = elements.select("div[class=cs-info]")
                    .select("a[class=item-action rate-like]")
                    .select("span[class=number]").text()
                val dislikes = elements.select("div[class=cs-info]")
                    .select("a[class=item-action rate-dislike]")
                    .select("span[class=number]").text()
                val count = elements.select("div[class=cs-info]")
                    .select("span[class=item-action]")
                    .select("span[class=text]").text()
                val description = elements.select("p[class=description]").text()

                result.add(0, backdrop)
                result.add(1, thumb)
                result.add(2, title)
                result.add(3, views)
                result.add(4, likes)
                result.add(5, dislikes)
                result.add(6, count)
                result.add(7, description)
                Log.e("infos", "${title} - ${views} - ${likes} - ${dislikes} - ${description}")
            } catch (e: IOException) {
                e.printStackTrace()
            }
        }
        return result
    }

    suspend fun getPictures(url: String): MutableList<ZPPicture> {
        var result = mutableListOf<ZPPicture>()
        withContext(Dispatchers.IO) {
            try {
                var document = Jsoup.connect(url).get()
                var elements = document.getElementsByClass("gallery-holder js-album")
                    .select("a[class=item]")
                elements.forEach {
                    var small = it.select("img").attr("data-src")
                    var big = it.attr("href")
                    var id = it.attr("data-pid")
                    var title = it.attr("title")
                    var name = "${title.replace(" ", "_")}_${id}"
                    result.add(ZPPicture(id, title, name, small, big))
                    Log.e("picture", "${small} - ${big} - ${id} - ${title} - ${name}")
                }
            } catch (io: IOException) {
                io.printStackTrace()
            }
        }
        return result
    }

    suspend fun getPlaylists(url: String): MutableList<ZBPlaylist> {
        var result = mutableListOf<ZBPlaylist>()
        withContext(Dispatchers.IO) {
            try {
                var document = Jsoup.connect(url).get()

                var limitPage = 0
                if (document.select("div[class=pagination]") == null) {
                    limitPage = 1
                } else {

                    if (document.getElementsByClass("pagination")
                            .select("li[class=last]").select("a").attr("href")
                            .substringBeforeLast("/").substringAfterLast("/").equals("")
                    ) {
                        limitPage = 1
                    } else {
                        limitPage = document.getElementsByClass("pagination")
                            .select("li[class=last]").select("a").attr("href")
                            .substringAfterLast("playlists/")
                            .substringBefore("/").toInt()
                    }
                }
                Log.e("ppp", "${limitPage}")
                Log.e("Playlists", "${limitPage}")

                var elements = document.select("div[class=thumbs]")
                    .select("a[class=th]")

                elements.forEach {
                    var href = it.attr("href")
                    var title = it.attr("title")
                    var thumb = it.select("img").attr("data-src")
                    var count = it.select("span[class=th-duration left]").text()

                    var preview1 = ""
                    var preview2 = ""
                    var preview3 = ""
                    var previewList = it.select("div[class=playlist-previews]")
                    previewList.forEach {
                        preview1 = it.select("img").first().attr("data-src")
                        preview2 = it.select("img").next().attr("data-src")
                        preview3 = it.select("img").last().attr("data-src")
                    }

                    if (!count.isNullOrEmpty()) {
                        result.add(
                            ZBPlaylist(
                                title, href, thumb, preview1,
                                preview2, preview3, count, limitPage
                            )
                        )
                        Log.e(
                            "Playlists", "${title} - ${preview1} - ${preview2} - ${preview3}"
                        )
                    }
                }
            } catch (io: IOException) {
                io.printStackTrace()
            }
        }
        Log.e("Playlists", "${result.size}")
        return result
    }

    suspend fun getModels(url: String): MutableList<ZBModel> {
        var result = mutableListOf<ZBModel>()
        withContext(Dispatchers.IO) {
            try {
                val document = Jsoup.connect(url).get()
                var limitPage = 0
                if (document.select("div[class=pagination]") == null) {
                    limitPage = 1
                } else {

                    if (document.getElementsByClass("pagination")
                            .select("li[class=last]").select("a").attr("href")
                            .substringBeforeLast("/").substringAfterLast("/").equals("")
                    ) {
                        limitPage = 1
                    } else {
                        limitPage = document.getElementsByClass("pagination")
                            .select("li[class=last]").select("a").attr("href")
                            .substringBeforeLast("/").substringAfterLast("/").toInt()
                    }
                }

                Log.e("Model", "${limitPage}")

                val elements = document.getElementsByClass("thumbs thumbs-albums")
                    .select("a[class=th]")
                var count = elements.size
//                Log.e("Count", "${count}")

                elements.forEach {
                    var href = it.attr("href")
                    var name = it.attr("title")
                    var thumb = it.select("img").attr("data-src")
                    var flag = it.select("div[class=th-row-title]")
                        .select("img").attr("data-src")

                    var info = it.getElementsByClass("th-bottom-info")
                        .select("span").text()
                    var videos = info.substringBefore(" ")
                    var photos = info.substringBeforeLast(" ").substringAfter(" ")
                    var likes = info.substringAfterLast(" ")

                    if (!thumb.contains("/images/no-avatar-vertical.svg")) {
                        result.add(
                            ZBModel(
                                href, name, thumb, likes,
                                videos, photos, flag, limitPage
                            )
                        )
                        Log.e(
                            "Model",
                            "${name} - ${videos} - ${photos} - ${likes} - ${thumb} - ${flag}"
                        )
                    }
                }

            } catch (io: IOException) {
                io.printStackTrace()
            }
        }
        return result.subList(6, result.size - 1)
//        return result
    }

    suspend fun getChannels(url: String): MutableList<ZBChannel> {
        var result = mutableListOf<ZBChannel>()
        withContext(Dispatchers.IO) {
            try {
                var document = Jsoup.connect(url).get()
                var elements = document.select("div[class=thumbs thumbs-categories]")
                    .select("a[class=th]")

                var limitPage = 0
                if (document.select("div[class=pagination]") == null) {
                    limitPage = 1
                } else {

                    if (document.getElementsByClass("pagination")
                            .select("li[class=last]").select("a").attr("href")
                            .substringBeforeLast("/").substringAfterLast("/").equals("")
                    ) {
                        limitPage = 1
                    } else {
                        limitPage = document.getElementsByClass("pagination")
                            .select("li[class=last]").select("a").attr("href")
                            .substringBeforeLast("/").substringAfterLast("/").toInt()
//                        Log.e("Pages", "${limitPage}")
                    }
                }
                Log.e("Channel", "${limitPage}")

                elements.forEach {
                    var href = it.attr("href")
                    var thumb = it.select("img").attr("data-src")
                    var title = it.select("img").attr("alt")
                    var count = it.select("span").first().text()
                    var rate = it.select("span").last().text()
                    if (!count.isNullOrEmpty()) {
                        result.add(ZBChannel(title, thumb, count, rate, href, limitPage))
                        Log.e("Channel", "${thumb} - ${href} - ${title} - ${count} - ${rate}")
                    }
                }
            } catch (io: IOException) {
                io.printStackTrace()
            }
        }
        Log.e("Channel", "${result.size}")
        return result
    }

    suspend fun getAlbums(url: String, isSearch: Boolean): MutableList<ZBAlbum> {
        var result = mutableListOf<ZBAlbum>()
        withContext(Dispatchers.IO) {
            try {
                var document = Jsoup.connect(url).get()

                var cssQuery = ""
                if (isSearch) {
                    cssQuery = "div[class=thumbs thumbs-albums-full]"
                } else {
                    cssQuery = "div[class=thumbs thumbs-albums]"
                }

                var elements = document.select(cssQuery)
                    .select("a[class=th]")

                var limitPage = 0
                if (document.select("div[class=pagination]") == null) {
                    limitPage = 1
                } else {

                    if (document.getElementsByClass("pagination")
                            .select("li[class=last]").select("a").attr("href")
                            .substringBeforeLast("/").substringAfterLast("/").equals("")
                    ) {
                        limitPage = 1
                    } else {
                        limitPage = document.getElementsByClass("pagination")
                            .select("li[class=last]").select("a").attr("href")
                            .substringBeforeLast("/").substringAfterLast("/").toInt()
//                        Log.e("Pages", "${limitPage}")
                    }
                }
                Log.e("Album", "${limitPage}")

                elements.forEach {
                    var href = it.attr("href")
                    var title = it.attr("title")
                    var thumb = "https:" + it.select("img").attr("data-src")
                    var count = it.select("span[class=th-duration]").text()
                    var date = it.select("div[class=th-date]").text()
                    if (!count.isNullOrEmpty()) {
                        result.add(ZBAlbum(title, thumb, count, date, href, limitPage))
                        Log.e("Album", "${thumb} - ${date} - ${title} - ${count}")
                    }
                }
            } catch (io: IOException) {
                io.printStackTrace()
            }
        }
        Log.e("Album", "${result.size}")
        return result
    }

    suspend fun getCategories(url: String): MutableList<ZBCategory> {
        var result = mutableListOf<ZBCategory>()
        withContext(Dispatchers.IO) {
            try {
                var document = Jsoup.connect(url).get()
                var elements = document.select("div[class=thumbs thumbs-categories]")
                    .select("a[class=th]")

                Log.e("Category", "${elements.size}")

                elements.forEach {
                    var href = it.attr("href")
                    var title = it.attr("title")
                    var thumb = it.select("img").attr("data-src")
                    var count = it.select("span[class=number]").first().text()
                    result.add(ZBCategory(title, thumb, count, href))
//                    Log.e("Category","${thumb} - ${href} - ${title} - ${count}")

                }
            } catch (io: IOException) {
                io.printStackTrace()
            }
        }
        return result
    }

    suspend fun getVideos(url: String, isSearch: Boolean): MutableList<ZBVideo> {
        var result = mutableListOf<ZBVideo>()
        withContext(Dispatchers.IO) {
            try {
                var document = Jsoup.connect(url).get()
                var cssQuery = ""
                if (isSearch) {
                    cssQuery = "div[class=thumbs thumbs-videos-full]"
                } else {
                    cssQuery = "div[class=thumbs]"
                }
                var elements = document.select(cssQuery)
                    .select("a[class=th]")

                var limitPage = 0
                if (document.select("div[class=pagination]") == null) {
                    limitPage = 1
                } else {

                    if (document.getElementsByClass("pagination")
                            .select("li[class=last]").select("a").attr("href")
                            .substringBeforeLast("/").substringAfterLast("/").equals("")
                    ) {
                        limitPage = 1
                    } else {
                        limitPage = document.getElementsByClass("pagination")
                            .select("li[class=last]").select("a").attr("href")
                            .substringBeforeLast("/").substringAfterLast("/").toInt()
//                        Log.e("Pages", "${limitPage}")
                    }
                }
                Log.e("Pages", "${limitPage}")



                elements.forEach {
                    var href = it.attr("href")
                    var title = it.attr("title")
                    var thumb = it.select("img").attr("data-src")
                    var preview = it.select("div[class=th-image]")
                        .attr("data-preview")
                    var duration = it.select("div[class=th-image]")
                        .select("span").text()
                    var date = it.select("div[class=th-bottom-info]")
                        .select("div[class=th-date]").text()
                    var likes = it.select("div[class=th-bottom-info]")
                        .select("div[class=th-rating]").select("span").text()
                    if (!duration.isNullOrEmpty()) {
                        result.add(
                            ZBVideo(
                                href, title, thumb, duration,
                                date, likes, preview, limitPage
                            )
                        )
//                    Log.e("video", "${duration} - ${date} - ${likes}")
                        Log.e(
                            "video",
                            "${title} - ${thumb} - ${preview} - ${duration} - ${date} - ${likes} - ${href}"
                        )
                    }
                }
            } catch (io: IOException) {
                io.printStackTrace()
            }
        }
        Log.e("count", "${result.size}")
        return result
    }

}