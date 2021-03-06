package es.i12capea.rickandmortyapiclient.data.repository

import android.util.Log
import es.i12capea.rickandmortyapiclient.data.api.LocationApi
import es.i12capea.rickandmortyapiclient.data.api.call
import es.i12capea.rickandmortyapiclient.data.local.dao.LocalLocationDao
import es.i12capea.rickandmortyapiclient.data.local.dao.LocalLocationPageDao
import es.i12capea.rickandmortyapiclient.data.mappers.*
import es.i12capea.rickandmortyapiclient.domain.entities.LocationEntity
import es.i12capea.rickandmortyapiclient.domain.entities.PageEntity
import es.i12capea.rickandmortyapiclient.domain.repositories.LocationRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.launch
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class LocationRepositoryImpl @Inject constructor(
    val api : LocationApi,
    val locationDao: LocalLocationDao,
    val locationPageDao: LocalLocationPageDao
) : LocationRepository {

    override suspend  fun getLocationsInPage(page: Int): Flow<PageEntity<LocationEntity>> {
        return flow{
            locationPageDao.searchPageById(page)?.let{
                emit(it.toDomain())
            }
            try {
                val result = api.getLocations(page)
                    .call()

                val locationDomain = result.locationPageToDomain()

                emit(locationDomain)

                Thread{
                    CoroutineScope(Dispatchers.IO).launch {
                        try {
                            locationDao.insertLocationList(locationDomain.list.listLocationEntityToLocal(locationDomain.actualPage))
                            locationPageDao.insertPage(locationDomain.toLocal())
                        }catch (e: Exception){
                            Log.d("BD", "Error insertando lista de localizaciones")
                        }
                    }
                }.start()
            }catch(t: Throwable) {
                throw t
            }

        }
    }

    override suspend  fun getLocation(id: Int): Flow<LocationEntity> {
        return flow{

            val result = api.getLocation(id)

            //emit(result.toDomain())
        }
    }
}