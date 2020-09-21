package es.i12capea.rickandmortyapiclient.domain.usecases

import es.i12capea.rickandmortyapiclient.domain.entities.CharacterEntity
import es.i12capea.rickandmortyapiclient.domain.repositories.CharacterRepository
import es.i12capea.rickandmortyapiclient.presentation.entities.Character
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn

class GetCharacterUseCase(
    private val characterRepository: CharacterRepository
) {
    suspend operator fun invoke(characterId: Int) : Flow<CharacterEntity> {
        return flow {
            characterRepository.getCharacter(characterId)
                .flowOn(Dispatchers.IO)
                .collect {
                    emit(it)
                }
        }
    }
}