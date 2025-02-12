package com.github.kotlintelegrambot.echo

import com.github.kotlintelegrambot.Bot
import com.github.kotlintelegrambot.bot
import com.github.kotlintelegrambot.dispatch
import com.github.kotlintelegrambot.dispatcher.command
import com.github.kotlintelegrambot.dispatcher.text
import com.github.kotlintelegrambot.entities.ChatId
import com.github.kotlintelegrambot.entities.Message
import java.io.File


fun main() {

    val bot = bot {
        token = "7600244845:AAGwZSHipisSITdBu3TCsMeOMKnyceAdTA0"


        dispatch {
            text {
                bot.sendMessage(
                    chatId = ChatId.fromId(message.chat.id),
                    messageThreadId = message.messageThreadId,
                    text = text,
                    protectContent = true,
                    disableNotification = false,
                )
            }

        }

        dispatch {
            command("start") {
                val result = bot.sendMessage(chatId = ChatId.fromId(message.chat.id), text = "Put all your words here!")
                result.fold({
                    processMessage(it, bot)
                },{
                    // do something with the error
                })
            }
        }

    }

    bot.startPolling()
}

fun processMessage(message: Message, bot: Bot) {
    val NEW_FILE = "new.txt"
    val text = message.text ?: return
    File(NEW_FILE).appendText("$text\n")
    runSortingScript()
    pickRandomWords()
    sendTodaySelection(message.chat.id, bot)
}

fun runSortingScript() {
    ProcessBuilder("kotlin", "sorter.kt").start().waitFor()
}

fun pickRandomWords() {
    val TODAY_FILE = "today.txt"
    val allLines = File("words.txt").readLines() + File("phrases.txt").readLines()
    val randomSelection = allLines.shuffled().take(5)
    File(TODAY_FILE).writeText(randomSelection.joinToString("\n"))
}

fun sendTodaySelection(chatId: Long, bot: Bot) {
    val TODAY_FILE = "today.txt"
    val text = File(TODAY_FILE).readText()
    bot.sendMessage(ChatId.fromId(chatId), text)
}
