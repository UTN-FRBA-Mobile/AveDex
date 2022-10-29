package com.sophiadiagrams.avedex.lib.services

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class FirebaseService(var auth: FirebaseAuth, var db: FirebaseFirestore)