# Walkthrough: Workout Tracker Interactivo (wger API)

Se ha completado la refactorización integral del módulo de gimnasio, transformándolo en una experiencia interactiva guiada.

## Cambios Realizados

### Capa de Datos e Infraestructura
- **Modelos de Dominio**: Creados `WgerExercise`, `RoutineExercise`, `DayRoutine` y `ExerciseSetEntry` para soportar el flujo interactivo.
- **API wger**: Implementado `WgerApiService` con soporte para ejercicios en español, imágenes y categorías.
- **Offline-first**:
    - Creada entidad `WgerExerciseEntity` en Room.
    - Implementada precarga en paralelo en `WgerRepositoryImpl` que combina ejercicios e imágenes en una única entidad local.
    - Rutinas por defecto mapeadas por día de la semana (Push, Pull, Legs).

### Lógica de Entrenamiento (ViewModel)
- **Estados Duales**: Transición entre `Planning` (selección de día y visualización de rutina) y `Execution` (seguimiento en vivo).
- **Temporizador de Descanso Resiliente**: Basado en `System.currentTimeMillis()`, garantizando precisión ante cambios de configuración o segundo plano.
- **Auto-avance**: El sistema detecta cuando se completan todas las series de un ejercicio y avanza automáticamente al siguiente.
- **Cálculo de Calorías**: Actualizado para calcularse dinámicamente según el volumen de carga completado en tiempo real.

### Interfaz de Usuario (Compose)
- **Rediseño Estético**: Manteniendo el estilo neón/oscuro de Fitlog.
- **Componentes Interactivos**:
    - `DaySelectorTabs`: Pestañas horizontales para navegar rutinas.
    - `SetsInteractionTable`: Tabla de series con inputs de peso/reps y confirmación táctil.
    - `RestTimerOverlay`: Superposición animada para el descanso entre series.
    - `LiveWorkoutView`: Pantalla principal de entrenamiento con imágenes de wger vía Coil.
- **Feedback Háptico**: Vibración automática al finalizar el descanso.

## Verificación Realizada
- **Sincronización de Dependencias**: Agregado Coil y configurado desugaring implícito mediante tipos seguros.
- **Arquitectura**: Separación clara entre red, base de datos, dominio y presentación siguiendo Clean Architecture.
