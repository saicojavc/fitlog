# Tareas de Refactorización: Workout Tracker Interactivo

## [Core: Model]
- [x] Crear `WgerExercise.kt`
- [x] Crear `RoutineModels.kt`

## [Core: Network]
- [x] Crear `WgerDtos.kt` con `@Serializable` o `@Json` (Moshi)
- [x] Crear `WgerApiService.kt`
- [x] Actualizar `NetworkModule.kt` para incluir el servicio de wger

## [Core: Database & Data]
- [x] Crear `WgerExerciseEntity.kt`
- [x] Crear `WgerExerciseDao.kt`
- [x] Actualizar `FitlogDatabase.kt` para registrar la nueva entidad y DAO
- [x] Implementar `WgerRepositoryImpl.kt` con lógica de precarga en paralelo (Ejercicios + Imágenes)

## [Feature: GymWork]
- [x] Refactorizar `GymWorkViewModel.kt`:
    - [x] Implementar estados `Planning` vs `Execution`.
    - [x] Implementar `Rest Timer` resiliente usando `System.currentTimeMillis()`.
    - [x] Lógica de cambio automático de ejercicio al completar series.
- [x] Rediseñar `GymWorkScreen.kt`:
    - [x] Implementar `DaySelectorTabs`.
    - [x] Implementar `PlanningView` (Lista de ejercicios con imágenes).
    - [x] Implementar `LiveWorkoutView` (Tracker activo).
    - [x] Crear componentes: `WgerExerciseCard`, `RestTimerOverlay`, `SetsInteractionTable`.
