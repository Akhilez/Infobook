package `in`.akhilkanna.myinfo.libs

import `in`.akhilkanna.myinfo.db.InfoRepository
import `in`.akhilkanna.myinfo.db.TitleEnt
import android.app.Application
import android.arch.lifecycle.AndroidViewModel
import android.arch.lifecycle.LiveData

class TitleViewModel(application: Application) : AndroidViewModel(application) {

    val repository: InfoRepository = InfoRepository(application)
    val allTitles: LiveData<List<TitleEnt>>? = repository.getAllTitles()

    fun insert(title: TitleEnt) {
        repository.insert(title)
    }

    fun getAllTItles(): LiveData<List<TitleEnt>>? {
        return allTitles
    }
}