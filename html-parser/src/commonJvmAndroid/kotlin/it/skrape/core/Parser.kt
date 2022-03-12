/*
 *    Copyright 2022, Petr Laštovička as Lasta apps, All rights reserved
 *
 *     This file is part of Menza.
 *
 *     Menza is free software: you can redistribute it and/or modify
 *     it under the terms of the GNU General Public License as published by
 *     the Free Software Foundation, either version 3 of the License, or
 *     (at your option) any later version.
 *
 *     Menza is distributed in the hope that it will be useful,
 *     but WITHOUT ANY WARRANTY; without even the implied warranty of
 *     MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *     GNU General Public License for more details.
 *
 *     You should have received a copy of the GNU General Public License
 *     along with Menza.  If not, see <https://www.gnu.org/licenses/>.
 */

package it.skrape.core

import it.skrape.SkrapeItDsl
import it.skrape.selects.Doc
import org.intellij.lang.annotations.Language
import org.jsoup.nodes.Document
import java.io.BufferedReader
import java.io.File
import java.io.InputStream
import java.nio.charset.Charset
import org.jsoup.parser.Parser.parse as jSoupParser

internal class Parser(
    var html: String, val baseUri: String,
) {

    fun parse(): Doc {
        return jSoupParser(html, baseUri).toDocWrapper()
    }

    private fun Document.toDocWrapper() = Doc(this)

    class MissingDependencyException(message: String = "") :
        Exception(message)
}

/**
 * Read and parse HTML from a String.
 * @param html represents a html snippet
 * @param charset defaults to UTF-8
 * @param jsExecution defaults to false
 * @param baseUri defaults to empty String
 */
public fun <T> htmlDocument(
    @Language("HTML") html: String,
    charset: Charset = Charsets.UTF_8,
    jsExecution: Boolean = false,
    baseUri: String = "",
    init: Doc.() -> T
): T = htmlDocument(html, charset, jsExecution, baseUri).init()

/**
 * Read and parse a html file from local file-system.
 * @param file
 * @param charset defaults to UTF-8
 * @param jsExecution defaults to false
 * @param baseUri defaults to empty String
 */
public fun <T> htmlDocument(
    file: File,
    charset: Charset = Charsets.UTF_8,
    jsExecution: Boolean = false,
    baseUri: String = "",
    init: Doc.() -> T
): T = htmlDocument(file, charset, jsExecution, baseUri).init()

/**
 * Read and parse a html file from InputStream.
 * @param bytes
 * @param charset defaults to UTF-8
 * @param jsExecution defaults to false
 * @param baseUri defaults to empty String
 */
public fun <T> htmlDocument(
    bytes: InputStream,
    charset: Charset = Charsets.UTF_8,
    jsExecution: Boolean = false,
    baseUri: String = "",
    init: Doc.() -> T
): T = htmlDocument(bytes, charset, jsExecution, baseUri).init()

@SkrapeItDsl
public fun htmlDocument(
    @Language("HTML") html: String,
    charset: Charset = Charsets.UTF_8,
    jsExecution: Boolean = false,
    baseUri: String = ""
): Doc = Parser(html, baseUri).parse()

public fun htmlDocument(
    file: File,
    charset: Charset = Charsets.UTF_8,
    jsExecution: Boolean = false,
    baseUri: String = ""
): Doc = htmlDocument(file.readText(charset), charset, jsExecution, baseUri)

public fun htmlDocument(
    bytes: InputStream,
    charset: Charset = Charsets.UTF_8,
    jsExecution: Boolean = false,
    baseUri: String = ""
): Doc = htmlDocument(
    bytes.bufferedReader().use(BufferedReader::readText),
    charset,
    jsExecution,
    baseUri
)
