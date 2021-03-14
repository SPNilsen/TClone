package com.example.tclone.activities

import com.google.firebase.database.DatabaseReference

interface TinderCallback {

    fun onSignout()
    fun onGetUserId(): String
    fun onGetUserDatabase(): DatabaseReference
    fun profileComplete()
}