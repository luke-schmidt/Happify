package app

import kotlinext.js.Object
import kotlinx.html.*
import kotlinx.html.dom.create
import kotlinx.html.js.onClickFunction
import react.*
import react.dom.*
import logo.*
import org.w3c.dom.events.Event
import org.w3c.fetch.RequestCredentials
import org.w3c.fetch.RequestInit
import org.w3c.fetch.Response
import ticker.*
import kotlin.browser.localStorage
import kotlin.browser.window
import kotlin.js.Date
import kotlin.js.Promise
import kotlin.js.json

interface AppState : RState {
    var myFaceUrl: String
}

class App : RComponent<RProps, AppState>() {
    override fun componentDidMount() {
        window.addEventListener("message", {}, false)
    }
    override fun RBuilder.render() {
        div("App-header") {
            logo()
            h2 {
                +"Welcome to React with Kotlin"
            }
        }
        p("App-intro") {
            +"To get started, edit "
            code { +"app/App.kt" }
            +" and save to reload."
        }
        p("App-ticker") {
            ticker()
        }
        button {
            +"click"
            attrs.onClickFunction = { login() }
        }
        button {
            +"show my face"
            attrs.onClickFunction = { getData() }
        }
        img(src = state.myFaceUrl) {}

    }

    private fun getData() {
        val token = findGetParameter("access_token")
        getMyData(token).then( {
            console.log(it, "it")
            setState { myFaceUrl = it.asDynamic().images[0].url }
        })
    }
    private fun findGetParameter(parameterName: String): String {
        var result = ""
        var tmp: List<String>
        window.location.hash.substring(1)
                .split("&")
                .forEach({ item ->
                    tmp = item.split("=")
                    if (tmp[0] === parameterName) {
                        result = tmp[1]
                    }
                })
        return result
    }

    private fun login() {
        val clientId = "82e5a4c22f7348ff96167dc2b2214ca1"
        val redirectUri = "http://localhost:3000/"
        val scopes = arrayOf("user-read-private",
                "playlist-read-private",
                "playlist-modify-public",
                "playlist-modify-private",
                "user-library-read",
                "user-library-modify",
                "user-follow-read",
                "user-follow-modify")

        val url = "https://accounts.spotify.com/authorize?client_id=$clientId" +
                "&response_type=token" +
                "&scope=${scopes.joinToString(" ")}" +
                "&redirect_uri=${encodeURIComponent(redirectUri)}"

        val width = 450
        val height = 730
        val left = (window.screen.width / 2) - (width / 2)
        val top = (window.screen.height / 2) - (height / 2)

        window.open(url,
                "Spotify",
                "menubar=no,location=no,resizable=no,scrollbars=no,status=no,width=$width,height=$height,top=$top,left=$left")
    }
}

external fun encodeURIComponent(str: String): String


fun getMyData(token: String): Promise<Any?> {
    return window.fetch("https://api.spotify.com/v1/me", object : RequestInit {
        override var method: String? = "GET"
        override var headers: dynamic = json("Accept" to "application/json", "Authorization" to "Bearer $token")
    }).then { it.json() }
}
fun RBuilder.app() = child(App::class) {}
