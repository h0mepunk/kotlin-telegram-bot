package com.example.vocab.src

import java.io.File
import
import com.github.kotlintelegrambot.entities.ChatId
import com.github.kotlintelegrambot.entities.Message
import com.github.kotlintelegrambot.entities.Update
import com.github.kotlintelegrambot.network.fold

val NEW_FILE = "new.txt"
val TODAY_FILE = "today.txt"

fun main() {
    val bot = Bot.Builder()
        .token("YOUR_BOT_TOKEN")
        .updateListener { updates ->
            updates.forEach { update ->
                update.message?.let { processMessage(it) }
            }
        }
        .build()
    bot.startPolling()
}

fun processMessage(message: Message) {
    val text = message.text ?: return
    File(NEW_FILE).appendText("$text\n")
    runSortingScript()
    pickRandomWords()
    sendTodaySelection(message.chat.id)
}

fun runSortingScript() {
    ProcessBuilder("kotlin", "sorter.kt").start().waitFor()
}

fun pickRandomWords() {
    val allLines = File("words.txt").readLines() + File("phrases.txt").readLines()
    val randomSelection = allLines.shuffled().take(5)
    File(TODAY_FILE).writeText(randomSelection.joinToString("\n"))
}

fun sendTodaySelection(chatId: Long) {
    val bot = Bot.Builder().token("YOUR_BOT_TOKEN").build()
    val text = File(TODAY_FILE).readText()
    bot.sendMessage(ChatId.fromId(chatId), text)
}
