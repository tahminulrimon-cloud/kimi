package com.regt17fd.manpower.data.repository

import com.regt17fd.manpower.data.local.dao.AuditDao
import com.regt17fd.manpower.data.local.dao.PersonnelDao
import com.regt17fd.manpower.data.local.entity.AuditLog
import com.regt17fd.manpower.data.local.entity.Personnel
import com.regt17fd.manpower.data.local.entity.User
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class PersonnelRepository @Inject constructor(
    private val personnelDao: PersonnelDao,
    private val auditDao: AuditDao,
) {
    fun observeAll(): Flow<List<Personnel>> = personnelDao.observeAll()

    fun search(query: String): Flow<List<Personnel>> = personnelDao.search(query)

    suspend fun getByServiceNumber(serviceNumber: String): Personnel? =
        personnelDao.getByServiceNumber(serviceNumber)

    suspend fun create(personnel: Personnel, actor: User) {
        personnelDao.insert(personnel)
        audit(actor, "PERSONNEL_CREATE", personnel.serviceNumber)
    }

    suspend fun update(personnel: Personnel, actor: User) {
        personnelDao.update(personnel)
        audit(actor, "PERSONNEL_UPDATE", personnel.serviceNumber)
    }

    suspend fun deactivate(personnel: Personnel, actor: User) {
        personnelDao.update(personnel.copy(isActive = false))
        audit(actor, "PERSONNEL_DEACTIVATE", personnel.serviceNumber)
    }

    private suspend fun audit(actor: User, action: String, detail: String) {
        auditDao.insert(AuditLog(userId = actor.id, userName = actor.fullName, action = action, detail = detail))
    }
}
