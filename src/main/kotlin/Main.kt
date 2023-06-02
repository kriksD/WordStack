// Copyright 2000-2021 JetBrains s.r.o. and contributors. Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.selection.SelectionContainer
import androidx.compose.material.Icon
import androidx.compose.material.OutlinedTextField
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Clear
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.WindowPosition
import androidx.compose.ui.window.application
import androidx.compose.ui.window.rememberWindowState
import java.awt.Desktop
import java.io.File
import java.net.URI
import java.net.URISyntaxException
import java.net.URL


val colorBackground = Color(20, 20, 20)
val colorBackgroundSecond = Color(40, 40, 40)
val colorText = Color(220, 220, 220)
val colorTextSecond = Color(180, 180, 180)

val corners = 12.dp
val padding = 8.dp

fun main() = application {
    val windowState = rememberWindowState(width = 440.dp, height = 340.dp, position = WindowPosition(Alignment.Center))
    val words = remember {
        val list = mutableStateListOf<String>()
        list.addAll(load())
        list
    }
    var isAdding by remember { mutableStateOf(false) }

    Window(onCloseRequest = ::exitApplication, state = windowState) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(colorBackground)
        ) {
            Header(
                modifier = Modifier
                    .fillMaxWidth(),
                onAdd = {
                    isAdding = true
                },
                onNext = {
                    words.removeFirstOrNull()
                    save(words)
                },
            )

            if (isAdding) {
                AddWordCard(
                    onCancel = {
                        isAdding = false
                    },
                    onAdd = {
                        words.add(it)
                        save(words)
                        isAdding = false
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1F),
                )

            } else {
                WordCard(
                    words.firstOrNull() ?: "(no words)",
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1F),
                )
            }
        }
    }
}

fun MutableList<String>.makeString(): String {
    var newString = ""
    forEach {
        newString += "$it\n".replace("\r", "")
    }
    return newString.dropLast(1)
}

fun readString(string: String): List<String> {
    val strings = string.split("\n")
    val newStrings = mutableListOf<String>()

    strings.forEachIndexed { i, str ->
        if (str.contains(Regex("[a-zA-Z]"))) {
            newStrings.add(str.replace("\r", ""))
        }
    }
    return newStrings
}

val file = File("words.txt")
fun load(): List<String> {
    if (!file.exists()) return listOf()
    return readString(file.readText())
}

fun save(list: MutableList<String>) {
    file.writeText(list.makeString())
}

@Composable
private fun Header(
    onNext: () -> Unit = {},
    onAdd: () -> Unit = {},
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier
            .padding(padding),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Icon(
            Icons.Default.Add,
            contentDescription = null,
            tint = colorText,
            modifier = Modifier
                .size(40.dp)
                .aspectRatio(1F)
                .clickable(onClick = onAdd),
        )
        Icon(
            Icons.Default.ArrowForward,
            contentDescription = null,
            tint = colorText,
            modifier = Modifier
                .size(40.dp)
                .aspectRatio(1F)
                .clickable(onClick = onNext),
        )
    }
}

@Composable
private fun WordCard(
    word: String,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier
            .padding(padding)
            .background(colorBackgroundSecond, RoundedCornerShape(corners))
            .padding(padding),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Column {
            Row {
                SelectionContainer {
                    Text(
                        word,
                        color = if (word == "(no words)") colorTextSecond else colorText,
                        fontSize = 44.sp,
                    )
                }

                if (word != "(no words)") {
                    Icon(
                        painterResource("volume_up.svg"),
                        null,
                        tint = colorText,
                        modifier = Modifier
                            .size(28.dp)
                            .align(Alignment.CenterVertically)
                            .clickable {
                                openWebpage(URL("https://www.google.com/search?q=$word+pronunciation&client=opera&biw=1880&bih=962&sxsrf=APwXEde9lAteZy-_Igt7aIiBIGIY-VsDvg%3A1685710535001&ei=xuZ5ZJDhPIOSrgTdkpuICw&oq=versatile+pr&gs_lcp=Cgxnd3Mtd2l6LXNlcnAQAxgAMgcIABCKBRBDMgcIABCKBRBDMgoIABCABBAUEIcCMgoIABCABBAUEIcCMgUIABCABDIHCAAQigUQQzIFCAAQgAQyBQgAEIAEMgUIABCABDIFCAAQgAQ6CggAEEcQ1gQQsAM6EAguEIoFEMcBENEDELADEEM6CggAEIoFELADEEM6DAgjEIoFECcQRhD5AToNCC4QigUQxwEQ0QMQQzoGCAAQBxAeOggIABCABBDLATogCAAQigUQRhD5ARCXBRCMBRDdBBBGEPQDEPUDEPYDGAFKBAhBGABQ-hlYryVgri1oAXABeACAAZcBiAH6BJIBAzAuNZgBAKABAcABAcgBCtoBBggBEAEYEw&sclient=gws-wiz-serp"))
                            },
                    )
                }
            }

            Text(
                "definition",
                color = Color(255, 60, 255),
                textDecoration = TextDecoration.Underline,
                fontSize = 20.sp,
                modifier = Modifier
                    .clickable {
                        openWebpage(URL("https://www.google.com/search?client=opera&q=$word+meaning&sourceid=opera&ie=UTF-8&oe=UTF-8"))
                    }
            )
        }
    }
}

@Composable
private fun AddWordCard(
    onCancel: () -> Unit = {},
    onAdd: (String) -> Unit = {},
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier
            .padding(padding)
            .background(colorBackgroundSecond, RoundedCornerShape(corners))
            .padding(padding),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        var text by remember { mutableStateOf("") }
        OutlinedTextField(
            value = text,
            textStyle = TextStyle(
                color = colorText,
                fontSize = 28.sp,
            ),
            onValueChange = {
                text = it
            },
        )
        Row {
            Icon(
                Icons.Default.Check,
                contentDescription = null,
                tint = colorText,
                modifier = Modifier
                    .size(40.dp)
                    .aspectRatio(1F)
                    .clickable(onClick = { onAdd.invoke(text) }),
            )
            Icon(
                Icons.Default.Clear,
                contentDescription = null,
                tint = colorText,
                modifier = Modifier
                    .size(40.dp)
                    .aspectRatio(1F)
                    .clickable(onClick = onCancel),
            )
        }
    }
}

fun openWebpage(uri: URI?): Boolean {
    val desktop = if (Desktop.isDesktopSupported()) Desktop.getDesktop() else null
    if (desktop != null && desktop.isSupported(Desktop.Action.BROWSE)) {
        try {
            desktop.browse(uri)
            return true
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
    return false
}

fun openWebpage(url: URL): Boolean {
    try {
        return openWebpage(url.toURI())
    } catch (e: URISyntaxException) {
        e.printStackTrace()
    }
    return false
}