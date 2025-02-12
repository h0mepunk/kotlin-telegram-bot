package com.github.kotlintelegrambot.echo

import java.io.File

fun main(args: Array<String>) {
        val inputFile = File("new.txt")
        val wordsFile = File("words.txt")
        val phrasesFile = File("phrases.txt")

        // Ensure words.txt and phrases.txt exist
        if (!wordsFile.exists()) wordsFile.createNewFile()
        if (!phrasesFile.exists()) phrasesFile.createNewFile()

        // Read existing data to avoid duplicates
        val words = wordsFile.readLines().toMutableList()
        val phrases = phrasesFile.readLines().toMutableList()

        val wordsMap = mutableMapOf<String, MutableList<String>>()
        val phrasesMap = mutableMapOf<String, MutableList<String>>()

        // Load existing data to prevent duplicates
        fun loadFile(file: File, map: MutableMap<String, MutableList<String>>) {
            if (!file.exists()) return
            var currentCategory = ""
            file.readLines().forEach { line ->
                when {
                    line.startsWith("----#") -> currentCategory = line
                    line.isNotBlank() -> map.getOrPut(currentCategory) { mutableListOf() }.add(line)
                }
            }
        }

        loadFile(wordsFile, wordsMap)
        loadFile(phrasesFile, phrasesMap)

        inputFile.readLines().forEach { line ->
            val match = Regex("\\[(.*?)\\](?: #(\\w+))?").find(line)
            match?.let {
                val (word, category) = it.destructured
                val targetMap = if (word.contains(" ")) phrasesMap else wordsMap
                val categoryLine = if (category.isNotEmpty()) "----#$category" else ""

                if (categoryLine.isNotEmpty()) {
                    targetMap.putIfAbsent(categoryLine, mutableListOf())
                    if (word !in targetMap[categoryLine]!!) targetMap[categoryLine]!!.add(word)
                } else {
                    if (word !in targetMap.getOrPut("") { mutableListOf() })
                        targetMap[""]!!.add(word)
                }
            }
        }
        inputFile.writeText("")

        // Function to write sorted data to a file
        fun writeToFile(file: File, map: Map<String, MutableList<String>>) {
            file.bufferedWriter().use { writer ->
                map.entries.sortedBy { it.key }.forEach { (category, words) ->
                    if (category.isNotEmpty()) writer.write("$category\n")
                    words.sorted().forEach { writer.write("$it\n") }
                }
            }
        }

        writeToFile(wordsFile, wordsMap)
        writeToFile(phrasesFile, phrasesMap)

        println("Sorting complete. Check words.txt and phrases.txt")
    }
