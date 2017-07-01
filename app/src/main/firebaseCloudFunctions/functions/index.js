var functions = require('firebase-functions');
var admin = require('firebase-admin');
admin.initializeApp(functions.config().firebase);
exports.createUserAccount = functions.auth.user().onCreate(event => {
    const email = event.data.email;
    const uid = event.data.uid;
    const photoURL = event.data.photoURL || "https://cdn.pixabay.com/photo/2015/10/05/22/37/blank-profile-picture-973460_960_720.png";
    const displayName = event.data.displayName || "";
    return admin.database().ref("/users").child(uid).set({
        email,
        displayName,
        photoURL
    });
})