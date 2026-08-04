# Plan de Refactorización: Workout Tracker Interactivo (wger API)

Esta actualización transformará el módulo `:feature:gymwork` de un registro manual a una experiencia guiada **offline-first**, consumiendo ejercicios de la API wger y automatizando el flujo de entrenamiento con temporizadores de descanso y rutinas predefinidas.

## User Review Required

> [!IMPORTANT]
> **Consumo de API:** La API de wger se consultará inicialmente para poblar la base de datos local (`WgerExerciseEntity`). Se recomienda una precarga al iniciar la app o al entrar por primera vez al módulo de gimnasio para garantizar el funcionamiento offline.
>
> **Mapeo de Imágenes:** Wger separa ejercicios de sus imágenes. Implementaremos un `WgerRepository` que combine ambas fuentes de datos para mostrar una UI rica.

## Proposed Changes

### [Core: Model]
Definición de las estructuras de dominio para soportar rutinas guiadas y el catálogo de wger.

#### [NEW] [WgerExercise.kt](file:///C:/Users/arian/StudioProjects/fitlog/core/model/src/main/java/com/saico/core/model/WgerExercise.kt)
#### [NEW] [RoutineModels.kt](file:///C:/Users/arian/StudioProjects/fitlog/core/model/src/main/java/com/saico/core/model/RoutineModels.kt) (Incluye `DayRoutine`, `RoutineExercise`, `ExerciseSetEntry`)

---

### [Core: Network]
Implementación del cliente para wger.

#### [NEW] [WgerApiService.kt](file:///C:/Users/arian/StudioProjects/fitlog/core/network/src/main/java/com/saico/core/network/api/WgerApiService.kt)
#### [NEW] [WgerDtos.kt](file:///C:/Users/arian/StudioProjects/fitlog/core/network/src/main/java/com/saico/core/network/dto/WgerDtos.kt)
#### [MODIFY] [NetworkModule.kt](file:///C:/Users/arian/StudioProjects/fitlog/core/network/src/main/java/com/saico/core/network/di/NetworkModule.kt) (Si existe, o crear uno para proveer el servicio de wger)

---

### [Core: Database & Data]
Persistencia local para ejercicios de wger y lógica de rutinas por defecto.

#### [NEW] [WgerExerciseEntity.kt](file:///C:/Users/arian/StudioProjects/fitlog/core/database/src/main/java/com/saico/core/database/entity/WgerExerciseEntity.kt)
#### [NEW] [WgerExerciseDao.kt](file:///C:/Users/arian/StudioProjects/fitlog/core/database/src/main/java/com/saico/core/database/dao/WgerExerciseDao.kt)
#### [MODIFY] [FitlogDatabase.kt](file:///C:/Users/arian/StudioProjects/fitlog/core/database/src/main/java/com/saico/core/database/FitlogDatabase.kt) (Registrar nueva entidad y DAO)
#### [NEW] [WgerRepositoryImpl.kt](file:///C:/Users/arian/StudioProjects/fitlog/core/data/src/main/java/com/saico/core/data/repository/WgerRepositoryImpl.kt)

---

### [Feature: GymWork]
Rediseño completo de la experiencia de usuario.

#### [MODIFY] [GymWorkViewModel.kt](file:///C:/Users/arian/StudioProjects/fitlog/feature/gymwork/src/main/java/com/saico/feature/gymwork/GymWorkViewModel.kt)
- Lógica de estados `Planning` vs `Execution`.
- Temporizador de descanso (`Rest Timer`) reactivo.
- Cálculo de calorías por volumen.

#### [MODIFY] [GymWorkScreen.kt](file:///C:/Users/arian/StudioProjects/fitlog/feature/gymwork/src/main/java/com/saico/feature/gymwork/GymWorkScreen.kt)
- `DaySelectorTabs`: Selector horizontal de Lunes a Domingo.
- `PlanningView`: Lista de ejercicios del día con imágenes.
- `LiveWorkoutView`: Pantalla de seguimiento activa con tabla de series y temporizador superpuesto.

#### [NEW] Components
- `WgerExerciseCard.kt`
- `RestTimerOverlay.kt`
- `SetsInteractionTable.kt`

---

## Verification Plan

### Automated Tests
- Test unitario para `GymWorkViewModel` verificando la transición automática de ejercicios al completar series.
- Test de repositorio para asegurar que `WgerRepository` priorice el cache local sobre la red.

### Manual Verification
- Iniciar sesión, navegar a "Gym", seleccionar un día y verificar que se carguen los ejercicios de wger.
- Iniciar entrenamiento, completar una serie y validar que aparezca el temporizador de 90s con vibración al finalizar.
- Apagar el WiFi/Datos y verificar que la rutina siga siendo accesible y funcional.
