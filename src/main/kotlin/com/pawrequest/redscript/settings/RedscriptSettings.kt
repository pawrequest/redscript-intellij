package com.pawrequest.redscript.settings


import com.intellij.openapi.application.ApplicationManager
import com.intellij.openapi.components.PersistentStateComponent
import com.intellij.openapi.components.State
import com.intellij.openapi.components.Storage

@State(name = "RedscriptSettings", storages = [Storage("redscript.xml")])
class RedscriptSettings : PersistentStateComponent<RedscriptSettings.State?> {
    private var myState = State()

    class State {
        var gameDir: String = ""
    }

    override fun getState(): State {
        return myState
    }

    override fun loadState(state: State) {
        myState = state
    }

    var gameDir: String
        get() = myState.gameDir
        set(gameDir) {
            myState.gameDir = gameDir
        }


    companion object {
        fun getInstance(): RedscriptSettings {
            return ApplicationManager.getApplication().getService(RedscriptSettings::class.java)
        }
    }
}




//import com.intellij.openapi.components.PersistentStateComponent
//import com.intellij.openapi.components.State
//import com.intellij.openapi.components.Storage
//
//@State(name = "RedscriptSettings", storages = [Storage("redscript.xml")])
//class RedscriptSettings : PersistentStateComponent<RedscriptSettings.State?> {
//    private var myState = State()
//
//    class State {
//        var gameDir: String = ""
//    }
//
//    override fun getState(): State {
//        return myState
//    }
//
//    override fun loadState(state: State) {
//        myState = state
//    }
//    //
////    var _gameDir: String
////        get() = myState.gameDir
////        set(gameDir) {
////            myState.gameDir = gameDir
////        }
//
//    companion object {
//        var gameDir: String = RedscriptSettings.state.gameDir
//    }
//}
