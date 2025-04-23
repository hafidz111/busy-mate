package com.example.busymate.data

import android.annotation.SuppressLint
import android.content.Context
import com.example.busymate.model.Category
import com.example.busymate.model.UMKM
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
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

    @SuppressLint("UseKtx")
    fun logout(context: Context) {
        firebaseAuth.signOut()
        val sharedPreferences = context.getSharedPreferences("app_prefs", Context.MODE_PRIVATE)
        sharedPreferences.edit().remove("is_logged_in").apply()
    }

    fun getCategories(): Flow<Result<List<Category>>> = callbackFlow {
        database.child("categories").addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val list = mutableListOf<Category>()
                for (catSnap in snapshot.children) {
                    val category = catSnap.getValue(Category::class.java)
                    category?.let { list.add(it) }
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
                    umkm?.let { list.add(it) }
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
}