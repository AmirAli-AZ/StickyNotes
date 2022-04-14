package com.amirali.stickynotes.utils

import com.amirali.stickynotes.model.Note
import java.io.File
import java.nio.file.Files
import java.nio.file.Paths
import java.sql.Connection
import java.sql.DriverManager

object DBManager {

    private val connection:Connection

    init {
        connection = DriverManager.getConnection("jdbc:sqlite:${getAppDataPath().plus(File.separator).plus("NotesDB.db")}")
    }

    fun initDB():DBManager {
        val statement = connection.createStatement()
        statement.executeUpdate(
            "create table if not exists notes(id integer primary key autoincrement , date long not null, color text not null , content text not null);"
        )
        statement.close()

        return this
    }

    fun getConnection():Connection {
        return connection
    }

    fun insertNewData(id:Int? , color:String , content:String) {
        val statement = connection.createStatement()
        statement.executeUpdate(
            "insert or ignore into notes values($id , ${System.currentTimeMillis()} , \"$color\" , \"$content\");"
        )
        statement.close()
    }

    fun insertNewData(color:String , content:String) {
        val statement = connection.createStatement()
        statement.executeUpdate(
            "insert or ignore into notes values(null , ${System.currentTimeMillis()} , \"$color\" , \"$content\");"
        )
        statement.close()
    }

    fun selectAll():ArrayList<Note> {
        val statement = connection.createStatement()
        val executeQuery = statement.executeQuery("select * from notes order by id desc;")
        val resultList:ArrayList<Note> = arrayListOf()
        while (executeQuery.next()) {
            val note = Note(
                executeQuery.getInt("id"),
                executeQuery.getLong("date"),
                executeQuery.getString("color"),
                executeQuery.getString("content")
            )

            resultList.add(note)
        }

        return resultList
    }

    fun updateByID(id:Int , color:String , content:String) {
        val statement = connection.createStatement()
        statement.executeUpdate(
            "update notes set date = ${System.currentTimeMillis()} , color = \"$color\" , content =\"$content\" where id = $id;"
        )
        statement.close()
    }

    fun deleteByID(id:Int) {
        val statement = connection.createStatement()
        statement.executeUpdate(
            "delete from notes where id = $id;"
        )
        statement.close()
    }

    fun selectByID(id:Int):Note? {
        val statement = connection.createStatement()
        val executeQuery = statement.executeQuery("select * from notes where id = $id;")
        while (executeQuery.next()) {
            return Note(
                executeQuery.getInt("id"),
                executeQuery.getLong("date"),
                executeQuery.getString("color"),
                executeQuery.getString("content")
            )
        }

        return null
    }

    private fun getAppDataPath():String {
        val workingDir = when(OSUtils.get()) {
            OSUtils.OS.WINDOWS ->
                System.getenv("APPDATA").plus(File.separator).plus("com.amirali.stickynotes")
            OSUtils.OS.LINUX ->
                System.getProperty("user.home").plus(File.separator).plus("com.amirali.stickynotes")
            else ->
                "com.amirali.stickynotes"
        }

        val file = File(workingDir)
        if (!file.exists())
            Files.createDirectories(Paths.get(file.path))

        return workingDir
    }
}