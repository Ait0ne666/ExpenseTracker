package com.ait0ne.expensetracker.syncadapter

import android.app.Service
import android.content.Intent
import android.os.IBinder

class AuthenticatorService:Service() {
    private lateinit var mAuthenticator: Authenticator


    override fun onCreate() {
        mAuthenticator = Authenticator(this)
    }

    override fun onBind(intent: Intent?): IBinder = mAuthenticator.iBinder
}