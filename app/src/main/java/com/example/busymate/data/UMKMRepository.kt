package com.example.busymate.data

import android.annotation.SuppressLint
import android.content.Context
import android.util.Log
import com.example.busymate.model.Board
import com.example.busymate.model.Category
import com.example.busymate.model.UMKM
import com.google.firebase.auth.AuthResult
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.UserProfileChangeRequest
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.channels.trySendBlocking
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow

class UMKMRepository(private val firebaseAuth: FirebaseAuth) {
    private val database = FirebaseDatabase.getInstance().reference

    fun login(email: String, password: String): Flow<Result<FirebaseUser>> = callbackFlow {
         firebaseAuth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    firebaseAuth.currentUser?.let {
                        trySendBlocking(Result.success(it))
                    } ?: trySendBlocking(Result.failure(Exception("User not found")))
                } else {
                    trySendBlocking(Result.failure(task.exception ?: Exception("Login failed")))
                }
                close()
            }
        awaitClose {}
    }

    fun register(email: String, password: String, name: String): Flow<Result<AuthResult>> = callbackFlow {
        val authListener = firebaseAuth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    val user = task.result?.user
                    val uid = user?.uid.orEmpty()

                    val userData = mapOf(
                        "email" to email, "name" to name
                    )
                    database.child("users").child(uid).setValue(userData)

                    val profileUpdates =
                        UserProfileChangeRequest.Builder().setDisplayName(name).build()

                    user?.updateProfile(profileUpdates)?.addOnCompleteListener {
                        if (it.isSuccessful) {
                            FirebaseAuth.getInstance().currentUser?.reload()
                                ?.addOnCompleteListener {
                                    trySendBlocking(Result.success(task.result))
                                    close()
                                }
                        } else {
                            trySendBlocking(Result.failure(task.exception ?: Exception("Gagal update profile")))
                            close()
                        }
                    }?.addOnFailureListener {
                        trySendBlocking(Result.failure(it))
                        close()
                    }
                } else {
                    trySendBlocking(Result.failure(task.exception ?: Exception("Register Gagal")))
                    close()
                }
            }
        awaitClose { authListener.isCanceled }
    }

    @SuppressLint("UseKtx")
    fun logout(context: Context) {
        firebaseAuth.signOut()
        val sharedPreferences = context.getSharedPreferences("app_prefs", Context.MODE_PRIVATE)
        sharedPreferences.edit().remove("is_logged_in").apply()
    }

    fun getCategories(): Flow<Result<List<Category>>> = callbackFlow {
        database.child("umkm").addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val list = mutableListOf<Category>()
                var idCounter = 0

                for (umkmSnap in snapshot.children) {
                    val rawCategory = umkmSnap.child("category").getValue(String::class.java)
                    rawCategory?.split(",")?.map { it.trim() }?.forEach { categoryText ->
                        list.add(Category(categoryId = idCounter++, textCategory = categoryText))
                    }
                }

                trySendBlocking(Result.success(list))
                close()
            }

            override fun onCancelled(error: DatabaseError) {
                trySendBlocking(Result.failure(error.toException()))
                close()
            }
        })
        awaitClose {}
    }

    fun getUMKM(): Flow<Result<List<UMKM>>> = callbackFlow {
        database.child("umkm").addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val list = mutableListOf<UMKM>()
                for (umkmSnap in snapshot.children) {
                    val umkm = umkmSnap.getValue(UMKM::class.java)
                    umkm?.let {
                        list.add(it)
                    }
                }
                trySendBlocking(Result.success(list))
                close()
            }

            override fun onCancelled(error: DatabaseError) {
                trySendBlocking(Result.failure(error.toException()))
                close()
            }
        })
        awaitClose {}
    }

    fun getUMKMById(umkmId: String): Flow<Result<UMKM>> = callbackFlow {
        database.child("umkm")
            .orderByChild("id")
            .equalTo(umkmId)
            .addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    if (snapshot.exists()) {
                        val umkm = snapshot.children.firstOrNull()?.getValue(UMKM::class.java)
                        if (umkm != null) {
                            trySendBlocking(Result.success(umkm))
                        } else {
                            trySendBlocking(Result.failure(Exception("UMKM not found")))
                        }
                    } else {
                        trySendBlocking(Result.failure(Exception("UMKM not found")))
                    }
                    close()
                }

                override fun onCancelled(error: DatabaseError) {
                    trySendBlocking(Result.failure(error.toException()))
                    close()
                }
            })
        awaitClose {}
    }

    fun getUMKMByUserId(userId: String): Flow<Result<UMKM>> = callbackFlow {
        val umkmRef = database.child("umkm").child(userId)
        umkmRef.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val umkm = snapshot.getValue(UMKM::class.java)
                if (umkm != null) {
                    trySendBlocking(Result.success(umkm))
                } else {
                    trySendBlocking(Result.failure(Exception("UMKM tidak ditemukan")))
                }
                close()
            }

            override fun onCancelled(error: DatabaseError) {
                trySendBlocking(Result.failure(error.toException()))
                close()
            }
        })
        awaitClose {}
    }

    fun createUMKM(umkm: UMKM): Flow<Result<Unit>> = callbackFlow {
        database.child("umkm")
            .child(umkm.id)
            .setValue(umkm)
            .addOnSuccessListener {
                trySendBlocking(Result.success(Unit))
                close()
            }
            .addOnFailureListener { exc ->
                trySendBlocking(Result.failure(exc))
                close()
            }
        awaitClose { }
    }

    fun updateUMKM(umkm: UMKM): Flow<Result<Unit>> = callbackFlow {
        database.child("umkm")
            .child(umkm.id)
            .setValue(umkm)
            .addOnSuccessListener {
                trySendBlocking(Result.success(Unit))
                close()
            }
            .addOnFailureListener {
                trySendBlocking(Result.failure(it))
                close()
            }

        awaitClose {}
    }
    // Board
    fun getBoard(): Flow<Result<List<Board>>> = callbackFlow {
        database.child("board").addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val boardList = mutableListOf<Board>()
                val tempList = mutableListOf<Board>()
                val userIds = mutableSetOf<String>()

                for (boardSnap in snapshot.children) {
                    val board = boardSnap.getValue(Board::class.java)
                    board?.let {
                        tempList.add(it)
                        userIds.add(it.umkm.id)
                    }
                }

                if (userIds.isEmpty()) {
                    trySendBlocking(Result.success(emptyList()))
                    close()
                    return
                }

                database.child("users").addListenerForSingleValueEvent(object : ValueEventListener {
                    override fun onDataChange(userSnapshot: DataSnapshot) {
                        for (board in tempList) {
                            val userId = board.umkm.id
                            val userNode = userSnapshot.child(userId)

                            val name = userNode.child("name").getValue(String::class.java).orEmpty()
                            val photoUrl =
                                userNode.child("photoUrl").getValue(String::class.java).orEmpty()

                            board.umkm = board.umkm.copy(
                                nameUMKM = name,
                                imageUMKM = photoUrl
                            )
                            boardList.add(board)
                            Log.d(
                                "getBoard",
                                "Fetching for userId: $userId, name: $name, photoUrl: $photoUrl"
                            )
                        }
                        trySendBlocking(Result.success(boardList))
                        close()
                    }

                    override fun onCancelled(error: DatabaseError) {
                        trySendBlocking(Result.failure(error.toException()))
                        close()
                    }
                })
            }

            override fun onCancelled(error: DatabaseError) {
                trySendBlocking(Result.failure(error.toException()))
                close()
            }
        })

        awaitClose { }
    }
    fun createBoard(board: Board): Flow<Result<Unit>> = callbackFlow {
        val key = database.child("board").push().key!!
        board.id = key
        database.child("board")
            .child(key)
            .setValue(board)
            .addOnSuccessListener {
                trySendBlocking(Result.success(Unit))
                close()
            }
            .addOnFailureListener { exc ->
                trySendBlocking(Result.failure(exc))
                close()
            }
        awaitClose { }
    }
}