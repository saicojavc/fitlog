package com.saico.core.domain.usecase.outdoor

import javax.inject.Inject

data class OutdoorUseCase @Inject constructor(
    val saveOutdoorSessionUseCase: SaveOutdoorSessionUseCase,
    val getOutdoorSessionsUseCase: GetOutdoorSessionsUseCase
)
