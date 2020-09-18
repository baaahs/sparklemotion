package baaahs.server

import kotlinx.html.*

fun HTML.index() {
    head {
        meta(charset = "UTF-8")
        meta("viewport", "width=device-width, user-scalable=no")
        title("sparklemotion")
        link(rel = "stylesheet", href = "/styles.css")
        link(rel = "stylesheet", href = "/react-mosaic-component.css")
        link(rel = "stylesheet", href = "/normalize.css")
        link(rel = "stylesheet", href = "/blueprintjs/icons/lib/css/blueprint-icons.css")
        link(rel = "stylesheet", href = "/blueprintjs/core/lib/css/blueprint.css")
        link(rel = "stylesheet", href = "/fontawesome-free/css/all.min.css")
    }

    body {
        div {
            id = "content"

            div {
                style = """
                    background: white;
                    color: black;
                    font - family: 'Press Start 2P', monospace;
                    border: 2px solid black;
                    display: flex;
                    margin: auto;
                    padding: 1em;
                    height: 150px;
                    align-items: center;
                """

                img(src = "/loading.gif", alt = "") {
                    style = "transform: scale(.5);"
                }
                div {
                    style = "padding-right: 5em;"
                    +"Patience..."
                }
            }
        }

        script(type = "application/javascript") {
            unsafe {
                /**language=js*/
                +"""document.resourcesBase = "..";
                    document.sparklemotionMode = "UI";
                    
                    (function () {
                      function loadJs(path) {
                        let el = document.createElement("script");
                        el.setAttribute("src", path);
                        document.head.appendChild(el);
                      }
                    
                      setTimeout(function () {
                        loadJs("/static/sparklemotion.js");
                      });
                    })();
                """.trimIndent()
            }
        }
    }
}
