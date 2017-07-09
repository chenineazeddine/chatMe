var functions = require('firebase-functions');
const gcs = require('@google-cloud/storage')({
    keyFilename: "chatme-30a53-firebase-adminsdk-spbrj-95b05f5e2e-1.json"
});
const spawn = require('child-process-promise').spawn;


var admin = require('firebase-admin');
admin.initializeApp(functions.config().firebase);

exports.createUserAccount = functions.auth.user().onCreate(event => {
    const email = event.data.email;
    const uid = event.data.uid;
    const photoURL = event.data.photoURL || "";
    var username = event.data.displayName;

    /**
     * if the username is null we user the fist part of the user email
     * as the the default username 
     */
    if (username == null) {
        username = email.split("@")[0];
    }
    return admin.database().ref("/users").child(uid).set({
        email,
        username,
        photoURL
    });
});

exports.generateProfileThumbnail = functions.storage.object().onChange(event => {

    const data = event.data;

    const filePath = data.name;
    const fileName = filePath.split("/").pop();
    const tmpFilePath = "/tmp/" + fileName;
    const thumbFilePath = "/users/profileImages/thumb_" + fileName;

    const metageneration = data.metageneration;
    const resourceState = data.resourceState;
    const fileBucket = data.bucket;
    const bucket = gcs.bucket(fileBucket);

    console.log("downloading the image to " + tmpFilePath);

    /**
     * if the image is already resize stop the process
     */
    if (fileName.startsWith('thumb_')) {
        console.log("the thumbnail is already created");
        return
    }

    /**
     * downloading the image to a temp folder at 
     * the google cloud storage bucket 
     */
    return bucket.file(filePath).download({
        destination: tmpFilePath
    }).then(() => {
        console.log("resizing the image");
        return spawn('convert', [tmpFilePath, '-thumbnail', '200x200>', tmpFilePath])
    }).then(() => {
        console.log("uploading the thumb to " + thumbFilePath);
        /**
         * uploading the thumbnail from the google cloud storage to 
         * the firebase storage 
         */
        return bucket.upload(tmpFilePath, {
            destination: thumbFilePath
        });
    }).then(() => {
        /**
         * getting the signed url from the google cloud storage bucker 
         */
        const thumbFile = bucket.file(thumbFilePath);
        const config = {
            action: "read",
            expires: "12-12-2222"
        }
        return thumbFile.getSignedUrl(config)
    }).then(photoURL => {
        /**
         * updating the user profile image in the firebase 
         * realtime database 
         */
        console.log("updating the user profile picture" + photoURL);
        const uid = fileName.split(".")[0];
        var updates = {};
        updates["/users/" + uid + "/photoURL"] = photoURL[0];
        return admin.database().ref().update(updates)
    })

})