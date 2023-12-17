package com.tech.chatappcompose.viewmodel

import android.content.Context
import android.net.Uri
import android.util.Log
import android.widget.Toast
import androidx.compose.runtime.mutableStateOf
import androidx.core.text.isDigitsOnly
import androidx.lifecycle.ViewModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.Filter
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.toObject
import com.google.firebase.firestore.toObjects
import com.google.firebase.storage.FirebaseStorage
import com.tech.chatappcompose.data.CHAT
import com.tech.chatappcompose.data.ChatData
import com.tech.chatappcompose.data.ChatUser
import com.tech.chatappcompose.data.Event
import com.tech.chatappcompose.data.USER_NODE
import com.tech.chatappcompose.data.UserData
import dagger.hilt.android.lifecycle.HiltViewModel
import java.lang.Exception
import java.util.UUID
import javax.inject.Inject

@HiltViewModel
class LCViewModel @Inject constructor(
    private val auth: FirebaseAuth,
    private val db: FirebaseFirestore,
    private val storage: FirebaseStorage
) : ViewModel() {

    var inProgress = mutableStateOf(false)
    var inProgressChat = mutableStateOf(false)
    private val eventMutableState = mutableStateOf<Event<String>?>(null)
    var signIn = mutableStateOf(false)
    var userData = mutableStateOf<UserData?>(null)
    val chats = mutableStateOf<List<ChatData>>(listOf())

    init {
        val currentUser = auth.currentUser
        signIn.value = currentUser != null
        currentUser?.uid?.let {
            getUserData(it)
        }
    }

    fun logout(context: Context) {
        signIn.value = false
        auth.signOut()
        userData.value = null
        eventMutableState.value = Event("Logged Out")
        Toast.makeText(context, "Logged Out", Toast.LENGTH_SHORT).show()
    }

    fun signUp(name: String, number: String, email: String, password: String, context: Context) {
        inProgress.value = true
        if (name.isEmpty() or number.isEmpty() or email.isEmpty() or password.isEmpty()) {
            handleException(customMessage = "Please Fill All fields.")
            return
        }
        inProgress.value = true
        db.collection(USER_NODE).whereEqualTo("number", number).get().addOnSuccessListener {
            if (it.isEmpty) {
                auth.createUserWithEmailAndPassword(email, password).addOnCompleteListener {
                    if (it.isSuccessful) {
                        signIn.value = true
                        createOrUpdateProfile(name = name, number = number, context = context)
                        Log.d("@@vm", "signUp: User Logged IN")
                    } else {
                        handleException(it.exception, customMessage = "Sign Up failed", context)
                    }
                }
            } else {
                handleException(customMessage = "Number Already Exists", context = context)
                inProgress.value = false
            }
        }
    }

    fun createOrUpdateProfile(   //write all information in database when user creating or updating.
        name: String? = null,
        number: String? = null,
        imageUrl: String? = "",  //when updating the profile
        context: Context
    ) {
        var uid = auth.currentUser?.uid
        val userData = UserData(
            userId = uid!!,
            name = (name ?: userData.value?.name)!!,
            number = number ?: userData.value?.number!!,
            imageUrl = (if(imageUrl!="") {
                imageUrl
            }else{
                if(userData.value?.imageUrl != null){
                    userData.value?.imageUrl
                }else{
                    ""
                }
            })!!
        ).toMap()
        uid.let {
            inProgress.value = true
            db.collection(USER_NODE).document(uid).get().addOnSuccessListener {
                if (it.exists()) {
                    //update user data
                    db.collection(USER_NODE).document(uid).set(userData)
                    getUserData(uid, context)

                    if(chats.value.isNotEmpty())
                    alsoUpdateChatDetail()

                    Toast.makeText(context, "Update Profile Data.", Toast.LENGTH_SHORT).show()
                } else {
                    //for create user
                    db.collection(USER_NODE).document(uid).set(userData)
                    getUserData(uid, context)
                }
                inProgress.value = false
            }.addOnFailureListener {
                inProgress.value = false
                handleException(it, "Cannot Retrieve User", context = context)
            }
        }
    }

    private fun alsoUpdateChatDetail() {
        db.collection(CHAT).get().addOnCompleteListener { task ->
            if (task.isSuccessful) {
                for (document in task.result) {
                    val storeUser =
                        if (document.getString("user1.number") == userData.value?.number) {
                            "user2"
                        } else {
                            "user1"
                        }
                    val documentId = document.id
                    val partnerNumber = document.getString("$storeUser.number")
                    val partnerImageUrl = document.getString("$storeUser.imageUrl")
                    val partnerName = document.getString("$storeUser.name")
                    val partnerUserId = document.getString("$storeUser.userId")
                    Log.d("@@alsoUpdate", "alsoUpdateChatDetail: $documentId")
                    Log.d("@@alsoUpdate", "alsoUpdateChatDetail: $partnerNumber")
                    Log.d("@@alsoUpdate", "alsoUpdateChatDetail: ${userData.value?.number}")
                    Log.d("@@alsoUpdate", "alsoUpdateChatDetail: ${userData.value?.imageUrl}")

                    var chat = ChatData(
                        chatId = documentId,
                        user2 = if (storeUser == "user1") {
                            ChatUser(
                                userData.value?.userId,
                                userData.value?.name,
                                userData.value?.imageUrl,
                                userData.value?.number
                            )
                        } else {
                            ChatUser(
                                partnerUserId,
                                partnerName,
                                partnerImageUrl,
                                partnerNumber
                            )
                        },
                        user1 = if (storeUser == "user2") {
                            ChatUser(
                                userData.value?.userId,
                                userData.value?.name,
                                userData.value?.imageUrl,
                                userData.value?.number
                            )
                        } else {
                            ChatUser(
                                partnerUserId,
                                partnerName,
                                partnerImageUrl,
                                partnerNumber
                            )
                        }
                    )
                    db.collection(CHAT).document(documentId).set(chat)
                }
            }
        }
    }

    private fun getUserData(uid: String, context: Context? = null) {
        inProgress.value = true
        db.collection(USER_NODE).document(uid).addSnapshotListener { value, error ->

            if (error != null) {
                handleException(error, "Can not Retrieve user", context)
            }
            if (value != null) {
                var user = value.toObject<UserData>()
                userData.value = user
                inProgress.value = false
                populateChats(context)
            }
        }

    }

    fun login(email: String, password: String, context: Context) {
        if (email.isEmpty() or password.isEmpty()) {
            handleException(customMessage = "Please Fill the all Fields", context = context)
            return
        } else {
            inProgress.value = true
            auth.signInWithEmailAndPassword(email, password).addOnCompleteListener {
                if (it.isSuccessful) {
                    signIn.value = true
                    inProgress.value = false
                    auth.currentUser?.uid?.let {
                        getUserData(it, context)
                    }
                    Toast.makeText(context, "Successful Login User.", Toast.LENGTH_SHORT).show()

                } else {
                    handleException(
                        customMessage = "Login Failed",
                        exception = it.exception,
                        context = context
                    )
                }
            }
        }
    }

    private fun handleException(
        exception: Exception? = null,
        customMessage: String = "",
        context: Context? = null
    ) {
        Log.e("@@vm", "handleException: ", exception)
        Log.d("@@vm", "handleException: $customMessage")
        if (context != null) {
            Toast.makeText(context, customMessage, Toast.LENGTH_SHORT).show()
        }
        exception?.printStackTrace()
        val errorMsg = exception?.localizedMessage ?: ""
        val message = if (customMessage.isNullOrEmpty()) errorMsg else customMessage

        eventMutableState.value = Event(message)
        inProgress.value = false
    }

    fun uploadProfileImage(uri: Uri, context: Context) {
        Log.d("@@profileImage", "URI: $uri")
        uploadImage(uri, context) {
            Log.d("@@profileImage", "URI: $uri")
            createOrUpdateProfile(imageUrl = it.toString(), context = context)
        }
    }

    private fun uploadImage(uri: Uri, context: Context, onSuccess: (Uri) -> Unit) {
        inProgress.value = true
        val storageRef = storage.reference
        val uuid = UUID.randomUUID()
        val imageRef = storageRef.child("images/$uuid")
        val uploadTask = imageRef.putFile(uri)
        uploadTask.addOnSuccessListener {
            val result = it.metadata?.reference?.downloadUrl
            result?.addOnSuccessListener(onSuccess)
            Log.d("@@profileImage", "URI: Image Upload on Storage")
            inProgress.value = false
        }.addOnFailureListener {
            handleException(it, context = context)
        }
    }

    fun onAddChat(number: String, context: Context) {
        if (number.isEmpty() or !number.isDigitsOnly()) {
            handleException(
                customMessage = "Number must be contain digits only.",
                context = context
            )
        } else {
            Log.d("@@AddChat", "onAddChat: ${userData.value?.number}")
            Log.d("@@AddChat", "onAddChat: ${userData.value?.imageUrl}")
            Log.d("@@AddChat", "onAddChat: ${number}")
            db.collection(CHAT).where(
                Filter.or(
                    Filter.and(
                        Filter.equalTo("user1.number", number),
                        Filter.equalTo("user2.number", userData.value?.number)  //login user
                    ),
                    Filter.and(
                        Filter.equalTo("user1.number", userData.value?.number),
                        Filter.equalTo("user2.number", number)
                    )
                )
            ).get().addOnSuccessListener {
                if (it.isEmpty) {
                    db.collection(USER_NODE).whereEqualTo("number", number).get()
                        .addOnSuccessListener {
                            if (it.isEmpty) {
                                handleException(
                                    customMessage = "Number not found",
                                    context = context
                                )
                            } else {
                                Log.d("@@AddChat", "onAddChat: ${userData.value?.userId}")
                                Log.d("@@AddChat", "onAddChat: ${userData.value?.name}")
                                Log.d("@@AddChat", "onAddChat: ${userData.value?.imageUrl}")
                                Log.d("@@AddChat", "onAddChat: ${userData.value?.number}")
                                val chatPartner = it.toObjects<UserData>()[0]
                                val id = db.collection(CHAT).document().id
                                var chat = ChatData(
                                    chatId = id,
                                    user1 = ChatUser(
                                        userData.value?.userId,
                                        userData.value?.name,
                                        userData.value?.imageUrl,
                                        userData.value?.number
                                    ),
                                    user2 = ChatUser(
                                        chatPartner.userId,
                                        chatPartner.name,
                                        chatPartner.imageUrl,
                                        chatPartner.number
                                    )
                                )
                                db.collection(CHAT).document(id).set(chat).addOnSuccessListener {
                                    Toast.makeText(context, "Number Added.", Toast.LENGTH_SHORT)
                                        .show()
                                }.addOnFailureListener {
                                    handleException(
                                        customMessage = "Something went wrong",
                                        context = context
                                    )
                                }
                            }
                        }.addOnFailureListener {
                            handleException(it, context = context)
                        }
                } else {
                    handleException(customMessage = "Chat already exists.")
                }
            }
        }
    }

    private fun populateChats(context: Context?) {
        inProgressChat.value = true
        db.collection(CHAT).where(
            Filter.or(
                Filter.equalTo("user1.userId", userData.value?.userId),
                Filter.equalTo("user2.userId", userData.value?.userId)
            )
        ).addSnapshotListener { value, error ->
            if (error != null) {
                handleException(exception = error, context = context)
            }
            if (value != null) {
                chats.value = value.documents.mapNotNull {
                    it.toObject<ChatData>()
                }
                inProgressChat.value = false
            }
        }
    }
}
