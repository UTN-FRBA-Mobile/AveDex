package com.sophiadiagrams.avedex.lib.services

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage

class FirebaseService(var auth: FirebaseAuth, var db: FirebaseFirestore, var storage: FirebaseStorage)