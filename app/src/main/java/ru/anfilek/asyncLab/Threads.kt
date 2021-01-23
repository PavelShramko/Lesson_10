package ru.anfilek.asyncLab

import android.os.Handler
import android.os.Message
import android.os.Looper
import android.util.Log
import kotlin.random.Random


const val FIRST_THREAD = "FIRST"
const val SECOND_THREAD = "SECOND"
const val THINK = "THINK"
const val FINISH = "FINISH"


class FirstThreadHandler(private val callback: Handler.Callback) :
        android.os.HandlerThread(FIRST_THREAD), Handler.Callback {

    var handler: Handler
        private set

    init {
        start()
        handler = Handler(looper)
    }

    override fun handleMessage(msg: Message): Boolean {
        handleMassages(this.name, msg, callback)
        return true
    }

    fun startThink() {

        val message = Message()

        message.data.apply {
            putString("tag", THINK)
            putString("name", FIRST_THREAD)
            putInt("number", 0)
        }
        callback.handleMessage(message)
    }
}

class SecondThreadHandler : android.os.HandlerThread(SECOND_THREAD), Handler.Callback {

    var handler: Handler
         private set

    lateinit var callback: Handler.Callback

    init {
        start()
        handler = Handler(Looper.getMainLooper())
    }


    override fun handleMessage(msg: Message): Boolean {
        handleMassages(this.name, msg, callback)
        return true
    }
}

fun handleMassages(tag: String, msg: Message, callback: Handler.Callback) {
    Log.d(tag, msg.data.getInt("value").toString())
    val status: String
    var name: String = FIRST_THREAD
    val value: Int

    if (msg.data != null && msg.data.getString("status") != FINISH) {
        value = msg.data.getInt("value") + Random.nextInt(1, 3)
        status = if (value < 100) {
            THINK
        } else FINISH


        //костыли
        /*if (name == FIRST_THREAD){
            name = SECOND_THREAD
        } else name = FIRST_THREAD*/

        msg.data.apply {
            putString("status", status)
            putString("name", name)
            putInt("value", value)
        }
        callback.handleMessage(msg)

    }
}

fun testSharedResources() {
    val thread2 = SecondThreadHandler()
    val thread1 = FirstThreadHandler(thread2)
    thread2.callback = thread1
    thread1.startThink()
}



