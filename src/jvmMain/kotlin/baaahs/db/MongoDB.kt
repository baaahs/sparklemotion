package baaahs.db

import org.litote.kmongo.*

data class User(val _id: Id<User> = newId(), val name: String)

data class Eye(val eyeSide: String, val pan: Int, val tilt: Int, val color: String)
data class EyePreset(
    val _id: Id<EyePreset> = newId(),
    val name: String,
    val partyEyePan: Int,
    val partyEyeTilt: Int,
    val businessEyePan: Int,
    val businessEyeTilt: Int
)

class MongoDB {
    val client = KMongo.createClient("127.0.0.1", 27017)
    val database = client.getDatabase("test")


//    fun makeAdminUser() {
//        val col = database.getCollection<User>()
//        col.insertOne(User("Admin"))
//    }
//
//    fun getAdminUser() {
//        val col = database.getCollection<User>()
//        val admin: User? = col.findOne(User::name eq "Admin")
//
//        println("GOT THIS USER::::::::::: $admin")
//    }

    fun saveNewEyePreset(val preset: EyePreset) {
        val col = database.getCollection<EyePreset>()
        col.insertOne(preset)
    }

    fun getEyePreset(val eyePresetName: String): EyePreset? {
        val col = database.getCollection<EyePreset>()
        val preset: EyePreset? = col.findOne(EyePreset::name eq eyePresetName)

        return preset
    }

    fun updateEyePreset(val preset: EyePreset) {
        val col = database.getCollection<EyePreset>()
        col.updateOneById(preset._id, preset)
    }
}
