package com.example.staselovich_p2.DataBase


import androidx.lifecycle.LiveData
//  объявление функций для запуска в корутине
class UserRepository(private val userDao: UserDao) {

    val readAllData: LiveData<List<User>> = userDao.readAllData()

    suspend fun addUser(user: User){
        userDao.addUser(user)
    }

    suspend fun deleteAllUser(){
        userDao.deleteAllUsers()
    }

    suspend fun getById(id: Long) {
        userDao.getById(id)
    }

    suspend fun deleteUser(user: User) {
        userDao.deleteUser(user)
    }

}