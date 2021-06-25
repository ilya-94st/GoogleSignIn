package com.example.staselovich_p2.Arrays

import android.os.Environment
import android.util.Log
import android.widget.Toast
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.ViewModel
import com.example.staselovich_p2.DataBase.User
import com.example.staselovich_p2.UserMessageModell
import com.example.staselovich_p2.UserMessagesModelClass
import com.google.api.client.extensions.android.json.AndroidJsonFactory
import com.google.api.client.googleapis.extensions.android.gms.auth.GoogleAccountCredential
import com.google.api.client.googleapis.extensions.android.gms.auth.UserRecoverableAuthIOException
import com.google.api.client.http.javanet.NetHttpTransport
import com.google.api.client.util.ExponentialBackOff
import com.google.api.services.gmail.Gmail
import com.google.api.services.gmail.GmailScopes
import com.google.api.services.gmail.model.ListMessagesResponse
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.apache.commons.codec.binary.Base64
import java.io.File
import java.io.FileOutputStream
import kotlin.collections.mutableListOf as mutableListOf1

class ArrayViewModel : ViewModel() {
    // передаем сюда креды к примеру (SHA1)
    fun getServise(credential: GoogleAccountCredential): Gmail {
        return Gmail.Builder(
            NetHttpTransport(), AndroidJsonFactory.getDefaultInstance(), credential
        )
            .setApplicationName("Staselovich_P2")
            .build()
    }

    // передаём сюда контекст фрагмента
    fun getCredential(fragment: FragmentActivity): GoogleAccountCredential {
        return GoogleAccountCredential.usingOAuth2(
            fragment.applicationContext, listOf(GmailScopes.GMAIL_READONLY)
        )
            .setBackOff(ExponentialBackOff())
            .setSelectedAccountName(FirebaseAuth.getInstance().currentUser?.email)
    }

    suspend fun readEmail(
        service: Gmail,
        arraymessages: com.example.staselovich_p2.DataBase.ViewModel
    ) {
        try {
            val executeResult = getResultContent(service) // список gmail

            val message = executeResult?.messages // колекция сообщений
            var count = 0
            if (message != null) {
                while (count < message.size) {
                    val messageRead =
                        withContext(Dispatchers.IO) { // куронтина чтобы вытянуть сообщения
                            service.users().messages()
                                ?.get(
                                    FirebaseAuth.getInstance().currentUser?.email,
                                    message?.get(count).id
                                )
                                ?.setFormat("full")?.execute()
                        }
                    val id = messageRead?.id!!
                    val headers = messageRead!!.payload.headers
                    val body = messageRead.payload.parts
                    var filename = ""
                    var date = ""
                    var subject = ""
                    var from = ""
                    var attachmentId = ""

                    body.forEach {
                        if (!it.body.attachmentId.isNullOrEmpty()) {
                            attachmentId = it.body.attachmentId
                            filename = it.filename
                        }
                    }
                    headers.forEach {
                        when (it.name) {
                            "Date" -> date = it.value
                            "Subject" -> subject = it.value
                            "From" -> from = it.value
                        }
                    }
                    writeData(from, date, subject, attachmentId, id, filename)
                    if (count < message.size) {
                        arraymessages.addUser(User(0, date, subject, from, attachmentId, filename))
                    }
                    count++
                }
            }
        } catch (e: UserRecoverableAuthIOException) {
        }
    }

     suspend fun getData(service: Gmail, id: String, attachId: String, filename: String): String? { // вложения
        val dataResult = withContext(Dispatchers.IO) {
            service.users().messages().attachments()
                ?.get(
                    FirebaseAuth.getInstance().currentUser?.email,
                    id, attachId
                )?.execute()
        }

        CoroutineScope(Dispatchers.IO).launch {
            Log.w("asd", "saveAttachmentsAsync start")
            val data = Base64.decodeBase64(dataResult?.data)
            val file = File(
                "${
                    Environment.getExternalStoragePublicDirectory(
                        Environment.DIRECTORY_DOWNLOADS
                    )
                }/${filename}"
            )
            file.createNewFile()
            val fOut = FileOutputStream(file)
            fOut.write(data)
            fOut.close()
            Log.w("asd", "saveAttachmentsAsync end")
        }
        return dataResult?.data
    }

    private suspend fun getResultContent(service: Gmail): ListMessagesResponse? {
        return withContext(Dispatchers.IO) {
            service.users().messages()?.list("me")?.setQ("to:me")?.execute()
        }
    }

    private fun writeData(
        from: String,
        date: String,
        subject: String,
        attachmentId: String,
        messageId: String,
        filename: String
    ) {
        UserMessagesModelClass.dataObject.add(
            UserMessageModell(
                date,
                from,
                subject,
                attachmentId,
                messageId,
                filename
            )
        )
    }

}